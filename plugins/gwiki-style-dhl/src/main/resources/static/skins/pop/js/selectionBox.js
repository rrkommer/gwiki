/**
 * 
 * 
 */
function selectionBox(srcClass, destClass)
{
	
	function initTransferer(srcClass, destClass){
  $("."+srcClass + destClass).click(function() {
  	  var arr = new Array();
  	  var allOptions = $($('.'+srcClass))[0].options;
  	  for(var i =0; i < allOptions.length; ++i){
  	  	var option = allOptions[i];
  	  	if(option.selected){
            arr.push(option);  	  		
  	  	}
  	  }
  	  $('.'+srcClass+' :selected').each(
            function(){
             $('<option value="'+$(this).val()+'">'+$(this).text()+'</option>').appendTo('.'+destClass);             
            $(this).remove();
          }           
        );
	});
	}
	
	function initSorter(boxClass){
  $("."+boxClass + "Up").click(function() {
   var arr = $('.'+boxClass+' :selected')
	  if(arr.length == 0){
	  	return;
    }
  var index = arr[0].index; 
  if( index == 0){
  	  	return;
  }
 var opt = $($(arr[0]).prev("option")[0]).clone();
 $($(arr[0]).prev("option")[0]).remove();
 $(arr[arr.length -1]).after(opt);
  });
  $("."+boxClass + "Down").click(function() {
   var arr = $('.'+boxClass+' :selected')
    if(arr.length == 0){
      return;
    }
  var index = arr[arr.length  - 1].index;//last element index 
  var boxSize = $("."+boxClass).children("option").length; 
  if( index ==  boxSize - 1){ //
        return;
  }
 var firstSelectedOpt = arr[0];
 var opt = $($(arr[arr.length  - 1]).next("option")[0]).clone();
 $($(arr[arr.length -1]).next("option")[0]).remove();
 $(firstSelectedOpt).before(opt);
  });
 }
 
//$(document).ready( function() {
  initTransferer(srcClass, destClass);
  initTransferer(destClass, srcClass);
  initSorter(srcClass);
  initSorter(destClass);
 $($("."+destClass).parents("form")[0]).bind("submit",function(){
   $('.'+destClass +" option").each(function(){
      $(this).attr('selected','true');
  }
  );
 });

    
  //}
  //)
 }

/**
 * Select all items in a list.
 */

function selectAll(id) {
 $('#'+id+' option').each(
		function() {
			$(this).attr('selected','true');
		}
	);
	return true;	
}
