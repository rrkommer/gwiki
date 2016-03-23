var curAutocomplete;
function WeditAutocomplete(jwedit, weditconfig, completeChar) {
	this.jwedit = jwedit;
	this.weditconfig = weditconfig;
	this.completeChar = completeChar;
	this.list = [];
	this.typedText = '';
	// popup window
	this.popup = null;
	curAutocomplete = this;
	var thisobj = this;
	this.start = function() {
		var range = wedit_getSavedRange();
		console.debug("Char before: " + completeChar);

		var point = wedit_getCursorCoords();
		var startc = range.startContainer;
		var _this = this;
		weditconfig.autocompletegetitemshandler(jwedit, weditconfig, completeChar, "", function(itemlist) {
			_this.list = itemlist;

			_this.popup = $("<div id='wautocopletewindow' class='wautocmp'>");
			var jul = $("<ul id='wautocopleteul'></ul>");
			_this.popup.append(jul);
			_this.popup.attr("style", "position: absolute; left, " + point.x + "; top: " + point.y + ";");
			_this.fillList(jul);

			range.insertNode(_this.popup[0]);

			weditconfig.keydownhandler = function(jwedit, weditconfig, event) {
				return _this.onkeydown(event);
			};
			weditconfig.keyuphandler = function(jwedit, weditconfig, event) {
				return _this.onkeyup(event);
			};
			var clickOutsideHandler = function(event) {
				console.debug("clicked somewhere");
				$(window).unbind("click", clickOutsideHandler);
				_this.close();
			};
			$(window).bind("click", clickOutsideHandler);

		});
	};
	this.close = function() {
		this.popup.remove();
		this.weditconfig.keydownhandler = weditconfig.stdkeydownhandler;
		this.weditconfig.keyuphandler = null;
		this.typedText = '';
		curAutocomplete = null;

	}
	this.filterItem = function(item) {
		var search = this.typedText.trim();
		var len = search.length;
		if (len < 1) {
			return true;
		}
		dumpString(search);
		var label = item.label.toLowerCase();
		var key = item.url.toLowerCase();
		var search = search.toLowerCase();
		if (label && label.indexOf(search) != -1) {
			return true;
		}
		if (key && key.indexOf(search) != -1) {
			return true;
		}
		console.debug("No match: " + search + "> " + label + ", " + key);
		return false;
	};
	this.fillList = function(jul) {
		var _this = this;
		console.debug("weditautoc_fillList: '" + _this.typedText + "'");
		for ( var i in _this.list) {
			var selitem = _this.list[i];
			if (_this.filterItem(selitem) == false) {
				continue;
			}

			var li = $("<li class='wautoli' data-acidx='" + i + "'></li>");
			li.click(function(event) {
				_this.popup.remove();
				_this.onselect(selitem);
				event.stopPropagation();
			});
			var label = selitem.label;
			var key = selitem.key;
			li.append(label);
			jul.append(li);
		}
	};
	this.refreshList = function() {
		var ul = $("#wautocopleteul");
		ul.empty();
		this.fillList(ul);
	}
	this.onselect = function(item) {
		var text = item.url;
		// console.debug("insert: " + text);

		this.close();
		jwedit.focus();
		if (item.onInsert) {
			var fn = window[item.onInsert];
			// is object a function?
			if (typeof fn === "function") {
				fn.apply(null, [ item] );
			} else {
				console.error("Cannot find function")
			}
			
		} else {
			wedit_restoreSelection(this.jwedit, this.weditconfig);
			// wedit_restoreSelectionToCur();
			wedit_removeLeftUntil(this.completeChar, false);
			wedit_moveCursorserToCur();
			wedit_insertIntoPos(text);
			wedit_moveforward(text.length);
		}
	};
	this.insertSelected = function(jselitem) {
		var idx = jselitem.attr("data-acidx");
		var item = this.list[idx];
		return item;
	};

	this.onkeyup = function(event) {
		console.debug("popkeypup	: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
		    + event.altKey + "; curtext: '" + this.typedText + "'");
		switch (event.which) {
		case 9: // TAB
		case 13: // ENTER
		case 27: // ESC
		case 38: // UP
		case 40: // DOWN
			event.stopPropagation();
			return false;
		case 8: // backspace
			if (this.typedText.length < 1) {
				this.close();
				return false;
			}
			this.typedText = weditautoc_curtext.substring(0, this.typedText.length - 1);
			this.refreshList();
			break;
		default:
			if (event.which < 33) {
				break;
			}
			var ch = keyeventToString(event);
			if (event.shiftKey == false) {
				ch = ch.toLowerCase();
			}
			this.typedText += ch;
			this.refreshList();
			return true;
		}

	};
	this.onkeydown = function(event) {
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
				var item = this.insertSelected(found)
				this.onselect(item);
			} else {
				this.close();
			}
			event.stopPropagation();
			event.preventDefault();
			break;
		case 27: // ESC
			this.close();
			event.stopPropagation();
			event.preventDefault();
			console.debug("popup removed");
			return false;
		case 38: // UP
			var found = $(".wautocmpsel");
			if (found.length > 0 && found.prev()) {
				found.removeClass("wautocmpsel");
				found.prev().addClass("wautocmpsel");
			}
			event.stopPropagation();
			event.preventDefault();
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
			event.preventDefault();
			return false;
		}
	}

}

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

function wedit_autoComplete_checkstart(jwedit, typed, weditconfig, negoffs) {
	var before = wedit_charAtPos(negoffs);
	console.debug("checkautoc: " + before);
	if (weditconfig.ctrlSpaceAttchars.indexOf(before) == -1) {
		return;
	}
	curAutocomplete = new WeditAutocomplete(jwedit, weditconfig, before);
	curAutocomplete.start();
}

function dumpString(str) {
	var out = '';
	for (var i = 0; i < str.length; ++i) {
		var c = str.charAt(i);
		out += "'" + c + "' (" + c.charCodeAt(0) + "), ";
	}
	console.debug("> " + out);
}
