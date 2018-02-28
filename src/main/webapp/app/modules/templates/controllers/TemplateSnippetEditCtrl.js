define(['modules/templates/module','lodash'],function(module,_){
	
	"use strict";
	
	module.registerController('TemplateSnippetEditCtrl',function($scope,$state,$stateParams,$location,resource,SmallBoxMessage){
		//form data init
		$scope.snippet={
				id:null,
				snippetName:null,
				snippetTag:null,
				snippetCode:null,
				sortId:null,
		}
		$scope.sortsTree = {
		    data : null,
		    editCurrentItem : null
		};
		
		var id=$stateParams.id;
		
		if(id!='null'&&id!=''&&id!=null){
			resource.snippetsResource.get({id:id}).$promise.then(function(data) {
				$scope.snippet.id=data.id;
				$scope.snippet.snippetName=data.snippetName;
				$scope.snippet.snippetTag=data.snippetTag;
				$scope.snippet.snippetCode=data.snippetCode;
				$scope.snippet.sortId=data.sort.id;
			});
		}
		
		// request sorts
		$scope.loadSortsAsTreeRequest = function() {
			resource.sortsResource.sortsTree({sortType:"template_snippet"}).$promise.then(function(data) {
				$scope.sortsTree.data = data;
			});
		};
		
		$scope.back=function(){
			$state.go('app.template.snippet',{
				searchSortId:$stateParams.searchSortId,
				pageth:$stateParams.pageth
			});
		}
		
		$scope.$on('createSnippet', function(event, args) {
			 $scope.snippet.sortId=$("select[name='sortId']").val();
			 if(id!=null){
				 new resource.snippetsResource($scope.snippet).$save().then(function(data) {
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
				 new resource.snippetsResource($scope.snippet).$create().then(function(data) {
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
		
		$scope.loadSortsAsTreeRequest();
	})
	
})