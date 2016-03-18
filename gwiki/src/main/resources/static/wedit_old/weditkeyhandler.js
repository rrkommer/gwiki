
function keyeventToString(event) {
	var ch = String.fromCharCode(event.which);
	if (event.shiftKey == false) {
		ch = ch.toLowerCase();
	}
	return ch;
}

function standardEditKeyEvent(jwedit, weditconfig, event) {
	console.debug("stdkeypup	: " + event.which + "; ctr: " + event.ctrlKey + "; shift " + event.shiftKey + "; alt "
	    + event.altKey);
	if (event.ctrlKey == true) {
		switch (event.which) {
		case 32: // space
			event.stopPropagation();
			wedit_autoComplete_checkstart(jwedit, "", weditconfig, 1);
			break;
		case 69: // e
			wedit_moveToEndline();
			event.stopPropagation();
			return false;
		case 70: // f debug only
			wedit_moveforward(2);
			return false;
		}
	} else {
		if (weditconfig.autocompleteImmediatelly == true && event.which > 32) {
				setTimeout(function() {
				wedit_autoComplete_checkstart(jwedit, "", weditconfig, 1)}, 100);
				// javascript IS Nasty HTML DOMs + assembler + Lisp. German keyboards are not supported

				//			var ch = keyeventToString(event);
//			if (ch) {
//				console.debug("check autocomplete on: " + ch);
//				if (weditconfig.ctrlSpaceAttchars.indexOf(ch) != -1) {
//					wedit_autoComplete_start(wedit, "", weditconfig, ch);
//				}
//			}
		}
	}

}

function wedit_registerkeyhandler(jwedit, weditconfig) {
	jwedit.keydown(function(event) {
		return weditconfig.keydownhandler(jwedit, weditconfig, event);
	});
	jwedit.keyup(function(event) {
		if (weditconfig.keyuphandler) {
			return weditconfig.keyuphandler(jwedit, weditconfig, event);
		}
	});
}
