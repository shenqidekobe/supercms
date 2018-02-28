define([ 'modules/datas/module', 'lodash' ], function(model, _) {

	'use strict';

	model.registerController('ModelFormCtrl', function($scope, $rootScope, $state, $compile, $templateCache, $filter, $stateParams, resource) {
		$scope.model = {
			formCode : null
		};
		$scope.loadModelRequest = function() {
			resource.modelsResource.get({
				id : $stateParams.modelId
			}).$promise.then(function(model) {
				$scope.model = model;
				$scope.model.formCode = !!model.formCode ? model.formCode : model.defaultFormCode;
			});
		};
		$scope.saveModelFormRequest = function() {
			$scope.model.$save();
			$state.go('app.datas.model');
		};
		$scope.loadModelRequest();
	})
});
