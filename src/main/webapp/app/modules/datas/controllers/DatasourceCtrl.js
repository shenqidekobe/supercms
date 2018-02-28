define([ 'modules/datas/module', 'lodash' ], function(datasource, _) {

	'use strict';

	datasource.registerController('DatasourceCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.datasource = {
		    id : '',
		    title : '',
		    description : '',
		    modelId : '',
		    origin : '',
		    sortId : '',
		    passStatus : 'NO'
		};
		$scope.initialDatasource = angular.copy($scope.datasource);
		$scope.datasourceDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.sortsTree = {
		    data : null,
		    searchCurrentItem : null,
		    editCurrentItem : null
		};
		$scope.models = [];
		$scope.origins = [ {
		    'name' : '系统录入',
		    'value' : 'SYSTEM'
		}, {
		    'name' : '内容中心',
		    'value' : 'CONTENT_CENTER'
		}, {
		    'name' : '投稿',
		    'value' : 'PUBLICATION'
		}, {
		    'name' : '采集',
		    'value' : 'GATHER'
		} ];
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.title;
		        }
		    }, {
			    data : 'modelId',
			    render: function(data, type, row){
			    	return '[' + data + '] ' + row.model.title;
			    }
		    }, {
			    data : 'origin',
			    render: function(data, type, row){
			    	var rendered = '';
			    	switch (data) {
                    case 'SYSTEM':
                    	rendered = '系统录入';
	                    break;
                    case 'CONTENT_CENTER':
                    	rendered = '内容中心';
	                    break;
                    case 'PUBLICATION':
                    	rendered = '投稿';
	                    break;
                    case 'GATHER':
                    	rendered = '采集';
	                    break;
                    default:
	                    break;
                    }
			    	return rendered;
			    }
		    }, {
		        data : 'sortId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.sort.sortName;
		        }
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
		    ajax : function(data, callback, settings) {
		    	var data = _.extend(data, {
				    sortId : ($scope.sortsTree && $scope.sortsTree.searchCurrentItem) ? $scope.sortsTree.searchCurrentItem.id : ''
			    });
			    resource.datasourcesResource.datatable(data).$promise.then(function(datasources) {
				    $scope.datatable.data = datasources;
				    callback($scope.datatable);
				    $scope.datasource = $scope.initialDatasource;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request sorts
		$scope.loadSortsAsTreeRequest = function() {
			resource.datasourcesResource.sortsTree().$promise.then(function(data) {
				$scope.sortsTree.data = data;
				$scope.$watch('sortsTree.searchCurrentItem', function(item) {
					if (item) {
						$('#datasources_table').DataTable().ajax.reload(null, true);
					}
				});
			});
		};
		
		// request models
		$scope.loadModelsRequest = function(){
			resource.modelsResource.all().$promise.then(function(data) {
				$scope.models = data;
			});
		}

		// request create datasource
		$scope.createDatasourceRequest = function() {
			$scope.datasource.sortId = $scope.sortsTree.editCurrentItem.id;
			new resource.datasourcesResource($scope.datasource).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save datasource
		$scope.saveDatasourceRequest = function() {
			$scope.datasource.sortId = $scope.sortsTree.editCurrentItem.id;
			$scope.datasource.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove datasource
		$scope.removeDatasourceRequest = function() {
			$scope.datasource.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#datasources_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.datasource = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitDatasourceFormHandler = function() {
			var form = angular.element(datasourceForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelDatasourceFormHandler = function() {
			$('#datasource_dialog').dialog('close');
		}

		// create command handler
		$scope.createDatasourceHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.datasourceDialog.isNew = true;
			$scope.datasourceDialog.icon = 'fa fa-plus';
			$scope.datasourceDialog.title = '创建数据源';
			$scope.datasource = angular.copy($scope.initialDatasource);
			$scope.sortsTree.editCurrentItem = null;
		};

		// save command handler
		$scope.saveDatasourceHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.datasourceDialog.isNew = false;
			$scope.datasourceDialog.icon = 'fa fa-edit';
			$scope.datasourceDialog.title = '修改数据源';
			$scope.rowSelected.$get().then(function(data) {
				$scope.datasource = data;
				var result = [];
				var fn = function(result, list) {
					for (var i = 0; i < list.length; i++) {
						result[result.length] = list[i];
						if (list[i].childrens && list[i].childrens.length != 0) {
							fn(result, list[i].childrens);
						}
					}
				};
				fn(result, $scope.sortsTree.data);
				$scope.sortsTree.editCurrentItem = _.find(result, function(item) {
					return item.id == $scope.datasource.sortId;
				});
			});
		};

		// remove command handler
		$scope.removeDatasourceHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除数据源",
			    content : "是否删除选中的数据源？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeDatasourceRequest();
				}
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#datasource_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'datasources_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------

		$scope.loadSortsAsTreeRequest();
		$scope.loadModelsRequest();
	})
});
