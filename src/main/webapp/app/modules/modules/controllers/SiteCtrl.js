define([ 'modules/modules/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('SiteCtrl', function($scope, $state,$rootScope, $compile, $templateCache, $filter, resource,makeSocket,SmallBoxMessage) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.site = {
		    id : '',
		    title : '',
		    description : '',
		    testDomain : '',
		    productDomain : '',
		    dirName : '',
		    homeTemplateId : '',
		    extensionName : ''
		};
		$scope.initialSite = angular.copy($scope.site);
		$scope.siteDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.homeTemplates = [];
		$scope.homeTemplate=null;
		$scope.rowSelected = null;
		$scope.tableOptions = {
		    columns : [ {
		        data : 'id',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.title;
		        }
		    }, {
			    data : 'testDomain'
		    }, {
			    data : 'productDomain'
		    }, {
			    data : 'dirName'
		    }, {
		        data : 'homeTemplateId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.homeTemplate.templateName;
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
			    resource.sitesResource.datatable(data).$promise.then(function(sites) {
				    $scope.datatable.data = sites;
				    callback($scope.datatable);
				    $scope.site = $scope.initialSite;
				    $scope.rowSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request all home templates
		$scope.loadHomeTemplates = function() {
			/**
			var data = {templateType: 'index'};
			$scope.homeTemplates = resource.templatesResource.queryByType(data);
			*/
			
			resource.sortsResource.byType({
				type : 'template_index'
			}).$promise.then(function(sorts) {
				resource.templatesResource.queryByType({templateType: 'index'}).$promise.then(function(templates){
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

		// request create site
		$scope.createSiteRequest = function() {
			new resource.sitesResource($scope.site).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save site
		$scope.saveSiteRequest = function() {
			$scope.site.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove site
		$scope.removeSiteRequest = function() {
			$scope.site.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#sites_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.site = $scope.rowSelected = table.row('.highlight').data();
		};

		// submit form command handler
		$scope.submitSiteFormHandler = function() {
			var form = angular.element(siteForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelSiteFormHandler = function() {
			$('#site_dialog').dialog('close');
		}

		// create command handler
		$scope.createSiteHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.siteDialog.isNew = true;
			$scope.siteDialog.icon = 'fa fa-plus';
			$scope.siteDialog.title = '创建站点';
			$scope.site =  angular.copy($scope.initialSite);
			$scope.homeTemplate = null;
		};

		// save command handler
		$scope.saveSiteHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.siteDialog.isNew = false;
			$scope.siteDialog.icon = 'fa fa-edit';
			$scope.siteDialog.title = '修改站点';
			$scope.rowSelected.$get().then(function(data) {
				$scope.site = data;
				var homeTemplate = angular.copy($scope.site.homeTemplate);
				homeTemplate.name=homeTemplate.templateName;
				homeTemplate.t = 'template';
				$scope.homeTemplate = homeTemplate;
			});
		};
		
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
			var message = "INDEX#"+$scope.rowSelected.id;
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
		$scope.templateIndex = function(e) {
            var sortId=$scope.rowSelected.homeTemplate.sort.id;
            var id=$scope.rowSelected.id;
            window.open('#/app.template.edit',{
				id:id,
				templateType:'index',
				sortId:sortId,
				searchSortId:null,
				pageth:$scope.null
			});
            e.preventDefault();
		};
		$scope.preview = function(e) {
			var url=$scope.rowSelected.testDomain;
			var header="http://";
			if(url.substr(0,header.length)!=header){
			   url=header+url;
			}
			window.open(url);
			e.preventDefault();
		};
		// remove command handler
		$scope.removeSiteHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除站点",
			    content : "是否删除选中的站点并删除该站点目录下的所有文件？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeSiteRequest();
				}
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#site_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'sites_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------

		$scope.loadHomeTemplates();

	})
});
