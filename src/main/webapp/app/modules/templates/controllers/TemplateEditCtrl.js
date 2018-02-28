define(['modules/templates/module','lodash'],function(module,_){
	
	"use strict";
	
	module.registerController('TemplateEditCtrl',function($scope,$state,$stateParams,$location,resource,SmallBoxMessage){
		//form data init
		$scope.templateType=$stateParams.templateType;
		$scope.template={
				id:'',
				templateName:'',
				templateCode:'',
				templateType:$stateParams.templateType,
				sortId:'',
				sortName:''
		}
		var id=$stateParams.id;
		
		if(id!=null){
			resource.templatesResource.get({id:id}).$promise.then(function(data) {
				$scope.template.id=data.id;
				$scope.template.templateName=data.templateName;
				$scope.template.templateCode=data.templateCode;
				$scope.template.templateType=data.templateType;
				if(!angular.isUndefined(data.sort)){
					$scope.template.sortId=data.sort.id;
					$scope.template.sortName=data.sort.sortName;
				}
			});
		}
		
		
		$scope.back=function(){
			if(angular.isUndefined($scope.template.templateType)){
				$state.go("app.dashboard");
				return;
			}
			var state="app.template."+$scope.template.templateType;
			$state.go(state,{
				searchSortId:$stateParams.searchSortId,
				pageth:$stateParams.pageth
			});
		}
		
		
		$scope.$on('createTemplate', function(event, args) {
			 $scope.template.sortId=$("select[name='sortId']").val();
			 if(id!=null){
				 new resource.templatesResource($scope.template).$save().then(function(data) {
					if(data.$resolved){
						SmallBoxMessage.tips({
		 	    			title:"提示",
		 	    			content:"修改成功",
		 	    			timeout:1000
		 	    		});
						$scope.back();
					}
				});
			 }else{
				 new resource.templatesResource($scope.template).$create().then(function(data) {
					if(data.$resolved){
						SmallBoxMessage.tips({
		 	    			title:"提示",
		 	    			content:"保存成功",
		 	    			timeout:1000
		 	    		});
						$scope.back();
					}
				});
			 }
		 });
	})
	
})