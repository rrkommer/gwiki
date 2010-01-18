/*
 * JHeartbeat 0.1.1 Beta
 * By Jason Levine (http://www.jasons-toolbox.com)
 * A heartbeat plugin for the jquery library to help keep sessions alive.
 */

 $.jheartbeat = {

    options: {
                url: "",
                delay: -1,
                maxBeats: -1
    },
       
       count: 0,

        beatfunction:  function(){

        },

        timeoutobj:  {
                id: -1
        },
  

      
    set: function(options, onbeatfunction) {
                if (this.timeoutobj.id > -1) {
                        clearTimeout(this.timeoutobj);
                }
        if (options) {
            $.extend(this.options, options);
        }
        if (onbeatfunction) {
            this.beatfunction = onbeatfunction;
        }
      // Add the HeartBeatDIV to the page
       $("body").append("<div id=\"HeartBeatDIV\" style=\"display: none;\"></div>");
       this.timeoutobj.id = setTimeout("$.jheartbeat.beat();", this.options.delay);
    },

    beat: function() {
    	if(this.count >= this.options.maxBeats){
   		    clearTimeout(this.timeoutobj);
   		    return;
    	}
     //$("#HeartBeatDIV").load(this.options.url);
    	if (this.options.url) {
    		$.post(this.options.url);
    	}
     this.timeoutobj.id = setTimeout("$.jheartbeat.beat();", this.options.delay);
     this.beatfunction();
     ++this.count;
    }
};