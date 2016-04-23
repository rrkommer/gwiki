function twedit_bind_native_paste(ed, selector) {
	// var frame = tinymce.$(selector);
	// frame = $("#tinymce");
	// var el = frame[0];
	window.addEventListener("paste", function(event) {
//		console.debug("twedit_bind_native_paste");
		var cp = new ClipData(event, event.clipboardData);
//		cp.dumpEvent();
		// twedit_dump_pasteEvent(event);
	});
}
function twedit_drop(ed, event) {
	console.debug('twedit_drop');
	var cp = new ClipData(event, event.dataTransfer);
	if (cp.hasAttachment == true) {
		cp.waitForBinaryData(function(clipData) {
			gwikiInsertNewAttachmentDialog(ed, clipData);
		});
	}
}
function twedit_paste(ed, event) {
	console.debug('twedit_paste');
	var cp = new ClipData(event, event.clipboardData);
	if (cp.hasAttachment == true) {
		cp.waitForBinaryData(function(clipData) {
			gwikiInsertNewAttachmentDialog(ed, clipData);
		});
	}

}

function twedit_preprocess(self, args) {
	console.debug("twedit_preprocess: " + args.content);
	var typ = wedit_get_selection_el_type(args.target);
	if (typ == 'PRE' && args.content) {
		var nc = args.content.replace(/<br\/>/g, '\n');
		nc = nc.replace(/<br>/g, '\n');
		//args.content = nc;
	}
}

function twedit_postprocess(ed, args) {
	console.debug("twedit_postprocess: " + args.content);
}

// http://joelb.me/blog/2011/code-snippet-accessing-clipboard-images-with-javascript/

// We start by checking if the browser supports the
// Clipboard object. If not, we need to create a
// contenteditable element that catches all pasted data

function wedit_registerClipboard(ed) {
	if (!window.Clipboard) {

		// We can hide the element and append it to the body,
		// pasteCatcher.style.opacity = 0;
		// document.body.appendChild(pasteCatcher);

		// as long as we make sure it is always in focus
		ed.focus();
		// document.addEventListener("click", function() {
		// pasteCatcher.focus();
		// });
	}
	window.addEventListener("paste", function(e) {
		// We need to check if event.clipboardData is supported (Chrome)
		//console.debug("pasted");
		if (e.clipboardData) {
			// Get the items from the clipboard
			var items = e.clipboardData.items;
			if (items && items.length > 0) {
				// Loop through all items, looking for any kind of image
				for (var i = 0; i < items.length; i++) {
					if (items[i].type.indexOf("image") !== -1) {
						// We need to represent the image as a file,
						var blob = items[i].getAsFile();
						// and use a URL or webkitURL (whichever is available to the
						// browser)
						// to create a temporary URL to the object
						var URLObj = window.URL || window.webkitURL;
						var source = URLObj.createObjectURL(blob);

						// The URL can then be used as the source of an image
						weditclipboard_createImage(jnode, weditconfig, source);
						//console.debug('screenshotPasteHandler image created: ' + source);
						e.stopPropagation();
					}
				}
			} else {
				//console.debug("paste has no items in clipboard")
			}

			// If we can't handle clipboard data directly (Firefox),
			// we need to read what was pasted from the contenteditable element
		} else {
			// This is a cheap trick to make sure we read the data
			// AFTER it has been inserted.
			setTimeout(function() {
				weditclipboard_checkInput(jnode, weditconfig);
			}, 1);
		}
	});
}
/* Parse the input in the paste catcher element */
function weditclipboard_checkInput(jnode, weditconfig) {
	// Store the pasted content in a variable
	// var pasteCatcher = document.getElementById(jnode.attr("id"));
	// var child = pasteCatcher.childNodes[0];
	var child = jnode.find(">:first-child");
	// Clear the inner html to make sure we're always
	// getting the latest inserted content
	// pasteCatcher.innerHTML = "";
	jnode.html("");

	if (child) {
		// If the user pastes an image, the src attribute
		// will represent the image as a base64 encoded string.
		if (child.tagName === "IMG") {
			weditclipboard_createImage(jnode, weditconfig, child.src);
		}
	}
}

/* Creates a new image from a given source */
function weditclipboard_createImage(jnode, weditconfig, source) {
	var range = wedit_getSavedRange();
	wedit_storeDroppedImage(jnode, weditconfig, range.startContainer, range, "newfile.png", source);
	// var pastedImage = new Image();
	// pastedImage.onload = function() {
	// // You now have the image!
	// }
	// pastedImage.src = source;
	// jnode.append(pastedImage);
}