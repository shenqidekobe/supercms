define([ 'modules/modules/module', 'lodash','lodash-deep', 'datatables-responsive' ], function(module, _) {

	'use strict';

	module.registerController('ColumnCtrl', function($scope, $compile, $templateCache, $filter, $location,resource,makeSocket,SmallBoxMessage) {
		// ------------------
		// Data Definition
		// ------------------
		$scope.column = {
		    id : '',
		    title : '',
		    description : '',
		    siteId : '',
		    parentId : '',
		    dirName : '',
		    datasourceId : '',
		    homeTemplateId : '',
		    contentTemplateId : ''
		};
		$scope.initialColumn = angular.copy($scope.column);
		$scope.columnDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.columnsTree = {
		    data : null,
		    editCurrentItem : null
		};
		$scope.homeTemplatesTree = {
			 data : null,
			 editCurrentItem : null
		};
		$scope.datasources = [];
		$scope.datasource = null;
		$scope.homeTemplates = [];
		$scope.homeTemplate = null;
		$scope.contentTemplates = [];
		$scope.contentTemplate = null;
		$scope.searchSiteId = '';
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.title;
		        }
		    }, {
		        data : 'dirWebpath',
		        render : function(data, type, row) {
			        return row.site.dirName + data;
		        }
		    }, {
		        data : 'homeTemplateId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.homeTemplate.templateName;
		        }
		    }, {
		        data : 'contentTemplateId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.contentTemplate.templateName;
		        }
		    }, {
		        data : 'datasourceId',
		        render : function(data, type, row) {
			        return '[' + data + ']' + row.datasource.title;
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
		    pageLength : 10000,
		    oTreeTable : {
		        showExpander : true,
		        expandable : true,
		        force : true,
		        // initialState : 'expanded',
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
				    siteId : $scope.searchSiteId,
			    });
			    resource.columnsResource.datatable(data).$promise.then(function(columns) {
				    $scope.datatable.data = columns;
				    callback($scope.datatable);
				    $scope.column = $scope.initialColumn;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// -----------------------------------
		// Resource Request Methods Definition
		// -----------------------------------

		// request columns as dropdown tree
		$scope.loadColumnsAsTreeRequest = function() {
			resource.columnsResource.tree().$promise.then(function(data) {
				$scope.columnsTree.data = data;
			});
		};
		
		// request all sites
		$scope.loadSitesRequest = function() {
			resource.sitesResource.all().$promise.then(function(data) {
				$scope.sites = data;
			});
		};

		// request all datasources
		$scope.loadDatasourcesRequest = function() {
			/**
			resource.datasourcesResource.all().$promise.then(function(data) {
				$scope.datasources = data;
			});
			*/
			resource.sortsResource.byType({
				type : 'datasource'
			}).$promise.then(function(sorts) {
				resource.datasourcesResource.all().$promise.then(function(datasources){
					_.forEach(sorts, function(sort, key){
						sorts[key] = _.mapKeys(sort, function(value, key){
							return key.replace(/sortName/g, 'name');
						});
						sorts[key].t='sort';
						sorts[key].children=[];
					});
					var sortsWithItems = [];
					_.forEach(datasources, function(datasource, key){
						datasources[key].t = 'datasource';
						datasources[key] = _.mapKeys(datasource, function(value, key){
							return key.replace(/title/g, 'name');
						});
						var parentSort = _.find(sorts, function(sort){
							return sort.id == datasources[key].sort.id;
						})
						if(parentSort){
							parentSort.children.push(datasources[key]);
							sortsWithItems.push(parentSort);
						}
					});
					sortsWithItems = _.uniq(sortsWithItems, function(sort){
						return sort.id;
					});
					var treelineSorts = [];
					var treelineSortsFn = function(s){
						treelineSorts.push(s);
						if(s.parentId != null){
							treelineSortsFn(_.find(sorts, function(s1){
								return s1.t=='sort' && s1.id==s.parentId
							}));
						}
					};
					_.forEach(sortsWithItems, function(s){
						treelineSortsFn(s);
					});
					treelineSorts = _.uniq(treelineSorts, function(s){
						return s.id;
					});
					
					_.forEach(treelineSorts, function(sort, key){
						var children = _.filter(sorts, {parentId : sort.id});
						_.forEach(children, function(s){
							var b = _.some(treelineSorts, function(s1){
								return s.t=='sort' &&s.id == s1.id;
							});
							if(b){
								sort.children.push(s);
							}
						});
					});
					$scope.datasources = _.filter(treelineSorts, {parentId : null});
				});
			});
		};

		// request all home templates
		$scope.loadHomeTemplatesAsTree = function() {
			/*
			 * var data = {templateType: 'list'}; $scope.homeTemplates =
			 * resource.templatesResource.queryByType(data);
			 */
			resource.sortsResource.byType({
				type : 'template_list'
			}).$promise.then(function(sorts) {
				resource.templatesResource.queryByType({templateType: 'list'}).$promise.then(function(templates){
					_.forEach(sorts, function(sort, key){
						sorts[key] = _.mapKeys(sort, function(value, key){
							return key.replace(/sortName/g, 'name');
						});
						sorts[key].t='sort';
						sorts[key].children=[];
					});
					var sortsWithItems = [];
					_.forEach(templates, function(template, key){
						templates[key].t = 'template';
						templates[key] = _.mapKeys(template, function(value, key){
							return key.replace(/templateName/g, 'name');
						});
						var parentSort = _.find(sorts, function(sort){
							return sort.id == templates[key].sort.id;
						})
						if(parentSort){
							parentSort.children.push(templates[key]);
							sortsWithItems.push(parentSort);
						}
					});
					sortsWithItems = _.uniq(sortsWithItems, function(sort){
						return sort.id;
					});
					var treelineSorts = [];
					var treelineSortsFn = function(s){
						treelineSorts.push(s);
						if(s.parentId != null){
							treelineSortsFn(_.find(sorts, function(s1){
								return s1.t=='sort' && s1.id==s.parentId
							}));
						}
					};
					_.forEach(sortsWithItems, function(s){
						treelineSortsFn(s);
					});
					treelineSorts = _.uniq(treelineSorts, function(s){
						return s.id;
					});
					
					_.forEach(treelineSorts, function(sort, key){
						var children = _.filter(sorts, {parentId : sort.id});
						_.forEach(children, function(s){
							var b = _.some(treelineSorts, function(s1){
								return s.t=='sort' &&s.id == s1.id;
							});
							if(b){
								sort.children.push(s);
							}
						});
					});
					$scope.homeTemplates = _.filter(treelineSorts, {parentId : null});
				});
			});
			
		};

		// request all content templates
		$scope.loadContentTemplates = function() {
			/**
			var data = {templateType: 'content'};
			$scope.contentTemplates = resource.templatesResource.queryByType(data);
			*/
			resource.sortsResource.byType({
				type : 'template_content'
			}).$promise.then(function(sorts) {
				resource.templatesResource.queryByType({templateType: 'content'}).$promise.then(function(templates){
					_.forEach(sorts, function(sort, key){
						sorts[key] = _.mapKeys(sort, function(value, key){
							return key.replace(/sortName/g, 'name');
						});
						sorts[key].t='sort';
						sorts[key].children=[];
					});
					var sortsWithItems = [];
					_.forEach(templates, function(template, key){
						templates[key].t = 'template';
						templates[key] = _.mapKeys(template, function(value, key){
							return key.replace(/templateName/g, 'name');
						});
						var parentSort = _.find(sorts, function(sort){
							return sort.id == templates[key].sort.id;
						})
						if(parentSort){
							parentSort.children.push(templates[key]);
							sortsWithItems.push(parentSort);
						}
					});
					sortsWithItems = _.uniq(sortsWithItems, function(sort){
						return sort.id;
					});
					var treelineSorts = [];
					var treelineSortsFn = function(s){
						treelineSorts.push(s);
						if(s.parentId != null){
							treelineSortsFn(_.find(sorts, function(s1){
								return s1.t=='sort' && s1.id==s.parentId
							}));
						}
					};
					_.forEach(sortsWithItems, function(s){
						treelineSortsFn(s);
					});
					treelineSorts = _.uniq(treelineSorts, function(s){
						return s.id;
					});
					
					_.forEach(treelineSorts, function(sort, key){
						var children = _.filter(sorts, {parentId : sort.id});
						_.forEach(children, function(s){
							var b = _.some(treelineSorts, function(s1){
								return s.t=='sort' &&s.id == s1.id;
							});
							if(b){
								sort.children.push(s);
							}
						});
					});
					$scope.contentTemplates = _.filter(treelineSorts, {parentId : null});
				});
			});
		};

		// request create column
		$scope.createColumnRequest = function() {
			$scope.column.siteId = $scope.columnsTree.editCurrentItem.siteId;
			$scope.column.parentId = $scope.columnsTree.editCurrentItem.type == 'SITE' ? '' : $scope.columnsTree.editCurrentItem.id;
			new resource.columnsResource($scope.column).$create().then(function() {
				$scope._refreshTable();
				$scope.loadColumnsAsTreeRequest();
			});
		};

		// request save column
		$scope.saveColumnRequest = function() {
			$scope.column.siteId = $scope.columnsTree.editCurrentItem.siteId;
			$scope.column.parentId = $scope.columnsTree.editCurrentItem.type == 'SITE' ? '' : $scope.columnsTree.editCurrentItem.id;
			$scope.column.$save().then(function() {
				$scope._refreshTable();
				$scope.loadColumnsAsTreeRequest();
			});
		};

		// request remove column
		$scope.removeColumnRequest = function() {
			$scope.column.$remove().then(function() {
				$scope._refreshTable();
				$scope.loadColumnsAsTreeRequest();
			});
		};

		// --------------------------------
		// Event Handler Methods Definition
		// --------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#columns_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.column = $scope.rowSelected = table.row('.highlight').data();
		};

		// change site command handler
		$scope.changeSiteHandler = function() {
			$('#columns_table').DataTable().ajax.reload(null, true);
		};

		// submit form command handler
		$scope.submitColumnFormHandler = function() {
			var form = angular.element(columnForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelColumnFormHandler = function() {
			$('#column_dialog').dialog('close');
		}

		// create command handler
		$scope.createColumnHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.columnDialog.isNew = true;
			$scope.columnDialog.icon = 'fa fa-plus';
			$scope.columnDialog.title = '创建栏目';
			$scope.column = angular.copy($scope.initialColumn);
			$scope.columnsTree.editCurrentItem = null;
			$scope.homeTemplate = null;
			$scope.contentTemplate = null;
			$scope.datasource = null;
		};

		// save command handler
		$scope.saveColumnHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.columnDialog.isNew = false;
			$scope.columnDialog.icon = 'fa fa-edit';
			$scope.columnDialog.title = '修改栏目';
			$scope.rowSelected.$get().then(function(data) {
				$scope.column = data;
				var currentItem = function(columns, column) {
					if (column.parentId == null) {
						return _.find(columns, function(item) {
							return item.id == column.siteId;
						});
					} else {
						var result = [];
						var fn = function(result, columns) {
							for (var i = 0; i < columns.length; i++) {
								var data = columns[i];
								if (data.type == 'COLUMN') {
									result[result.length] = data;
								}
								if (data.childrens && data.childrens.length != 0) {
									fn(result, data.childrens);
								}
							}
						};
						fn(result, columns);
						return _.find(result, function(item) {
							return item.id == column.parentId;
						});
					}
				};
				$scope.columnsTree.editCurrentItem = currentItem($scope.columnsTree.data, $scope.column);
				
				var homeTemplate = angular.copy($scope.column.homeTemplate);
				homeTemplate.name=homeTemplate.templateName;
				homeTemplate.t = 'template';
				$scope.homeTemplate = homeTemplate;
				
				var contentTemplate = angular.copy($scope.column.contentTemplate);
				contentTemplate.name=contentTemplate.templateName;
				contentTemplate.t = 'template';
				$scope.contentTemplate = contentTemplate;
				
				var datasource = angular.copy($scope.column.datasource);
				datasource.name=datasource.title;
				datasource.t = 'datasource';
				$scope.datasource = datasource;
			});
		};

		// remove scommand handler
		$scope.removeColumnHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除栏目",
			    content : "是否删除选中的栏目并删除该栏目目录下的所有文件？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeColumnRequest();
				}
			});
		};

		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#column_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'columns_table'
			});
		}
		
		makeSocket.initialize(function(msg){
			var tips="生成成功";
			if (msg.warn) {
				tips= msg.warn;
			} else {
				if (msg.error) {
					tips = msg.error;
				}
			}
			SmallBoxMessage.tips({
	    			title:"提示",
	    			content:tips,
	    			timeout:3000
	    		});
		});
		$scope.make = function(e,type) {
			var message = type+"#"+$scope.rowSelected.id;
			makeSocket.send(message);
			e.preventDefault();
		};
		$scope.template = function(e,type) {
			var sortId=null;
			if(type=="list"){
				sortId=$scope.rowSelected.homeTemplate.sort.id;
			}else{
				sortId=$scope.rowSelected.contentTemplate.sort.id;
			}
            var id=$scope.rowSelected.id;
            window.open('#/template/edit?sessionId='+$location.search().sessionId,{
				id:id,
				templateType:type,
				sortId:sortId,
				searchSortId:null,
				pageth:$scope.null
			});
            e.preventDefault();
		};
		$scope.preview = function(e) {
			var url=$scope.rowSelected.site.testDomain;
			var header="http://";
			if(url.substr(0,header.length)!=header){
			   url=header+url;
			}
			url=url+$scope.rowSelected.dirWebpath+"/index.shtml";
			window.open(url);
			e.preventDefault();
		};

		// ------------------
		// Data Initializing
		// ------------------

		$scope.loadColumnsAsTreeRequest();
		$scope.loadSitesRequest();
		$scope.loadDatasourcesRequest();
		$scope.loadHomeTemplatesAsTree();
		$scope.loadContentTemplates();
	})
});
