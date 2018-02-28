define(['modules/manuscripts/module'], function (module) {

    'use strict';

    module.registerDirective('duallistboxExpand', function () {
        return {
            restrict: 'AE',
            scope : {
		        items : '='
		    },
            link: function (scope, element, attributes) {
            	scope.$watch('items', function() {
            		
            		alert(scope.items);
            	})
            }
        }
    });
});
