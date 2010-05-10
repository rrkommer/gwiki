$(document).ready(function(){
	$('img.smallThumb').each(function() {
		var maxWidth = 150;
		var maxHeight = 150;
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
	$('img.mediumThumb').each(function() {
		var maxWidth = 300;
		var maxHeight = 300;
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
	$('img.largeThumb').each(function() {
		var maxWidth = 450;
		var maxHeight = 450;
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
	$("img.smallThumb, img.mediumThumb, img.largeThumb").each(function() {
		var src = $(this).attr('src');
		var a = $('<a class="showimage"/>').attr('href', src);
		$(this).wrap(a);
	});
	$("a.showimage").fancybox({
		'speedIn' : 400,
		'speedOut' : 200,
		'titleShow' : false,
		'overlayOpacity' : 0.75,
		'overlayColor' : '#000'
	});
});