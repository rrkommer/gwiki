function gwikiBuildNavMenuTree(menuDivId, searchTextId, treeChildrenServiceUrl, currentPageId) {
	if (!currentPageId) {
		currentPageId = gwikiContext.pageId;
	}
	var treedata;
	if (typeof treeChildrenServiceUrl == 'object') {
		treedata = treeChildrenServiceUrl;
	} else {
		treedata = {
			  url : treeChildrenServiceUrl + '?type=wiki',
			  dataType : 'json',
			  data : function(n) {
				  return {
				    method_onLoadAsync : 'true',
				    id : n.id
				  }
			  }
			};
	}
	var tree = $('#' + menuDivId).jstree({
	  plugins : [ 'search', 'themes', 'ui' ],
	  core : {
	    themes : {
	      theme : 'classic',
	      dots : false,
	      icons : false
	    },
	    data : treedata
	  }
	});
	var to = false;
	$('#' + searchTextId).on('keydown', function(event) {
		if (event.keyCode == 13) { // ENTER
			var first = $('.jstree-search').first().attr('id');
			if (!first) {
				return;
			}
			var tree = $('#' + menuDivId).jstree(true);
			var node = tree.get_node(first);
			if (!node.data || !node.data.url) {
				return;
			}
			var nurl = gwikiLocalUrl(node.data.url);
			if (window.location.pathname != nurl) {
				window.location = nurl;
			}
		}
	});
	$('#' + searchTextId).on('keyup', function(event) {
		if (to) {
			clearTimeout(to);
		}
		to = setTimeout(function() {
			var v = $('#' + searchTextId).val();
			$('#' + menuDivId).jstree(true).search(v);
		}, 250);
	});
	tree.on('select_node.jstree', function(e, data) {

		var selNone = data.node.data;
		var nurl = gwikiLocalUrl(selNone.url);
		if (window.location.pathname != nurl) {
			window.location = nurl;
		}
	});
	// tree.on('check_node.jstree',
	tree.on("changed.jstree", function(e, data) {
		var selNone = data.node.data;
		window.localation = selNone.url;
	});
	tree.on('ready.jstree', function(event) {
		var tree = $('#' + menuDivId).jstree(true);
		if (currentPageId) {
			var ln = currentPageId.replace(/\//g, '_');
			var st = tree.get_node(ln);
			tree.select_node(ln);
			tree.open_node(ln);
		}
	});
}
