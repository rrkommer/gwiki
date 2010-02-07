
function gwikiActivateCodeEditor(textId, partName, lang)
{
	editAreaLoader.init(
			{ id : textId,
				syntax: lang,
				start_highlight: true,
				replace_tab_by_spaces: 2
			});
}
function gwikiDeActivateCodeEditor(textId, partName)
{
	if (editAreaLoader.get(textId)) {
		editAreaLoader.toggle(textId);
	}
}