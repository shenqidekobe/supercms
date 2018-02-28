define([ 'modules/templates/module'], function(module) {

	"use strict";

	module.registerDirective("delete", function($document,SmallBoxMessage) {
		
		return {
			restrict : 'AE',
			require : 'ngModel',
			replace : true,
			scope:{
				type:'@type'
			},
			link : function(scope, element, attrs, ngModel) {
				var del=function(){
					var id = ngModel.$modelValue.id;
					scope.$emit("delTableRow",{id:id,type:scope.type});//向上广播删除行事件
				};
			
				element.bind("click", function(e) {
					element.parent().parent().css("background-color", "#e5e5e5");
					SmallBoxMessage.confirm({
						title:"确认框",
						content:"您确定要删除该条数据吗？",
						callback:del,
						cancel:function(){
							element.parent().parent().css("background-color", "#FFFFFF");
						}
					});
				});
			}
		}
		
	});
})