/*
Supersized - Full Screen Background/Slideshow jQuery Plugin
supersized.1.0.js
February 2009
By Sam Dunn
www.buildinternet.com / www.onemightyroar.com
*/
(function($){

	//Resize image on ready or resize
	$.fn.supersized = function() {
	
		var options = $.extend($.fn.supersized.defaults, $.fn.supersized.options);
		
		if (options.slideshow == 1){
			setInterval("theslideshow()", options.slideinterval);
		}
		
		$().ready(function() {
			$('#supersize').resizenow(); 
		});
		$(window).bind("resize", function(){
    		$('#supersize').resizenow(); 
		});
	};
	
	//Adjust image size
	$.fn.resizenow = function() {
		
		var options = $.extend($.fn.supersized.defaults, $.fn.supersized.options);
		
	  	return this.each(function() {
	  		
			//Define image ratio & minimum dimensions
			var minwidth = options.minsize*(options.startwidth);
			var minheight = options.minsize*(options.startheight);
			var ratio = options.startheight/options.startwidth;
			
			//Gather browser and current image size
			var imagewidth = $(this).width();
			var imageheight = $(this).height();
			var browserwidth = $(window).width();
			var browserheight = $(window).height();
			
			//Check for minimum dimensions
			if ((browserheight < minheight) && (browserwidth < minwidth)){
				$(this).height(minheight);
				$(this).width(minwidth);
			}
			else{	
				//When browser is taller	
				if (browserheight > browserwidth){
				    imageheight = browserheight;
				    $(this).height(browserheight);
				    imagewidth = browserheight/ratio;
				    $(this).width(imagewidth);
				    
				    if (browserwidth > imagewidth){
				    	imagewidth = browserwidth;
				    	$(this).width(browserwidth);
				    	imageheight = browserwidth * ratio;
				    	$(this).height(imageheight);
				    }
				
				}
				
				//When browser is wider
				if (browserwidth >= browserheight){
				    imagewidth = browserwidth;
				    $(this).width(browserwidth);
				    imageheight = browserwidth * ratio;
				    $(this).height(imageheight);
				    
				    if (browserheight > imageheight){
				    	imageheight = browserheight;
				    	$(this).height(browserheight);
				    	imagewidth = browserheight/ratio;
				    	$(this).width(imagewidth);
				    }
				}
			}
			return false;
		});
	};
	
	$.fn.supersized.defaults = { 
			startwidth: 640,  
			startheight: 480,
			minsize: .5,
			slideshow: 1,
			slideinterval: 5000  
	};
	
})(jQuery);

//Slideshow Add-on
function theslideshow() {
    
    var currentslide = $('#supersize .activeslide');
    		
    if ( currentslide.length == 0 ) currentslide = $('#supersize :last');
		
    var nextslide =  currentslide.next().length ? currentslide.next() : $('#supersize :first');
		
    nextslide.addClass('activeslide');
    currentslide.removeClass('activeslide');

}  	