(function() {
	tinymce.create('tinymce.plugins.gwikiEditorLevel', {
        createControl: function(n, cm) {
                switch (n) {
                        case 'advancedButton':
                                var c = cm.createButton('advancedButton', {
                                        title : 'Show advanced button bar',
                                        image : gwikiContextPath + gwikiServletPath + '/static/tiny_mce/plugins/gwikieditorlevel/img/plus.png',
                                        icons : false,
                                        onclick : function() {
                                        	if ($('#' + tinyMCE.activeEditor.id + '_toolbar2').is(':visible')) {
                                        		gwikiSaveUserPrev('EDITOR_LEVEL', '0', true);
                                        		$('#' + tinyMCE.activeEditor.id + '_toolbar2').hide('slow');
                                        	} else {
                                        		gwikiSaveUserPrev('EDITOR_LEVEL', '1', true);
                                        		$('#' + tinyMCE.activeEditor.id + '_toolbar2').show('slow');
                                        	}
                                        	
                                        	$('#' + tinyMCE.activeEditor.id + '_toolbar3').hide('slow');
                                        }
                                });

                                return c;
                        case 'expertButton':
                            var c = cm.createButton('expertButton', {
                                    title : 'Show expert button bar',
                                    image : gwikiContextPath + gwikiServletPath + '/static/tiny_mce/plugins/gwikieditorlevel/img/plusplus.png',
                                    icons : false,
                                    onclick : function() {
                                    	if ($('#' + tinyMCE.activeEditor.id + '_toolbar3').is(':visible')) {
                                    		gwikiSaveUserPrev('EDITOR_LEVEL', '1', true);
                                    		$('#' + tinyMCE.activeEditor.id + '_toolbar3').hide('slow');
                                    	} else {
                                    		gwikiSaveUserPrev('EDITOR_LEVEL', '2', true);
                                    		$('#' + tinyMCE.activeEditor.id + '_toolbar3').show('slow');
                                    	}
                                    }
                            });

                            return c;
                }

                return null;
        }
});

// Register plugin with a short name
tinymce.PluginManager.add('gwikiEditorLevel', tinymce.plugins.gwikiEditorLevel);
})();