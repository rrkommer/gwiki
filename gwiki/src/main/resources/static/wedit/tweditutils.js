

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

/**
 * split source title|lin|opt1=val1
 * @param text
 * @param item
 */
function wedit_split_wiki_link_title(text, item)
{
	if (!text) {
		return;
	}
	var idx = text.indexOf('|');
	if (idx != -1) {
		item.url = text.substring(idx + 1);
		item.title = text.substring(0, idx);
	} else {
		item.url = text;
	}
	idx = item.url.indexOf('|');
	if (idx != -1) {
		var rest = item.url.substring(idx + 1);
		item.url = item.url.substring(0, idx);
		var params =wedit_parese_pipe_params(rest);
		item.windowTarget= params.get('target');
		item.styleClass = params.get('class');
		item.style = params.get('style');
	}
}
function wedit_gen_wiki_link(item)
{
	var res = '';
	if (item.title.length > 0) {
		res += wedit_escape_wikiparam(item.title)
	}
	if (res.length > 0) {
		res += '|';
	}
	res += wedit_escape_wikiparam(item.url);
	if (item.windowTarget.length > 0) {
		res += '|target=' + wedit_escape_wikiparam(item.windowTarget);
	}
	if (item.styleClass.length > 0) {
		res += '|class=' + wedit_escape_wikiparam(item.styleClass);
	}
	if (item.style.length > 0) {
		res += '|style=' + wedit_escape_wikiparam(item.style);
	}
	return res;
}

function wedit_split_wiki_image(text, item)
{
	var idx = text.indexOf('|');
	if (idx == -1) {
		item.url = text;
		return;
	}
	item.url = text.substring(0, idx);
	var rest = text.substring(idx + 1);
	var params = wedit_parese_pipe_params(rest);
	item.width = params.get('width');
	item.height = params.get('height');
	item.thumbnail = params.get('thumbnail');
	item.styleClass = params.get('class');
	item.style = params.get('style');
	
}

function wedit_gen_wiki_image(item)
{
	var res = wedit_escape_wikiparam(item.url);
	var hasparam = false;
	if (item.width.length > 0) {
		res += '|width=' + wedit_escape_wikiparam(item.width);
	}
	if (item.height.length > 0) {
		res += '|height=' + wedit_escape_wikiparam(item.height);
	}
	if (item.thumbnail.length > 0) {
		res += '|thumbnail=' + wedit_escape_wikiparam(item.thumbnail);
	}
	if (item.styleClass.length > 0) {
		res += '|class=' + wedit_escape_wikiparam(item.styleClass);
	}
	if (item.style.length > 0) {
		res += '|style=' + wedit_escape_wikiparam(item.style);
	}
	return res;
}


function wedit_escape_wikiparam(text)
{
	if (!text) {
		return text;
	}
	var ntext = text.replace(/\\/g, '\\\\');
	ntext = ntext.replace(/\|/g, '\\|');
	ntext = ntext.replace(/\=/g, '\\=');
	return ntext;
}

function wedit_parese_pipe_params(text)
{
	var ret = new Map();
	var keyStart = 0;
	var keyEnd = -1;
	var valueStart = -1;
	var valueEnd = -1;
	var key = '';
	var value = '';
	var inKey = true;
	for (var i = 0; i < text.length; ++i) {
		var c = text[i];
		if (c == '\\') {
			++i;
			if (inKey == true) {
				key += c;
			} else {
				value += c;
			}
		} else if (c == '=') {
			if (inKey){
				inKey = false;
			}
		} else if (c == '|') {
			ret.set(key, value);
			key = '';
			value = '';
			inKey = true;
		} else {
			if (inKey) {
				key += c;
			} else {
				value += c;
			}
			
		}
	}
	if (key.length > 0) {
		ret.set(key, value);
	}
	return ret;
}
