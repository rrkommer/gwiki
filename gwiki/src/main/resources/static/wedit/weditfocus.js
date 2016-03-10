console.debug("Loaded contenteditfocus2.js");
savedRanges = new Object();


function wedit_getSavedRange() {
	return savedRanges[0];
}
function wedit_getCurrentRange() {
	return window.getSelection().getRangeAt(0);
}

function wedit_insertIntoPos(text) {

	var range = window.getSelection().getRangeAt(0);
	var startc = range.startContainer;

	if (startc instanceof Text) {
		var starto = range.startOffset;
		var endo = range.endOffset;
		var txt = startc.nodeValue;
		var newtext = txt.substring(0, starto) + text + txt.substring(starto);
		startc.nodeValue = newtext;
		console.debug("wedit_insertIntoPos: " + text + "; " + starto);
		wedit_moveforward(starto + text.length);

	} else {
		var textnode = document.createTextNode(text);
		startc.appendChild(textnode);
		console.debug("No text node: " + startc);

	}

}

function wedit_charBeforePos() {
	if (!savedRanges[0]) {
		return 0;
	}
	var range = savedRanges[0];
	var startc = range.startContainer;

	if ((startc instanceof Text) == false) {
		return 0;
	}
	var txt = startc.nodeValue;
	var starto = range.startOffset;
	if (starto == 0) {
		return 0;
	}
	var ret = txt.charAt(starto - 1);
	return ret;
}

function wedit_createRange() {
	if (!document.selection || !document.selection.createRange) {
		return new Range();
	} else {
		return document.selection.createRange();
	}
}

function wedit_moveToEndline() {
	var range = savedRanges[0];
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

	// window.setTimeout(function() {
	var range = window.getSelection().getRangeAt(0);// savedRanges[0];
	var startc = range.startContainer;
	var starto = range.startOffset;
	if (!(startc instanceof Text)) {
		console.warn("startc is no text: " + startc);
		return;
	}
	var sel = window.getSelection();
	var txt = startc.nodeValue;
	var noff = starto + num;
	noff = Math.min(noff, txt.length);
	console.debug("nodetext: " + txt + "; offset: " + noff);
	var nrange = wedit_createRange();

	nrange.setStart(range.startContainer, noff);
	sel.removeAllRanges();
	sel.addRange(nrange);
	console.debug("wedit_moveforward: " + txt + "; " + noff + "; old: " + starto);
	// }, 10);
}
function wedit_getCursorCoords() {
	var x = 0, y = 0;
	if (!savedRanges[0]) {
		return {
		  x : x,
		  y : y
		};
	}
	var range = savedRanges[0];
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

	if (!range) {
		range = savedRanges[0];
	}
	console.debug("wedit_restoreSelection: " + range.startContainer);
	// $('div[contenteditable="true"]').focus();
	jnode.focus();

	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);

}

function wedit_bindfocushandler(jobj) {
	
	jobj.focus(function() {
		console.debug("wedit got fous");
		var s = window.getSelection();
		
		var t = $('div[contenteditable="true"]').index(this);
		if (typeof (savedRanges[t]) === "undefined") {
			if (!document.selection || !document.selection.createRange) {
				savedRanges[t] = new Range();
			} else {
				savedRanges[t] = document.selection.createRange();
			}

		} else if (s.rangeCount > 0) {
			try {
				s.removeAllRanges();
				s.addRange(savedRanges[t]);
			} catch (e) {
				console.error("range not supported by browser: " + e);
			}
		}
	}).bind("mouseup keyup", function() {
		// console.debug("cef mouseup keyup");
		var t = $('div[contenteditable="true"]').index(this);
		var range = window.getSelection().getRangeAt(0);
		savedRanges[t] = range;
		if (range.startContainer instanceof Text) {
			console.debug("lastr is text");
		} else {
			console.debug("lastr is NO text: " + range.startContainer + "; " + range.endContainer + "; " + range.startOffset + ":" + range.endOffset);
		}
		
		
	}).on("mousedown click", function(e) {
		// console.debug("cef mousedown click");
		if (!$(this).is(":focus")) {
			e.stopPropagation();
			e.preventDefault();
			$(this).focus();
		}
	});
}
