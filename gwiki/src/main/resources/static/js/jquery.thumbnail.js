function dom_init() {
	$('a.imagelink').fancybox(){
		'speedIn' : 600,
	    'speedOut' : 300,
		'titleShow' : false
	}
	$('img.thumb').each(function() {
		var maxWidth = 100;
		var maxHeight = 100;
		var ratio;
		var width = $(this).width();
		var height = $(this).height();
		
		if(width > maxWidth) {
			ratio = maxWidth/width;
			$(this).css("width", maxWidth);
			$(this).css("height", height * ratio);
			height = height * ratio;
			width = width * ratio;
		}
		if(height > maxHeight) {
			ratio = maxHeight/height;
			$(this).css("height", maxHeight);
			$(this).css("width", width * ratio);
			width = width * ratio;
		}
	}); 
	$('img.thumb').click(function(){
		var par = $(this).parent().get(0).tagName;
		if(par != 'A'){
			window.open(this.src);
		}
	});
}    