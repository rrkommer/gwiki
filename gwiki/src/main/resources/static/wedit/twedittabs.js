//if (!window.console){ window.console.log = function {}; }

function gwikidbglog(msg) {
	if (typeof console == "undefined")
		return;
	console.log(msg);
}

function gwikiSetContentChanged() {
	gwikiContentChanged = true;
}
function gwikiUnsetContentChanged(partName, chain) {
	gwikiContentChanged = false;
	if (chain) {
		chain();
	}
}
function gwikiOnUnload() {
	if (gwikiContentChanged == false)
		return;
	return "gwiki.editor.message.leave".i18n();
}

window.onbeforeunload = gwikiOnUnload;

var preContent = '';
var htmlIsNotDirty = true;

function gwikiEditCleanup(type, value) {
	switch (type) {
	// case "get_from_editor":
	// value = value.replace(/\<p /g, '<div ');
	// value = value.replace(/\<\/p\>/g, '</div>');
	// break;
	}
	return value;
}

// hides advanced and / or expert toolbar if necessary
function hideToolbars(editorId, body, doc) {
	var inst = tinyMCE.getInstanceById(editorId);
	var data = document.cookie.split(';');

	for (i in data) {
		if (data[i].trim().startsWith("EDITOR_LEVEL")) {
			if (data[i].trim().split('=')[1].trim() == '1') {
				if ($('#' + editorId + '_toolbar3')) {
					$('#' + editorId + '_toolbar3').hide();
					return;
				}
			} else if (data[i].trim().split('=')[1].trim() == '2') {
				return;
			}
		}
	}
	if ($('#' + editorId + '_toolbar2') && $('#' + editorId + '_toolbar3')) {
		$('#' + editorId + '_toolbar2').hide();
		$('#' + editorId + '_toolbar3').hide();
	}
};

function gwikiRestoreFromRte(partName, chain) {
	var callback = chain;
	var edit = tinyMCE.get('gwikihtmledit' + partName);
	console.debug(' gwikiRestoreFromRte');
	if (!edit) {
		return callback();
	}
	// var firefox = false;
	if (edit.isDirty() == false && htmlIsNotDirty == true) {
		tinyMCE.execCommand('mceRemoveControl', false, 'gwikihtmledit' + partName);
		tinyMCE.remove(edit);
		return callback();
	}
	var content = edit.getContent();
	// alert("gwk: " + content);
	tinyMCE.execCommand('mceRemoveControl', false, 'gwikihtmledit' + partName);
	tinyMCE.remove(edit);
	content = wedit_cleanuphtml(content);
	jQuery.ajax({
	  cache : false,
	  url : './EditPage?method_onRteToWiki=true',
	  type : 'POST',
	  dataType : "html",
	  data : {
		  htmlCode : content
	  },
	  complete : function(res, status) {
		  console.debug(' html2wiki: ' + res.responseText);
		  if (status == "success" || status == "notmodified") {
			  if (res.status == 200) {
				  $("#textarea" + partName).val(res.responseText);
				  callback();
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
	var pn = partName;
	$(document).ready(
	    function() {
		    var tabs = $("#gwikiwktabs" + pn).tabs(
		        {
			        activate : function(event, ui) {
				        var tabindex = ui.newTab.index();
				        // ui.tab // anchor element of the selected (clicked) tab
				        // ui.panel // element, that contains the selected/clicked
				        // tab contents
				        // ui.index
				        // jQuery('#WikiPreview').html("Loading...");
				        console.debug("gwikiwktabs.activate: " + tabindex);
				        if (tabindex != 1) {
					        gwikiRestoreFromRte(pn, function(){});
				        }
				        if (tabindex == 0) {
					        var frmqs = jQuery("#editForm").serialize();
					        jQuery.ajax({
					          cache : false,
					          url : './EditPage?method_onAsyncWikiView=true&partName=' + partName,
					          type : 'POST',
					          dataType : "html",
					          data : frmqs,
					          complete : function(res, status) {
					          }
					        });
				        } else if (tabindex == 1) {
					        var frmqs = jQuery("#editForm").serialize();
					        jQuery.ajax({
					          cache : false,
					          url : './EditPage?method_onAsyncRteCode=true&partName=' + partName,
					          type : 'POST',
					          dataType : "html",
					          data : frmqs,
					          complete : function(res, status) {
						          if (status == "success" || status == "notmodified") {
							          if (res.status == 200) {
								          if (!$('#gwikihtmledit' + pn).length) {
									          var te = "<textarea rows='30' cols='100' id='gwikihtmledit" + pn
									              + "' style='width: 100%;height: 100%'>";
									          $('#WikiRte' + pn).html(te);
								          }
								          $('#gwikihtmledit' + pn).val(res.responseText);
								          twedit_create(pn, res.responseText);
								          // window
								          // .setTimeout(
								          // "ajustScreen('gwikiWikiEditorFrame" + pn + "')",
								          // 200);
								          window.setTimeout("gwikiFitTiny('" + pn + "')", 500);

							          } else {
								          alert(res.responseText);
							          }
						          }
					          }
					        });
				        } else if (tabindex == 2) {
					        var frmqs = jQuery("#editForm").serialize();
					        jQuery.ajax({
					          cache : false,
					          url : './EditPage?method_onAsyncWikiPreview=true&partName=' + partName,
					          type : 'POST',
					          dataType : "html",
					          data : frmqs,
					          complete : function(res, status) {
						          if (status == "success" || status == "notmodified") {
							          if (res.status == 200) {
								          var html = res.responseText
								          document.getElementById('WikiPreview' + pn).innerHTML = html;
							          } else {
								          alert("Failure rendering Preview: " + res.responseText);
							          }
						          }
					          }
					        });
				        }

			        }
		        });
		    if (true || gwikiRteDefault) {
		    	tabs.tabs("option", "active", 1);
		    }
		    if (gwikiEditDefaultFullscreen) {
			    gwikiFullscreen('gwikiwktabs');
		    }
	    });

}
function isFullScreen() {
	var pn = gwikiCurrentPart;
	var framId = 'gwikiWikiEditorFrame' + pn;
	if ($("#" + framId).hasClass("fullscreen") == true) {
		return true;
	} else {
		return false;
	}
}

window.onresize = function(event) {
	var pn = gwikiCurrentPart;
	var framId = 'gwikiWikiEditorFrame' + pn;
	if ($("#" + framId).hasClass("fullscreen") == true) {
		gwikimaximizeWindow(framId, pn);
	} else {
		gwikirestoreWindow(framId);
	}
}

function gwikiFullscreen(framId) {
	var pn = gwikiCurrentPart;
	// alert("framId: " + framId);
	if ($("#" + framId + pn).hasClass("fullscreen") == false) {
		gwikimaximizeWindow(framId + pn, pn);
	} else {
		gwikirestoreWindow(framId + pn, pn);
	}
}

function ajustScreen(framId) {
	// var pn = gwikiCurrentPart;
	// if ($("#" + framId + pn).hasClass("fullscreen") == false) {
	// gwikirestoreWindow(framId + pn, pn);
	// } else {
	// gwikimaximizeWindow(framId + pn, pn);
	// }
}
function getViewPort() {
	var doc = window.document;
	var b = jQuery.support.boxModel ? doc.documentElement : doc.body;
	return {
	  x : window.pageXOffset || b.scrollLeft,
	  y : window.pageYOffset || b.scrollTop,
	  w : window.innerWidth || b.clientWidth,
	  h : window.innerHeight || b.clientHeight
	};
}
function gwikimaximizeWindow(framId, partName) {
	// window.console.log("gwikimaximizeWindow");
	var vp = getViewPort();
	var pn = partName;
	var ie6 = jQuery.browser.version == '6.0' && jQuery.browser.msie == true;
	var position = (ie6 || (jQuery.browser.msie && !jQuery.support.boxModel)) ? 'absolute' : 'fixed';
	// alert("framId: " + framId);
	$("#" + framId).addClass('fullscreen');
	$("#" + framId).css({
	  'position' : position,
	  'z-index' : '1001',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : vp.w, // width + 'px',
	  'height' : vp.h - 5,
	  'background-color' : '#FFFFFF'
	});
	// $(".gwiki-editor").val('cols', '200');
	$(".gwiki-editor").css({
	  'position' : 'relative',
	  'left' : '0px',
	  'right' : '0px',
	  'top' : '0px',
	  // 'bottom': '20px',
	  'width' : vp.w - 20,
	  'height' : vp.h - 75
	// height - 120//200
	});
	$('#gwikihtmledit' + pn).css({
	  'position' : 'relative',
	  'left' : '0px',
	  'right' : '0px',
	  'top' : '0px',
	  // 'bottom': '20px',
	  'width' : vp.w - 20,
	  'height' : vp.h - 75
	// height - 120//200
	});

	// var ed = tinyMCE.get('gwikihtmledit' + pn);
	// if (ed) {
	// $(ed.getContainer()).css( {
	// // 'position' : 'relative',
	// 'left' : '0px',
	// 'top' : '0px',
	// 'width' : "100%",
	// 'height' : '100%'
	// });
	// }
	$("#WikiEdit" + pn).css({
	  'width' : "100%",
	  'height' : '100%'
	});
	// $("#WikiRte" + pn).css( {
	// // 'position' : 'relative',
	// 'left' : '0px',
	// 'top' : '0px',
	// 'width' : "100%",
	// 'height' : '100%'
	// });

	// $("#gwikihtmledit" + pn + "_parent").css( {
	// // 'position' : 'relative',
	// 'left' : '0px',
	// 'top' : '0px',
	// 'width' : "100%",
	// 'height' : '100%'
	// });
	$("#gwikiwktabs" + pn).css({
	  // 'position' : 'relative',
	  'left' : '0px',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : "100%",
	  'height' : '100%'
	});

	$("#WikiPreview" + pn).css({
	  'position' : 'relative',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : "100%",
	  'height' : '100%'
	});
	// //das ist es:
	// $("#gwikihtmledit" + pn + "_tbl").css( {
	// 'position' : 'relative',
	// 'left' : '0px',
	// // 'right' : '10px',
	// 'top' : '0px',
	// 'bottom' : '0px',
	// //'width' : "100%",
	// 'width': vp.w - 20,
	// 'height' : '100%'
	// });
	gwikiFitTiny(pn);
	jQuery.ajax({
	  cache : false,
	  url : './EditPage?method_onAsyncFullscreen=true&showFullScreen=true',
	  type : 'POST',
	  dataType : "html",
	  // data : frmqs,
	  complete : function(res, status) {
	  }
	});
}

function gwikiStdNestedCss(selector) {
	$(selector).css({
	  'position' : 'relative',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : '100%',
	  'height' : '100%'
	});
}

function dumpDimension(id) {
	if (typeof console == "undefined")
		return;
	console.log('gwikiFitTiny: ' + id + ': width %d height %d', $(id).outerWidth(), $(id).outerHeight());

}
function dumpElDimension(id) {
	// console.log('gwikiFitTiny: ' + $(id).attr('id') + ': width %d height %d',
	// $(id).outerWidth(),
	// $(id).outerHeight());
}
function gwikiFitTiny(partName) {
	var ed = tinyMCE.get('gwikihtmledit' + partName);
	if (!ed) {
		gwikidbglog('editor does not exists. ' + 'gwikihtmledit' + partName)
		return;
	}
	dumpDimension("#gwikihtmledit" + partName + "_tbl");
	var edc = ed.getContainer();
	var idcid = $(edc).attr('id');
	// $(edc).attr('height', '100%');
	// console.log('gwikiFitTiny: c1: width %d height %d; ' + idcid,
	// $(edc).outerWidth(),
	// $(edc).outerHeight());
	var width = '100%';
	var height = '100%';
	if (isFullScreen() == true) {
		var vp = getViewPort();
		width = vp.w - 20;
		height = vp.h - 150;
	} else {
		height = '450px';// '100%';//$("#gwikihtmledit" + partName).outerHeight();
	}

	// console.log('gwikiFitTiny: width: %d height: %d', width, height);
	$("#gwikihtmledit" + partName + "_tbl").css({
	  'position' : 'relative',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : width,
	  'height' : height
	});
	$("#" + idcid).css({
	  'position' : 'relative',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : width,
	  'height' : '100%'// height
	});
	$("#gwikihtmledit" + partName + "_ifr").css({
	  'position' : 'relative',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : width,
	  'height' : height
	});
	// console.log('gwikiFitTiny: c2: width %d height %d; ' + idcid,
	// $(edc).outerWidth(),
	// $(edc).outerHeight());
}

function gwikirestoreWindow(framId, partName) {
	// removeEventListener('nameOfEvent',referenceToFunction,phase)
	$("#" + framId).removeClass('fullscreen');
	$("#" + framId).css({
	  'position' : 'relative',
	  'z-index' : '1',
	  'left' : '0px',
	  'top' : '0px',
	  'width' : '100%',
	  'height' : '100%'
	});
	gwikiStdNestedCss(".gwiki-editor");
	gwikiStdNestedCss("#gwikihtmledit" + partName);
	gwikiStdNestedCss("#gwikiwktabs" + partName);
	// gwikiStdNestedCss("#gwikihtmledit" + partName + "_ifr");
	gwikiFitTiny(partName);
	jQuery.ajax({
	  cache : false,
	  url : './EditPage?method_onAsyncFullscreen=true&showFullScreen=false',
	  type : 'POST',
	  dataType : "html",
	  // data : ,
	  complete : function(res, status) {
	  }
	});
}

function gwikiShowFullPreview() {
	gwikiRestoreFromRte(gwikiCurrentPart, function(){});
	gwikiUnsetContentChanged();
	jQuery("#gwikieditpreviewbutton").click();
}
function gwikiEditSave() {
	gwikiRestoreFromRte(gwikiCurrentPart, function(){});
	gwikiUnsetContentChanged();
	jQuery("#gwikieditsavebutton").click();
}
function gwikiEditCancel() {
	gwikiUnsetContentChanged();
	jQuery("#gwikieditcancelbutton").click();
}

function gwikiHelp() {
	var myWindow = open('../gwikidocs/help/de/WikiSyntax', "wikihelp", "dependend=yes,resizable=yes,scrollbars=yes");
	if (myWindow && myWindow.outerWidth && myWindow.outerHeight) {
		/*
		 * myWindow.outerWidth = 1200; myWindow.outerHeight = 800;
		 */
	}
	// myWindow.moveTo(40, 40);
	myWindow.focus();
}
