
function wedit_autocomplete_getdummyentries(jwedit, weditconfig, completeChar, typedText, callback) {
	console.error("wedit_autocomplete_getdummyentries called");
	callback([ {
	  label : "X Eintrag",
	  key : "first!"
	}, {
	  label : "X Eintrag",
	  key : 'second!'
	} ]);

}
var wedit_currentaclist;

/**
 * TODO 
 * @param ce
 * @param url where to get the data
 * @param weditconfig
 */
function wedit_autoComplete(jwedit, url, weditconfig) {
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

	weditconfig.autocompletegetitemshandler(jwedit, weditconfig, before, "", function(list) {
		wedit_currentaclist = list;

		var popup = $("<div id='wautocopletewindow' class='wautocmp'>");
		var jul = $("<ul id='wautocopleteul'></ul>");
		popup.append(jul);
		popup.attr("style", "position: absolute; left, " + point.x + "; top: " + point.y + ";");

		for ( var i in wedit_currentaclist) {
			var li = $("<li class='wautoli' data-acidx='" + i + "'></li>");
			li.click(function(event) {
				popup.remove();
				var selitem = wedit_currentaclist[i];
				wedit_autocomplete_onselect(weditconfig, popup, selitem);

				event.stopPropagation();
			});
			var label = wedit_currentaclist[i].label;
			var key = wedit_currentaclist[i].key;
			li.append(label);
			jul.append(li);
		}

		range.insertNode(popup[0]);

		weditconfig.keydownhandler = wedit_autocompleKeyEventHandler;
		weditconfig.keyuphandler = autocompleKeyUpHandler;
		var clickOutsideHandler = function(event) {
			console.debug("clicked somewhere");
			$(window).unbind("click", clickOutsideHandler);
			wedit_autocomplete_close(weditconfig, popup);
		};
		$(window).bind("click", clickOutsideHandler);

	});

}

function autocompleKeyUpHandler(jwedit, weditconfig, event) {
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

function wedit_getautocompletedSelectedInsert(jselitem) {
	var idx = jselitem.attr("data-acidx");
	var item = wedit_currentaclist[idx];
	return item;
}

function wedit_autocomplete_close(weditconfig, popup) {
	popup.remove();
	weditconfig.keydownhandler = weditconfig.stdkeydownhandler;
	weditconfig.keyuphandler = null;

}

function wedit_autocomplete_onselect(weditconfig, popup, item) {
	var text = item.key;
	console.debug("insert: " + text);
	wedit_autocomplete_close(weditconfig, popup);

	wedit_insertIntoPos(text);
	wedit_moveforward(text.length);
}

function wedit_autocompleKeyEventHandler(jwedit, weditconfig, event) {
	console.debug("popkeydown: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
	    + event.altKey);
	// var el = document.getElementById("wautocopletewindow");
	var el = $("#wautocopletewindow");
	var ul = $("#wautocopleteul");
	switch (event.which) {
	case 9: // TAB
	case 13: // ENTER
		var found = $(".wautocmpsel");
		if (found.length && found.next().get()) {
			var item = wedit_getautocompletedSelectedInsert(found)
			wedit_autocomplete_onselect(weditconfig, el, item);
		} else {
			wedit_autocomplete_close(weditconfig, el);
		}
		break;
	case 27: // ESC
		wedit_autocomplete_close(weditconfig, el);
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
			if (ul.children(":first")) {
				var fel = ul.children(":first");
				fel.addClass("wautocmpsel");
			}
		}
		event.stopPropagation();
		return false;
	}
}
