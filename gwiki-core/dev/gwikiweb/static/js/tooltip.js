function browsererkennung()
{
	this.version = navigator.appVersion;

	if (document.getElementById)
	{
		this.dom = true;
	}
	else
	{
		this.dom = false;
	}

	if (navigator.appName.indexOf("Internet Explorer") != -1)
	{
		this.ie = 1;
	}
	else
	{
		this.ie =0;
	}

	if ((this.version.indexOf("MSIE 5") > -1) || (this.version.indexOf("MSIE 6")>-1) && this.dom)
	{
		this.ie5 = 1;
	}
	else
	{
		this.ie5 =0;
	}
	
	if (this.version.indexOf("MSIE 7") > -1)
	{
		this.ie7 = 1;
		//alert(this.version + " compatMode " + document.compatMode);
		if (document.compatMode == 'BackCompat') {
			this.ie7std = 0;
		} else {
			this.ie7std = 1;
		}
	}
	else
	{
		this.ie7 = 0;
	}

	if (document.all && !this.dom)
	{
		this.ie4 = 1;
	}
	else
	{
		this.ie4 = 0;
	}

	if (navigator.appName.indexOf("Netscape") != -1)
	{
		this.ns = 1;
	}
	else
	{
		this.ns =0;
	}

    if (this.dom && (parseInt(this.version) >= 5))
	{
		this.ns5 = 1;
	}
	else
	{
		this.ns5 = 0;
	}

	if (document.layers && !this.dom)
	{
		this.ns4 = 1;
	}
	else
	{
		this.ns4 = 0;
	}

	this.myBrowser = (this.ie5 || this.ie4 || this.ns4 || this.ns5);

    return this;
}

myBrowser = new browsererkennung();


	hilfeStartHtml 		= '<table class="tooltip"; style="border: 5px solid #FFE88A; background:#FFE88A;" cellspacing="0" cellpadding="0" width="203"><tr>';
  	hilfeTextStartHtml 	= '<td width="100%" valign="top"><div class="help_box">';
  	hilfeTextEndeHtml 	= '</div></td>';
	hilfeEndeHtml 		= '<td valign="top"><div><img src="now/img/closetooltips.gif" width="17" height="17" alt="Info schlie&szlig;en" /></div></td></tr></table>';

function getObject(theObject)
{
	if (!myBrowser.ns5)
	{
       	if (myBrowser.dom)
       	{
       		this.css	= document.getElementById(theObject).style;
       		this.wref	= document.getElementById(theObject);
       		this.forms	= document.forms;
       		this.images	= document.images;
       	}
       	else if (myBrowser.ie4)
       	{
       		this.css	= document.all[theObject].style;
       		this.wref	= document.all[theObject];
       		this.forms	= document.forms;
       		this.images	= document.images;
       	}
       	else if(myBrowser.ns4)
       	{
       		this.css	= document.layers[theObject];
       		this.wref	= document.layers[theObject].document;
       		this.forms	= document.layers[theObject].document.forms;
       		this.images	= document.layers[theObject].document.images;
       	}
       	else
       	{
       		this.css	= 0;
       		this.wref	= 0;
       		this.forms	= 0;
       		this.images	= 0;
       	}

 		if (myBrowser.ie)
 		{
 			this.object = document.all[theObject];
 		}
 		else
 		{
	 		this.object = document.layers[theObject];
	 	}
	}
	else
	{
		this.css	= document.getElementById(theObject).style;
		this.wref	= document.getElementById(theObject);
		this.forms	= document.forms;
		this.images = document.images;
		this.object	= document.getElementById(theObject);
	}

	this.displayHilfeLayerHtml	= displayHilfeLayerHtml;
	this.moveHilfeLayerTo		= moveHilfeLayerTo;
	this.showHilfeLayer			= showHilfeLayer;
	this.hideHilfeLayer			= hideHilfeLayer;

	return this;
}

function displayHilfeLayerHtml(theText)
{
	if(myBrowser.ns4)
	{
		this.wref.write(theText);
		this.wref.close();
	}
	else
	{
		this.wref.innerHTML = theText;
	}
}

function moveHilfeLayerTo(x,y)
{
	this.css.left 	= x+"px";
	this.css.top 	= y+"px";
}

function showHilfeLayer()
{
  if(notOpen == false){
  	this.css.visibility='visible';
  	this.css.zIndex='200';
  }
}

function hideHilfeLayer()
{
	this.css.visibility='hidden';
	this.css.zIndex='-100';
}

var isLoaded 			= false;
var descx 				= 0;
var descy 				= 0;
var isHilfeLayerVisible = false;
var oldID         = "";
var notOpen       = false;

function updateMousePosition(theEvent)
{
	if (!myBrowser.ns5)
	{
		if (myBrowser.ns4)
		{
			descx = theEvent.pageX;
			descy = theEvent.pageY;
		}
		else
		{
			if (!myBrowser.ie7std)
			{
				descx = event.x;
				descy = event.y;
			}
			else
			{
				descx = event.clientX;
				descy = event.clientY;
			}
		}
	}
	else
	{
		descx = theEvent.pageX;
		descy = theEvent.pageY;
	}

	if(myBrowser.ie5)
	{
		descy = descy + document.body.scrollTop;
		descx = descx + document.body.scrollLeft;
	}
	else if(myBrowser.ie7)
	{
		if (document.documentElement.scrollTop != 0) {
			descy = descy + document.documentElement.scrollTop;
			descx = descx + document.documentElement.scrollLeft;
		} else if (document.body.scrollTop != 0) {
			descy = descy + document.body.scrollTop;
			descx = descx + document.body.scrollLeft;
		}
	}

	if(isHilfeLayerVisible)
	{	
		//myObject.moveHilfeLayerTo(descx + 16, descy - 3);
	}
}

function initTooltips()
{
	if(myBrowser.ns4)
	{
		myObject = new getObject('hilfelayer');
		document.captureEvents(Event.MOUSEMOVE);
		document.onmousemove = updateMousePosition;
		//Close ToolTip
		myObject.wref.onclick = removeHilfeLayer;
		
			
	}
	else if (myBrowser.ns5)
	{
			myObject = new getObject('hilfelayer')
			document.addEventListener("mousemove",updateMousePosition,true);
			//Close ToolTip
		  myObject.wref.addEventListener("click",removeHilfeLayer,true);
		  
	}
	else if(myBrowser.ie)
	{
			myObject = new getObject('hilfelayer')
			document.onmousemove = updateMousePosition;
			//Close ToolTip
		  myObject.wref.onclick = removeHilfeLayer;
		  
	}

	isLoaded = true;
}

function displayHilfeLayer(theMessage,theID)
{
	if(isLoaded)
	{
		removeHilfeLayer();
	  notOpen = false;
	  if(theID != oldID){
	  	isHilfeLayerVisible = false;
	  }
	  
	  if(isHilfeLayerVisible == false){
	  	myObject.displayHilfeLayerHtml(hilfeStartHtml + hilfeTextStartHtml  + theMessage + hilfeTextEndeHtml  + hilfeEndeHtml);
			myObject.moveHilfeLayerTo(descx + 16, descy - 3);								
			setTimeout('myObject.showHilfeLayer()',500);			
			oldID = theID;
			isHilfeLayerVisible = true;	  
			 $(".tooltip").bgiframe(); 
	  }		
	}
}

function removeHilfeLayer()
{
	if (isLoaded)
	{
		myObject.hideHilfeLayer();
	}
	isHilfeLayerVisible = false;
}

function doNotOpenHilfeLayer(){  
	notOpen = true;
	isHilfeLayerVisible = false;
}