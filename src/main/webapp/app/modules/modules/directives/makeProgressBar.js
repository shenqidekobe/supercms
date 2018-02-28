define(['modules/templates/module','bootstrap-progressbar'], function (module) {

    'use strict';

    module.registerDirective('makeProgressBar', function () {
        return {
        	 restrict: 'AE',
        	 link: function (scope,element, attrs) {
        		 var loadProgressbar=function(){
        			 element.removeAttr('smart-progressbar data-smart-progressbar');
            		 element.progressbar({
                         display_text : 'fill',
                     });
        		 }
        		 scope.$watch(scope.progress, function (newValue,oldValue, scope) {
        			 console.info("newValue = "+newValue+";oldValue = "+oldValue);
        			 loadProgressbar();
                 },true);
        		 
             }
        }
    });
   
});