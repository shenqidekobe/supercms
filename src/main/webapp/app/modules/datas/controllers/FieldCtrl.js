define([ 'modules/datas/module', 'lodash' ], function(field, _) {

	'use strict';

	field.registerController('FieldCtrl', function($scope, $stateParams, $rootScope, $compile, $templateCache, $filter, resource) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.model = null;
		$scope.field = {
		    id : '',
		    title : '',
		    fieldCode : '',
		    required : false,
		    dataType : 'STRING',
		    dataLength : '',
		    formType : 'TEXT',
		    ordinal : '',
		    modelId : $stateParams.modelId,
		    options : '{}'
		};
		$scope.initialField = angular.copy($scope.field);
		$scope.fieldDialog = {
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
			    data : 'fieldCode'
		    }, {
			    data : 'dataType',
			    render : function(data, type, row) {
			       var rs = "未知";
			       if(data == 'STRING'){
			    	   rs = "字符"
			       }else if(data == 'NUMBER'){
			    	   rs = "数字"
			       }else if(data == 'BOOLEAN'){
			    	   rs = "布尔"
			       }else if(data == 'DATE'){
			    	   rs = "日期"
			       }else if(data == 'CLOB'){
			    	   rs = "字符大对象"
			       }
			       return rs;
		        }
		    }, {
			    data : 'required',
			    render : function(data, type, row) {
			    	return data == true ? '是' : '否';
			    }
		    },{
			    data : 'dataLength'
		    },{
			    data : 'formType'
		    },{
			    data : 'ordinal'
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
			    var data = _.extend(data, {
				    modelId : $stateParams.modelId,
			    });
			    resource.fieldsResource.datatable(data).$promise.then(function(fields) {
				    $scope.datatable.data = fields;
				    callback($scope.datatable);
				    $scope.field = $scope.initialField;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		$scope.loadModelRequest = function() {
			resource.modelsResource.get({
				id : $stateParams.modelId
			}).$promise.then(function(data) {
				$scope.model = data;
			});
		}

		// request create field
		$scope.createFieldRequest = function() {
			new resource.fieldsResource($scope.field).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save field
		$scope.saveFieldRequest = function() {
			$scope.field.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove field
		$scope.removeFieldRequest = function() {
			$scope.field.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#fields_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.field = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitFieldFormHandler = function() {
			var form = angular.element(fieldForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelFieldFormHandler = function() {
			$('#field_dialog').dialog('close');
		}

		// create command handler
		$scope.createFieldHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.fieldDialog.isNew = true;
			$scope.fieldDialog.icon = 'fa fa-plus';
			$scope.fieldDialog.title = '创建字段';
			$scope.field = angular.copy($scope.initialField);
		};

		// save command handler
		$scope.saveFieldHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.fieldDialog.isNew = false;
			$scope.fieldDialog.icon = 'fa fa-edit';
			$scope.fieldDialog.title = '修改字段';
			$scope.rowSelected.$get().then(function(data) {
				$scope.field = data;
			});
		};

		// remove command handler
		$scope.removeFieldHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除字段",
			    content : "是否删除选中的字段？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeFieldRequest();
				}
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#field_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'fields_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------
		$scope.loadModelRequest();
	})
});
