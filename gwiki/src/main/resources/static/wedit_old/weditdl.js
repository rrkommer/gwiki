
var wedit_domobserver_active = true;
var wedit_domobserver_ignore_next_image = false;
function wedit_register_modifaction_monitor(jnode, weditconfig)
{
	var target = jnode.get(0);

	// create an observer instance
	var observer = new MutationObserver(function(mutations) {
		mutations.forEach(function(mutation) {
			if (wedit_domobserver_active == false) {
				return;
			}
			console.debug(" Mutation : " + mutation.type + "; " + mutation);
			if (mutation.type == 'childList') {
				var target = mutation.target;
				var addedNodes = mutation.addedNodes;
				if (addedNodes.length > 0) {
					var node = addedNodes[0];
					console.debug("mod node: " + node + "; " + node.name);
					if (node instanceof HTMLImageElement) {
						if (wedit_domobserver_ignore_next_image == true) {
							wedit_domobserver_ignore_next_image = false;
							return;
						}
						console.debug("IS HTMLImageElement: " + node + "; " + target + ";parent " + node.parentNode);
						
						weditclipboard_createImage(jnode, weditconfig, node.src);
						if (node.parentNode) {
							node.parentNode.removeChild(node);
						} else {
							console.debug("image parent node missing: " + node);
						}
					}

				}

			}

		});
	});

	// configuration of the observer:
	var config = {
		attributes : true,
		childList : true,
		characterData : true
	}

	// pass in the target node, as well as the observer options
	observer.observe(target, config);

}