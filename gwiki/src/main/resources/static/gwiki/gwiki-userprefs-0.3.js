function gwikiSaveUserPrev(key, value, persist) {
	jQuery.ajax({
		// url: gwikiHomeUrl + "/edit/UserPrefAsync",
		url : gwikiContextPath + gwikiServletPath + "/edit/UserPrefAsync",
		type : 'POST',
		dataType : "json",
		data : {
			key : key,
			value : value,
			persist : persist,
			method_onSave : true
		}
	});
}