
function wedit_storeDroppedImage(jnode, weditconfig, targetNode, range, file, data) {

	var fileName = file.name;
	var fileType = file.type;
	var fileSize = file.size;

	var name = $("#imguploadfilename");
	name.val(fileName);
	var node = targetNode;
	if (range && range.startContainer) {
		node = range.startContainer;
	}

	dialog = $("#imgupload-form").dialog({
	  autoOpen : false,
	  height : 300,
	  width : 350,
	  modal : true,
	  buttons : {
	    "New Image" : function() {
		    var name = $("#imguploadfilename");
		    var html;
		    if (node instanceof Text) {
			    html = node.nodeValue
			    node.nodeValue = html + "!" + name.val() + "!";
		    } else {
			    html = node.innerHTML;
			    node.innerHTML = html + "!" + name.val() + "!";
		    }

		    dialog.dialog("close");
		    var sname = name.val();
		    setTimeout(function() {
			    wedit_restoreSelection(jnode, weditconfig, range);
			    wedit_insertIntoPos("!" + sname + "!");
		    }, 100);
	    },
	    Cancel : function() {
		    dialog.dialog("close");
	    }
	  },
	  close : function() {
		  form[0].reset();

	  }
	});

	form = dialog.find("form").on("submit", function(event) {
		event.preventDefault();
	});
	dialog.dialog("open");
}
