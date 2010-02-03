var gwikiContentChanged = false;

function gwikiSetContentChanged() {
	// alert('content changed');
	gwikiContentChanged = true;
}
function gwikiUnsetContentChanged() {
	// alert('clear content changed');
	gwikiContentChanged = false;
}
function gwikiOnUnload() {
	if (gwikiContentChanged == false)
		return;
	return "Wollen Sie wirklich den Editor verlassen?";
}

window.onbeforeunload = gwikiOnUnload;
// alert('now activate unloading');

function gwikiCreateHtmlEditor(content) {
	gwikiCreateTiny(content);
}
var preContent = '';
var htmlIsNotDirty = true;

function gwikiEditCleanup(type, value) {
	switch (type) {
	case "get_from_editor":
		value = value.replace(/\<p /g, '<div ');
		value = value.replace(/\<\/p\>/g, '</div>');
		break;
	}
	return value;
}

function gwikiCreateTiny(content) {
	var ed = tinyMCE.get('gwikihtmledit');
	if (ed) {
		ed.setContent(content);
		preContent = content;
		ed.isNotDirty = true;
		htmlIsNotDirty = true;
		return;
	}
	tinyMCE
			.init( {
				mode : "none",
				theme : "advanced",
				auto_resize : true,
				// plugins :
				// "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,visualchars,nonbreaking,xhtmlxtras,template,imagemanager,filemanager",
				// plugins :
				// "safari,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",
				plugins : "-gwiki,advhr,pagebreak,style,layer,table,searchreplace,print,contextmenu,noneditable,paste,iespell,inlinepopups,xhtmlxtras",
				// Theme options
				theme_advanced_buttons1 : "undo,redo,|,cut,copy,paste,pastetext,pasteword,|,search,replace,|,wikilink,unlink,wikiimage,anchor,|,bullist,numlist,outdent,indent,|,hr,visualchars,charmap,|,removeformat,iespell,cleanup,code,help,", // image,
				theme_advanced_buttons2 : "bold,italic,underline,del,sub,sup,|,attribs,styleprops,formatselect,fontselect,fontsizeselect,|,forecolor,backcolor,|,	blockquote,justifyleft,justifycenter,justifyright,justifyfull", // strikethrough
				theme_advanced_buttons3 : "tablecontrols,|,insertlayer,moveforward,movebackward,absolute,|,styleprops", // ,|,cite,ins,,abbr,acronym
				// theme_advanced_buttons4 : "",
				// //spellchecker,cite,abbr,acronym,del,ins,attribs,
				// ,nonbreaking,template,blockquote,pagebreak,,insertfile,insertimage
				theme_advanced_blockformats : "p,div,h1,h2,h3,h4,h5,h6,blockquote",
				theme_advanced_toolbar_location : "top",
				theme_advanced_toolbar_align : "left",
				theme_advanced_resizing : true,
				paste_auto_cleanup_on_paste : true,
				force_br_newlines : true,
				force_p_newlines : false,
				forced_root_block : '',
				inline_styles : false,
				// external_link_list_url :
				// "/popweb/gw2/gwiki/static/gwiki/myexternallist.js",
				// convert_newlines_to_brs : true,
				convert_urls : false,
				// extended_valid_elements : "",
				// //table[class=gwikiTable],td[class=gwikitd],th[class=gwikith],
				cleanup_callback : "gwikiEditCleanup",
				content_css : gwikiContentCss,
				setup : function(ed) {
					ed.onChange.add(function(ed, l) {
						// alert('Editor contents was modified. Contents: ' + l.content);
							ed.isNotDirty = false;
							htmlIsNotDirty = false;
							gwikiSetContentChanged();
						});
				}
			});
	ed = tinyMCE.get('gwikihtmledit');
	if (!ed) {
		tinyMCE.execCommand('mceAddControl', false, 'gwikihtmledit');
		ed = tinyMCE.get('gwikihtmledit');
		// var ed = new tinymce.Editor('gwikihtmledit');
	}
	if (ed) {
		ed.setContent(content);
		ed.isNotDirty = true;
		htmlIsNotDirty = true;
	}
	preContent = content;
	$("#gwikihtmledit_tbl").css( {
		'position' : 'relative',
		'left' : '10px',
		'top' : '20px',
		'width' : "100%",
		'height' : '100%'
	});
	// alert('content set');

}

function gwikiRestoreFromRte() {
	var edit = tinyMCE.get('gwikihtmledit');
	if (!edit) {
		// alert('edit not found');
		return true;
	}
	// var firefox = false;
	if (edit.isDirty() == false && htmlIsNotDirty == true) {
		// if (preContent == edit.getContent()) {
		// alert('not dirty');
		// tinyMCE.remove(edit);
		return false;
		// }
		// alert('not dirty but not equal:\n' + edit.getContent() + '\n\nPREV:\n'
		// + preContent);
	}
	var content = edit.getContent();
	// alert("gwk: " + content);
	tinyMCE.execCommand('mceRemoveControl', false, 'gwikihtmledit');
	tinyMCE.remove(edit);
	jQuery.ajax( {
		async : false,
		cache : false,
		url : './EditPage?method_onRteToWiki=true',
		type : 'POST',
		dataType : "html",
		data : {
			htmlCode : content
		},
		complete : function(res, status) {
			// alert(' html2wiki: ' + res.responseText);
		if (status == "success" || status == "notmodified") {
			if (res.status == 200) {
				$(".gwiki-editor").val(res.responseText);
			} else {
				alert(res.responseText);
			}
		}
		// gwikiEditField.value = res.responseText;
	}
	});
}

function gwikiRelaodPreviewFrame() {

}

function gwikicreateEditTab(partName) {
	$(document)
			.ready(
					function() {
						$("#gwikiwktabs")
								.tabs( {
									select : function(event, ui) {
										// ui.tab // anchor element of the selected (clicked) tab
										// ui.panel // element, that contains the selected/clicked
										// tab contents
										// ui.index
										// jQuery('#WikiPreview').html("Loading...");

										if (ui.index != 1) {
											gwikiRestoreFromRte();
										}
										if (ui.index == 1) {
											var frmqs = jQuery("#editForm").serialize();
											jQuery
													.ajax( {
														cache : false,
														url : './EditPage?method_onAsyncRteCode=true&partName=' + partName,
														type : 'POST',
														dataType : "html",
														data : frmqs,
														complete : function(res, status) {
															if (status == "success"
																	|| status == "notmodified") {
																if (res.status == 200) {
																	if (!$('#gwikihtmledit').length) {
																		var te = "<textarea rows='40' cols='100' id='gwikihtmledit'>";
																		$('#WikiRte').html(te);
																	}
																	$('#gwikihtmledit').val(res.responseText);
																	// CKEDITOR.replace('gwikihtmledit');
																	gwikiCreateHtmlEditor(res.responseText);
																	window
																			.setTimeout(
																					"ajustScreen('gwikiWikiEditorFrame')",
																					50);
																	// ajustScreen('gwikiWikiEditorFrame');
																} else {
																	alert(res.responseText);
																}
															}
														}
													});
										}
										if (ui.index == 2) {
											var frmqs = jQuery("#editForm").serialize();
											jQuery
													.ajax( {
														cache : false,
														url : './EditPage?method_onAsyncWikiPreview=true&partName=' + partName,
														type : 'POST',
														dataType : "html",
														data : frmqs,
														complete : function(res, status) {
															if (status == "success"
																	|| status == "notmodified") {
																if (res.status == 200) {
																	var html = res.responseText
																	document.getElementById('WikiPreview').innerHTML = html;
																} else {
																	alert("Failure rendering Preview: "
																			+ res.responseText);
																}
															}
														}
													});
										}

									}
								});
					});
}

window.onresize = function(event) {
	var framId = 'gwikiWikiEditorFrame';
	if ($("#" + framId).hasClass("fullscreen") == true) {
		//alert('resized now');
		gwikimaximizeWindow(framId);
	}
}


function gwikiFullscreen(framId) {

	if ($("#" + framId).hasClass("fullscreen") == false) {
		gwikimaximizeWindow(framId);
	} else {
		gwikirestoreWindow(framId);
	}
}

function ajustScreen(framId) {
	if ($("#" + framId).hasClass("fullscreen") == false) {
		gwikirestoreWindow(framId);
	} else {
		gwikimaximizeWindow(framId);
	}
}
function getViewPort()
{
	var doc = window.document;
	var b = jQuery.support.boxModel ? doc.documentElement : doc.body;
	return {
		x : window.pageXOffset || b.scrollLeft,
		y : window.pageYOffset || b.scrollTop,
		w : window.innerWidth || b.clientWidth,
		h : window.innerHeight || b.clientHeight
	};
}
function gwikimaximizeWindow(framId) {
	var vp = getViewPort();
	//alert("vp: w: " + vp.w + ";h: " + vp.w);
	var ie6 = jQuery.browser.version == '6.0' &&  jQuery.browser.msie == true;
	var position = (ie6 || (jQuery.browser.msie && ! jQuery.support.boxModel)) ? 'absolute' : 'fixed';
	$("#" + framId).addClass('fullscreen');
	$("#" + framId).css( {
		'position' : position,
		'z-index' : '900',
		'left' : '0px',
		'top' : '0px',
		'width' : vp.w - 9, // width + 'px',
		'height' : vp.h - 40,
		'background-color' : '#FFFFFF'
	});
	// $(".gwiki-editor").val('cols', '200');
	$(".gwiki-editor").css( {
		'position' : 'relative',
		'left' : '0px',
		'right' : '0px',
		'top' : '0px',
		'bottom': '20px',
		'width' : '100%',
		'height' : vp.h - 90 //height - 120//200
	});
	var ed = tinyMCE.get('gwikihtmledit');
	if (ed) {
		$(ed.getContainer()).css( {
			// 'position' : 'relative',
			'left' : '0px',
			'top' : '0px',
			'width' : "100%",
			'height' : '100%'
		});
	}
	$("#WikiEdit").css( {
		'width' : "100%",
		'height' : '100%'
	});
	$("#WikiRte").css( {
		// 'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : "100%",
		'height' : '100%'
	});

	$("#gwikihtmledit_parent").css( {
		// 'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : "100%",
		'height' : '100%'
	});
	$("#gwikiwktabs").css( {
		// 'position' : 'relative',
		'left' : '0px',
		'left' : '0px',
		'top' : '0px',
		'width' : "100%",
		'height' : '100%'
	});

	$("#WikiPreview").css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : "100%",
		'height' : '100%'
	});
	// das ist es:
	$("#gwikihtmledit_tbl").css( {
		'position' : 'relative',
		'left' : '0px',
		// 'right' : '10px',
		'top' : '0px',
		'bottom' : '0px',
		'width' : "100%",
		'height' : '100%'
	});
	$("#gwikihtmledit_ifr").css( {
		'position' : 'relative',
		'left' : '0px',
		// 'right' : '10px',
		'top' : '0px',
		'width' : "100%"/*,
		'height' : '100%'*/// - 110
	});

	if ($("#gwikihtmledit").length) {
		$("#gwikihtmledit").css( {
			'position' : 'relative',
			'left' : '0px',
			'top' : '0px',
			'width' : "100%",
			'height' : '100%'
		});
	}
}
function gwikirestoreWindow(framId) {
	//removeEventListener('nameOfEvent',referenceToFunction,phase)
	$("#" + framId).removeClass('fullscreen');
	$("#" + framId).css( {
		'position' : 'relative',
		'z-index' : '1',
		'left' : '0px',
		'top' : '0px',
		'width' : '100%'
	});
	$(".gwiki-editor").css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : '100%'
	});
}

function gwikiShowFullPreview() {
	gwikiRestoreFromRte();
	gwikiUnsetContentChanged();
	jQuery("#gwikieditpreviewbutton").click();
}
function gwikiEditSave() {
	gwikiRestoreFromRte();
	gwikiUnsetContentChanged();
	jQuery("#gwikieditsavebutton").click();
}
function gwikiEditCancel() {
	gwikiUnsetContentChanged();
	jQuery("#gwikieditcancelbutton").click();
}

function gwikiHelp() {
	var myWindow = open('../gwikidocs/GWikiWikinotation', "wikihelp",
			"dependend=yes,resizable=yes,scrollbars=yes");
	if (myWindow && myWindow.outerWidth && myWindow.outerHeight) {
		/*
		 * myWindow.outerWidth = 1200; myWindow.outerHeight = 800;
		 */
	}
	// myWindow.moveTo(40, 40);
	myWindow.focus();
}
function gwikiCreateCKEDITOR() {
	var editor = CKEDITOR.replace('gwikihtmledit', {
		// Defines a simpler toolbar to be used in this
		// sample.
		// Note that we have added out "MyButton" button
		// here.
		toolbar : [ [ /* 'Source', '-', */'Bold', 'Italic', 'Underline', 'Strike',
				'-', 'Link', '-', 'MyButton' ] ],
		uiColor : '#9AB8F3'
	});
	editor.on('pluginsLoaded', function(ev) {
		// If our custom dialog has not been registered, do that now.
			if (!CKEDITOR.dialog.exists('myDialog')) {
				// We need to do the following trick to find out the dialog
			// definition file URL path. In the real world, you would simply
			// point to an absolute path directly, like "/mydir/mydialog.js".
			var href = document.location.href.split('/');
			href.pop();
			href.push('api_dialog', 'my_dialog.js');
			href = href.join('/');

			// Finally, register the dialog.
			CKEDITOR.dialog.add('myDialog', href);
		}

		// Register the command used to open the dialog.
		editor.addCommand('myDialogCmd', new CKEDITOR.dialogCommand('myDialog'));

		// Add the a custom toolbar buttons, which fires the above
		// command..
		editor.ui.addButton('MyButton', {
			label : 'My Dialog',
			command : 'myDialogCmd'
		});
	});
}