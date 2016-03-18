wedit_rdebug("Loaded contenteditfocus2.js");
savedRanges = new Object();

var weditfocus_lastrange;
var weditfocus_prevrange;

function wedit_rdebug(text)
{
	console.debug(text);
}

function wedit_rangetostring(range)
{
	var s =  "" + range.startContainer + ":" + range.startOffset + ", " + range.endContainer + ":" + range.endOffset;
	if (range.startContainer instanceof Text) {
		s += " (" + range.startContainer.nodeValue + ")";
	}
	return s;
}


function wedit_cloneRange(range) {
	var nr = wedit_createRange();
	nr.setStart(range.startContainer, range.startOffset);
	nr.setEnd(range.endContainer, range.endOffset);
	return nr;
}

function wedit_getSavedRange() {
	// TODO not correct in case of mulitple windows.

	var range = weditfocus_lastrange;// savedRanges[0];
	if (range) {
		wedit_rdebug("getrange " + wedit_rangetostring(range));
		if ((range.startContainer instanceof Text) == false) {
			var crange = wedit_getCurrentRange();
			wedit_rdebug("getrange prev: " + wedit_rangetostring(weditfocus_prevrange));
			if (weditfocus_prevrange.startContainer instanceof Text) {
				return wedit_cloneRange(weditfocus_prevrange);
			}
		}
		return wedit_cloneRange(range);
	}

	return range;
}
function wedit_storeSavedRange(t, range) {
	var nr = wedit_cloneRange(range);
	// savedRanges[t] = nr;
	weditfocus_prevrange = weditfocus_lastrange;
	weditfocus_lastrange = nr;
	wedit_rdebug("setrange: " + wedit_rangetostring(nr));
}

function wedit_getCurrentRange() {
	return window.getSelection().getRangeAt(0);
}

function wedit_getElementAtRangeStartPos(range) {
	var startc = range.startContainer;
	var el = startc.getChildren(range.startOffset);
	if (el) {
		return el;
	}
	wedit_rdebug("No offset element found", range);
	return startc;
}

/**
 * removes text from right to left until character will be found
 * 
 * @param character
 *          character
 */
function wedit_removeLeftUntil(character, includechar) {
	var range = wedit_getSavedRange();
	var startc = range.startContainer;
	
	if (startc instanceof Text) {
		var starto = range.startOffset;
		var endo = range.endOffset;
		var txt = startc.nodeValue;
		var offset = -1;
		for (var i = endo; i >= 0; --i) {
			if (txt.charAt(i) == character) {
				offset = i;
				if (includechar) {
					--offset;
				}
				break;
			}
		}
		if (offset == -1) {
			console.warn("Cannot find character at left: " + character + "; " + wedit_rangetostring(range));
			return;
		}
		var newtext = txt.substring(0, offset + 1) + txt.substring(starto);
		startc.nodeValue = newtext;
		wedit_rdebug("wedit_removeLeftUntil: " + txt + "; " + newtext);
	} else {
		console.warn("Cannot remove text because no text node");
	}

}

function wedit_moveCursorserToCur() {
	var sel = window.getSelection();
	var range = wedit_getSavedRange();
	var nrange = wedit_createRange();
	nrange.setStart(range.startContainer, range.startOffset);
	sel.removeAllRanges();
	sel.addRange(nrange);

}



function wedit_insertIntoPos(text) {
	var range = wedit_getSavedRange();
	var startc = range.startContainer;

	if (startc instanceof Text) {
		var starto = range.startOffset;
		var endo = range.endOffset;
		var txt = startc.nodeValue;
		var newtext = txt.substring(0, starto) + text + txt.substring(starto);
		startc.nodeValue = newtext;
		wedit_rdebug("wedit_insertIntoPos: " + text + "; " + starto);
		wedit_moveforward(starto + text.length);

	} else {
		var insel = wedit_getElementAtRangeStartPos(range);
		var textnode = document.createTextNode(text);
		insel.appendChild(textnode);
		wedit_dumpposition("insertpos No text node", range);

	}

}



function wedit_charAtPos(negoffset) {
	var range = wedit_getSavedRange();
	var startc = range.startContainer;

	if ((startc instanceof Text) == false) {
		return 0;
	}
	var txt = startc.nodeValue;
	var starto = range.startOffset;
	if (starto == 0) {
		return 0;
	}
	var ret = txt.charAt(starto - negoffset);
	return ret;
}

function wedit_charBeforePos() {
	return wedit_charAtPos(1);
}

function wedit_createRange() {
	if (!document.selection || !document.selection.createRange) {
		return new Range();
	} else {
		return document.selection.createRange();
	}
}

function wedit_caretafterelement(el)
{
	var range = wedit_getCurrentRange();
	range.setStartAfter(el);
	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);
	wedit_rdebug("careteafter: " + wedit_rangetostring(range));
}


function wedit_moveToEndline() {
	var range = wedit_getSavedRange();
	var startc = range.startContainer;

	if (!(startc instanceof Text)) {
		console.warn("startc is no text: " + startc);
		return;
	}
	var sel = window.getSelection();
	var txt = startc.nodeValue;
	var nrange = wedit_createRange();
	nrange.setStart(range.startContainer, txt.length);

	sel.removeAllRanges();
	sel.addRange(nrange);

}
function wedit_moveforward(num) {

	var range = wedit_getSavedRange();
	var startc = range.startContainer;
	var starto = range.startOffset;
	if (!(startc instanceof Text)) {
		wedit_dumpposition("startc is no text", range);
		return;
	}
	var sel = window.getSelection();
	var txt = startc.nodeValue;
	var noff = starto + num;
	noff = Math.min(noff, txt.length);
	wedit_rdebug("nodetext: " + txt + "; offset: " + noff);
	var nrange = wedit_createRange();

	nrange.setStart(range.startContainer, noff);
	sel.removeAllRanges();
	sel.addRange(nrange);
	wedit_rdebug("wedit_moveforward: " + txt + "; " + noff + "; old: " + starto);
	// }, 10);
}
function wedit_getCursorCoords() {
	var range = wedit_getSavedRange();
	var x = 0, y = 0;
	if (!weditfocus_lastrange) {
		return {
		  x : x,
		  y : y
		};
	}
	var startc = range.startContainer;
	// a trick to get coordinates.
	var span = document.createElement("span");
	span.appendChild(document.createTextNode("\u200b") /*
																											 * Zero-width space
																											 * character
																											 */);
	window.getSelection().getRangeAt(0).insertNode(span);

	var rect = span.getBoundingClientRect();
	span.parentNode.removeChild(span);

	// if (range.getClientRects()) {
	// // range.collapse(true);
	// var rect = range.getClientRects()[0];
	y = rect.top;
	x = rect.left;
	// }

	return {
	  x : x,
	  y : y
	};
}

function wedit_restoreSelection(jnode, weditconfig, range) {

	// if (!range) {
	range = wedit_getSavedRange();
	// }
	wedit_rdebug("wedit_restoreSelection: " + range.startContainer);
	// $('div[contenteditable="true"]').focus();
	jnode.focus();

	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);

}

function wedit_restoreSelectionToCur()
{
	var range = wedit_getCurrentRange();
	wedit_rdebug("restoretocur: " + range.startContainer + ":" + range.startOffset)
	wedit_getSavedRange();
}


function wedit_bindfocushandler(jobj) {

	jobj.focus(function() {

		var sr = wedit_getSavedRange();
		var s = window.getSelection();
		var selrange = s.getRangeAt(0);
		wedit_rdebug("wedit got fous: " + selrange.startContainer);

		var t = $('div[contenteditable="true"]').index(this);

		if (typeof (weditfocus_lastrange) === "undefined") {
			wedit_rdebug("focus and new range");
			if (!document.selection || !document.selection.createRange) {
				wedit_storeSavedRange(t, new Range());
			} else {
				wedit_storeSavedRange(t, document.selection.createRange());
			}

		} else { // if (s.rangeCount > 0) {
			try {
				s.removeAllRanges();
				s.addRange(sr);
			} catch (e) {
				console.error("range not supported by browser: " + e);
			}
		}
	}).bind("mouseup keyup", function() {
		// wedit_rdebug("cef mouseup keyup");
		var t = $('div[contenteditable="true"]').index(this);
		var range = window.getSelection().getRangeAt(0);
		wedit_storeSavedRange(t, range);
		if (range.startContainer instanceof Text) {
			wedit_rdebug("lastr[" + t + "] is text");
		} else {
			wedit_dumpposition("lastpos[" + t + "] NO text", range);
		}

	}).on("mousedown click", function(e) {
		// wedit_rdebug("cef mousedown click");
		if (!$(this).is(":focus")) {
			e.stopPropagation();
			e.preventDefault();
			$(this).focus();
		}
	});
}

function wedit_dumpposition(range, message) {
	wedit_rdebug(message + ": " + range.startContainer + "; " + range.endContainer + "; " + range.startOffset + ":"
	    + range.endOffset + ": " + $(range.startContainer).html());
}
