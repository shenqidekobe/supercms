define([ 'modules/settings/module', 'lodash', 'datatables-responsive' ], function(module, _) {

	'use strict';

	module.registerController('SortManageCtrl', function($scope, $compile, $templateCache, $filter, resource) {
		$scope.searchType = 'template_index';//默认第一个为首页模版分类
		$scope.sort = {
		    id : '',
		    sortName : '',
		    sortType : $scope.searchType,
		    parentId : '',
		};
		$scope.initialSort = angular.copy($scope.sort);
		$scope.sortDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.sortsTree = {
		    data : null,
		    searchCurrentItem : null,
		    editCurrentItem : null
		};
		$scope.isEdit=false;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.sortName;
		        }
		    }, {
		        data : 'sortType',
		        render : function(data, type, row) {
			    	var sortType="";
			    	switch (data) {
					case 'template_index':
						sortType="首页模版分类";
						break;
					case 'template_list':
						sortType="列表模版分类";
						break;
					case 'template_content':
						sortType="内容模版分类";
						break;
					case 'template_custom':
						sortType="自定义模版分类";
						break;
					case 'template_snippet':
						sortType="模版片段分类";
						break;
					case 'custom_page':
						sortType="自定义页分类";
						break;
					case 'datasource':
						sortType="数据源分类";
						break;
					case 'data_tag':
						sortType="数据标签分类";
						break;
					default:
						break;
					}
			    	return sortType;
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
		   // pageLength : 2,
		    oTreeTable : {
		        showExpander : true,
		        expandable : true,
		        force : true,
		        initialState : 'expanded',
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
			    var data = _.extend(data, {
				    sortType : $scope.searchType,
			    });
			    resource.sortsResource.datatable(data).$promise.then(function(sorts) {
				    $scope.datatable.data = sorts;
				    callback($scope.datatable);
				    $scope.sort = $scope.initialSort;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// -----------------------------------
		// Resource Request Methods Definition
		// -----------------------------------
		
		// request columns as dropdown tree
		$scope.loadColumnsAsTreeRequest = function() {
			var sortType=$scope.sort.sortType;
			if(angular.isUndefined(sortType)){
				sortType=$scope.searchType;
				$scope.sort.sortType=$scope.searchType;
			}
			resource.sortsResource.sortsTree({sortType:$scope.sort.sortType}).$promise.then(function(data) {
				$scope.sortsTree.data = data;
			});
		};

		// request create column
		$scope.createSortRequest = function() {
			$scope.sort.parentId =  $scope.sortsTree.editCurrentItem==null?null:$scope.sortsTree.editCurrentItem.id;
			new resource.sortsResource($scope.sort).$create().then(function() {
				$scope._refreshTable();
				$scope.loadColumnsAsTreeRequest();
			});
		};

		// request save column
		$scope.saveSortRequest = function() {
			$scope.sort.parentId = $scope.sortsTree.editCurrentItem==null?null:$scope.sortsTree.editCurrentItem.id;
			$scope.sort.$save().then(function() {
				$scope._refreshTable();
				$scope.loadColumnsAsTreeRequest();
			});
		};

		// request remove column
		$scope.removeSortRequest = function() {
			$scope.sort.$remove().then(function() {
				$scope._refreshTable();
				$scope.loadColumnsAsTreeRequest();
			});
		};

		// --------------------------------
		// Event Handler Methods Definition
		// --------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#sorts_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.sort = $scope.rowSelected = table.row('.highlight').data();
		};

		// change site command handler
		$scope.changeSortTypeHandler = function() {
			$scope.sort.sortType=$scope.searchType;
			$('#sorts_table').DataTable().ajax.reload(null, true);
			$scope.loadColumnsAsTreeRequest();
		};
		
		$scope.changeSortTypeHandler2 = function() {
			$scope.loadColumnsAsTreeRequest();
		};
		
		// submit form command handler
		$scope.submitSortFormHandler = function() {
			var form = angular.element(sortForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelSortFormHandler = function() {
			$('#sort_dialog').dialog('close');
		}

		// create command handler
		$scope.createSortHandler = function() {
			$scope.isEdit=false;
			$scope.$broadcast('resetForm');
			$scope.sortDialog.isNew = true;
			$scope.sortDialog.icon = 'fa fa-plus';
			$scope.sortDialog.title = '创建分类';
			$scope.sort = angular.copy($scope.initialSort);
			$scope.sortsTree.editCurrentItem = null;
			$scope.sort.sortType = $scope.searchType;
		};

		// save command handler
		$scope.saveSortHandler = function() {
			$scope.isEdit=true;
			$scope.$broadcast('resetForm');
			$scope.sortDialog.isNew = false;
			$scope.sortDialog.icon = 'fa fa-edit';
			$scope.sortDialog.title = '修改分类';
			$scope.sort.sortType = $scope.searchType;
			$scope.rowSelected.$get().then(function(data) {
				$scope.sort = data;
				var currentItem = function(sorts, sort) {
					if (sort.parentId == null) {
						return null;
					} else {
						var result = [];
						var fn = function(result, sorts) {
							for (var i = 0; i < sorts.length; i++) {
								var data = sorts[i];
								result[result.length] = data;
								if (data.childrens && data.childrens.length != 0) {
									fn(result, data.childrens);
								}
							}
						};
						fn(result, sorts);
						return _.find(result, function(item) {
							return item.id == sort.parentId;
						});
					}
				};
				$scope.sortsTree.editCurrentItem = currentItem($scope.sortsTree.data, $scope.sort);
			});
		};

		// remove scommand handler
		$scope.removeSortHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除分类",
			    content : "是否删除选中的分类？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeSortRequest();
				}
			});
		};

		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#sort_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'sorts_table'
			});
		}
		
		$scope.loadColumnsAsTreeRequest();

	})
});
