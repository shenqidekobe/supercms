define([ 'modules/modules/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('CustomCtrl', function($scope, $compile, $templateCache, $filter, resource,CTX,makeSocket,SmallBoxMessage) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.custom = {
		    id : '',
		    title : '',
		    description : '',
		    location : '',
		    extensionName : 'shtml',
		    customTemplateId: '',
		    sortId : ''
		};
		$scope.initialCustom = angular.copy($scope.custom);
		$scope.customDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.sortsTree = {
		    data : null,
		    searchCurrentItem : null,
		    editCurrentItem : null
		};
		$scope.customTemplates = [];
		$scope.customTemplate = null;
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.title;
		        }
		    }, {
		        data : 'location',
		        render : function(data, type, row) {
			        return data + "." + row.extensionName;
		        }
		    }, {
		        data : 'customTemplateId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.customTemplate.templateName;
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
			    resource.customsResource.datatable(data).$promise.then(function(customs) {
				    $scope.datatable.data = customs;
				    callback($scope.datatable);
				    $scope.custom = $scope.initialCustom;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request sorts
		$scope.loadSortsAsTreeRequest = function() {
			resource.customsResource.sortsTree().$promise.then(function(data) {
				$scope.sortsTree.data = data;
				$scope.$watch('sortsTree.searchCurrentItem', function(item) {
					if (item) {
						$('#customs_table').DataTable().ajax.reload(null, true);
					}
				});
			});
		};
		// request load custom templates
		$scope.loadCustomTemplatesRequest = function() {
			/**
			var data = {templateType: 'custom'};
			$scope.customTemplates = resource.templatesResource.queryByType(data);
			*/
			resource.sortsResource.byType({
				type : 'template_custom'
			}).$promise.then(function(sorts) {
				resource.templatesResource.queryByType({templateType: 'custom'}).$promise.then(function(templates){
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
					$scope.customTemplates = _.filter(treelineSorts, {parentId : null});
				});
			});
		}

		// request create custom
		$scope.createCustomRequest = function() {
			$scope.custom.sortId = $scope.sortsTree.editCurrentItem.id;
			new resource.customsResource($scope.custom).$create().then(function() {
				$scope._refreshTable();
				$scope.loadSortsAsTreeRequest();
			});
		};

		// request save custom
		$scope.saveCustomRequest = function() {
			$scope.custom.sortId = $scope.sortsTree.editCurrentItem.id;
			$scope.custom.$save().then(function() {
				$scope._refreshTable();
				$scope.loadSortsAsTreeRequest();
			});
		};

		// request remove custom
		$scope.removeCustomRequest = function() {
			$scope.custom.$remove().then(function() {
				$scope._refreshTable();
				$scope.loadSortsAsTreeRequest();
			});
		};

		// ----------------------
		// Event Handler Methods
		// ----------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#customs_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.custom = $scope.rowSelected = table.row('.highlight').data();
		};

		// change site command handler
		$scope.changeSiteHandler = function() {
			$('#customs_table').DataTable().ajax.reload(null, true);
		};

		// submit form command handler
		$scope.submitCustomFormHandler = function() {
			var form = angular.element(customForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelCustomFormHandler = function() {
			$('#custom_dialog').dialog('close');
		}

		// create command handler
		$scope.createCustomHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.customDialog.isNew = true;
			$scope.customDialog.icon = 'fa fa-plus';
			$scope.customDialog.title = '创建自定义';
			$scope.custom = angular.copy($scope.initialCustom);
			$scope.sortsTree.editCurrentItem = null;
			$scope.homeTemplate = null;
		};

		// save command handler
		$scope.saveCustomHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.customDialog.isNew = false;
			$scope.customDialog.icon = 'fa fa-edit';
			$scope.customDialog.title = '修改自定义';
			$scope.rowSelected.$get().then(function(data) {
				$scope.custom = data;
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
					return item.id == $scope.custom.sortId;
				});
				
				var customTemplate = angular.copy($scope.custom.customTemplate);
				customTemplate.name=customTemplate.templateName;
				customTemplate.t = 'template';
				$scope.customTemplate = customTemplate;
			});
		};

		// remove scommand handler
		$scope.removeCustomHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除自定义",
			    content : "是否删除选中的自定义？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeCustomRequest();
				}
			});
		};

		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#custom_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'customs_table'
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
		$scope.make = function(e) {
			var message = "CUSTOM#"+$scope.rowSelected.id;
			makeSocket.send(message);
			e.preventDefault();
		};
		$scope.publish = function(e) {
			$scope.rowSelected.$publish().then(function() {
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"发布成功.",
 	    			timeout:3000
 	    		});
			});
			e.preventDefault();
		};
		$scope.template = function(e) {
            var sortId=$scope.rowSelected.customTemplate.sort.id;
            var id=$scope.rowSelected.id;
            window.open('#/app.template.edit',{
				id:id,
				templateType:'custom',
				sortId:sortId,
				searchSortId:null,
				pageth:$scope.null
			});
            e.preventDefault();
		};
		$scope.preview = function(e) {
			var url=CTX+"/sites"+$scope.rowSelected.location+"."+$scope.rowSelected.extensionName;
			window.open(url);
			e.preventDefault();
		};
		
		
		

		// ------------------
		// Data Initializing
		// ------------------

		$scope.loadSortsAsTreeRequest();
		$scope.loadCustomTemplatesRequest();
	})
});
