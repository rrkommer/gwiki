
var pastedImage;
var imageSource;
var imageBlob;
// if (!window.Clipboard) {
// var pasteCatcher = document.getElementById("imguploadpreview");
//    
// // Firefox allows images to be pasted into contenteditable elements
// pasteCatcher.setAttribute("contenteditable", "");
//    
// // We can hide the element and append it to the body,
// pasteCatcher.style.opacity = 0;
//   
// 
// // as long as we make sure it is always in focus
// pasteCatcher.focus();
// document.addEventListener("click", function() { pasteCatcher.focus(); });
// }
// // Add the paste event listener
// window.addEventListener("paste", pasteHandler);
 
/* Handle paste events */
function screenshotPasteHandler(e) {
   // We need to check if event.clipboardData is supported (Chrome)
//	console.debug('screenshotPasteHandler' + e);
   if (e.clipboardData) {
//	   console.debug('screenshotPasteHandler has data' + e);
      // Get the items from the clipboard
      var items = e.clipboardData.items;
      if (items) {
         // Loop through all items, looking for any kind of image
         for (var i = 0; i < items.length; i++) {
           var item = items[i]; 
            if (item.type.indexOf("image") !== -1) {
               // We need to represent the image as a file,
               var blob = item.getAsFile();
               imageBlob = blob;
               // and use a URL or webkitURL (whichever is available to the
				// browser)
               // to create a temporary URL to the object
               var URLObj = window.URL || window.webkitURL;
               var source = URLObj.createObjectURL(blob);
                
               // The URL can then be used as the source of an image
               createImage(source);
//               console.debug('screenshotPasteHandler image created: ' + source);
            }
         }
      }
   // If we can't handle clipboard data directly (Firefox),
   // we need to read what was pasted from the contenteditable element
   } else {
      // This is a cheap trick to make sure we read the data
      // AFTER it has been inserted.
	setTimeout(checkInput, 1);
   }
}
 
/* Parse the input in the paste catcher element */
function checkInput() {
	var pasteCatcher = document.getElementById("imguploadpreview");
	for (var i = 0; i < pasteCatcher.childNodes.length; ++i) {
		var child = pasteCatcher.childNodes[i];
		 if (child.tagName === "IMG") {
			 createImage(child.src);
			 break;
		 }
	}
 
}
 
/* Creates a new image from a given source */
function createImage(source) {
  imageSource = source;
   pastedImage = new Image();
   pastedImage.onload = function() {
      // You now have the image!
   }
   pastedImage.src = source;
}

function storeImage() {
//	console.debug("storeImage");
	if (imageSource === undefined) {
		checkInput();
	}
	if (imageSource === undefined) {
		gwiki_alert('No image found', 'Warning');
		return;
	}
//	console.debug("image found: " + imageSource);
	var fnameel = document.getElementById("imguploadfilename");
	var fname = fnameel.value;
	var blankRE=/^[\s]+$/;
	if (!fname || fname == "" || blankRE.test(fname)) {
		gwiki_alert("Please give a file name", 'Missing file name');
		return;
	}
	
	$.ajax({
		method: 'POST',
		url: 'UploadAttachment', 
		data : {
			method_onUploadImage: true,
			storeTmpFile: false,
			json: true,
			fileName: fname,
			encData: imageSource
		},
		success: function(result){
//			console.debug("image uploaded: " + result);
			 var resobj = eval('(' +result + ')');
//			if (result.rc === 0) {
				gwikieditInsertImageCb(fname, resobj.tmpFileName);
//			}
        },
        error: function(xhr,status,error) {
        	console.error("Upload failed");
        }
        });
	
	
	
}

