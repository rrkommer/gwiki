var gwiki18nCloseEditDialog = false;
var gwiki18nMenu;

function gwikiCloseI18NCtxMenu() {
	var cmenu = gwiki18nMenu;
	if (cmenu.shown) {
		if (cmenu.iframe) {
			$(cmenu.iframe).hide();
		}
		if (cmenu.menu) {
			cmenu.menu[cmenu.hideTransition](cmenu.hideSpeed,
					((cmenu.hideCallback) ? function() {
						cmenu.hideCallback.call(cmenu);
					} : null));
		}
		if (cmenu.shadow) {
			cmenu.shadowObj[cmenu.hideTransition](cmenu.hideSpeed);
		}
	}
	cmenu.shown = false;
}

function gwikiI18NCtxMenu(element, pageId, key, backurl, gwikibase) {
	gwiki18nCloseEditDialog = false;
	var params = {
		key : key,
		pageId : pageId,
		backUrl: backurl
	};
	$("#gwikii18nmenu").load(gwikibase + "/edit/I18NAjaxEdit", params)
	$(element).contextMenu("#gwikii18nmenu", {
			shadow: false,
			hide : function() {
				gwiki18nMenu = this;
			}
		});
	return false;
}