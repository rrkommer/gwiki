

function wedit_register_modifaction_monitor(jnode, weditconfig)
{
	var target = jnode.get(0);

	// create an observer instance
	var observer = new MutationObserver(function(mutations) {
		mutations.forEach(function(mutation) {
			console.debug(" Mutation : " + mutation.type + "; " + mutation);
			if (mutation.type == 'childList') {
				var target = mutation.target;
				var addedNodes = mutation.addedNodes;
				if (addedNodes.length > 0) {
					var node = addedNodes[0];
					console.debug("mod node: " + node + "; " + node.name);
					if (node instanceof HTMLImageElement) {
						console.debug("IS HTMLImageElement: " + node.src);
						
						weditclipboard_createImage(jnode, weditconfig, node.src);
						node.parentNode.removeChild(node);
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