function gwedit_buildUrl(pageId) {
	return gwikiLocalUrl(pageId);
}

function twedit_ac_hasParentElement(container, elementName) {
	if (container.parentNode) {
		if (container.parentNode.nodeName == elementName) {
			return true;
		}
	}
	return false;
}

function twedit_ac_is_protected_area(ed, startChar, container) {
	if (twedit_ac_hasParentElement(container, 'PRE') == true) {
		return true;
	}
	return false;
}
function tedit_insertRaw(ed, html) {
	var range = ed.selection.getRng(true);
	var node = ed.$(html)[0];
	range.insertNode(node);
	ed.selection.setCursorLocation(node.nextSibling, 0);
	return node;
}

/**
 * May not work
 * 
 * @param ed
 * @param text
 */
function tedit_insert_into_text(ed, text) {
	var sel = ed.selection;
	var range = sel.getRng(true);
	var cont = range.startContainer;
	if (cont.nodeName = '#text') {
		var nodetext = cont.nodeValue;
		var pf = nodetext.substring(0, range.startOffset);
		var ef = nodetext.substring(range.startOffset);
		var ntext = pf + text + ef;
		cont.nodeValue = ntext;
		sel.setCursorLocation(cont, range.startOffset);

	}
}
function gwedit_getCharBeforePos(ed) {
	var rng = ed.selection.getRng(true);
	var txt = rng.startContainer.textContent;
	if (rng.startOffset == 0) {
		return null;
	}
	var rchar = txt.substring(rng.startOffset - 1, rng.startOffset);
	return rchar;
}

function wedit_deleteLeftUntil(ed, char) {
	var range = ed.selection.getRng(true);
	var txt = range.startContainer.textContent;
	var found = false;
	var i;
	for (i = range.startOffset; i >= 0; --i) {
		if (txt.charAt(i) == char) {
			found = true;
		}
	}
	var ntext = txt.substring(0, i) + txt.substring(range.startOffset);
	range.startContainer.textContent = ntext;
}

function wedit_find_parent_node_type_el(el, nodeName) {
	if (el.nodeName == nodeName) {
		return el;
	}
	if (el.parentNode) {
		return wedit_find_parent_node_type_el(el.parentNode, nodeName);
	}
	return null;
}
function wedit_find_parent_node_type(ed, nodeName) {
	var rng = ed.selection.getRng(true);
	var startC = rng.startContainer;
	return wedit_find_parent_node_type_el(startC, nodeName);
}

function wedit_ed_get_el_attribute(el, dataname, nativename) {
	var ret = el.getAttribute('data-wiki-' + dataname);
	if (ret) {
		return ret;
	}
	if (!nativename) {
		nativename = dataname;
	}
	ret = el.getAttribute(nativename);
	return ret;
}

function wedit_find_current_link(ed) {
	var ret = new WikiLinkItem('wiki');
	var startC = wedit_find_parent_node_type(ed, 'A');
	if (!startC) {
		return ret;
	}
	ret.element = startC;
	ret.url = wedit_ed_get_el_attribute(ret.element, 'url', 'href');
	ret.title = wedit_ed_get_el_attribute(ret.element, 'title');
	ret.styleClass = ret.element.getAttribute('data-wiki-styleClass');
	ret.style = ret.element.getAttribute('data-wiki-style');
	ret.windowTarget = ret.element.getAttribute('data-wiki-windowTarget');

	return ret;
}

function wedit_find_current_image(ed) {
	var ret = new WikiLinkItem('image');
	var startC = ed.selection.getNode();
	if (!startC || startC.nodeName != 'IMG') {
		startC = wedit_find_parent_node_type(ed, 'IMG');
	}
	if (!startC) {
		return ret;
	}
	ret.element = startC;
	ret.url = wedit_ed_get_el_attribute(ret.element, 'url', 'src');
	ret.title = wedit_ed_get_el_attribute(ret.element, 'title');
	ret.width = wedit_ed_get_el_attribute(ret.element, 'width');
	ret.height = wedit_ed_get_el_attribute(ret.element, 'height');
	ret.styleClass = ret.element.getAttribute('data-wiki-styleClass');
	ret.style = ret.element.getAttribute('data-wiki-style');
	return ret;
}

function _wedit_set_attr_in_jq_or_el(el, key, value)
{
	if (el.setAttribute) {
		el.setAttribute(key, value);
	} else {
		el.attr(key, value);
	}
}

function wedit_update_or_insert_image(ed, item, element) {
	var url = gwedit_buildUrl(item.url);
	var tel = element;
	if (element == null) {
		tel = $("<img>");
	}
	_wedit_set_attr_in_jq_or_el(tel, 'src', url);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-url', item.url);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-title', item.title);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-height', item.height);
	_wedit_set_attr_in_jq_or_el(tel, 'height', item.height);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-width', item.width);
	_wedit_set_attr_in_jq_or_el(tel, 'width', item.width);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-style', item.style);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-styleClass', item.styleClass);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-thumbnail', item.thumbnail);
	
	if (element) {
		element.textContent = item.title;
	} else {
		tel.html(item.title);
		var html = $("<div>").append(tel).html();
		var node = tedit_insertRaw(ed, html);
	}
}

function gwedit_insert_pagelink(ed, item, element) {

	var url = gwedit_buildUrl(item.url);
	var tel = element;
	if (element == null) {
		tel = $("<a>");
	}
	_wedit_set_attr_in_jq_or_el(tel, 'href', url);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-url', item.url);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-title', item.title);
	
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-style', item.style);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-styleClass', item.styleClass);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-windowTarget', item.windowTarget);
	
	if (element) {
		element.textContent = item.title;
	} else {
		tel.html(item.title);
		var html = $("<div>").append(tel).html();
		var node = tedit_insertRaw(ed, html);
	}
}

function gwedit_replace_image_before(ed, item) {
	var range = ed.selection.getRng(true);
	var startc = range.startContainer;
	var starto = range.startOffset;
	if (range.startContainer.childNodes.length > starto - 1) {
		var oldimg = range.startContainer.childNodes[starto - 1];
		if (oldimg.nodeName.toLowerCase() == "img") {
			range.startContainer.removeChild(range.startContainer.childNodes[starto - 1]);
		} else {
			console.debug("no img found to replace (no img)")
		}
	} else {
		console.debug("no img found to replace (no children)")
	}
	wedit_update_or_insert_image(ed, item);
}

