//create attribute
function gwikiEditAttribute(string) {
	if (string) {
		return string.replace(/@(.*?)@/g, function(a) {
			return prompt(a.replace(/@/g, ""), "")
		});
	} else {
		return "";
	}
}

function gwikiEditTag(field, button) {
	gwikiEditGet(field);
	// if it's a function to fire
	if ($.isFunction(eval(button.callBack))) {
		eval(button.callBack).call();
		// if it's tag to apply
	} else {
		openTag = gwikiEditAttribute(button.openTag);
		closeTag = gwikiEditAttribute(button.closeTag);
		if (selection != "") {
			gwikiEditWrap(field, openTag, closeTag);
		} else {
			if (!tagIsOpen(closeTag)) {
				if (closeTag) {
					openTags.push(closeTag);
				}
				gwikiEditWrap(field, openTag, "");
			} else {
				openTags.pop();
				gwikiEditWrap(field, "", closeTag);
			}
		}
	}
}

// add tag
function gwikiEditWrap(field, openTag, closeTag) {
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
		start = openPos + openTag.length + selection.length + closeTag.length;
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
	gwikiEditSet(field, start, end);
}

// set a selection
function gwikiEditSet(field, start, end) {
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
function gwikiEditGet(field) {
	field.focus();
	scrollPos = field.scrollTop;
	if (document.selection) {
		selection = document.selection.createRange().text;
		if ($.browser.msie) { // ie
			range = field.createTextRange();
			range.moveToBookmark(document.selection.createRange().getBookmark());
			range.moveEnd("character", field.value.length);
			openPos = field.value.length - range.text.length;
			openPos = openPos - field.value.substr(0, openPos).split("\r\n").length; // ie
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