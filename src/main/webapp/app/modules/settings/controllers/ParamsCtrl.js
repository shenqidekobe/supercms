define([ 'modules/settings/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('ParamsCtrl', function($scope, resource,SmallBoxMessage) {
		$scope.param=null;
		$scope.emailParam= null;
		$scope.recallParam= null;
		$scope.produceHistoryParam = null;
		//加载系统邮箱参数
		$scope.loadEamilParamRequest= function(){
			var data={
				paramCode:"system",
				paramKey:"systemEmail"
			};
			resource.paramsResource.find(data).$promise.then(function(data) {
		    	$scope.emailParam =data;
		    });
		}
		//加载稿件的撤回参数
		$scope.loadRecallParamRequest= function(){
			var data={
				paramCode:"manuscript",
				paramKey:"recallIsRemoveFile"
			};
			resource.paramsResource.find(data).$promise.then(function(data) {
		    	$scope.recallParam =data;
		    	$scope.recallParam.paramVal=data.paramVal=="true"?true:false;
		    });
		}
		//加载生成历史的参数设置
		$scope.loadProduceHistoryParamRequest= function(){
			var data={
				paramCode:"produce",
				paramKey:"isProduceHistory"
			};
			resource.paramsResource.find(data).$promise.then(function(data) {
		    	$scope.produceHistoryParam =data;
		    	$scope.produceHistoryParam.paramVal=data.paramVal=="true"?true:false;
		    });
		}

		$scope.saveEamilHandler = function(){
			//if(event.which === 13) {
			var reg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
		    if(reg.test($scope.emailParam.paramVal)){
		    	$scope.param=$scope.emailParam;
		    	$scope.saveParamRequest();
		    	SmallBoxMessage.tips({
					title:"提示",
					content:"保存成功.",
					timeout:2000
				});
		    }
		};
		$scope.saveRecallParamHandler = function() {
			$scope.param=$scope.recallParam;
			$scope.param.paramVal=$scope.recallParam.paramVal==false?true:false;
			$scope.saveParamRequest();
		};
		$scope.saveProduceHistoryParamHandler = function() {
			$scope.param=$scope.produceHistoryParam;
			$scope.param.paramVal=$scope.produceHistoryParam.paramVal==false?true:false;
			$scope.saveParamRequest();
		};
		
		//保存编辑的参数
		$scope.saveParamRequest = function(){
			new resource.paramsResource($scope.param).$save().then(function(){$scope.param=null;});
		};
		
		$scope.loadEamilParamRequest();
		$scope.loadRecallParamRequest();
		$scope.loadProduceHistoryParamRequest();

	})
});
