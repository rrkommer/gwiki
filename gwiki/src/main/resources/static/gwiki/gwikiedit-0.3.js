var gwikiEditCurrentDialog;
var gwikiEditField;

function gwikieditInsertImageCb(fileName, tmpFileName) {
	// alert('receceived image: ' + tmpFileName);
	$(gwikiEditCurrentDialog).dialog('close');
	gwikiEditField.focus();
	if (tmpFileName != null && tmpFileName != null && tmpFileName != "") {
		insertIntoTextArea(gwikiEditField, '!' + tmpFileName + '!', '');
	}
}

(function($) {
	$.fn.gwikiedit = function(settings) {
		var field = this;
		//alert("field: " + $(field).attr('id'));
		gwikiEditField = field;
		var currentSearchType = '';
		var maximized = false;
		var pn = settings['partName'];
		options = {
			parentPageId : '',
			partName : pn,
			linkAutoCompleteUrl : '',
			editorClassName : "gwiki-editor",
			toolBarClassName : "gwiki-toolBar",
			tagMask : "<(.*?)>",
			toolbars : {
				viewmode : [ {
					label : "WikiPreview",
					tooltip : "",
					accessKey : "",
					callBack : "onPreview"
				} ],
				wikitb : [ {
					label : "H1",
					tooltip : "Head 1 (Ctrl+1)",
					accessKey : "1",
					closeTag : "",
					openTag : "h1. ",
					keyCode : 49
				}, {
					label : "B",
					tooltip : "Bold (Ctrl+b)",
					accessKey : "b",
					openTag : "*",
					closeTag : "*",
					keyCode : 66
				}, {
					label : "I",
					tooltip : "Italic (Ctrl+i)",
					accessKey : "i",
					openTag : "_",
					closeTag : "_",
					keyCode : 73
				}, {
					label : "{{}}",
					tooltip : "Codeblock (Alt+c)",
					// accessKey : "b",
					openTag : "{{",
					closeTag : "}}",
					altKeyCode : 67
				}, {
					label : "Link",
					tooltip : "Link (Alt+l)",
					accessKey : "l",
					callBack : "showLinkProposal",
					altKeyCode : 76
				}, {
					label : "Image",
					tooltip : "Image (Alt+i)",
					accessKey : "i",
					callBack : "showImageProposal",
					altKeyCode : 73
				}, {
					label : "Screenshot",
					tooltip : "Insert new Screenshot (Alt+s)",
					accessKey : "",
					callBack : "insertImage"// ,
				// altKeyCode : 73
						} ]
			}
		};
		var options = $.extend(options, settings);

		var openPos = 0;
		var closePos = 0;
		var scrollPos = 0;
		var openTags = new Array();
		var keyCtrl = false;
		var keyShift = false;
		var keyAlt = false;
		var pageType = '';
		var linkstart = '';
		var linkend = '';
		var keyMap = {};
		var altKeyMap = {};
		return this.each(function() {
			var field = this;
			var $$ = $(field);
			var oId = $$.attr("id") || "";
			var oClassName = $$.attr("class") || "";
			var oTitle = $$.attr("title") || "";
			var oName = $$.attr("name") || "";

			// wrap textfield in a container div
				var editdiv = $(field).wrap(
						"<div id=\"" + oId + "\" title=\"" + oTitle + "\" class=\""
								+ oClassName + "\" width=\"100%\" height=\"100%\"></div>");

				// var editdiv = document.elementById(oId);
				// alert(editdiv);
				// add the toolbar et statusbar
				var toolbar = $(
						"<div class=\"" + options.toolBarClassName + "\"></div>")
						.insertBefore(field);
				// gwikiEditFrame(editdiv);
				// copy attributes
				$(field).attr("class", options.editorClassName);

				$(options.toolbars['wikitb']).each(
						function(i) {
							var button = this;
							if (button.keyCode != undefined) {
								keyMap[button.keyCode] = button;
							}
							if (button.altKeyCode != undefined) {
								altKeyMap[button.altKeyCode] = button;
							}
							var title = button.label;
							if (button.tooltip != undefined) {
								title = button.tooltip;
							}
							$(
									"<span><a href=\"\" accesskey=\"" + button.accesskey
											+ "\" title=\"" + title + "\">" + button.label
											+ "</a>&nbsp;|&nbsp;</span>").click(function() {
								tag(button);
								return false;
							}).appendTo(toolbar);
						});

				if (!$.browser.opera)
					$(field).keydown(keyEvent);
				else
					$(field).keypress(keyEvent); // opera fix

				// create attribute
				function attribute(string) {
					if (string) {
						return string.replace(/@(.*?)@/g, function(a) {
							return prompt(a.replace(/@/g, ""), "")
						});
					} else {
						return "";
					}
				}

				function tag(button) {
					get();
					// if it's a function to fire
					if ($.isFunction(eval(button.callBack))) {
						eval(button.callBack).call();
						// if it's tag to apply
					} else {
						openTag = attribute(button.openTag);
						closeTag = attribute(button.closeTag);
						if (selection != "") {
							wrap(openTag, closeTag);
						} else {
							if (!tagIsOpen(closeTag)) {
								if (closeTag) {
									openTags.push(closeTag);
								}
								wrap(openTag, "");
							} else {
								openTags.pop();
								wrap("", closeTag);
							}
						}
					}
				}

				// add tag
				function wrap(openTag, closeTag) {
					string = openTag + selection + closeTag;
					// if Ctrl, Alt or Shift key pressed
					if (keyCtrl == true && keyShift == true) {
						lines = selection.replace(new RegExp("\r?\n", "g"), "~�~"); // ie
						// hack
						lines = lines.split("~�~");
						n = lines.length;
						for (i = n - 1; i >= 0; i--) {
							lines[i] = (lines[i] != "") ? openTag + lines[i] + closeTag : "";
						}
						string = lines.join("\r\n");
						start = openPos;
						end = openPos + string.length - n + 1;
					} else if (keyCtrl == true) {
						start = openPos + openTag.length;
						end = openPos + openTag.length + selection.length;
					} else if (keyShift == true) {
						start = openPos;
						end = openPos + string.length;
					} else {
						start = openPos + openTag.length + selection.length
								+ closeTag.length;
						end = start;
					}
					// replace selection by the new string
					if (document.selection) {
						newSelection = document.selection.createRange();
						newSelection.text = string;
					} else if (openPos || openPos == "0") {
						field.value = field.value.substring(0, openPos) + string
								+ field.value.substring(closePos, field.value.length);
					} else {
						field.value += string;
					}
					set(start, end);
				}

				// set a selection
				function set(start, end) {
					if (field.createTextRange) {
						range = field.createTextRange();
						range.collapse(true);
						range.moveStart("character", start);
						range.moveEnd("character", end - start);
						range.select();
					} else if (field.setSelectionRange) {
						field.setSelectionRange(start, end);
					}
					field.scrollTop = scrollPos;
					field.focus();
				}

				// get the selection
				function get() {
					field.focus();
					scrollPos = field.scrollTop;
					if (document.selection) {
						selection = document.selection.createRange().text;
						if ($.browser.msie) { // ie
							range = field.createTextRange();
							range.moveToBookmark(document.selection.createRange()
									.getBookmark());
							range.moveEnd("character", field.value.length);
							openPos = field.value.length - range.text.length;
							openPos = openPos
									- field.value.substr(0, openPos).split("\r\n").length; // ie
							// hack
							if (selection.length > 0)
								openPos -= 1;
							closePos = selection.length;
						} else { // opera
							openPos = field.selectionStart;
							closePos = field.selectionEnd;
						}
					} else if (field.selectionStart || field.selectionStart == "0") { // gecko
						openPos = field.selectionStart;
						closePos = field.selectionEnd;
						selection = field.value.substring(openPos, closePos);
					} else {
						selection = "";
					}
					return selection;
				}

				// check if tag is already open
				function tagIsOpen(tag) {
					var n = openTags.length;
					for ( var i = 0; i < n; i++) {
						if (openTags[i] == tag) {
							return true;
						}
					}
					return false;
				}

				// set keys pressed
				function keyUpDown(evt) { // safari doesn't fire event on
					// shift and control key
					keyCtrl = evt.ctrlKey;
					keyShift = evt.shiftKey;
					keyAlt = evt.altKey;
				}
				function showLinkProposal() {
					pageType = 'gwiki';
					linkstart = '[';
					linkend = ']';
					showSuggest();
				}
				function showImageProposal() {
					pageType = 'attachment';
					linkstart = '!';
					linkend = '!';
					showSuggest();
				}

				function insertImage() {
					wikiEditInsertImage(options, field);
				}
				function showSuggest() {
					wikiEditShowSuggest(options, field, pageType, linkstart, linkend);

				}
				function keyEvent(evt) {
					gwikiSetContentChanged();
					if (evt.ctrlKey == true && evt.keyCode != 17) {
						var ta = evt.target;
						if (keyMap[evt.keyCode] != undefined) {
							var button = keyMap[evt.keyCode];
							tag(button);
							evt.stopPropagation();
							return false;
						}
						// alert("ctr:" + evt.keyCode);
					}
					if (evt.altKey == true && evt.keyCode != 18) {
						// alert("alt: " + evt.keyCode);
						if (altKeyMap[evt.keyCode] != undefined) {
							var button = altKeyMap[evt.keyCode];
							tag(button);
							evt.stopPropagation();
							return false;
						}
					}
					if (evt.ctrlKey == true && evt.keyCode != 17) {
						// alert('press: ' + evt.keyCode + " " + evt.ctrlKey + " " +
						// evt.altKey);
						return;
					}
				}
			});
	};
})(jQuery);
