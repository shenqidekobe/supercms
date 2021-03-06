define([ 'modules/ui/module', 'lodash', 'jquery-ui' ], function(module, _) {
	'use strict';

	/*
	 * CONVERT DIALOG TITLE TO HTML REF:
	 * http://stackoverflow.com/questions/14488774/using-html-in-a-dialogs-title-in-jquery-ui-1-10
	 */
	$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
		_title : function(title) {
			if (!this.options.title) {
				title.html("&#160;");
			} else {
				title.html(this.options.title);
			}
		}
	}));

	module.registerDirective('smartJquiDialog', function($compile) {

		var optionAttributes = [ 'autoOpen', 'modal', 'width', 'resizable', 'appendTo' ];

		var defaults = {
		    width : Math.min($(window).width() * .7, 600),
		    autoOpen : false,
		    resizable : false
		};

		return {
		    restrict : 'A',
		    link : function(scope, element, attributes) {
			   
		    	var title = $compile(element.find('[data-dialog-title]').remove().html())(scope);

			    var options = _.clone(defaults);

			    optionAttributes.forEach(function(option) {
				    if (element.data(option)) {
					    options[option] = element.data(option);
				    }
			    });

			    var buttons = element.find('[data-dialog-buttons]').remove().find('button').map(function(idx, button) {
				    return {
				        class : button.className,
				        html : button.innerHTML,
				        click : function() {
					        if ($(button).data('action')) {
						        scope.$eval($(button).data('action'));
					        }
				        }
				    }
			    });

			    element.dialog(_.extend({
			        title : title,
			        buttons : buttons
			    }, options));

		    }
		}
	});
});
