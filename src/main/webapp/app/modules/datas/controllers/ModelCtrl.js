define([ 'modules/datas/module', 'lodash' ], function(model, _) {

	'use strict';

	model.registerController('ModelCtrl', function($scope, $rootScope,$state, $compile, $templateCache, $filter, resource) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.model = {
		    id : '',
		    title : '',
		    tableCode : ''
		};
		$scope.initialModel = angular.copy($scope.model);
		$scope.modelDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.title;
		        }
		    }, {
			    data : 'tableCode'
		    }, {
		        data : 'createTime',
		        render : function(data, type, row) {
			        return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss');
		        }
		    }, {
		        data : 'updateTime',
		        render : function(data, type, row) {
			        return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss');
		        }
		    } ],
		    pageLength : 10000,
		    ajax : function(data, callback, settings) {
			    resource.modelsResource.datatable(data).$promise.then(function(models) {
				    $scope.datatable.data = models;
				    callback($scope.datatable);
				    $scope.model = $scope.initialModel;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request create model
		$scope.createModelRequest = function() {
			new resource.modelsResource($scope.model).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save model
		$scope.saveModelRequest = function() {
			$scope.model.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove model
		$scope.removeModelRequest = function() {
			$scope.model.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#models_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.model = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitModelFormHandler = function() {
			var form = angular.element(modelForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelModelFormHandler = function() {
			$('#model_dialog').dialog('close');
		}

		// create command handler
		$scope.createModelHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.modelDialog.isNew = true;
			$scope.modelDialog.icon = 'fa fa-plus';
			$scope.modelDialog.title = '创建模型';
			$scope.model = angular.copy($scope.initialModel);
		};

		// save command handler
		$scope.saveModelHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.modelDialog.isNew = false;
			$scope.modelDialog.icon = 'fa fa-edit';
			$scope.modelDialog.title = '修改模型';
			$scope.rowSelected.$get().then(function(data) {
				$scope.model = data;
			});
		};

		// remove command handler
		$scope.removeModelHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除模型",
			    content : "是否删除选中的模型？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeModelRequest();
				}
			});
		};
		
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#model_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'models_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------
	})
});
