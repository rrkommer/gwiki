function loadInBackground(url, str, precallback, postcallback){

  var off = url.indexOf(" ");
        if ( off >= 0 ) {
            var selector = url.slice(off+1, url.length);
            selector = jQuery.trim(selector);
            url = url.slice(0, off);
            url = jQuery.trim(url);
            var selectors = selector.split(" ");
        }
    jQuery.ajax({
            url: url,
            type: 'POST',
            dataType: "html",
            data: str,
            complete: function(res, status) {
                if ( status == "success" || status == "notmodified" ) {
                  if(precallback(res.responseText, status, res) == false){
                  	return;
                  }                	
                    var rt = res.responseText.replace(/<script(.|\s)*?\/script>/g, "");
                    var div = jQuery("<div/>").append(rt);
                    for(var i = 0; i < selectors.length; ++i){
                       var curSelector =jQuery.trim(selectors[i]);
                       $(curSelector).html(div.find(curSelector + " > *"));
                    }
                  }
                postcallback(res.responseText, status, res);
            }
        });
}



function serform2map(serform){

//var str = $(form).serialize();

	var params = serform.split("&");
	var map   = [];
	for(var i=0; i < params.length; ++i){
		var keyValue = params[i].split("=");
		var  key = decodeURIComponent(keyValue[0]);
		var value = keyValue.length > 1 ? keyValue[1] : "";
		map.push(key, decodeURIComponent(value));
	}
	return map;
}

function returnFalse(){
	return false;
}

function returnTrue(){
  return true;	
}