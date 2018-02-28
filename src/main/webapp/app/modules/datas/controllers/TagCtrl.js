define([ 'modules/datas/module', 'lodash' ], function(tag, _) {

	'use strict';

	tag.registerController('TagCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.tag = {
		    id : '',
		    title : ''
		};
		$scope.initialTag = angular.copy($scope.tag);
		$scope.tagDialog = {
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
			    resource.tagsResource.datatable(data).$promise.then(function(tags) {
				    $scope.datatable.data = tags;
				    callback($scope.datatable);
				    $scope.tag = $scope.initialTag;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request create tag
		$scope.createTagRequest = function() {
			new resource.tagsResource($scope.tag).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save tag
		$scope.saveTagRequest = function() {
			$scope.tag.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove tag
		$scope.removeTagRequest = function() {
			$scope.tag.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#tags_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.tag = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitTagFormHandler = function() {
			var form = angular.element(tagForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelTagFormHandler = function() {
			$('#tag_dialog').dialog('close');
		}

		// create command handler
		$scope.createTagHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.tagDialog.isNew = true;
			$scope.tagDialog.icon = 'fa fa-plus';
			$scope.tagDialog.title = '创建标签';
			$scope.tag = angular.copy($scope.initialTag);
		};

		// save command handler
		$scope.saveTagHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.tagDialog.isNew = false;
			$scope.tagDialog.icon = 'fa fa-edit';
			$scope.tagDialog.title = '修改标签';
			$scope.rowSelected.$get().then(function(data) {
				$scope.tag = data;
			});
		};

		// remove command handler
		$scope.removeTagHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除标签",
			    content : "是否删除选中的标签？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeTagRequest();
				}
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#tag_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'tags_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------

	})
});
