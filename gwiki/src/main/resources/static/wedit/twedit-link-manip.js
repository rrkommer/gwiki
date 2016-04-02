function gwedit_buildUrl(pageId) {
	if (!pageId) {
		return pageId;
	}
	var idx = pageId.indexOf('://');
	if (idx != -1) {
		return pageId;
	}
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
	if (cont.nodeName == '#text') {
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

	ret.styleClass = ret.element.getAttribute('data-wiki-styleClass');
	ret.style = ret.element.getAttribute('data-wiki-style');
	ret.tip = ret.element.getAttribute('data-wiki-tip');
	ret.windowTarget = ret.element.getAttribute('data-wiki-windowTarget');
	ret.title = wedit_ed_get_el_attribute(ret.element, 'title');

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

function _wedit_set_attr_in_jq_or_el(el, key, value) {
	if (el.setAttribute) {
		el.setAttribute(key, value);
	} else {
		el.attr(key, value);
	}
}
function wedit_set_titel_default(item) {
	if (item.title == null || item.title == '' || item.title == undefined) {
		item.title = item.url;
	}
}
function wedit_update_or_insert_image(ed, item, element) {
	var url = gwedit_buildUrl(item.url);
	var tel = element;
	if (element == null) {
		tel = $("<img>");
	}
	wedit_set_titel_default(item);
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
	wedit_set_titel_default(item);
	_wedit_set_attr_in_jq_or_el(tel, 'href', url);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-url', item.url);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-title', item.title);
	_wedit_set_attr_in_jq_or_el(tel, 'data-wiki-tip', item.tip);
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
function twedit_ac_is_protected_area(ed, startChar, container) {
	var type = wedit_get_selection_el_type(ed);
	if (type == 'IMG' || type == 'A' || type == 'PRE') {
		return true;
	} else {
		return false;
	}
}

function twedit_is_macro_button_enabled(ed) {
	var type = wedit_get_selection_el_type(ed);
	if (type == 'IMG' || type == 'A') {
		return false;
	}
	return true;
}
function twedit_is_image_button_enabled(ed) {
	var type = wedit_get_selection_el_type(ed);
	if (type == 'PRE' || type == 'A') {
		return false;
	}
	return true;
}
function twedit_is_link_button_enabled(ed) {
	var type = wedit_get_selection_el_type(ed);
	if (type == 'PRE' || type == 'IMG') {
		return false;
	}
	return true;
}

function tweid_collect_containers_between_inParent(start, end, collection) {
	var foundthis = false;
	for (var cc = start.parentNode.firstChild; cc; cc = cc.nextSibling) {
		if (foundthis == false) {
			if (cc == start) {
				foundthis = true;
			}
		} else {
			if (twedit_collect_containers_between(cc, end, collection)) {
				return true;
			}
		}
	}
	if (start.parentNode.parentNode) {
		return tweid_collect_containers_between_inParent(start.parentNode, end, collection);
	}
}
function twedit_collect_containers_between(start, end, collection) {
	if (collection.indexOf(start) != -1) {
		return false;
	}
	collection[collection.length] = start;
	if (start == end) {
		return true;
	}
	if (start.children) {
		for (var cc = start.firstChild; cc; cc = cc.nextSibling) {
			if (twedit_collect_containers_between(cc, end, collection) == true) {
				return true;
			}
		}
	}
	if (start.nextElementSibling) {
		return twedit_collect_containers_between(start.nextElementSibling, end, collection);
	} else if (start.parentNode) {
		return tweid_collect_containers_between_inParent(start, end, collection);
	}
	return false;
}
function twedit_check_valid_range_for_insideOutsideMacros(colels) {
	var wasOutsideBody = false;
	var wasInsideBody = false;
	for (var i = 0; i < colels.length; ++i) {
		var el = colels[i];
		if (el.getAttribute) {
			var cl = el.getAttribute('class');
			if (cl && cl.indexOf('weditmacroframe') != -1) {
				return true;
			}
		}
	}
	return false;

}
function twedit_check_valid_range_for_del(ed, event) {
	var rng = ed.selection.getRng(true);
	return tweid_check_valid_range_for_del_in_range(ed, rng, event);
}

function tweid_has_element_class(el, clazz) {
	if (!el.getAttribute) {
		return false;
	}
	var cls = el.getAttribute('class');
	if (!cls) {
		return false;
	}
	return cls.indexOf(clazz) != -1;
}

function tweid_is_at_startOfBody(ed, rng) {
	var sc = rng.startContainer;
	if (sc.nodeName == 'P' && sc.parentNode.nodeName == 'DIV') {
		if (tweid_has_element_class(sc.parentNode, 'weditmacrobody') == true) {
			return true;
		}
		return false;
	}
	if (sc.nodeName == '#text') {
		if (rng.startOffset != 0) {
			return false;
		}
		var pnode = sc.parentNode;
		if (pnode.nodeName == 'P') {
			pnode = pnode.parentNode;
		}
		if (tweid_has_element_class(pnode, 'weditmacrobody') == true) {
			return true;
		}
	}
	return false;
}

function tweid_is_at_endOfBody(ed, rng) {
	var sc = rng.endContainer;
	if (sc.nodeName == 'P' && sc.parentNode.nodeName == 'DIV') {
		if (tweid_has_element_class(sc.parentNode, 'weditmacrobody') == true) {
			return true;
		}
		return false;
	}
	if (sc.nodeName == '#text') {
		if (rng.startOffset != sc.nodeValue.length) {
			return false;
		}
		var pnode = sc.parentNode;
		if (pnode.nodeName == 'P') {
			pnode = pnode.parentNode;
		}
		if (tweid_has_element_class(pnode, 'weditmacrobody') == true) {
			return true;
		}
	}
	return false;
}


function tweid_is_direct_after_Macro(ed, rng) {
	if (rng.startOffset != 0) {
		return false;
	}
	var sc = rng.startContainer;
	if (sc.previousSibling && tweid_has_element_class(sc.previousSibling, 'weditmacroframe') == true) {
		return true;
	}
	if (sc.nodeName == '#text' && sc == sc.parentNode.firstChild) {
		sc = sc.parentNode;
		if (sc.previousSibling && tweid_has_element_class(sc.previousSibling, 'weditmacroframe') == true) {
			return true;
		}
	}
	return false;
}
function tweid_is_direct_before_Macro(ed, rng) {
	var sc = rng.endContainer;
	if (sc.nodeName == '#text' ) {
		if (sc != sc.parentNode.lastChild) {
			return false;
		}
		if (rng.endOffset != sc.nodeValue.length) {
			return false;
		} 
		sc = sc.parentNode;
	}
	if (sc.nextSibling && tweid_has_element_class(sc.nextSibling, 'weditmacroframe') == true) {
		return true;
	}
	return false;
}

function tweid_check_valid_range_for_del_in_range(ed, rng, event) {

	if (rng.startContainer == rng.endContainer) {
		var sc = rng.startContainer;
		if (tweid_has_element_class(sc, 'weditmacrohead') || tweid_has_element_class(sc, 'weditmacroframe')) {
			console.debug("Found mod in macro: delete macro");
			// wedit_macro_delete_current(ed, rng.startContainer);
			return false;
		}
		if (tweid_has_element_class(sc, 'weditmacrobody')) {
			switch (event.keyCode) {
			case 8:
			case 46:
				return false;
			}
		}
	}
	if (event.keyCode == 8) { // backspace
		if (tweid_is_at_startOfBody(ed, rng) == true) {
			return false;
		}
		if (tweid_is_direct_after_Macro(ed, rng) == true) {
			return false;
		}

	}
	if (event.keyCode == 46) { // entf
		if (tweid_is_at_endOfBody(ed, rng) == true) {
			return false;
		}
		if (tweid_is_direct_before_Macro(ed, rng) == true) {
			return false;
		}
	}

	var start = rng.startContainer;
	var end = rng.endContainer
	var colels = [];
	var found = twedit_collect_containers_between(start, end, colels);
	if (!found) {
		return true;
	}
	var insideout = twedit_check_valid_range_for_insideOutsideMacros(colels);
	return !insideout;
}

/**
 * IMG, A, PRE
 * 
 * @param ed
 */
function wedit_get_selection_el_type(ed) {
	var startc = ed.selection.getNode();
	if (startc.nodeName == 'IMG' || startc.nodeName == 'A' || startc.nodeName == 'PRE') {
		return startc.nodeName;
	}
	if (twedit_ac_hasParentElement(startc, 'A') == true) {
		return 'A';
	}
	if (twedit_ac_hasParentElement(startc, 'PRE') == true) {
		return 'PRE';
	}
	return "P";
}

function twedit_ac_get_text_betweenranges(ed, oldRange, newRange) {
	if (oldRange == null) {
		return null;
	}
	if (newRange == null) {
		newRange = ed.selection.getRng();
	}
	if (newRange.startContainer.nodeName != "#text") {
		console.warn("twedit_ac_get_text_betweenranges: End container is not text");
		return null;
	}
	if (newRange.startContainer != oldRange.startContainer) {
		console.warn("twedit_ac_get_text_betweenranges: End container start container not equal");
		return null;
	}
	if (newRange.startOffset < oldRange.startOffset) {
		console.warn("twedit_ac_get_text_betweenranges: neg range");
		return null;
	}
	var text = newRange.startContainer.nodeValue;
	var ret = text.substring(oldRange.startOffset, newRange.startOffset);
	console.debug("range text is: " + ret);

	return ret;
}

// not working
// function wedit_tinyCommand_delete(ed, event) {
// }
// function wedit_tinyCommand_beginLine(ed, event) {
//
// }
// function wedit_tinyCommand_endLine(ed, event) {
// var endKey = 35;
// event.stopPropagation();
// event.preventDefault();
// var e = jQuery.Event("keydown");
// e.which = endKey; // # c code value
// jQuery(event.target.parentNode).trigger(e);
// }
