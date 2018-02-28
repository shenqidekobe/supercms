define([ 'modules/templates/module', 'lodash' ], function(module, _) {

	"use strict";

	module.registerController('TemplateExplainCtrl', function($scope, resource) {

		$scope.modelList = null;
		$scope.fieldList = null;
		$scope.selectedItem = "";

		//加载所有的数据模型
		$scope.loadModelRequest = function() {
			resource.modelsResource.all().$promise.then(function(models) {
				$scope.modelList = models;
			});
		};
		//根据模型检索其下的所有属性信息
		$scope.loadFieldsRequest = function(modelId){
			resource.fieldsResource.fields({modelId:modelId}).$promise.then(function(fields) {
			    $scope.fieldList = fields;
		    });
		};

		//模型改变事件
		$scope.changeModelHandler = function() {
			var modelId=$scope.selectedItem;
			$scope.loadFieldsRequest(modelId);
		};
		
		$scope.loadModelRequest();
	});
});