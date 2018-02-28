define([ 'modules/widgets/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('EditSortCtrl', function($scope, $rootScope,resource,SmallBoxMessage) {
		
		$scope.sort = {
		    id : '',
		    parentId : '',
		    parentName:'',
		    sortType:'template_content',
		    sortName : '',
		};
		$scope.initialSort = angular.copy($scope.sort);

		$scope.sortDialog = {
		    isNew : true,
		    icon : 'fa fa-plus',
		    title : '创建分类'
		};
		
		$scope.refreshTreeview=function(){
			$rootScope.treeview=Math.random();
		}
		
		// request create Sort
		$scope.createSortRequest = function() {
			if($scope.sort.id!=''){
				 new resource.sortsResource($scope.sort).$save().then(function(data) {
					if(data.$resolved){
						SmallBoxMessage.tips({
		 	    			title:"提示",
		 	    			content:"修改成功",
		 	    			timeout:1000
		 	    		});
						$scope.cancelSortFormHandler();
		 	    		$scope.refreshTreeview();
					}
				});
			 }else{
				 new resource.sortsResource($scope.sort).$create().then(function(data) {
					if(data.$resolved){
						SmallBoxMessage.tips({
		 	    			title:"提示",
		 	    			content:"保存成功",
		 	    			timeout:1000
		 	    		});
						$scope.cancelSortFormHandler();
		 	    		$scope.refreshTreeview();
					}
				});
			 }
		};

		// request remove Sort
		$scope.removeSortRequest = function() {
			new resource.sortsResource({id:$scope.sort.id}).$remove().then(function(data) {
				if(data.$resolved){
					SmallBoxMessage.tips({
	 	    			title:"提示",
	 	    			content:"删除成功",
	 	    			timeout:1000
	 	    		});
					$scope.refreshTreeview();
				}
			});
		};


		$scope.submitSortFormHandler = function() {
			var form = angular.element(sortForm);
			var valid=form.valid();
			if(valid){
			    $scope.createSortRequest();	
			}
		}

		$scope.cancelSortFormHandler = function() {
			$('#sort_dialog').dialog('close');
		}
		
		$scope.$watch('sortOper', function(obj, oldObj) {
			//console.log("watch sortContextMenu Oper params:"+angular.toJson(obj));
			if(!angular.isUndefined(obj)&&obj!=oldObj){
				var oper=(obj.sortOper);
				if(oper=="add"){
					$scope.sort.parentId=obj.parentId;
					$scope.sort.sortType=obj.sortType;
					$scope.sort.sortName='';
					$scope.sort.id='';
				}else if(oper=="edit"){
					$scope.sort.id=obj.id;
					$scope.sort.sortType=obj.sortType;
					$scope.sort.sortName=obj.sortName;
				}else if(oper=="remove"){
					$scope.sort.id=obj.id;
					SmallBoxMessage.confirm({
						title:"确认框",
						content:"您确定要删除当前分类吗？",
						callback:$scope.removeSortRequest,
					});
				}
			}
		},true);

	})
});
