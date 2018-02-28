define(['modules/templates/module'], function (module) {

    'use strict';

    module.registerDirective('hrefSort', function () {
        return {
            restrict: 'AE',
            link: function (scope, element, attributes) {
                element.attr('href','#');
                element.on('click', function(e){
                	var id=element.parent().attr("id");
                	var name=element.text();
                	var obj=element.attr("obj");
                	
                	if(obj=="template"){
                		scope.template.sortId=id;
                    	scope.template.sortName=name;
                	}else if(obj=="snippet"){
                		scope.snippet.sortId=id;
                    	scope.snippet.sortName=name;
                	}
                	
                	scope.$emit('closeDialog', {
    				    id : 'smart_sort_tree'
    			    });
                    e.preventDefault();
                })
            }
        }
    });
});
