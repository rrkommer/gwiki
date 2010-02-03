function gwikiCreateHtmlPageEditorSimple(fieldId) {
	if (!window.tinyMCE) {
		alert('TinyMCE is NOT in the DOM !');
		return;
	}
	fieldId = "MainPage.htmlText";
	var f = document.getElementById('MainPage.htmlText');
	if (!f) {
		alert('field not found: ' + f);
		return;
	}

	// alert('field found');
	$(document).ready(function() {
		tinyMCE.init( {
			mode : "exact",
			elements : fieldId,
			theme : "advanced"
		});
	});
}
function gwikiCreateHtmlPageEditor(fieldId) {
	// alert("tinyMCE: " + tinyMCE + ";tinymce: " + tinymce);
	// if (!$("#" + fieldId).length) {
	// alert("Field cannot be found: " + fieldId);
	// }
	// console.warn("tinyMCE: " + tinyMCE);
	tinyMCE
			.init( {
				mode : "none",
				// elements: fieldId,
				theme : "advanced",
				// plugins :
				// "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,filemanager",
				// plugins :
				// "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",

				plugins : "advhr,advimage,advlink,pagebreak,style,layer,table,emotions,searchreplace,print,contextmenu,paste,directionality,fullscreen",
				// plugins : "table,emotions",

				// // Theme options
				theme_advanced_buttons1 : "save,newdocument,|,bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect",
				theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,cleanup,help,code,|,insertdate,inserttime,preview,|,forecolor,backcolor",
				theme_advanced_buttons3 : "tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,emotions,iespell,media,advhr,|,print,|,ltr,rtl,|,fullscreen",
				theme_advanced_buttons4 : "insertlayer,moveforward,movebackward,absolute,|,styleprops,spellchecker,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,template,blockquote,pagebreak,|,insertfile,insertimage",
				theme_advanced_toolbar_location : "top",
				theme_advanced_toolbar_align : "left",
				theme_advanced_resizing : true,
				paste_auto_cleanup_on_paste : true,
				force_br_newlines : true,
				width : "100%",
				height : "100%",
				forced_root_block : ''
			// convert_newlines_to_brs : true,
			// convert_urls : false,
			// extended_valid_elements :
			// "table[class=gwikiTable],td[class=gwikitd],th[class=gwikith],a[href|target|wikitarget],img[wikitarget|src|id|width|height|align|hspace|vspace]",
			// document_base_url : "http://www.site.com/path1/"
			// setup : function(ed) {
			// ed.onChange.add(function(ed, l) {
			// // alert('Editor contents was modified. Contents: ' + l.content);
			// // ed.isNotDirty = false;
			// // htmlIsNotDirty = false;
			// });
			// }
			});
	// alert('after create');
	// console.warn("tinyMCE: " + tinyMCE);
	var ed = tinyMCE.get(fieldId);
	if (!ed) {
		// alert("ed doesn not exists");
		tinyMCE.execCommand('mceAddControl', true, fieldId);
		ed = tinyMCE.get(fieldId);
		// alert('ed created: ' + ed);
	}
}
