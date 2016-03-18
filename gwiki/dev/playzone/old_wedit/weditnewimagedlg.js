function storePastedImage(imgnode) {
	var parent = imgnode.parentNode;

	dialog = $("#imgupload-form").dialog({
		autoOpen : false,
		height : 300,
		width : 350,
		modal : true,
		buttons : {
			"New Image" : function() {
				var name = $("#imguploadfilename");
				var newNode = document.createElement("span");
				newNode.innerHTML = "!" + name.val() + "!";

				parent.insertBefore(newNode, imgnode);
				parent.removeChild(imgnode);
				dialog.dialog("close");
			},
			Cancel : function() {
				parent.removeChild(imgnode);
				dialog.dialog("close");
			}
		},
		close : function() {
			form[0].reset();
			parent.removeChild(imgnode);
		}
	});

	form = dialog.find("form").on("submit", function(event) {
		event.preventDefault();

	});

	dialog.dialog("open");
}

function storeDroppedImage(targetNode, range, file, data) {

	var fileName = file.name;
	var fileType = file.type;
	var fileSize = file.size;

	var name = $("#imguploadfilename");
	name.val(fileName);
	var node = targetNode;
	if (range && range.startContainer) {
		node = range.startContainer;
	}
	var parent = node;

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
				// wedit_insertIntoPos("!" + name.val() + "!");
				dialog.dialog("close");
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
