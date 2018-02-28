define([ 'modules/settings/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('PlugInCtrl', function($state,$scope, $rootScope, $compile, $templateCache, $filter, resource,SmallBoxMessage) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.plugin = {
		    id : '',
		    plugName : '',
		    fieldName : '',
		    plugContent : '',
		    jsDefinition : '',
		    jsDemo : '',
		    attr1 : '',
		};
		$scope.initialplugin = angular.copy($scope.plugin);
		$scope.pluginDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
			    data : 'plugName'
		    }, {
			    data : 'fieldName'
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
			    resource.pluginsResource.datatable(data).$promise.then(function(plugins) {
				    $scope.datatable.data = plugins;
				    callback($scope.datatable);
				    $scope.plugin = $scope.initialplugin;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request create plugin
		$scope.createpluginRequest = function() {
			new resource.pluginsResource($scope.plugin).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save plugin
		$scope.savepluginRequest = function() {
			$scope.plugin.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove plugin
		$scope.removepluginRequest = function() {
			$scope.plugin.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#plugins_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.plugin = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitpluginFormHandler = function() {
			var form = angular.element(pluginForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelpluginFormHandler = function() {
			$('#plugin_dialog').dialog('close');
		}

		// create command handler
		$scope.createpluginHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.pluginDialog.isNew = true;
			$scope.pluginDialog.icon = 'fa fa-plus';
			$scope.pluginDialog.title = '创建插件';
			$scope.plugin =  angular.copy($scope.initialplugin);
		};

		// save command handler
		$scope.savepluginHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.pluginDialog.isNew = false;
			$scope.pluginDialog.icon = 'fa fa-edit';
			$scope.pluginDialog.title = '修改插件';
			$scope.rowSelected.$get().then(function(data) {
				$scope.plugin = data;
			});
		};

		// remove command handler
		$scope.removepluginHandler = function() {
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要删除该条数据吗？",
				callback:$scope.removepluginRequest
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#plugin_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'plugins_table'
			});
		};
		
		//插件的文件管理
		$scope.openPluginFileHandler = function(){
			$state.go("app.settings.pluginfile",{pluginPath:$scope.plugin.fieldName});
		};

	})
});
