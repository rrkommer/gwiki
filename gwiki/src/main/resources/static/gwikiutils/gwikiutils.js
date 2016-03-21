/**
 * http://stackoverflow.com/questions/7753448/how-do-i-escape-quotes-in-html-attribute-values
 * @param s
 * @param preserveCR
 * @returns
 */
function gwikiEscapeAttr(s, preserveCR) {
	preserveCR = preserveCR ? '&#13;' : '\n';
	return ('' + s) /* Forces the conversion to string. */
	.replace(/&/g, '&amp;') /* This MUST be the 1st replacement. */
	.replace(/'/g, '&apos;') /* The 4 other predefined entities, required. */
	.replace(/"/g, '&quot;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
	/*
	 * You may add other replacements here for HTML only (but it's not necessary).
	 * Or for XML, only if the named entities are defined in its DTD.
	 */
	.replace(/\r\n/g, preserveCR) /* Must be before the next replacement. */
	.replace(/[\r\n]/g, preserveCR);
	;
}
function gwikiEscapeUrlParam(s)
{
	return escape(s);
}



String.prototype.startsWith = function(str) {
	return (this.match("^" + str) == str);
}

String.prototype.trim = function() {
	return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""));
}

String.prototype.endsWith = function(str) {
	return (this.match(str + "$") == str);
}
