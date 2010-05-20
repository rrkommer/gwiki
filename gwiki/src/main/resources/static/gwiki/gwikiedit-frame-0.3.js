//if (!window.console){ window.console.log = function {}; }

function gwikiSetContentChanged() {
	gwikiContentChanged = true;
}
function gwikiUnsetContentChanged() {
	gwikiContentChanged = false;
}
function gwikiOnUnload() {
	if (gwikiContentChanged == false)
		return;
	return "Wollen Sie wirklich den Editor verlassen?";
}

window.onbeforeunload = gwikiOnUnload;

function gwikiCreateHtmlEditor(partName, content) {
	gwikiCreateTiny(partName, content);
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


function gwikiCreateTiny(partName, content) {
	if (1 == 2) {
		return;
	}
	var ed = tinyMCE.get('gwikihtmledit' + partName);
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
				/*force_br_newlines : true,
				force_p_newlines : false, */
				forced_root_block : '',
				inline_styles : false,
				// external_link_list_url :
				// "/popweb/gw2/gwiki/static/gwiki/myexternallist.js",
				// convert_newlines_to_brs : true,
				convert_urls : false,
				// XHMTL erlaubt:
				valid_elements: ""
					+"a[accesskey|charset|class|coords|dir<ltr?rtl|href|hreflang|id|lang|name"
				  +"|onblur|onclick|ondblclick|onfocus|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|rel|rev"
				  +"|shape<circle?default?poly?rect|style|tabindex|title|target|type],"
				+"abbr[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"acronym[class|dir<ltr?rtl|id|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"address[class|align|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"applet[align<bottom?left?middle?right?top|alt|archive|class|code|codebase"
				  +"|height|hspace|id|name|object|style|title|vspace|width],"
				+"area[accesskey|alt|class|coords|dir<ltr?rtl|href|id|lang|nohref<nohref"
				  +"|onblur|onclick|ondblclick|onfocus|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup"
				  +"|shape<circle?default?poly?rect|style|tabindex|title|target],"
				+"base[href|target],"
				+"basefont[color|face|id|size],"
				+"bdo[class|dir<ltr?rtl|id|lang|style|title],"
				+"big[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"blockquote[cite|class|dir<ltr?rtl|id|lang|onclick|ondblclick"
				  +"|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
				  +"|onmouseover|onmouseup|style|title],"
				+"body[alink|background|bgcolor|class|dir<ltr?rtl|id|lang|link|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onload|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|onunload|style|title|text|vlink],"
				+"br[class|clear<all?left?none?right|id|style|title],"
				+"button[accesskey|class|dir<ltr?rtl|disabled<disabled|id|lang|name|onblur"
				  +"|onclick|ondblclick|onfocus|onkeydown|onkeypress|onkeyup|onmousedown"
				  +"|onmousemove|onmouseout|onmouseover|onmouseup|style|tabindex|title|type"
				  +"|value],"
				+"caption[align<bottom?left?right?top|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"center[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"cite[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"code[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"col[align<center?char?justify?left?right|char|charoff|class|dir<ltr?rtl|id"
				  +"|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown"
				  +"|onmousemove|onmouseout|onmouseover|onmouseup|span|style|title"
				  +"|valign<baseline?bottom?middle?top|width],"
				+"colgroup[align<center?char?justify?left?right|char|charoff|class|dir<ltr?rtl"
				  +"|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown"
				  +"|onmousemove|onmouseout|onmouseover|onmouseup|span|style|title"
				  +"|valign<baseline?bottom?middle?top|width],"
				+"dd[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"
				+"del[cite|class|datetime|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"dfn[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"dir[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"div[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"dl[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"dt[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"
				+"em/i[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"fieldset[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"font[class|color|dir<ltr?rtl|face|id|lang|size|style|title],"
				+"form[accept|accept-charset|action|class|dir<ltr?rtl|enctype|id|lang"
				  +"|method<get?post|name|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|onreset|onsubmit"
				  +"|style|title|target],"
				+"frame[class|frameborder|id|longdesc|marginheight|marginwidth|name"
				  +"|noresize<noresize|scrolling<auto?no?yes|src|style|title],"
				+"frameset[class|cols|id|onload|onunload|rows|style|title],"
				+"h1[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"h2[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"h3[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"h4[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"h5[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"h6[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"head[dir<ltr?rtl|lang|profile],"
				+"hr[align<center?left?right|class|dir<ltr?rtl|id|lang|noshade<noshade|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|size|style|title|width],"
				+"html[dir<ltr?rtl|lang|version],"
				+"iframe[align<bottom?left?middle?right?top|class|frameborder|height|id"
				  +"|longdesc|marginheight|marginwidth|name|scrolling<auto?no?yes|src|style"
				  +"|title|width],"
				+"img[align<bottom?left?middle?right?top|alt|border|class|dir<ltr?rtl|height"
				  +"|hspace|id|ismap<ismap|lang|longdesc|name|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|src|style|title|usemap|vspace|width],"
				+"input[accept|accesskey|align<bottom?left?middle?right?top|alt"
				  +"|checked<checked|class|dir<ltr?rtl|disabled<disabled|id|ismap<ismap|lang"
				  +"|maxlength|name|onblur|onclick|ondblclick|onfocus|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|onselect"
				  +"|readonly<readonly|size|src|style|tabindex|title"
				  +"|type<button?checkbox?file?hidden?image?password?radio?reset?submit?text"
				  +"|usemap|value],"
				+"ins[cite|class|datetime|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"isindex[class|dir<ltr?rtl|id|lang|prompt|style|title],"
				+"kbd[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"label[accesskey|class|dir<ltr?rtl|for|id|lang|onblur|onclick|ondblclick"
				  +"|onfocus|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
				  +"|onmouseover|onmouseup|style|title],"
				+"legend[align<bottom?left?right?top|accesskey|class|dir<ltr?rtl|id|lang"
				  +"|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"li[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title|type"
				  +"|value],"
				+"link[charset|class|dir<ltr?rtl|href|hreflang|id|lang|media|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|rel|rev|style|title|target|type],"
				+"map[class|dir<ltr?rtl|id|lang|name|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"menu[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"meta[content|dir<ltr?rtl|http-equiv|lang|name|scheme],"
				+"noframes[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"noscript[class|dir<ltr?rtl|id|lang|style|title],"
				+"object[align<bottom?left?middle?right?top|archive|border|class|classid"
				  +"|codebase|codetype|data|declare|dir<ltr?rtl|height|hspace|id|lang|name"
				  +"|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|standby|style|tabindex|title|type|usemap"
				  +"|vspace|width],"
				+"ol[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|start|style|title|type],"
				+"optgroup[class|dir<ltr?rtl|disabled<disabled|id|label|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"option[class|dir<ltr?rtl|disabled<disabled|id|label|lang|onclick|ondblclick"
				  +"|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
				  +"|onmouseover|onmouseup|selected<selected|style|title|value],"
				+"p[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|style|title],"
				+"param[id|name|type|value|valuetype<DATA?OBJECT?REF],"
				+"pre/listing/plaintext/xmp[align|class|dir<ltr?rtl|id|lang|onclick|ondblclick"
				  +"|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout"
				  +"|onmouseover|onmouseup|style|title|width],"
				+"q[cite|class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"s[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"
				+"samp[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"script[charset|defer|language|src|type],"
				+"select[class|dir<ltr?rtl|disabled<disabled|id|lang|multiple<multiple|name"
				  +"|onblur|onchange|onclick|ondblclick|onfocus|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|size|style"
				  +"|tabindex|title],"
				+"small[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"span[align<center?justify?left?right|class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"strike[class|class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title],"
				+"strong/b[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"style[dir<ltr?rtl|lang|media|title|type],"
				+"sub[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"sup[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title],"
				+"table[align<center?left?right|bgcolor|border|cellpadding|cellspacing|class"
				  +"|dir<ltr?rtl|frame|height|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|rules"
				  +"|style|summary|title|width],"
				+"tbody[align<center?char?justify?left?right|char|class|charoff|dir<ltr?rtl|id"
				  +"|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown"
				  +"|onmousemove|onmouseout|onmouseover|onmouseup|style|title"
				  +"|valign<baseline?bottom?middle?top],"
				+"td[abbr|align<center?char?justify?left?right|axis|bgcolor|char|charoff|class"
				  +"|colspan|dir<ltr?rtl|headers|height|id|lang|nowrap<nowrap|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|rowspan|scope<col?colgroup?row?rowgroup"
				  +"|style|title|valign<baseline?bottom?middle?top|width],"
				+"textarea[accesskey|class|cols|dir<ltr?rtl|disabled<disabled|id|lang|name"
				  +"|onblur|onclick|ondblclick|onfocus|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|onselect"
				  +"|readonly<readonly|rows|style|tabindex|title],"
				+"tfoot[align<center?char?justify?left?right|char|charoff|class|dir<ltr?rtl|id"
				  +"|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown"
				  +"|onmousemove|onmouseout|onmouseover|onmouseup|style|title"
				  +"|valign<baseline?bottom?middle?top],"
				+"th[abbr|align<center?char?justify?left?right|axis|bgcolor|char|charoff|class"
				  +"|colspan|dir<ltr?rtl|headers|height|id|lang|nowrap<nowrap|onclick"
				  +"|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown|onmousemove"
				  +"|onmouseout|onmouseover|onmouseup|rowspan|scope<col?colgroup?row?rowgroup"
				  +"|style|title|valign<baseline?bottom?middle?top|width],"
				+"thead[align<center?char?justify?left?right|char|charoff|class|dir<ltr?rtl|id"
				  +"|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup|onmousedown"
				  +"|onmousemove|onmouseout|onmouseover|onmouseup|style|title"
				  +"|valign<baseline?bottom?middle?top],"
				+"title[dir<ltr?rtl|lang],"
				+"tr[abbr|align<center?char?justify?left?right|bgcolor|char|charoff|class"
				  +"|rowspan|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title|valign<baseline?bottom?middle?top],"
				+"tt[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"
				+"u[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress|onkeyup"
				  +"|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style|title],"
				+"ul[class|compact<compact|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown"
				  +"|onkeypress|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover"
				  +"|onmouseup|style|title|type],"
				+"var[class|dir<ltr?rtl|id|lang|onclick|ondblclick|onkeydown|onkeypress"
				  +"|onkeyup|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup|style"
				  +"|title]"
				  ,
				extended_valid_elements : "",
				cleanup_callback : "gwikiEditCleanup",
				content_css : gwikiContentCss,
				setup : function(ed) {
					ed.onChange.add(function(ed, l) {
							ed.isNotDirty = false;
							htmlIsNotDirty = false;
							gwikiSetContentChanged();
						});
				}
			});
	ed = tinyMCE.get('gwikihtmledit' + partName);
	if (!ed) {
		tinyMCE.execCommand('mceAddControl', false, 'gwikihtmledit' + partName);
		ed = tinyMCE.get('gwikihtmledit' + partName);
		// var ed = new tinymce.Editor('gwikihtmledit');
	}
	if (ed) {
		ed.setContent(content);
		ed.isNotDirty = true;
		htmlIsNotDirty = true;
	}
	preContent = content;
	window.setTimeout("gwikiFitTiny('" + partName + "')", 50);
	
}

function gwikiRestoreFromRte(partName) {
	var edit = tinyMCE.get('gwikihtmledit' + partName);
	if (!edit) {
		return true;
	}
	// var firefox = false;
	if (edit.isDirty() == false && htmlIsNotDirty == true) {
		tinyMCE.execCommand('mceRemoveControl', false, 'gwikihtmledit' + partName);
		tinyMCE.remove(edit);
		// if (preContent == edit.getContent()) {
		// alert('not dirty');
		// tinyMCE.remove(edit);
		return false;
		// }
		// alert('not dirty but not equal:\n' + edit.getContent() + '\n\nPREV:\n'
		// + preContent);
	}
	var content = edit.getContent();
	//alert("gwk: " + content);
	tinyMCE.execCommand('mceRemoveControl', false, 'gwikihtmledit' + partName);
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
	var pn = partName;
	$(document)
			.ready(
					function() {
						$("#gwikiwktabs" + pn)
								.tabs( {
									select : function(event, ui) {
										// ui.tab // anchor element of the selected (clicked) tab
										// ui.panel // element, that contains the selected/clicked
										// tab contents
										// ui.index
										// jQuery('#WikiPreview').html("Loading...");

										if (ui.index != 1) {
											gwikiRestoreFromRte(pn);
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
																	if (!$('#gwikihtmledit' + pn).length) {
																		var te = "<textarea rows='30' cols='100' id='gwikihtmledit" + pn + "' style='width: 100%;height: 100%'>";
																		$('#WikiRte' + pn).html(te);
																	}
																	$('#gwikihtmledit' + pn).val(res.responseText);
																	gwikiCreateHtmlEditor(pn, res.responseText);
																	window
																			.setTimeout(
																					"ajustScreen('gwikiWikiEditorFrame" + pn + "')",
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
																	document.getElementById('WikiPreview' + pn).innerHTML = html;
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
function isFullScreen()
{
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
	//alert("framId: " + framId);
	if ($("#" + framId + pn).hasClass("fullscreen") == false) {
		gwikimaximizeWindow(framId + pn, pn);
	} else {
		gwikirestoreWindow(framId + pn, pn);
	}
}

function ajustScreen(framId) {
//	var pn = gwikiCurrentPart;
//	if ($("#" + framId + pn).hasClass("fullscreen") == false) {
//		gwikirestoreWindow(framId + pn, pn);
//	} else {
//		gwikimaximizeWindow(framId + pn, pn);
//	}
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
function gwikimaximizeWindow(framId, partName) {
	//window.console.log("gwikimaximizeWindow");
	var vp = getViewPort();
	var pn = partName;
	var ie6 = jQuery.browser.version == '6.0' &&  jQuery.browser.msie == true;
	var position = (ie6 || (jQuery.browser.msie && ! jQuery.support.boxModel)) ? 'absolute' : 'fixed';
	//alert("framId: " + framId);
	$("#" + framId).addClass('fullscreen');
	$("#" + framId).css( {
		'position' : position,
		'z-index' : '1001',
		'left' : '0px',
		'top' : '0px',
		'width' : vp.w, // width + 'px',
		'height' : vp.h - 5,
		'background-color' : '#FFFFFF'
	});
	// $(".gwiki-editor").val('cols', '200');
	$(".gwiki-editor").css( {
		'position' : 'relative',
		'left' : '0px',
		'right' : '0px',
		'top' : '0px',
		//'bottom': '20px',
		'width' : vp.w - 20,
		'height' : vp.h -75 //height - 120//200
	});
	$('#gwikihtmledit' + pn).css( {
		'position' : 'relative',
		'left' : '0px',
		'right' : '0px',
		'top' : '0px',
		//'bottom': '20px',
		'width' : vp.w - 20,
		'height' : vp.h -75 //height - 120//200
	});
	
//	var ed = tinyMCE.get('gwikihtmledit' + pn);
//	if (ed) {
//		$(ed.getContainer()).css( {
//			// 'position' : 'relative',
//			'left' : '0px',
//			'top' : '0px',
//			'width' : "100%",
//			'height' : '100%'
//		});
//	}
	$("#WikiEdit" + pn).css( {
		'width' : "100%",
		'height' : '100%'
	});
//	$("#WikiRte" + pn).css( {
//		// 'position' : 'relative',
//		'left' : '0px',
//		'top' : '0px',
//		'width' : "100%",
//		'height' : '100%'
//	});

//	$("#gwikihtmledit" + pn + "_parent").css( {
//		// 'position' : 'relative',
//		'left' : '0px',
//		'top' : '0px',
//		'width' : "100%",
//		'height' : '100%'
//	});
	$("#gwikiwktabs" + pn).css( {
		// 'position' : 'relative',
		'left' : '0px',
		'left' : '0px',
		'top' : '0px',
		'width' : "100%",
		'height' : '100%'
	});

	$("#WikiPreview" + pn).css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : "100%",
		'height' : '100%'
	});
////das ist es:
//	$("#gwikihtmledit" + pn + "_tbl").css( {
//		'position' : 'relative',
//		'left' : '0px',
//		// 'right' : '10px',
//		'top' : '0px',
//		'bottom' : '0px',
//		//'width' : "100%",
//		'width': vp.w - 20,
//		'height' : '100%'
//	});
	gwikiFitTiny(pn);
	
}

function gwikiStdNestedCss(selector)
{
	$(selector).css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : '100%',
		'height': '100%'
	});
}

function dumpDimension(id)
{
//	console.log('gwikiFitTiny: ' + id + ': width %d height %d',  $(id).outerWidth(),
//			 $(id).outerHeight());
}
function dumpElDimension(id)
{
//	console.log('gwikiFitTiny: ' + $(id).attr('id') + ': width %d height %d',  $(id).outerWidth(),
//			 $(id).outerHeight());
}
function gwikiFitTiny(partName)
{
//	dumpDimension("#gwikihtmledit" + partName);
	dumpDimension("#gwikihtmledit" + partName+ "_tbl");
	var ed = tinyMCE.get('gwikihtmledit' + partName);
	if (!ed) {
		return;
	}
	var edc = ed.getContainer();
	var idcid = $(edc).attr('id');
	//$(edc).attr('height', '100%');
//	console.log('gwikiFitTiny: c1: width %d height %d; ' + idcid,  $(edc).outerWidth(),
//			 $(edc).outerHeight());
	var width = '100%';
	var height = '100%';
	if(isFullScreen() == true) {
		var vp = getViewPort();
		width = vp.w - 20;
		height = vp.h - 150;
	} else {
		height = '450px';//'100%';//$("#gwikihtmledit" + partName).outerHeight();
	}
	
	//console.log('gwikiFitTiny: width: %d height: %d', width, height);
	$("#gwikihtmledit"+ partName + "_tbl").css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : width,
		'height' : height
	});
	$("#" + idcid).css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : width,
		'height' : '100%'//height
	});
	$("#gwikihtmledit"+ partName + "_ifr").css( {
		'position' : 'relative',
		'left' : '0px',
		'top' : '0px',
		'width' : width,
		'height' : height
	});
//	console.log('gwikiFitTiny: c2: width %d height %d; ' + idcid,  $(edc).outerWidth(),
//			 $(edc).outerHeight());
}

function gwikirestoreWindow(framId, partName) {
	//removeEventListener('nameOfEvent',referenceToFunction,phase)
	$("#" + framId).removeClass('fullscreen');
	$("#" + framId).css( {
		'position' : 'relative',
		'z-index' : '1',
		'left' : '0px',
		'top' : '0px',
		'width' : '100%',
		'height': '100%'
	});
	gwikiStdNestedCss(".gwiki-editor");
	gwikiStdNestedCss("#gwikihtmledit" + partName);
	gwikiStdNestedCss("#gwikiwktabs" + partName);
	//gwikiStdNestedCss("#gwikihtmledit" + partName + "_ifr");
	gwikiFitTiny(partName);
}

function gwikiShowFullPreview() {
	gwikiRestoreFromRte(gwikiCurrentPart);
	gwikiUnsetContentChanged();
	jQuery("#gwikieditpreviewbutton").click();
}
function gwikiEditSave() {
	gwikiRestoreFromRte(gwikiCurrentPart);
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
