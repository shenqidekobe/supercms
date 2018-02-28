define(['modules/templates/module','lodash'],function(module,_){

    "use strict";
    
    module.registerController('TemplateCtrl',function($scope,$state,$rootScope,$location,$stateParams,templateType,resource,SmallBoxMessage,PAGESIZE){
		if(!angular.isUndefined($rootScope.sessionId)){
			$rootScope.sessionId=$location.search().sessionId;
		}
    	$scope.lists=null;
		$scope.sortId='';
		$scope.templateType=templateType;
		$scope.pager={
				pageth:1,
				rowCount:null,
				pageSize:PAGESIZE
		}
		if(!angular.isUndefined($stateParams.pageth)){
			$scope.pager.pageth=$stateParams.pageth;
		}if(!angular.isUndefined($stateParams.searchSortId)){
			$scope.sortId=$stateParams.searchSortId;
		}
		
		$scope.list = function(sortId,pageth){
			if(!isNaN(sortId)){
				$scope.sortId=sortId;
			}else if(!isNaN(pageth)){
				$scope.pager.pageth=pageth;
			}
			
			var params={ sortId:$scope.sortId,
						 templateType:$scope.templateType,
						 pageth:$scope.pager.pageth,
		        		 pageSize:$scope.pager.pageSize };
			
			resource.templatesResource.datatable(params).$promise.then(function(data) {
				$scope.lists =data.result;
				$scope.pager.rowCount=data.totalCount;
				$stateParams.pageth=$scope.pager.pageth;
				$stateParams.searchSortId=$scope.sortId;
			});
		}
		
		$scope.list(function(){
			$scope.$watch('lists',function(newValue,oldValue, scope){},true);
		});
		
		$scope.create=function(){
			$state.go('app.template.edit',{
				id:null,
				templateType:$scope.templateType,
				sortId:null,
				searchSortId:$scope.sortId,
				pageth:$scope.pager.pageth
			});
		};
		$scope.$on('editTableRow', function(event, args) {
			if(args.type=='template'){
				$state.go('app.template.edit',{
					id:args.id,
					templateType:$scope.templateType,
					sortId:args.sortId,
					searchSortId:$scope.sortId,
					pageth:$scope.pager.pageth
				});
			}
			event.preventDefault();
		});
		$scope.$on('delTableRow', function(event, args) {
			if(args.type=='template'){
				new resource.templatesResource({id:args.id}).$remove().then(function(data) {
					if(data.$resolved){
						SmallBoxMessage.tips({
		 	    			title:"提示",
		 	    			content:"删除成功",
		 	    			timeout:1000
		 	    		});
						$scope.list();
					}
				});
			}
			event.stopPropagation();
		});
    })
});