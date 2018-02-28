define([ 'modules/security/module', 'lodash' ], function(menu, _) {

	'use strict';

	menu.registerController('MenuCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource,$http) {
		// ------------------
		// Data Definition
		// ------------------
		$scope.menu = {
		    id : '',
		    uisref : '',
		    title : '',
		    icon : '',
		    lvl : '',
		    parentId : ''
		};
		$scope.initialMenu = angular.copy($scope.menu);
		$scope.menuDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.menusTree = {
		    data : null,
		    editCurrentItem : null
		};
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.title;
		        }
		    }, {
			    data : 'uisref'
		    }, {
			    data : 'icon'
		    }, {
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
		    oTreeTable : {
		        showExpander : true,
		        expandable : true,
		        force : true,
		        //initialState : 'expanded',
		        fnPreInit : function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
			        var id = aData.id;
			        var parentId = aData.parentId;
			        if (parentId) {
				        var getParentId = function(parentId, parent) {
					        if (parent.parentId != null) {
						        parentId = parent.parentId + '-' + parentId;
						        return getParentId(parentId, parent.parent);
					        }
					        return parentId;
				        };
				        parentId = getParentId(parentId, aData.parent);
				        id = parentId + '-' + id;
				        $(nRow).attr('data-tt-parent-id', parentId);
			        }
			        $(nRow).attr('data-tt-id', id);
		        }
		    },
		    ajax : function(data, callback, settings) {
			    resource.menusResource.datatable(data).$promise.then(function(menus) {
				    $scope.datatable.data = menus;
				    callback($scope.datatable);
				    $scope.menu = $scope.initialMenu;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request menus as dropdown tree
		$scope.loadMenusAsTreeRequest = function() {
			resource.menusResource.tree().$promise.then(function(data) {
				$scope.menusTree.data = data;
			});
		};

		// request create menu
		$scope.createMenuRequest = function() {
			$scope.menu.parentId = $scope.menusTree.editCurrentItem == null ? '' : $scope.menusTree.editCurrentItem.id;
			new resource.menusResource($scope.menu).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save menu
		$scope.saveMenuRequest = function() {
			$scope.menu.parentId = $scope.menusTree.editCurrentItem == null ? '' : $scope.menusTree.editCurrentItem.id;
			$scope.menu.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove menu
		$scope.removeMenuRequest = function() {
			$scope.menu.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#menus_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.menu = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitMenuFormHandler = function() {
			var form = angular.element(menuForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelMenuFormHandler = function() {
			$('#menu_dialog').dialog('close');
		}

		// create command handler
		$scope.createMenuHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.menuDialog.isNew = true;
			$scope.menuDialog.icon = 'fa fa-plus';
			$scope.menuDialog.title = '创建菜单';
			$scope.menu = angular.copy($scope.initialMenu);
			$scope.menusTree.editCurrentItem = null;
		};

		// save command handler
		$scope.saveMenuHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.menuDialog.isNew = false;
			$scope.menuDialog.icon = 'fa fa-edit';
			$scope.menuDialog.title = '修改菜单';
			$scope.rowSelected.$get().then(function(data) {
				$scope.menu = data;
				$scope.menu.repeat = data.password;
				var currentItem = function(menus, menu) {
					var result = [];
					var fn = function(result, menus) {
						for (var i = 0; i < menus.length; i++) {
							var data = menus[i];
							result[result.length] = data;
							if (data.childrens && data.childrens.length != 0) {
								fn(result, data.childrens);
							}
						}
					};
					fn(result, menus);
					return _.find(result, function(item) {
						return item.id == menu.parentId;
					});
				};
				$scope.menusTree.editCurrentItem = currentItem($scope.menusTree.data, $scope.menu);
			});
		};

		// remove command handler
		$scope.removeMenuHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除菜单",
			    content : "是否删除选中的菜单？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeMenuRequest();
				}
			});
		};
		
		$scope.generatePermissionFileHandler = function(){
			$http.post('/security/permission?generate');
		}
		
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#menu_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'menus_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------
		$scope.loadMenusAsTreeRequest();

	})
});
