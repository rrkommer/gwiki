

$(document).ready(function() {
	$("#btnBunt").click(function() {
		var currange = savedRanges;
		
		console.debug("btnbunt clicked: " + currange);
		if (currange[0]) {
			var range = currange[0];
			var startc = range.startContainer;
			if (startc instanceof Text) {
				
				var starto = range.startOffset;
				var endo = range.endOffset;
				console.debug("IS Textnode: " + starto + " - " + endo + "; " + startc.nodeValue);
				var txt = startc.nodeValue;
				var newtext = txt.substring(0, starto) + "_" + txt.substring(starto, endo) + "_" + txt.substring(endo);
				startc.nodeValue = newtext;
				$(startc).focus();
			} else {
				console.error("Cannot find insert pos in node: " + startc);
			}
			
		}
		
	});
	
});

