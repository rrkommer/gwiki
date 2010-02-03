/**
 * original taken from jquery.indent-1.0.js
 * 
 * usages
 * $('selector').indent(); //default values are taken: spaceForTabs = false, tabWidth =1
 * 
 * $('selector').indent({spaceForTabs:true, tabWidth:2});//use two spaces for a tab
 * 
 * 
 */
(function($) {
$.fn.indent = function(options)
    {
	return this.each(function() {
		var lb = '\n';
		var defaults = {
				spaceForTabs: false,
				tabWidth: 1
                	};
				options = $.extend(defaults, options);
                var indentSymbol = String.fromCharCode(9);
                if(options.spaceForTabs == true){
                    indentSymbol = String.fromCharCode(32);
                }
                var replacement = indentSymbol;
                for(var i = 0; i < options.tabWidth - 1; ++i){
                    replacement = replacement + indentSymbol;
                }
                
         this.tabSymbol = replacement;
        //textarea = $(this)
        //textarea.tabSymbol = replacement;
		
        if(!$.browser.opera) $(this).keydown(key_handler);
		else $(this).keypress(key_handler); // opera fix

		if($.browser.msie || $.browser.opera) lb = '\r\n';
	
		
		function key_handler(e)
		{
		
			textarea = $(this); 
			textarea.tabSymbol = this.tabSymbol; 
			
		if(e.keyCode == 13) {
			var start = selection_range().start;
			var line = textarea[0].value.substring(0,start).lastIndexOf('\n');
			line = (line == -1 ? 0 : line + textarea.tabSymbol.length);
                        var re = new RegExp("^" + textarea.tabSymbol + "+","g");
			var matches = textarea[0].value.substring(line,start).match(re);
			if(matches != null)
			{
				e.preventDefault();
				var scroll_fix = fix_scroll_pre(textarea);
				var tabs = lb;
				for(var i = 0;i < matches[0].length;i++) tabs += textarea.tabSymbol;
				textarea[0].value = textarea[0].value.substring(0,start) + tabs + textarea[0].value.substring(start);
				set_focus(start + tabs.length,start + tabs.length);
				fix_scroll(textarea, scroll_fix);
			}
		}
		else if(e.keyCode == 9)
		{
			e.preventDefault();

			var scroll_fix = fix_scroll_pre(textarea);

			var range = selection_range();

			if(range.start != range.end && textarea[0].value.substr(range.start, textarea.tabSymbol.length) == '\n') range.start++;

			var matches = textarea[0].value.substring(range.start,range.end).match(/\n/g); // check if multiline

			if(matches != null)
			{
				var index = textarea[0].value.substring(0,range.start).lastIndexOf(lb);
				var start_tab = (index != -1 ? index : 0);

				if(!e.shiftKey)
				{
					var tab = textarea[0].value.substring(start_tab,range.end).replace(/\n/g,'\n' + textarea.tabSymbol);

					textarea[0].value = (index == -1 ? textarea.tabSymbol : '') + textarea[0].value.substring(0, start_tab) + tab + textarea[0].value.substring(range.end);

					set_focus(range.start + textarea.tabSymbol.length  ,range.end + matches.length +  textarea.tabSymbol.length);
				}
				else
				{
					var i = (textarea[0].value.substr((index != -1 ? index + lb.length : 0), textarea.tabSymbol.length) ==  textarea.tabSymbol ?  textarea.tabSymbol.length : 0);
                                        var re = new RegExp("\n" + textarea.tabSymbol,"g");
					var removed = textarea[0].value.substring(start_tab,range.end).match(re,'\n');
                                        if(index == -1 && textarea[0].value.substr(0, textarea.tabSymbol.length) == textarea.tabSymbol)
                                            {
						textarea[0].value = textarea[0].value.substr(textarea.tabSymbol.length);
						removed.push(0); // null problem in IE 7
                                            }

					var tab = textarea[0].value.substring(start_tab,range.end).replace(re,'\n');

					textarea[0].value = textarea[0].value.substring(0,start_tab) + tab + textarea[0].value.substring(range.end);

					set_focus(range.start - i,range.end - (removed != null ? removed.length : 0));
				}
			}
			else
			{
				if(!e.shiftKey)
				{
					textarea[0].value = textarea[0].value.substring(0,range.start) + textarea.tabSymbol  + textarea[0].value.substring(range.start);
					set_focus(range.start + textarea.tabSymbol.length, range.start + textarea.tabSymbol.length);
				}
				else
				{
					var i_o = textarea[0].value.substring(0,range.start).lastIndexOf('\n'); // index open (start line) -1 = first line
					var i_s = (i_o == -1 ? 0 : i_o); // index start line

					var i_e = textarea[0].value.substring(i_s + textarea.tabSymbol.length).indexOf('\n'); // index end of line -1 = last line
					if(i_e == -1)
                                            i_e = textarea[0].value.length;
					else
                                            i_e += i_s + 1;

					if(i_o == -1)
                                            {
                                                var re = new RegExp("^" + textarea.tabSymbol + "");
                                                var match = textarea[0].value.substring(i_s,i_e).match(re);
						var tab = textarea[0].value.substring(i_s,i_e).replace(re,'');
                                            }
					else
                                            {
                                                var re = new RegExp("\n" + textarea.tabSymbol + "");
						var match = textarea[0].value.substring(i_s,i_e).match(re);
						var tab = textarea[0].value.substring(i_s,i_e).replace(re,'\n');
					}

					textarea[0].value = textarea[0].value.substring(0,i_s) + tab + textarea[0].value.substring(i_e);

					if(match != null)
                        set_focus(range.start - (range.start - textarea.tabSymbol.length > i_o ? textarea.tabSymbol.length : 0),range.end - ((range.start - textarea.tabSymbol.length > i_o || range.start != range.end) ? textarea.tabSymbol.length : 0));
				}
			}

fix_scroll(textarea, scroll_fix);
		}
	}

	function fix_scroll_pre(textarea)
	{//	textarea = $(this);
		return {
		
			scrollTop:textarea.scrollTop(),
			scrollHeight:textarea[0].scrollHeight
		}
	}

	function fix_scroll(textarea, obj)
	{	
		textarea.scrollTop(obj.scrollTop + textarea[0].scrollHeight - obj.scrollHeight);
	}

	function set_focus(start,end)
	{
		if(!$.browser.msie)
		{
			textarea[0].setSelectionRange(start,end);
			textarea.focus();
		}
		else
		{
			var m_s = textarea[0].value.substring(0,start).match(/\r/g);
			m_s = (m_s != null ? m_s.length : 0);
			var m_e = textarea[0].value.substring(start,end).match(/\r/g);
			m_e = (m_e != null ? m_e.length : 0);

			var range = textarea[0].createTextRange();
			range.collapse(true);
			range.moveStart('character', start - m_s);
			range.moveEnd('character', end - start - m_e);
			range.select();
		}
	};

	function selection_range()
	{
		if(!$.browser.msie)
		{
			return {start: textarea[0].selectionStart,end: textarea[0].selectionEnd}
		}
		else
 		{
			var selection_range = document.selection.createRange().duplicate();

			var before_range = document.body.createTextRange();
			before_range.moveToElementText(textarea[0]);                    // Selects all the text
			before_range.setEndPoint("EndToStart", selection_range);     // Moves the end where we need it

			var after_range = document.body.createTextRange();
			after_range.moveToElementText(textarea[0]);                     // Selects all the text
			after_range.setEndPoint("StartToEnd", selection_range);      // Moves the start where we need it

			var before_finished = false, selection_finished = false, after_finished = false;
			var before_text, untrimmed_before_text, selection_text, untrimmed_selection_text, after_text, untrimmed_after_text;

			before_text = untrimmed_before_text = before_range.text;
			selection_text = untrimmed_selection_text = selection_range.text;
			after_text = untrimmed_after_text = after_range.text;

			do {
			  if (!before_finished) {
			      if (before_range.compareEndPoints("StartToEnd", before_range) == 0) {
			          before_finished = true;
			      } else {
			          before_range.moveEnd("character", -1)
			          if (before_range.text == before_text) {
			              untrimmed_before_text += "\r\n";
			          } else {
			              before_finished = true;
			          }
			      }
			  }
			  if (!selection_finished) {
			      if (selection_range.compareEndPoints("StartToEnd", selection_range) == 0) {
			          selection_finished = true;
			      } else {
			          selection_range.moveEnd("character", -1)
			          if (selection_range.text == selection_text) {
			              untrimmed_selection_text += "\r\n";
			          } else {
			              selection_finished = true;
			          }
			      }
			  }
			  if (!after_finished) {
			      if (after_range.compareEndPoints("StartToEnd", after_range) == 0) {
			          after_finished = true;
			      } else {
			          after_range.moveEnd("character", -1)
			          if (after_range.text == after_text) {
			              untrimmed_after_text += "\r\n";
			          } else {
			              after_finished = true;
			          }
			      }
			  }

			} while ((!before_finished || !selection_finished || !after_finished));

			return {start:untrimmed_before_text.length,end:untrimmed_before_text.length + untrimmed_selection_text.length};
		}
	}
    });
	};
})(jQuery);