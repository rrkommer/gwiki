

function gwiki_dlg_create_ok_cancel_buttons(dialogid, options) {
	var buttons = {};
	buttons["gwiki.common.ok".i18n()] = {
	  id : 'dlg-ok-button',
	  text : "gwiki.common.ok".i18n(),
	  click : options.onOk
	};
	var defaultCancel = function() {
		$(dialogid).dialog('close');
		// ed.focus();
	};
	var onCancel = defaultCancel;
	if (typeof options.onCancel != 'undefined') {
		onCancel = options.onCancel;
//		console.debug('custom onCancel');
	}

	buttons["gwiki.common.cancel".i18n()] = {
	  text : "gwiki.common.cancel".i18n(),
	  id : 'dlg-cancel-button',
	  click : onCancel
	};
	return buttons;
}

function gwiki_dlg_set_focus_first_input_or_ok(dialogid)
{
	var firstInput = $(dialogid).find('select, input, textarea').first();
	if (firstInput.length == 1) {
		firstInput.focus();
	} else {
		$("#dlg-ok-button").focus();
	}	
}

function gwiki_dlg_bind_input_enter_to_ok(dialogid, callback)
{
	$(dialogid).find('select, input').on('keydown', function(event){
		if (event.keyCode == 13) { //enter
			$('#dlg-ok-button').click();
		}
	});
}
function gwiki_dlg_click_ok()
{
	$('#dlg-ok-button').click();
}
