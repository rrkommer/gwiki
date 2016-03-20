

function keyeventToString(event) {
	var ch = String.fromCharCode(event.which);
	if (event.shiftKey == false) {
		ch = ch.toLowerCase();
	}
	return ch;
}

function dumpString(str) {
	var out = '';
	for (var i = 0; i < str.length; ++i) {
		var c = str.charAt(i);
		out += "'" + c + "' (" + c.charCodeAt(0) + "), ";
	}
	console.debug("> " + out);
}


var idseq = 0;
function wedit_genid(prefix) {
	++idseq;
	return prefix + idseq;
}