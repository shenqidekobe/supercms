define([ 'modules/templates/module' ], function(module) {

	"use strict";

	module.registerDirective("edit", function($document) {
		
		return {
			restrict : 'AE',
			require : 'ngModel',
			replace : true,
			scope:{
				type:'@type',
			},
			link : function(scope, element, attrs, ngModel) {
				element.bind("click", function(e) {
					var id = ngModel.$modelValue.id;
					var sortId =null;
					if(ngModel.$modelValue.sort!=null){
						sortId=ngModel.$modelValue.sort.id;
					}
					scope.$emit('editTableRow',{id:id,type:scope.type,sortId:sortId});//向上广播编辑行事件
				});
			}
		}
		
	});
})