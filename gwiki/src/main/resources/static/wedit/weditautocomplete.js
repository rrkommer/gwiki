
function wedit_autocomplete_getdummyentries(weditconfig, completeChar, typedText)
{
	return ["Erster Eintrag", "Zweiter Eintrag" ];
}


function wedit_autoComplete(ce, url,weditconfig) {
	var before = wedit_charBeforePos();
	if (weditconfig.ctrlSpaceAttchars.indexOf(before) == -1) {
		return;
	}
	
	if (!savedRanges[0]) {
		return;
	}
	console.debug("Char before: " + before);

	var range = savedRanges[0];
	var point = wedit_getCursorCoords();
	var startc = range.startContainer;

	var items = weditconfig.autocompletegetitemshandler(weditconfig, before, "");
	var popup = document.createElement("div");
	
	popup.setAttribute("id", "wautocopletewindow");
	
	popup.setAttribute("style", "position: absolute; left, " + point.x + "; top: " + point.y + ";");
	for (var i in items) {
		var p = document.createElement("p");
		var text = document.createTextNode(items[i]);
		p.appendChild(text);
		popup.appendChild(p);	
	}

	range.insertNode(popup);
	weditconfig.keydownhandler = autocompleKeyEventHandler;
	weditconfig.keyuphandler = autocompleKeyUpHandler; 
}

function autocompleKeyUpHandler(event, weditconfig) {
	console.debug("popkeypup	: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
	    + event.altKey);
	switch (event.which) {
	case 9: // TAB
	case 13: // ENTER
	case 27: // ESC
	case 38: // UP
	case 40: // DOWN
		event.stopPropagation();
		return false;
	}

}

function autocompleKeyEventHandler(event, weditconfig) {
	console.debug("popkeydown: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
	    + event.altKey);
	// var el = document.getElementById("wautocopletewindow");
	var el = $("#wautocopletewindow");

	switch (event.which) {
	case 9: // TAB
	case 13: // ENTER
		var found = $(".wautocmpsel");
		if (found.length && found.next().get()) {
			var text = found.html();
			console.debug("insert: " + text);
			wedit_insertIntoPos(text);
			wedit_moveforward(text.length);
		}
	case 27: // ESC
		el.remove();
		weditconfig.keydownhandler = weditconfig.stdkeydownhandler;
		weditconfig.keyuphandler = null;
		event.stopPropagation();
		console.debug("popup removed");
		return false;
	case 38: // UP
		var found = $(".wautocmpsel");
		if (found.length > 0 && found.prev()) {
			found.removeClass("wautocmpsel");
			found.prev().addClass("wautocmpsel");
		}
		event.stopPropagation();
		return false;
	case 40: // DOWN
		var found = $(".wautocmpsel");
		if (found.length > 0 && found.next()) {
			found.removeClass("wautocmpsel");
			found.next().addClass("wautocmpsel");
		} else {
			if (el.children(":first")) {
				var fel = el.children(":first");
				fel.addClass("wautocmpsel");
			}
		}
		event.stopPropagation();
		return false;
	}
}
