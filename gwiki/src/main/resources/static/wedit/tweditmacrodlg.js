// has to equal to GWikiMacroRenderFlags
var WEDIT_MACRO_RENDER_FLAG_NewLineAfterStart = 0x0001;
var WEDIT_MACRO_RENDER_FLAG_NewLineBeforeEnd = 0x0002;
var WEDIT_MACRO_RENDER_FLAG_TrimTextContent = 0x0010;
var WEDIT_MACRO_RENDER_FLAG_NoWrapWithP = 0x0020;
var WEDIT_MACRO_RENDER_FLAG_ContainsTextBlock = 0x0040;
var WEDIT_MACRO_RENDER_FLAG_TrimWsAfter = 0x0080;
var WEDIT_MACRO_RENDER_FLAG_RteInline = 0x0100;

function MacroInfo() {
	this.newMacro = true;
	this.macroName = null;
	this.macroHead = null;
	this.macroParams = [];
	this.macroHeadDiv = null;
	this.macroBodyDiv = null;
	this.macroMetaInfo = null;
	// html body of the editor
	this.macroBody = null;
	this.findParamByName = function(name) {
		if (!this.macroParams) {
			return null;
		}
		for (var i = 0; i < this.macroParams.length; ++i) {
			if (this.macroParams[i].name == name) {
				return this.macroParams[i];
			}
		}
		return null;
	}

}

function extractMacroDefinition(ed, el) {
	var divel = el;
	var divframe = wedit_macro_findFrame(ed, el);
	if (divframe == null) {
		console.warn('no macroframe div found');
	}
	var divel = $(divframe).find('.weditmacrohead')[0];
	var frameq = $(divframe).find('.weditmacrobody');
	var body = null;
	if (frameq.length > 0) {
		body = frameq[0];
	}
	var head = divel.getAttribute('data-macrohead');
	var ret = new MacroInfo();

	ret.newMacro = false;
	ret.macroName = divel.getAttribute('data-macroname');
	ret.macroHead = head;
	ret.macroHeadDiv = divel;
	ret.macroBodyDiv = body;
	if (body) {
		ret.macroBody = $(body).html();
	}
	return ret;

}

function wedit_show_editmacro_dialog(ed, el) {

	var macroInfo = extractMacroDefinition(ed, el);
	if (!macroInfo) {
		console.warn("Cannot find macro name from element: " + el);
		return;
	}
	wedit_getMacroInfo(macroInfo.macroName, macroInfo.macroHead, function(result) {
		console.debug("Got macro info: " + JSON.stringify(result));
		var resmacroInfo = result.macroInfo;
		if (resmacroInfo) {
			macroInfo.macroParams = resmacroInfo.macroParams;
			macroInfo.macroMetaInfo = resmacroInfo.macroMetaInfo;
		}
		wedit_open_macro_dialog(ed, macroInfo, macroInfo.macroMetaInfo, wedit_updateMacro);
	});

}

function wedit_render_select_new_macro(ed, dialog, modc, list) {
	modc.html('');
	var searchText = $("<input>").attr('id', "macrosearchbox").css('width', '100%');
	searchText.on('keyup', function(event) {
		if (event.which == 13) {
			var el = $(".dlgmacrodiv:visible").first();
			if (el) {
				var texth = $($(el).children()[0]);
				texth.trigger('click');
			}
		}
		var sv = searchText.val().toLowerCase();
		if (sv == '') {
			$('.macrodiv').show();
		} else {
			$(".dlgmacrodiv").each(function(index, el) {

				var texth = $($(el).children()[0]).html();
				var textb = '';// $($(el).children()[1]).html();
				texth = texth.toLowerCase();

				if (texth.indexOf(sv) != -1 || textb.indexOf(sv) != -1) {
					$(el).show();
				} else {
					$(el).hide();
				}
			});
		}
	});
	modc.append(searchText);
	var scrollable = $("<div style='overflow:auto; height: 300px'>").attr('id', 'dlgmacronlist');
	for (var i = 0; i < list.length; ++i) {
		var macroMetaInfo = list[i].macroMetaInfo;
		var macrodiv = $('<div>').addClass('dlgmacron' + macroMetaInfo.macroName).addClass('dlgmacrodiv');

		var p = $("<h4>").hover(function() {
			$(this).css("text-decoration", "underline");
			$(this).css("cursor", "pointer");
		}, function() {
			$(this).css("text-decoration", "none");
			$(this).css("cursor", "inherit");
		});
		p.attr('data-macroName', macroMetaInfo.macroName);
		p.on('click', function(event) {
			$(dialog).dialog('close');
			wedit_switch_to_macro_edit(ed, event.target, list);
		});
		p.text(macroMetaInfo.macroName);
		macrodiv.append(p);

		if (macroMetaInfo.info) {
			macrodiv.append($("<blockquote>").html(macroMetaInfo.info));
		}
		scrollable.append(macrodiv);
	}
	modc.append(scrollable);
}
function wedit_show_newmacro_dialog(ed) {
	wedit_getMacroInfos(ed, wedit_show_newmacro_dialog_impl);
}
function wedit_show_newmacro_dialog_impl(ed, list) {
	var dialog;
	var modc = $("#editDialogBox");
	wedit_render_select_new_macro(ed, dialog, modc, list);
	var buttons = {};
	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
		ed.focus();
	};

	dlghtml = modc.html();
	var dialog = modc.dialog({
	  width : 500,
	  modal : true,
	  dialogClass : 'jquiNoDialogTitle',
	  buttons : buttons
	});
}

function wedit_switch_to_macro_edit(ed, el, macroMetaInfoList) {
	var macroName = $(el).attr('data-macroName');
	var macroMetaInfo = null;
	for (var i = 0; i < macroMetaInfoList.length; ++i) {
		if (macroMetaInfoList[i].macroMetaInfo.macroName == macroName) {
			macroMetaInfo = macroMetaInfoList[i].macroMetaInfo;
			break;
		}
	}
	var macroInfo = new MacroInfo();
	macroInfo.macroName = macroMetaInfo.macroName;
	macroInfo.macroMetaInfo = macroMetaInfo;

	wedit_open_macro_dialog(ed, macroInfo, macroMetaInfo, function(ed, curMacroInfo, newMacroInfo) {
		gwedit_insert_macro_impl(ed, newMacroInfo);

	});
}
function wedit_render_macro_info(ed, modc, curMacroInfo, macroMetaInfo) {
	modc.html('');
	var contentdiv = $("<div class='ui-dialog-content ui-widget-content'>");
	var th = $("<h4>");
	th.text("Macro " + curMacroInfo.macroName);
	contentdiv.append(th);
	var info = $("<p id='wmd_info'></span>");
	if (macroMetaInfo.info) {
		info.text(macroMetaInfo.info);
	}
	contentdiv.append(info);
	if (macroMetaInfo.macroParams.length > 0) {
		// var form = $("<form>");
		var fieldset = $("<fieldset>");
		// form.append(fieldset);
		for (var i = 0; i < macroMetaInfo.macroParams.length; ++i) {

			var pmi = macroMetaInfo.macroParams[i];
			var label = $("<label style='display:block; padding-top: 5px;'>");
			label.attr('for', 'wmd_param_' + pmi.name);
			label.text(pmi.name)
			fieldset.append(label);
			var curParam = curMacroInfo.findParamByName(pmi.name);
			var curval = pmi.defaultValue;
			if (curParam) {
				curval = curParam.value;
			}

			if (pmi.type == 'Boolean') {
				var inp = $("<input >") //
				.attr("class", "quicheck") //
				.attr('type', 'checkbox') //
				.attr('id', 'wmd_param_' + pmi.name).attr('name', 'wmd_param_' + pmi.name);//
				;//
				if (curval == 'true') {
					inp.attr("checked", curval);
				}
				inp.val(curval);
				// only needed, because :checked doesn't worl
				inp.on('click', function(el) {
					var olval = $(el.target).val();
					var nval = olval == 'true' ? 'false' : 'true';
					$(el.target).val(nval);
				});
				fieldset.append(inp);

			} else if (pmi.enumValues && pmi.enumValues.length > 0) {
				var select = $("<select>") //
				.attr('id', 'wmd_param_' + pmi.name) //
				.attr('name', 'wmd_param_' + pmi.name) //
				.attr('class', "select ui-widget-content ui-corner-all");
				select.change(function() {
					console.log('select changed: ' + $(this).val());
				});
				for (var j = 0; j < pmi.enumValues.length; ++j) {
					var ev = pmi.enumValues[j];
					var option = $("<option>").attr("value", ev).text(ev);
					if (ev == curval) {
						option.attr('selected', true);
					}
					select.append(option);
				}
				fieldset.append(select);
			} else {
				fieldset.append( //
				$("<input>") //
				.attr("class", "text ui-widget-content ui-corner-all") //
				.attr('type', 'text') //
				.attr('style', 'width: 100%') //
				.attr('id', 'wmd_param_' + pmi.name)//
				.attr('name', 'wmd_param_' + pmi.name)//
				.val(curval)//
				);
				if (pmi.type == 'PageId') {
					wedit_ac_link('#wmd_param_' + pmi.name);
				}
				if (pmi.info) {
					fieldset.append($("<br/>"));
					fieldset.append($("<span>").html(pmi.info));
					fieldset.append($("<p>"));

				}

			}

		}
		// contentdiv.append(form);
		contentdiv.append(fieldset);
	}
	modc.append(contentdiv);
	if (macroMetaInfo.hasBody && macroMetaInfo.evalBody == false) {
		var txtdiv = $('<div>').css('display', 'inline-block').css('vertical-align', 'top').css('padding', '5px').css(
		    'width', 'calc(100% - 5px)');
		txtdiv.append($('<textarea>').attr('id', 'wm_macro_body').css('width', '100%').css('height', '300').html(
		    curMacroInfo.macroBody));

		modc.append(txtdiv);
	}
}
function wedit_open_macro_dialog(ed, curMacroInfo, macroMetaInfo, callback) {
	var modc = $("#editDialogBox");

	wedit_render_macro_info(ed, modc, curMacroInfo, macroMetaInfo);

	var buttons = {};
	buttons["gwiki.common.cancel".i18n()] = function() {
		$(dialog).dialog('close');
		ed.focus();
	};
	buttons["gwiki.common.ok".i18n()] = function() {
		var macroInfo = new MacroInfo();
		macroInfo.macroName = curMacroInfo.macroName;
		macroInfo.macroHeadDiv = curMacroInfo.macroHeadDiv;
		macroInfo.macroBodyDiv = curMacroInfo.macroBodyDiv;
		macroInfo.macroMetaInfo = macroMetaInfo;
		for (var i = 0; i < macroMetaInfo.macroParams.length; ++i) {
			var pmi = macroMetaInfo.macroParams[i];
			var val = null;
			if (pmi.type == 'Boolean') {
				var ceckb = $('#wmd_param_' + pmi.name);
				val = ceckb.val();
			} else if (pmi.enumValues && pmi.enumValues.length > 0) {
				// val = $('#wmd_param_' + pmi.name + ' option:selected').val();
				val = $('#wmd_param_' + pmi.name).val();
			} else {
				val = $('#wmd_param_' + pmi.name).val();
			}
			if (val && val != '') {
				macroInfo.macroParams[macroInfo.macroParams.length] = {
				  name : pmi.name,
				  value : val
				};
			}
		}
		macroInfo.macroHead = wedit_renderHead(macroInfo);
		if (macroMetaInfo.hasBody == true && macroMetaInfo.evalBody == false) {
			macroInfo.macroBody = $('#wm_macro_body').val();
		}
		$(dialog).dialog('close');
		ed.focus();
		callback(ed, curMacroInfo, macroInfo);
	};
	var dlghtml = modc.html();
	// console.debug("dialog: " + dlghtml);
	var dialog = modc.dialog({
	  // height: $(window).height() - 50,
	  width : $(window).width() - 50,
	  dialogClass : 'jquiNoDialogTitle',
	  modal : true,
	  buttons : buttons,
	  open : function(event, ui) {
		  // $(".quicheck").button();
	  }
	});

}

function wedit_escapemacrohead(k) {
	if (!k) {
		return k;
	}
	k = k.replace("\\", "\\\\");
	k = k.replace("|", "\\|");
	k = k.replace("=", "\\=");
	return k;
}

function wedit_macroHasRequiredParams(macroMetaInfo) {
	if (!macroMetaInfo.macroParams || macroMetaInfo.macroParams.length == 0) {
		return false;
	}
	for (var i = 0; i < macroMetaInfo.macroParams.length; ++i) {
		var pi = macroMetaInfo.macroParams[i];
		if (pi.required == true) {
			return true;
		}
	}
	return false;
}

function wedit_renderHead(macroInfo) {

	var h = macroInfo.macroName;
	if (macroInfo.macroParams && macroInfo.macroParams.length) {
		var firstP = true;
		for (var i = 0; i < macroInfo.macroParams.length; ++i) {

			var pm = macroInfo.macroParams[i];
			if (!pm.value) {
				continue;
			}
			if (firstP) {
				h += ':';
				firstP = false;
			} else {
				h += '|';
			}
			h += wedit_escapemacrohead(pm.name) + '=' + wedit_escapemacrohead(pm.value);
		}
	}
	return h;
}
function wedit_updateMacro(ed, curMacroInfo, newMacroInfo) {

	var hdiv = $(curMacroInfo.macroHeadDiv);
	hdiv.attr('data-macrohead', newMacroInfo.macroHead);
	hdiv.attr('data-macroname', newMacroInfo.macroName);
	var htext = hdiv.find(".weditmacrn");
	htext.text(newMacroInfo.macroHead);
	if (newMacroInfo.macroMetaInfo.hasBody == true && newMacroInfo.macroMetaInfo.evalBody == false) {
		$(curMacroInfo.macroBodyDiv).html(newMacroInfo.macroBody);
	}
}

function wedit_getMacroInfos(ed, callback) {
	var url = gwedit_buildUrl("edit/WeditService");

	$.ajax(url, {

	  data : {
		  method_onGetMacroInfos : 'true'
	  },
	  dataType : "text",
	  global : false,
	  success : function(data) {
		  var jdata = eval('(' + data + ')');
		  if (jdata.ret == 0) {
			  callback(ed, jdata.list);
		  } else {
			  console.warn("Error get list: " + jdata.ret + "; " + jdata.message);
		  }

	  },
	  fail : function(jqXHR, textStatus, errorThrown) {
		  console.error("got json error: " + textStatus);
	  }
	});
}
function wedit_getMacroInfo(macroName, macroHead, callback) {
	var url = gwedit_buildUrl("edit/WeditService");
	// "?method_onGetMacroInfo=true&macro="
	// + macroName;

	$.ajax(url, {

	  data : {
	    method_onGetMacroInfo : 'true',
	    macro : macroName,
	    macroHead : macroHead
	  },
	  dataType : "text",
	  global : false,
	  success : function(data) {
		  var jdata = eval('(' + data + ')');
		  if (jdata.ret == 0) {
			  callback(jdata);
		  } else {
			  console.warn("Error get list: " + jdata.ret + "; " + jdata.message);
		  }

	  },
	  fail : function(jqXHR, textStatus, errorThrown) {
		  console.error("got json error: " + textStatus);
	  }
	});
}

/**
 * from autocomplete
 * 
 * @param ed
 * @param item
 */
function gwedit_insert_macro(ed, item) {
	console.debug("insert macro");
	wedit_deleteLeftUntil(ed, "{");
	var macroInfo = new MacroInfo();
	macroInfo.macroMetaInfo = item.macroMetaInfo;
	macroInfo.macroName = item.url;
	macroInfo.macroHead = item.url;

	if (wedit_macroHasRequiredParams(macroInfo.macroMetaInfo) == false) {
		gwedit_insert_macro_impl(ed, macroInfo);
		return;
	}
	wedit_open_macro_dialog()
}

function gwedit_insert_macro_impl(ed, macroInfo) // todo here macroInfo
{
	var withbody = macroInfo.macroMetaInfo.hasBody;
	var evalbody = macroInfo.macroMetaInfo.evalBody;
	var headId = wedit_genid("mhead_");
	var bodyid = wedit_genid("mbody_");
	var templBegin = macroInfo.macroMetaInfo.macroTemplateBegin;
	var templEnd = macroInfo.macroMetaInfo.macroTemplateEnd;
	var fuellsel = '';
	if (macroInfo.macroMetaInfo.hasBody) {
		var fuellsel = "<p>&nbsp;</p>";

		if (WEDIT_MACRO_RENDER_FLAG_RteInline & macroInfo.macroMetaInfo.renderFlags) {
			fuellsel = "&nbsp;";
		} else if (macroInfo.macroMetaInfo.evalBody == false) {
			fuellsel = macroInfo.macroBody;
		}
	}
	var html = templBegin + fuellsel + templEnd;

	var node = tedit_insertRaw(ed, html);
	if (withbody) {

		if (node.childNodes.length >= 2) {
			var body = node.childNodes[1];
			// var body = ed.$.find("#" + bodyid);
			if (body.childNodes.length > 0) {
				var tn = body.childNodes[0];
				if (tn.childNodes.length > 0) {
					tn = tn.childNodes[0];
				}
				ed.selection.setCursorLocation(tn, 0);
			}
		}
	}
}

function wedit_hide_contextToollBar(ed) {

	var mcefloatpanel = $('.mce-floatpanel');
	if (mcefloatpanel) {
		mcefloatpanel.hide();
	}

}

function wedit_macro_findFrame(ed, divel) {
	if (divel == null) {
		return null;
	}
	if (divel.nodeName != 'DIV' && divel.nodeName != 'SPAN') {
		var pel = divel.parentNode;
		return wedit_macro_findFrame(ed, pel);
	}
	if (divel.getAttribute('class').indexOf('weditmacroframe') == -1) {
		var pel = divel.parentNode;
		return wedit_macro_findFrame(ed, pel);
	}
	return divel;
}

function wedit_macro_delete_current(ed, el) {

	var divel = wedit_macro_findFrame(ed, el);
	if (divel == null) {
		console.warn("no weditmacroframe found");
		return;
	}
	var pnode = divel.parentNode;
	pnode.removeChild(divel);
	wedit_hide_contextToollBar(ed);
}

function wedit_ac_link(selector, wikitype) {
	setTimeout(function() {
		var sel = $(selector);
		var linkAutoCompleteUrl = gwikiLocalUrl("edit/WeditService");
		sel.autocomplete({
		  source : function(req, callback) {
			  console.debug("wedit_ac_link ac: " + linkAutoCompleteUrl + ": " + req.term);
			  $.ajax({
			    url : linkAutoCompleteUrl,
			    type : 'GET',
			    data : {
			      method_onPageIdAutocomplete : true,
			      pageType : !wikitype || wikitype === undefined ? 'All' : wikitype,
			      q : req.term
			    },
			    success : function(data) {
				    // console.debug("receveived: " + data);
				    // var jdata = eval('(' + data + ')');
				    callback(data);
			    },
			    fail : function(jqXHR, textStatus, errorThrown) {
				    console.error("got  error: " + textStatus);
			    }
			  });
		  },
		  matchContains : true,
		  cacheLength : 2,
		  matchSubset : false,
		  minChars : 2,
		  width : 350,
		  scroll : true,
		  scrollHeight : 400,
		  select : function(even, ui) {
			  var item = ui.item;
			  var v = item.key;
			  $(selector).val(v);
			  return false
		  }

		}).autocomplete("instance")._renderItem = function(ul, item) {
			return $("<li>" + item.label + "</li>").appendTo(ul);

		};
	}, 200);
}