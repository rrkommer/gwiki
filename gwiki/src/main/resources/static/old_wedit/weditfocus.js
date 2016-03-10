//console.debug("Loaded contenteditfocus2.js");
savedRanges = new Object();

function wedit_getCurSel() {
	return savedRanges[0];
}

function wedit_insertIntoPos(text) {
	var currange = savedRanges;
	if (!currange[0]) {
		console.error("wedit; no range available");
		return;
	}
	var range = currange[0];
	var startc = range.startContainer;

	if (startc instanceof Text) {
		var starto = range.startOffset;
		var endo = range.endOffset;
		var txt = startc.nodeValue;
		var newtext = txt.substring(0, starto) + text + txt.substring(starto);
		startc.nodeValue = newtext;
	} else {
		console.error("No text node");

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

$(document).ready(function() {
	$('div[contenteditable="true"]').focus(function() {
		// console.debug("cef got focus");
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
		savedRanges[t] = window.getSelection().getRangeAt(0);
	}).on("mousedown click", function(e) {
		// console.debug("cef mousedown click");
		if (!$(this).is(":focus")) {
			e.stopPropagation();
			e.preventDefault();
			$(this).focus();
		}
	});
});