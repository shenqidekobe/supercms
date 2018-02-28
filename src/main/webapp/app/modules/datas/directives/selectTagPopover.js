define(['modules/datas/module', 'lodash'], function(module, _){
    "use strict";

    module.registerDirective('selectTagPopoverPopup', function ($compile,$templateCache, resource) {
        return {
            restrict: "EA",
            replace: true,
            scope: { title: "@", content: "@", placement: "@", animation: "&", isOpen: "&" },
            template: function(element){
            	return '<div class="popover {{placement}}" ng-class="{ in: isOpen(), fade: animation() }"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title" bind-html-unsafe="title" ng-show="title"></h3><div class="popover-content"></div></div></div>'
            },
            link : function(scope,element){
            	var html = $compile($templateCache.get('select-tag'))(scope.$parent);
            	element.find('.popover-content').empty().append(html);
            	var tags = scope.$parent.tags;
            	var select = html.find('#select2');
            	
            	resource.tagsResource.owns({
            		id: scope.$parent.data.id,
            		datasourceId: scope.$parent.datasource.id
            	}).$promise.then(function(data){
            		for (var i = 0; i < tags.length; i++) {
            			var tag = tags[i];
            			var e = _.find(data, function(d){
            				return tag.id == d.id
            			});
            			if(e){
            				select.append("<option selected='selected' value='"+tag.id+"'>"+tag.title+"</option>");
            			}else{
            				select.append("<option value='"+tag.id+"'>"+tag.title+"</option>");
            				
            			}
            		}
            		select.select2({
            			width : '250px'
            		});
            		$('.select2-hidden-accessible').remove();
            	});
            }
        };
    });
    module.registerDirective('selectTagPopover',function ($tooltip) {
    	var compileFn = $tooltip("selectTagPopover", "popover", "click");
    	return compileFn;
    });
});
