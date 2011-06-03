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

function gwikiEditInsertTemplate(templateText)
{
	gwikiEditField.focus();
	insertIntoTextArea(gwikiEditField, templateText, '');
}

(function($) {
	$.fn.gwikiedit = function(settings) {
		var field = this;
		// alert("field: " + $(field).attr('id'));
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
					label : "Save",
					tooltip : "gwiki.editor.button.tooltip.save".i18n(),
					accessKey : "",
					callBack : "gwikiEditSave",
					clazz : "gwikiedit_icon_tinymce gwikiedit_save"
				}, {
					label : "Cancel",
					tooltip : "gwiki.editor.button.tooltip.cancel".i18n(),
					accessKey : "",
					callBack : "gwikiEditCancel",
					clazz : "gwikiedit_icon_tinymce gwikiedit_cancel"
				}, {
					label : "Fullscreen",
					tooltip : "gwiki.editor.button.tooltip.fullscreen".i18n(),
					accessKey : "",
					callBack : "gwikiFullscreen('gwikiwktabs')",
					clazz : "gwikiedit_icon_tinymce gwikiedit_fullscreen"
				}, {
					type : "seperator"
				}, {
					label : "H1",
					tooltip : "gwiki.editor.button.tooltip.h1".i18n(),
					accessKey : "1",
					closeTag : "",
					openTag : "h1. ",
					keyCode : 49,
					clazz : "gwikiedit_head1"
				}, {
					label : "B",
					tooltip : "gwiki.editor.button.tooltip.bold".i18n(),
					accessKey : "b",
					openTag : "*",
					closeTag : "*",
					keyCode : 66,
					clazz : "gwikiedit_icon_tinymce gwikiedit_bold"
				}, {
					label : "I",
					tooltip : "gwiki.editor.button.tooltip.italic".i18n(),
					accessKey : "i",
					openTag : "_",
					closeTag : "_",
					keyCode : 73,
					clazz : "gwikiedit_icon_tinymce gwikiedit_italic"
				}, {
					label : "U",
					tooltip : "gwiki.editor.button.tooltip.underline".i18n(),
					accessKey : "u",
					openTag : "+",
					closeTag : "+",
					clazz : "gwikiedit_icon_tinymce gwikiedit_underline"
				}, {
					label : "{{}}",
					tooltip : "gwiki.editor.button.tooltip.codeblock".i18n(),
					// accessKey : "b",
					openTag : "{{",
					closeTag : "}}",
					altKeyCode : 67,
					clazz : "gwikiedit_codeblock"
				}, {
					label : "Link",
					tooltip : "gwiki.editor.button.tooltip.link".i18n(),
					accessKey : "l",
					callBack : "showLinkProposal",
					altKeyCode : 76,
					clazz : "gwikiToolbarIcon gwikiedit_link"
				}, {
					label : "Image",
					tooltip : "gwiki.editor.button.tooltip.image".i18n(),
					accessKey : "i",
					callBack : "showImageProposal",
					altKeyCode : 73,
					clazz : "gwikiedit_image"
				}, {
					label : "Screenshot",
					tooltip : "gwiki.editor.button.tooltip.screenshot".i18n(),
					accessKey : "",
					callBack : "insertImage",
					clazz : "gwikiedit_screenshot"// ,
				// altKeyCode : 73
				}, {
					label : "Attachment",
					tooltip : "gwiki.editor.button.tooltip.attachment".i18n(),
					accessKey : "",
					callBack : "insertAttachment",
					clazz : "gwikiedit_attachment"// ,
				}, {
					label : "Blueprint",
					tooltip : "gwiki.editor.button.tooltip.blueprint".i18n(),
					accessKey : "",
					callBack : "insertTemplate",
					clazz : "gwikiedit_template"// ,
				}, {
					type : "seperator"
				}, {
					label : "Help",
					tooltip : "gwiki.editor.button.tooltip.help".i18n(),
					accessKey : "",
					callBack : "gwikiHelp",
					clazz : "gwikiedit_icon_tinymce gwikiedit_help"
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
				var t = "<div id=\"gwikiwed" + pn
						+ "\" style=\"padding-right: 8px\"></div>";
				var tewrap = $(field).wrap(t);

				var editdiv = $("#gwikiwed" + pn).wrap(
						"<div id=\"" + "gwikiwed2" + oId + "\" title=\"" + oTitle
								+ "\" class=\"" + oClassName
								+ "\" width=\"100%\" height=\"100%\"></div>");

				// add the toolbar et statusbar
				var toolbar = $("<table class='gwikiedit_table'><tbody><tr></tr></tbody></table>");
				var toolbarWrapper = $("<div class=\"" + options.toolBarClassName + "\"></div>")
						.insertBefore("#gwikiwed" + pn);
				toolbar.appendTo(toolbarWrapper);
				
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
						var hasBody = button.closeTag != null && button.closeTag != "" ? true : false;
						var clazz = button.type != null && button.type == "seperator" 
							? "gwikiedit_seperator"
							: "gwikiedit_icon " + button.clazz;
						var output = $(
							"<td><a href=\"\" accesskey=\"" + button.accesskey + "\" "
									+ " bodytag=\"" + hasBody 
									+ "\" title=\"" + title + "\" class='" + clazz 
									+ "'></a></td>")
									.click(function() {
										tag(button);
										return false;
									});
						output.appendTo(toolbar.find("tr"));
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
				function insertAttachment() {
					wikiEditShowSuggest(options, field, 'attachment', '[', ']');
				}
				function insertTemplate() {
					gwikiEditField = field;
					WREF = window.open(gwikiHomeUrl + "/edit/EditBlueprint","Blueprint Template",'width=1200,height=900');
					if(!WREF.opener){ WREF.opener = this.window; }
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

