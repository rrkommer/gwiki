function gwikiSearchBox(field, curPageId, linkAutoCompleteUrl) {
	$(document).ready(function() {
		var _currVal = "";
		$(field).keyup(function() {
			_currVal = $(this).val();
		});

		$(field).autocomplete({
		  source : function(req, callback) {
			  $.ajax({
			    url : linkAutoCompleteUrl + '?method_onLinkAutocomplete=true&q=' + escape(req.term) + '&pageType=gwiki',
			    type : 'GET',
			    success : function(data) {
				    var jdata = eval('(' + data + ')');
				    callback(jdata);
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
		  extraParams : {
			  pageId : curPageId
		  },
		  select : function(even, ui) {
			  var item = ui.item;
			  if (item.key.trim().startsWith("/edit/Search")) {
			  	location.href = item.key.replace(/se=.*$/,"se=" + _currVal);;
				  //location.href = gwikiLocalUrl(item.key);
			  } else {
				  location.href = gwikiLocalUrl(item.key);
			  }
			  return false
		  }

		}).autocomplete("instance")._renderItem = function(ul, item) {
			return $("<li>" + item.label + "</li>").appendTo(ul);
			
		};
	});
}
