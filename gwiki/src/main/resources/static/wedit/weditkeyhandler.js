function autoComplete() {
	var before = wedit_charBeforePos();
	if (before != '{' && before != '[' && before != '!') {
		return;
	}
	var point = wedit_getCursorCoords();
	console.debug("Char before: " + before + "; " + point.x + "|" + point.y);
	var autocomp = $("#inplaceautocomplete")
	autocomp.css('display', 'block');
	autocomp.css('position', 'absolute');
	autocomp.css('z-index', '10000');
	autocomp.css('left', point.x);
	autocomp.css('top', point.y);
	var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion",
	    "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala",
	    "Scheme" ];
	$("#inplaceautocomplete").autocomplete({
		source : availableTags,
		select: function( event, ui ) 
		{
			var val = autocomp.val();
			wedit_insertIntoPos(val + before);
			autocomp.css('display', 'none');
			
		}
	});
	autocomp.focus();

}

$(document).ready(function() {
	$("#editordiv").keydown(function(event) {
		// Ctrl+Space autocomplete
		if (event.ctrlKey == true && event.which == 32) {
			event.stopPropagation();
			autoComplete();
		}
		// console.debug("keypress: " + event.which + "; ctr: " + event.ctrlKey + ";
		// shift " + event.shiftKey
		// + "; alt " + event.altKey);
	});
});