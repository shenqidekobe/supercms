define([ 'modules/forms/module' ], function(module) {

	'use strict';

	return module.registerDirective('dwSelectTree', function() {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/forms/directives/input/dwSelectTree.tpl.html',
		    scope : {
		        items : '=',
		        currentItem : '=',
		        placeHolder : '@'
		    },
		    link : function(scope, element, attrs, ctrl) {
			    element.find('input').attr('id', attrs['id']).attr('name', attrs['id']);
			    scope.$watch('items', function() {
				    if (scope['items']) {
					    var recursiveFn = function(container, item) {
						    var li = $('<li>').append($('<label>').html(item.name).data('item', item));
						    if (item.childrens && item.childrens.length != 0) {
							    var ul = $('<ul>');
							    li.append(ul);
							    $.each(item.childrens, function(index, child) {
								    recursiveFn(ul, child);
							    });
						    }
						    container.append(li);
					    }
					    var container = element.find('.tree-list').empty();
					    $.each(scope.items, function(index, item) {
						    recursiveFn(container, item);
					    });
				    }
			    });
			    element.on('click', 'label', function() {
				    element.find('input').val($(this).html());
				    scope.currentItem = $(this).data('item');
				    if (element.closest('form').validate) {
					    element.closest('form').validate().element(element.find('input'));
				    }
			    });
			    scope.$watch('currentItem', function(item) {
				    if (item == null) {
					    element.find('input').val('');
				    } else {
					    element.find('input').val(item.name);
				    }
			    });
		    }
		}
	});
});
