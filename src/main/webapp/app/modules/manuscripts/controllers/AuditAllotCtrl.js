define([ 'modules/manuscripts/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('AuditAllotCtrl', function($scope, $state,$stateParams, $compile, $templateCache, $filter, resource,SmallBoxMessage) {
		$scope.sendMembers=null;
		$scope.dataSources=null;
		
		$scope.memberId=$stateParams.memberId;
		$scope.memberName=$stateParams.memberName;
		$scope.memberIds=[];
		$scope.dataSourceIds=[];
		
		
		//读取所有的投稿用户
		$scope.loadMemberRequest = function() {
			new resource.membersResource.auditmembers({auditId:$scope.memberId}).$promise.then(function(data) {
				$scope.sendMembers=data;
				var k=0;
				for(var i=0;i<data.length;i++){
					var obj=data[i];
					if(obj.selected==true){
						$scope.memberIds[k]=obj.id;
						k++;
					}
				}
				console.info($scope.memberIds.length);
			});
		};
		//读取所有的数据源
		$scope.loadDataSourceRequest = function() {
			new resource.membersResource.auditdatasources({auditId:$scope.memberId}).$promise.then(function(data) {
				$scope.dataSources=data;
				var k=0;
				for(var i=0;i<data.length;i++){
					var obj=data[i];
					if(obj.selected==true){
						$scope.dataSourceIds[k]=obj.id;
						k++;
					}
				}
			});
		};
		//返回用户列表
		$scope.back=function(){
			$state.go("app.manuscripts.member");
		}
		//保存选择的数据
		$scope.saveSelected=function(){
			var params={
				auditUserId:$scope.memberId,
				memberIds:angular.isUndefined($scope.memberIds.length)?null:$scope.memberIds,
				dataSourceIds:angular.isUndefined($scope.dataSourceIds.length)?null:$scope.dataSourceIds
			}
			new resource.membersResource(params).$auditallot().then(function() {
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"保存成功",
 	    			timeout:1000
 	    		});
				$scope.back();
			});
		}
		
		$scope.loadMemberRequest();
		$scope.loadDataSourceRequest();
		
		
		
	})
	
});