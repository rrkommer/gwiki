function gwikiSearchBox(field, curPageId, linkAutoCompleteUrl) 
{
	$(field).autocomplete(linkAutoCompleteUrl, {
		matchContains : true,
		cacheLength: 1,
		matchSubset: false,
		minChars : 0,
		width : 350,
		scroll: true,
		scrollHeight: 400,
		extraParams: { pageId: curPageId },
		formatItem : function(row) {
			return row[1];
		}
	}).result(function(event, item) {
		location.href = item[0];
	});
}
