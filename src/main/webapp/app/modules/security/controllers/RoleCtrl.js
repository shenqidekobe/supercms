define([ 'modules/security/module', 'lodash' ], function(role, _) {

	'use strict';

	role.registerController('RoleCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource, $http) {

		// ------------------
		// Data Definition
		// ------------------
		$scope.allPermissions = [];
		$scope.allFlatPermissions = [];
		$scope.selectedPermissions = [];
		$scope.role = {
		    id : '',
		    name : '',
		    description : ''
		};
		$scope.initialRole = angular.copy($scope.role);
		$scope.roleDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.name;
		        }
		    }, {
			    data : 'description'
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
			    resource.rolesResource.datatable(data).$promise.then(function(roles) {
				    $scope.datatable.data = roles;
				    callback($scope.datatable);
				    $scope.role = $scope.initialRole;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request load all permissions
		$scope.loadPermissionRequest = function() {
			$http.get('/security/permission?load').then(function(permissions) {
				$scope.allPermissions = permissions.data;
			});
		};

		// request load permissions of specified role
		$scope.loadPermissionByRoleRequest = function() {
			resource.rolesResource.permissions({
			    roleId : $scope.role.id,
			}).$promise.then(function(permissions) {
				while($scope.selectedPermissions.length > 0) {
					$scope.selectedPermissions.pop();
				}
				$(permissions).each(function(index, perm){
					$scope.selectedPermissions.push(perm);
				});
			});
		};

		// request create role
		$scope.createRoleRequest = function() {
			new resource.rolesResource($scope.role).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save role
		$scope.saveRoleRequest = function() {
			$scope.role.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove role
		$scope.removeRoleRequest = function() {
			$scope.role.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		$scope.assignPermissionRequest = function() {
			resource.rolesResource.assignPermissions({
			    roleId : $scope.role.id,
			    permissions : $scope.selectedPermissions
			}).$promise.then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#roles_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.role = $scope.rowSelected = table.row('.highlight').data();
			if($scope.role){
				$scope.loadPermissionByRoleRequest();
			}
		};

		// submit form command handler
		$scope.submitRoleFormHandler = function() {
			var form = angular.element(roleForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelRoleFormHandler = function() {
			$('#role_dialog').dialog('close');
		}

		$scope.submitPermissFromionHandler = function() {
			var form = angular.element(permissionForm);
			form.submit();
		}

		$scope.cancelPermissionFormHandler = function() {
			$('#permission_dialog').dialog('close');
		}

		// create command handler
		$scope.createRoleHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.roleDialog.isNew = true;
			$scope.roleDialog.icon = 'fa fa-plus';
			$scope.roleDialog.title = '创建角色';
			$scope.role = angular.copy($scope.initialRole);
		};

		// save command handler
		$scope.saveRoleHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.roleDialog.isNew = false;
			$scope.roleDialog.icon = 'fa fa-edit';
			$scope.roleDialog.title = '修改角色';
			$scope.rowSelected.$get().then(function(data) {
				$scope.role = data;
				$scope.role.repeat = data.password;
			});
		};

		// remove command handler
		$scope.removeRoleHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除角色",
			    content : "是否删除选中的角色？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeRoleRequest();
				}
			});
		};

		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#role_dialog').dialog('close');
			$('#permission_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'roles_table'
			});
		};
		// ------------------
		// Data Initializing
		// ------------------
		$scope.loadPermissionRequest();
		$scope.$watch('allPermissions', function() {
			var result = [];
			var fn = function(result, list) {
				for (var i = 0; i < list.length; i++) {
					result[result.length] = list[i];
					if (list[i].chidldren && list[i].chidldren.length != 0) {
						fn(result, list[i].chidldren);
					}
				}
			};
			fn(result, $scope.allPermissions);
			$scope.allFlatPermissions = result;
		});
		$('#content').on('change', '#lvl1,#lvl2', function() {
			var that = this;
			$(that).parent().parent().find('input').each(function() {
				if ($(that).prop('checked') != $(this).prop('checked')) {
					$(this).click();
				}
			});
		});
	})
});
