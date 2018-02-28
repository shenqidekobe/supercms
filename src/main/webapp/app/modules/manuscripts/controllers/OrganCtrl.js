define([ 'modules/manuscripts/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('OrganCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource,SmallBoxMessage) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.organ = {
		    id : '',
		    organName : '',
		    platformOrganId : '',
		    organLevel : '',
		    manuMaxCount : '',
		    abortTime : null,
		};
		$scope.initialorgan = angular.copy($scope.organ);
		$scope.organDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.levels=null;
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
			    data : 'organName'
		    }, {
			    data : 'organLevel'
		    },
		    {
			    data : 'manuMaxCount'
		    },
		    {
			    data : 'alreadyManuCount'
		    },{
		        data : 'abortTime',
		        render : function(data, type, row) {
			        return $filter('date')(data, 'yyyy-MM-dd');
		        }
		    },{
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
			    resource.organsResource.datatable(data).$promise.then(function(organs) {
				    $scope.datatable.data = organs;
				    callback($scope.datatable);
				    $scope.organ = $scope.initialorgan;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------
		
		//load organ levels
		$scope.loadnLevels = function() {
			resource.organsResource.level().$promise.then(function(organs) {
				$scope.levels = organs;
			});
		};

		// request create organ
		$scope.createorganRequest = function() {
			$scope.organ.abortTime=angular.element("#abortTime").val();
			new resource.organsResource($scope.organ).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save organ
		$scope.saveorganRequest = function() {
			$scope.organ.abortTime=angular.element("#abortTime").val();
			$scope.organ.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove organ
		$scope.removeorganRequest = function() {
			$scope.organ.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#organs_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.organ = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitorganFormHandler = function() {
			var form = angular.element(organForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelorganFormHandler = function() {
			$('#organ_dialog').dialog('close');
		}

		// create command handler
		$scope.createorganHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.organDialog.isNew = true;
			$scope.organDialog.icon = 'fa fa-plus';
			$scope.organDialog.title = '创建单位';
			$scope.organ =  angular.copy($scope.initialorgan);
		};

		// save command handler
		$scope.saveorganHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.organDialog.isNew = false;
			$scope.organDialog.icon = 'fa fa-edit';
			$scope.organDialog.title = '修改单位';
			$scope.rowSelected.$get().then(function(data) {
				$scope.organ = data;
			});
		};

		// remove command handler
		$scope.removeorganHandler = function() {
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要删除该单位信息吗？",
				callback:$scope.removeorganRequest
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#organ_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'organs_table'
			});
		};
		
		$scope.loadnLevels();

	})
});
