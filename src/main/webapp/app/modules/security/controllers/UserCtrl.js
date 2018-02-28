define([ 'modules/security/module', 'lodash' ], function(user, _) {

	'use strict';

	user.registerController('UserCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource) {
		// ------------------
		// Data Definition
		// ------------------
		$scope.datasources = [];
		$scope.selectedDatasources = [];
		$scope.roles = [];
		$scope.user = {
		    id : '',
		    name : '',
		    username : '',
		    password : '',
		    repeat : '',
		    email : '',
		    telephone : '',
		    description : ''
		};
		$scope.initialUser = angular.copy($scope.user);
		$scope.userDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.datasourcesTree = {
		    checkableData : null
		};
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.name;
		        }
		    }, {
		        data : 'roleId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.role.name;
		        }
		    }, {
			    data : 'username'
		    }, {
			    data : 'email'
		    }, {
			    data : 'telephone'
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
			    resource.usersResource.datatable(data).$promise.then(function(users) {
				    $scope.datatable.data = users;
				    callback($scope.datatable);
				    $scope.user = $scope.initialUser;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------
		/**
		$scope.loadDatasourcesRequest = function() {
			resource.datasourcesResource.all().$promise.then(function(data) {
				$scope.datasources = data;
			});
		}
		*/
		$scope.loadDatasourcesRequest = function() {
			resource.datasourcesResource.sortsTree().$promise.then(function(data) {
				var orginSorsTreeData = _.clone(data, true);
				resource.datasourcesResource.all().$promise.then(function(data) {
					var map = {};
					for (var i = 0; i < data.length; i++) {
						var datasource = data[i];
						if (!map[datasource.sort.id]) {
							map[datasource.sort.id] = [];
						}
						map[datasource.sort.id].push(datasource);
					}
					var map1 = _.clone(map, true);
					var fn1 = function(sorts) {
						for (var j = 0; j < sorts.length; j++) {
							var sort = sorts[j];
							sort.type = 'SORT';
							var childrens = _.clone(sort.childrens);
							if (!map1[sort.id]) {
								map1[sort.id] = [];
							}
							for (var k = 0; k < map1[sort.id].length; k++) {
								var datasource = map1[sort.id][k];
								sort.childrens.push({
								    id : datasource.id,
								    name : datasource.title,
								    parentId : sort.id,
								    type : 'DATASOURCE',
								    children : [],
								    content : $('<span><label class="checkbox inline-block"><input id="datasourceId" name="datasourceId" type="checkbox" value="' + datasource.id + '"/><i></i>' + datasource.title + '</label></span>').data('item', datasource),
								    expanded : false
								});
							}
							if (sort.childrens && sort.childrens.length != 0) {
								sort.content = '<span><i class="fa fa-lg fa-minus-circle"></i> ' + sort.name + '</span>';
								sort.expanded = false;
							} else {
								sort.content = '<span><i class="icon-leaf"></i>' + sort.name + '</span>';
								sort.expanded = false;
							}
							sort.children = sort.childrens;
							if (childrens) {
								fn1(childrens);
							}
						}
					}
					fn1(orginSorsTreeData);
					$scope.datasourcesTree.checkableData = orginSorsTreeData;
				});
			});
			$('#datasourceTree').on('click', 'span', function(e) {
				if (e.target.tagName == 'SPAN' && $(this).data('item')) {
					$('#datasourceTree span').removeClass('active').removeClass('btn').removeClass('btn-default');
					$(this).addClass('active').addClass('btn').addClass('btn-default');
					$scope.datasourcesTree.currentItem = $(this).data('item') ? $(this).data('item') : null;
					$scope.filter.datasourceId = $scope.datasourcesTree.currentItem ? $scope.datasourcesTree.currentItem.id : null;
					$scope.filter.title = null;
					$scope.filter.startTime = null;
					$scope.filter.endTime = null;
					$scope.filter.checkStatus = 'PASS';
					$scope.loadModelRequest();
				}
			});
		};
		

		$scope.loadDatasourcesByUserRequest = function() {
			resource.usersResource.datasources({
				userId : $scope.user.id,
			}).$promise.then(function(datasourceIds) {
				$('input[id=datasourceId]').prop('checked', false);
				$scope.selectedDatasources = [];
				$(datasourceIds).each(function(index, datasourceId) {
					$('input[id=datasourceId][value='+datasourceId+']').prop('checked', true);
				});
			});
		};

		$scope.loadRolesRequest = function() {
			resource.rolesResource.all().$promise.then(function(data) {
				$scope.roles = data;
			});
		}

		// request create user
		$scope.createUserRequest = function() {
			new resource.usersResource($scope.user).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save user
		$scope.saveUserRequest = function() {
			$scope.user.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove user
		$scope.removeUserRequest = function() {
			$scope.user.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		$scope.assignDatasourceRequest = function() {
			$('input[id=datasourceId]:checked').each(function(){
				$scope.selectedDatasources.push(parseInt($(this).val()));
			});
			resource.usersResource.assignDatasources({
			    userId : $scope.user.id,
			    datasourceIds : $scope.selectedDatasources
			}).$promise.then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#users_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.user = $scope.rowSelected = table.row('.highlight').data();
			if ($scope.user) {
				$scope.loadDatasourcesByUserRequest();
			}
		};

		// submit form command handler
		$scope.submitUserFormHandler = function() {
			var form = angular.element(userForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelUserFormHandler = function() {
			$('#user_dialog').dialog('close');
		}

		$scope.submitDatasourceFromionHandler = function() {
			var form = angular.element(assignDatasourceForm);
			form.submit();
		}

		$scope.cancelDatasourceFormHandler = function() {
			$('#assign_datasource_dialog').dialog('close');
		}

		// create command handler
		$scope.createUserHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.userDialog.isNew = true;
			$scope.userDialog.icon = 'fa fa-plus';
			$scope.userDialog.title = '创建用户';
			$scope.user = angular.copy($scope.initialUser);
		};

		// save command handler
		$scope.saveUserHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.userDialog.isNew = false;
			$scope.userDialog.icon = 'fa fa-edit';
			$scope.userDialog.title = '修改用户';
			$scope.rowSelected.$get().then(function(data) {
				$scope.user = data;
				$scope.user.repeat = data.password;
			});
		};

		// remove command handler
		$scope.removeUserHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除用户",
			    content : "是否删除选中的用户？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeUserRequest();
				}
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#user_dialog').dialog('close');
			$('#assign_datasource_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'users_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------
		$scope.loadDatasourcesRequest();
		$scope.loadRolesRequest();
	})
});
