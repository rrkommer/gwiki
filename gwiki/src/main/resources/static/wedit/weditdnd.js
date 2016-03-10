function calcDropPoint(event) {
	var x = event.clientX;
	var y = event.clientY;

	console.debug("drop " + x + "; " + y);
	if (document.caretPositionFromPoint) {
		var pos = document.caretPositionFromPoint(x, y);
		range = document.createRange();
		range.setStart(pos.offsetNode, pos.offset);
		// range.collapse();
		// range.insertNode(img);
		return range;
	}
	// Next, the WebKit way
	else if (document.caretRangeFromPoint) {
		range = document.caretRangeFromPoint(x, y);
		// range.insertNode(img);
		return range;
	}
	// Finally, the IE way
	else if (document.body.createTextRange) {
		range = document.body.createTextRange();
		range.moveToPoint(x, y);
		return range;
		// var spanId = "temp_" + ("" + Math.random()).slice(2);
		// range.pasteHTML('<span id="' + spanId + '">&nbsp;</span>');
		// var span = document.getElementById(spanId);
		// span.parentNode.replaceChild(img, span);
	} else {
		console.error("no range determined");
	}

}

function wedit_registerdragndrop(jnode, weditconfig) {
	jnode.on(
	    'drop',
	    function(event) {
		    console.debug("got drop: " + event.originalEvent.dataTransfer.files)
		    // stop the browser from opening the file
		    event.preventDefault();

		    // Now we need to get the files that were dropped
		    // The normal method would be to use event.dataTransfer.files
		    // but as jquery creates its own event object you ave to access
		    // the browser even through originalEvent. which looks like this
		    var dt = event.originalEvent.dataTransfer;
		    var tp;
		    for (tp in dt.types) {
			    console.debug("drop type: " + tp);
		    }

		    var files = dt.files;
		    var data = dt.getData(files[0].type);

		    var selection = window.getSelection();
		    var selectedText = selection.toString();
		    var anchornode = selection.anchorNode;
		    var focusnode = selection.focusNode;
		    var range = calcDropPoint(event);

		    console.debug("cursel: " + selectedText + "; range: " + range + "; " + anchornode + "; " + focusnode + "; "
		        + event.target);
		    weditconfig.drophandler(weditconfig, event.target, range, files[0], data);
		    // see also http://help.dottoro.com/ljslrhdh.php

	    });
}
