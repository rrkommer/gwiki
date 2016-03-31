/**
 * http://stackoverflow.com/questions/7753448/how-do-i-escape-quotes-in-html-attribute-values
 * 
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
function gwikiEscapeUrlParam(s) {
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

/**
 * get all global function with prefix
 * 
 * @param prefix
 */
function gwikiGetGlobalFunctionsWithPrefix(prefix) {
	var ret = [];
	for ( var i in window) {
		var typ = typeof window[i];
		if (typ == 'function') {
			try {
				var func = window[i];
				var str = "" + func;
				var funcname = str;
				var idx = funcname.indexOf('function ');
				if (idx != -1) {
					funcname = funcname.substring(idx + 'function '.length);
					idx = funcname.indexOf('(');
					if (idx != -1) {
						funcname = funcname.substring(0, idx);
						if (funcname.startsWith(prefix) == true) {
							ret[ret.length] = func;
						}
					}
				}
			} catch (e) {
			}
		}
	}
	return ret;
}

function gwikiCallGlobalFunctionsWithPrefix(prefix, arguments) {
	var funcs = gwikiGetGlobalFunctionsWithPrefix(prefix);
	for ( var i in funcs) {
		var func = funcs[i];
		func(arguments);
	}
}

function gwikiSetCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; path=/; " + expires;
}

function gwikiGetCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}
