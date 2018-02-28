define([ 'angular','angular-couch-potato', 'angular-ui-router' ], function(ng,
		couchPotato) {

	"user strict";
	
	
	var module = ng.module('app.templates', [ 'ui.router','app.services']);

	couchPotato.configureApp(module);

	module.config(function($stateProvider, $couchPotatoProvider) {
	   $stateProvider
          .state('app.template', {
              abstract: true,
              data: {
                  title: '模版管理'
              },
              resolve: {
                  deps: $couchPotatoProvider.resolveDependencies([
                      'modules/services/resource',
                      'contextmenu'
                  ])
              }
          }).state('app.template.index',{
			url:'/template/index',
			data:{
				title:'首页模版'
			},
			params:{searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateCtrl',
					templateUrl:'app/modules/templates/views/template.html',
					resolve:{
						templateType:function(){
							return "index";
						},
						deps:$couchPotatoProvider.resolveDependencies([
						    'modules/templates/controllers/TemplateCtrl',
						    'modules/ui/directives/smartJquiDialog',
						    'modules/widgets/models/Sort',
						    'modules/widgets/directives/sortTreeview',
						    'modules/widgets/directives/hrefSortView',
						    'modules/widgets/directives/tableEdit',
						    'modules/widgets/directives/tableDel',
						    'modules/widgets/services/smallBoxMessage',
						    'modules/widgets/directives/sortForm',
						    'modules/widgets/controllers/EditSortCtrl',
						    'modules/widgets/directives/paginationWidget'
						    ])
					}
				}
			}
		}).state('app.template.list',{
			url:'/template/list',
			data:{
				title:'列表模版'
			},
			params:{searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateCtrl',
					templateUrl:'app/modules/templates/views/template.html',
					resolve:{
						templateType:function(){
							return "list";
						},
						deps:$couchPotatoProvider.resolveDependencies([
						    'modules/templates/controllers/TemplateCtrl',
						    'modules/ui/directives/smartJquiDialog',
						    'modules/widgets/models/Sort',
						    'modules/widgets/directives/sortTreeview',
						    'modules/widgets/directives/hrefSortView',
						    'modules/widgets/directives/tableEdit',
						    'modules/widgets/directives/tableDel',
						    'modules/widgets/services/smallBoxMessage',
						    'modules/widgets/directives/sortForm',
						    'modules/widgets/controllers/EditSortCtrl',
						    'modules/widgets/directives/paginationWidget'])
					}
				}
			}
		}).state('app.template.content',{
			url:'/template/content',
			data:{
				title:'内容模版'
			},
			params:{searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateCtrl',
					templateUrl:'app/modules/templates/views/template.html',
					resolve:{
						templateType:function(){
							return "content";
						},
						deps:$couchPotatoProvider.resolveDependencies([
						    'modules/templates/controllers/TemplateCtrl',
						    'modules/ui/directives/smartJquiDialog',
						    'modules/widgets/models/Sort',
						    'modules/widgets/directives/sortTreeview',
						    'modules/widgets/directives/hrefSortView',
						    'modules/widgets/directives/tableEdit',
						    'modules/widgets/directives/tableDel',
						    'modules/widgets/services/smallBoxMessage',
						    'modules/widgets/directives/sortForm',
						    'modules/widgets/controllers/EditSortCtrl',
						    'modules/widgets/directives/paginationWidget'])
					}
				}
			}
		}).state('app.template.custom',{
			url:'/template/custom',
			data:{
				title:'自定义页模版'
			},
			params:{searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateCtrl',
					templateUrl:'app/modules/templates/views/template.html',
					resolve:{
						templateType:function(){
							return "custom";
						},
						deps:$couchPotatoProvider.resolveDependencies([
						    'modules/templates/controllers/TemplateCtrl',
						    'modules/ui/directives/smartJquiDialog',
						    'modules/widgets/models/Sort',
						    'modules/widgets/directives/sortTreeview',
						    'modules/widgets/directives/hrefSortView',
						    'modules/widgets/directives/tableEdit',
						    'modules/widgets/directives/tableDel',
						    'modules/widgets/services/smallBoxMessage',
						    'modules/widgets/directives/sortForm',
						    'modules/widgets/controllers/EditSortCtrl',
						    'modules/widgets/directives/paginationWidget'])
					}
				}
			}
		}).state('app.template.edit',{
			url:'/template/edit',
			data:{
				title:'编辑模版'
			},
			params:{id:null,templateType:null,sortId:null,searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateEditCtrl',
					templateUrl:'app/modules/templates/views/addTemplate.html',
					resolve:{
						deps:$couchPotatoProvider.resolveDependencies([
						   'modules/templates/controllers/TemplateEditCtrl',
						   'modules/templates/directives/templateForm',
						   'modules/widgets/models/Sort',
						   'modules/widgets/directives/hrefSort',
						   'modules/widgets/directives/selectSortOption',
						   'modules/widgets/services/smallBoxMessage',
						   'modules/forms/directives/editors/smartCkEditor',
						   'modules/forms/directives/input/dwSelectTree',
                           'modules/ui/directives/smartJquiDialogLauncher'])
					}
				}
			}
		}).state('app.template.snippet',{
			url:'/template/sinppet',
			data:{
				title:'模版片段'
			},
			params:{searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateSnippetCtrl',
					templateUrl:'app/modules/templates/views/templateSnippet.html',
					resolve:{
						deps:$couchPotatoProvider.resolveDependencies([
						    'modules/templates/controllers/TemplateSnippetCtrl',
						    'modules/ui/directives/smartJquiDialog',
						    'modules/widgets/models/Sort',
						    'modules/widgets/directives/sortTreeview',
						    'modules/widgets/directives/hrefSortView',
						    'modules/widgets/directives/tableEdit',
						    'modules/widgets/directives/tableDel',
						    'modules/widgets/services/smallBoxMessage',
						    'modules/widgets/directives/sortForm',
						    'modules/widgets/controllers/EditSortCtrl',
						    'modules/widgets/directives/paginationWidget'])
					}
				}
			}
		}).state('app.template.snippet.edit',{
			url:'/edit',
			data:{
				title:'编辑模版片段'
			},
			params:{id:null,sortId:null,searchSortId:null,pageth:1},
			views:{
				"content@app":{
					controller:'TemplateSnippetEditCtrl',
					templateUrl:'app/modules/templates/views/addSnippet.html',
					resolve:{
						deps:$couchPotatoProvider.resolveDependencies([
						   'modules/templates/controllers/TemplateSnippetEditCtrl',
						   'modules/templates/directives/snippetForm',
						   'modules/widgets/models/Sort',
						   'modules/widgets/directives/hrefSort',
						   'modules/widgets/directives/selectSortOption',
						   'modules/widgets/services/smallBoxMessage',
						   'modules/forms/directives/editors/smartCkEditor',
						   'modules/forms/directives/input/dwSelectTree',
                           'modules/ui/directives/smartJquiDialogLauncher'])
					}
				}
			}
		}).state('explain',{
			url : '/explain',
			views : {
				root : {
					templateUrl : 'app/modules/templates/views/explain.html',
					controller : 'TemplateExplainCtrl',
					resolve : {
						deps : $couchPotatoProvider.resolveDependencies([
						   'modules/templates/controllers/TemplateExplainCtrl', 
						   'modules/services/resource',
						   'modules/ui/directives/smartProgressbar',
						])
					}
				}
			},
			data : {
				title : '模版说明',
				htmlId : 'extr-page'
			}
		});
		
	});

	module.run(function($couchPotato) {
		module.lazy = $couchPotato;
	});

	return module;

});