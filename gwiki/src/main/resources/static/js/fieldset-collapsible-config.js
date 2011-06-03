$(document).ready(function() {
	$("fieldset.collapsible").collapse();
	$("fieldset.startClosed:not(:first-child)").collapse({
		closed : true
	});
	$("fieldset.startClosed:first-child").collapse({
		closed : false
	});
});