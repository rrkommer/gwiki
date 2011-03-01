/**
 * Ergänzungen zu Stripes
 */

/**
 * WARNUNG: Sollte nur verwendet werden, wenn es innerhalb der Seite nur EIN Formular
 * gibt! Hier wird das erste Formular submitted!
 *
 * Submitted ein Formular und hängt den Namen der Stripes-Event-Methode an den Request
 * dran.
 */
function ok(button) {
  /* Is equivalent (for stripes) as clicking on a submit button with the name button. */
	var origAction = document.forms[0].action;
	if(origAction.indexOf('?') > -1){
		origAction=origAction.substring(0,origAction.indexOf('?'));
	}
	var action = origAction + '?' + button + '=';
    document.forms[0].action = action;
    return document.forms[0].submit();
}

/**
 * Submitted ein Formular und hängt den Namen der Stripes-Event-Methode an den Request
 * dran.
 *
 * @button Name des Stripes-Events
 * @formName Name des Forumlars das submitted werden soll
 */
function ok2(button, formName) {
    var forms = jQuery("form");
    jQuery.each(forms, function() {
      if(jQuery(this).attr("name") == formName) {
        var action = this.action + '?' + button + '=';
        this.action = action;
        return this.submit();
      }
    });
}

function ok3(button, payload) {
	  /* Is equivalent (for stripes) as clicking on a submit button with the name button. */
		var origAction = document.forms[0].action;
		if(origAction.indexOf('?') > -1){
			origAction=origAction.substring(0,origAction.indexOf('?'));
		}
	    var action = origAction + '?' + button + '=' + payload;
	    document.forms[0].action = action;
	    return document.forms[0].submit();
	}