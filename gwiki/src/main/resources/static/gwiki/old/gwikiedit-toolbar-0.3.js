// not working
function gwikiEditBuildToolBar(buttons, toolbar, editor, keyMap, altKeyMap) {
	$(buttons).each(
			function(i) {
				var button = this;
				if (button.keyCode != undefined) {
					keyMap[button.keyCode] = button;
				}
				if (button.altKeyCode != undefined) {
					altKeyMap[button.altKeyCode] = button;
				}
				var title = button.label;
				if (button.tooltip != undefined) {
					title = button.tooltip;
				}
				$(
						"<span><a href=\"\" accesskey=\"" + button.accesskey
								+ "\" title=\"" + title + "\">" + button.label
								+ "</a>&nbsp;|&nbsp;</span>").click(function() {
					editor.tag(button);
					return false;
				}).appendTo(toolbar);
			});
}
