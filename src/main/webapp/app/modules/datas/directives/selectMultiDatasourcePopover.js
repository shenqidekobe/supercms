define(['modules/datas/module'], function(module){
    "use strict";

    module.registerDirective('selectMultiDatasourcePopoverPopup', function ($compile,$templateCache) {
        return {
            restrict: "EA",
            replace: true,
            scope: { title: "@", content: "@", placement: "@", animation: "&", isOpen: "&" },
            template: function(element){
            	return '<div class="popover {{placement}}" ng-class="{ in: isOpen(), fade: animation() }" style="height:100%;overflow-x:hidden;overflow-y:auto;"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title" bind-html-unsafe="title" ng-show="title"></h3><div class="popover-content"></div></div></div>'
            },
            link : function(scope,element){
            	var html = $compile($templateCache.get('select-muli-datasource'))(scope.$parent);
            	element.find('.popover-content').empty().append(html);
            }
        };
    });
    module.registerDirective('selectMultiDatasourcePopover',function ($tooltip) {
    	var compileFn = $tooltip("selectMultiDatasourcePopover", "popover", "click");
    	return compileFn;
    });
});
