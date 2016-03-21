(function() {

	var config = {};
	var DOWN_ARROW_KEY = 40;
	var UP_ARROW_KEY = 38;
	var ESC_KEY = 27;
	var ENTER_KEY = 13;
	var END_WORD_KEYS = [ 32, 59, 186, 188, 190 ];

	function parseOptions(param) {
		return param.options == null && typeof param != "boolean" ? param.split(",") : param.options;
	}
	tinymce.create('tinymce.plugins.tweditac', {

	  setOptions : function(param) {
		  config.options = parseOptions(param);
	  },

	  getOptions : function() {
		  return config.options;
	  },

	  init : function(ed, url) {
		  console.debug("init tweditac");
		  config = {
		    tweidac_checkac_start : ed.getParam('tweidac_checkac_start'),
		    options : parseOptions(ed.getParam('autocomplete_options', '')),
		    ed : ed,
		    itemReceiver : gwedit_autocomplete_entries
		  };
		  /**
			 * div with ul popup window
			 */
		  var popup;
		  var popupul;
		  /**
			 * List of items
			 */
		  var itemlist = new Array();
		  var _this = this;
		  var keyupHandler = checkAutocomplete;
		  var keydownHandler = defaultHandler;
		  var typedText = '';

		  function startAutocomplete(ed, char) {
			  config.itemReceiver(char, typedText, popupList);
		  }
		  function popupList(list) {
			  itemlist = list;
			  var range = ed.selection.getRng();
			  var point = getCurrentCursorPos();
			  var doc = ed.contentWindow.document;
			  popup = ed.$("<div id='wautocopletewindow' class='wautocmp'>");
			  popupul = ed.$("<ul id='wautocopleteul'></ul>");
			  popup.append(popupul);
			  popup.attr("style", "position: absolute; left, " + point.x + "; top: " + point.y + ";");
			  fillList(popupul);
			  
			  range.insertNode(popup[0]);

			  keydownHandler = onpopupkeydown;
			  keyupHandler = onpopupkeyup;
			  var clickOutsideHandler = function(event) {
				  console.debug("clicked somewhere");
				  $(window).unbind("click", clickOutsideHandler);
				  close();
			  };
			  $(window).bind("click", clickOutsideHandler);

			  var clickOutsideHandlerEd = function(event) {
				  console.debug("clicked somewhere in ed");
				  ed.off("click", clickOutsideHandlerEd);
				  close();
			  };
			  ed.on('click', clickOutsideHandlerEd, true);
		  }
		  function close() {
			  popup.remove();
			  typedText = '';
			  keyupHandler = checkAutocomplete;
			  keydownHandler = defaultHandler;
		  }
		  function filterItem(item) {
			  var search = typedText.trim();
			  var len = search.length;
			  if (len < 1) {
				  return true;
			  }
			  dumpString(search);
			  var label = item.label.toLowerCase();
			  var key = item.key.toLowerCase();
			  var search = search.toLowerCase();
			  if (label && label.indexOf(search) != -1) {
				  return true;
			  }
			  if (key && key.indexOf(search) != -1) {
				  return true;
			  }
			  console.debug("No match: " + search + "> " + label + ", " + key);
			  return false;
		  }
		  function fillList(jul) {
			  console.debug("weditautoc_fillList: '" + typedText + "'");
			  for ( var i in itemlist) {
				  var selitem = itemlist[i];
				  if (filterItem(selitem) == false) {
					  continue;
				  }

				  var li = $("<li class='wautoli' data-acidx='" + i + "'></li>");
				  li.click(function(event) {
					  // strange dooesnt word
					  var idx = $(event.target).attr('data-acidx');
					  var clickitem = itemlist[idx];
					  popup.remove();
					  onselect(clickitem);
					  cancelEvent(event);
				  });
				  var label = selitem.label;
				  var key = selitem.key;
				  li.append(label);
				  jul.append(li);
			  }
		  }
		  function refreshList() {
			  popupul.empty();
			  fillList(popupul);
		  }
		  function onselect(item) {
			  var text = item.key;
			  console.debug("insert: " + text);

			  close();
			  if (item.onInsert) {
				  var fn = window[item.onInsert];
				  // is object a function?
				  if (typeof fn === "function") {
					  fn.apply(null, [ ed, item ]);
				  } else {
					  console.error("Cannot find function")
				  }

			  } else {
				  console.error("Cannot find function")
			  }
		  }
		  function insertSelected(jselitem) {
			  var idx = jselitem.attr("data-acidx");
			  var item = itemlist[idx];
			  return item;
		  }
		  function onpopupkeyup(event) {
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
				  if (typedText.length < 1) {
					  close();
					  return false;
				  }
				  typedText = typedText.substring(0, typedText.length - 1);
				  refreshList();
				  break;
			  default:
				  if (event.which < 33) {
					  break;
				  }
				  var ch = keyeventToString(event);
				  if (event.shiftKey == false) {
					  ch = ch.toLowerCase();
				  }
				  typedText += ch;
				  refreshList();
				  return true;
			  }

		  }
		  function onpopupkeydown(event) {
			  console.debug("popkeydown: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
			      + event.altKey);
			  var el = popup;// $("#wautocopletewindow");
			  var ul = popupul; // $("#wautocopleteul");
			  if (!ul[0]) {
				  console.warn("ac popup ul not found");
			  }
			  switch (event.which) {
			  case 9: // TAB
			  case 13: // ENTER
				  var found = ul.find(".wautocmpsel");
				  if (found.length && found.next()) {
					  var item = insertSelected(found)
					  onselect(item);
				  }
				  close();
				  return cancelEvent(event);
			  case 27: // ESC
				  close();
				  console.debug("popup removed");
				  return cancelEvent(event);
			  case 38: // UP
				  var found = ul.find(".wautocmpsel");
				  if (found.length > 0 && found.prev()) {
					  found.removeClass("wautocmpsel");
					  found.prev().addClass("wautocmpsel");
				  }
				  cancelEvent(event);
			  case 40: // DOWN
				  var found = ul.find(".wautocmpsel");
				  if (found.length > 0 && found.next()) {
					  found.removeClass("wautocmpsel");
					  found.next().addClass("wautocmpsel");
				  } else {
					  if (ul.children(":first")) {
						  var fel = ul.children(":first");
						  fel.addClass("wautocmpsel");
					  }
				  }
				  return cancelEvent(event);
			  }
		  }
		  function cancelEvent(event) {
			  // event.stopPropagation();
			  // event.preventDefault();
			  return tinymce.dom.Event.cancel(event);
		  }
		  function defaultHandler(event) {
			  // console.debug("defaultHandler");
		  }
		  function checkAutocomplete(event) {
			  var rng = ed.selection.getRng(true);
			  var txt = rng.startContainer.textContent;
			  var char = txt.substring(rng.startOffset - 1, rng.startOffset);
			  // console.log("character before current cursor position = [" + char +
				// "]");
			  if (config.tweidac_checkac_start.indexOf(char) != -1) {
				  startAutocomplete(ed, char);
			  }
		  }
		  function getCurrentCursorPos() {
			  var x = 0, y = 0;
			  var range = ed.selection.getRng();
			  var startc = range.startContainer;
			  // a trick to get coordinates.
			  var doc = ed.contentWindow.document;
			  var span = doc.createElement("span");
			  span.appendChild(doc.createTextNode("\u200b") /*
																														 * Zero-width space
																														 * character
																														 */);
			  range.insertNode(span);

			  var rect = span.getBoundingClientRect();
			  span.parentNode.removeChild(span);

			  y = rect.top;
			  x = rect.left;
			  return {
			    x : x,
			    y : y
			  };
		  }
		  ed.on('keyup', function(event) {
			  keyupHandler(event);
		  });
		  ed.on('keydown', function(event) {
			  keydownHandler(event);
		  });
		  // ed.on('keypress', function(event) {
		  // keyPressEvent(ed, event);
		  // });
		  // ed.on('click', function(event) {
		  // clickEvent(ed, event);
		  // });
	  },

	  getInfo : function() {
		  return {
		    longname : 'tweditac',
		    author : 'Roger Kommer',
		    version : tinymce.majorVersion + "." + tinymce.minorVersion
		  };
	  }
	});

	tinymce.PluginManager.add('tweditac', tinymce.plugins.tweditac);
})();

function twedit_dummy_itemReceiver(char, typedText, callback) {
	var dummy = [ {
	  key : "First",
	  label : "First"
	}, {
	  key : "second",
	  label : "second"
	} ];
	callback(dummy);
}