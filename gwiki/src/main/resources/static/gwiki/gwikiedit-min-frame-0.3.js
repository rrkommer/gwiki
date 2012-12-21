//if (!window.console){ window.console.log = function {}; }

function gwikidbglog(msg)
{
	if (typeof console=="undefined") return;
	console.log(msg);
}

function gwikiSetEditorContentChanged() {
	gwikiEditorContentChanged = true;
}

function gwikiCreateTiny(partName, content) {
	tinyMCE.init({
		theme : "advanced",
        mode : "exact",
        autor_resize : true,
        plugins : "-gwiki,paste,spellchecker",
        theme_advanced_buttons1 : "bold,italic,underline,|,bullist,numlist,|,wikichoosenlink,link,unlink,|,undo,redo,|,cut,copy,paste,|,iespell,spellchecker",
        theme_advanced_buttons2 : "",
        theme_advanced_buttons3 : "",
        theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left",
		theme_advanced_resizing : true,
		theme_advanced_blockformats : "p,div,h1,h2,h3,h4,h5,h6,pre,code,blockquote",
		spellchecker_rpc_url :  gwikiHomeUrl + "/edit/spellchecker/ScRteCheckerAjax", //spellcheck url for jazzy use /spellchecker/jazzy-spellchecker
		paste_auto_cleanup_on_paste : true,
		paste_remove_styles: true,
        paste_remove_styles_if_webkit: true,
        paste_strip_class_attributes: true,
        paste_text_use_dialog : true,
		force_br_newlines : false,
		force_p_newlines : true, 
		forced_root_block : '',
		inline_styles : false,
		convert_urls : false,
		// XHMTL erlaubt:
		valid_elements: ""
		+"a[accesskey|charset|class|coords|dir<ltr?rtl|href|hreflang|id|lang|name"
		  +"|onblur|onclick|ondblclick|onfocus|onkeydown|onkeypress|onkeyup"
		  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|rel|rev"
		  +"|shape<circle?default?poly?rect|style|tabindex|title|target|type],"
		+"br[class|clear<all?left?none?right|id|style|title],"
		+"div[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
		  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
		  +"|onmouseout|onmouseover|onmouseup|style|title],"
		+"li[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
		  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title|type"
		  +"|value],"
		+"ol[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
		  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
		  +"|onmouseup|start|style|title|type],"
		+"p[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
		  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
		  +"|onmouseout|onmouseover|onmouseup|style|title],"
		+"pre/listing/plaintext/xmp[align|class|dir<ltr?rtl|id|lang|onclick|ondblclick"
		  +"|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
		  +"|onmouseover|onmouseup|style|title|width],"
		+"em/i[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
		  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
		  +"|title],"
		+"strong/b[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
		  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
		  +"|title],"
		+"u[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
		  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"
		+"ul[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
		  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
		  +"|onmouseup|style|title|type],"
		  ,
		extended_valid_elements : "",
		content_css : gwikiContentCss,
		setup : function(ed) {
			ed.onChange.add(function(ed, l) {
					gwikiSetEditorContentChanged();
				});
		},
        elements : partName
	});

}