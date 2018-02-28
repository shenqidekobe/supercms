define([ 'angular', 'angular-couch-potato', 'angular-ui-router' ], function(ng, couchPotato) {
	"use strict";
	var module = ng.module('app.datas', [ 'ui.router', 'app.services' ]);
	couchPotato.configureApp(module);

	module.config(function($stateProvider, $couchPotatoProvider) {
		$stateProvider.state('app.datas', {
		    abstract : true,
		    data : {
			    title : '数据'
		    },
		    resolve : {
			    deps : $couchPotatoProvider.resolveDependencies([ 'modules/ui/directives/smartJquiDialogLauncher', 'modules/ui/directives/smartJquiDialog', 'modules/services/resource' ])
		    }
		}).state('app.datas.model', {
		    url : '/datas/model',
		    data : {
			    title : '模型'
		    },
		    views : {
			    "content@app" : {
			        controller : 'ModelCtrl',
			        templateUrl : "app/modules/datas/views/model.html",
			        resolve : {
				        deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/ModelCtrl', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/datas/directives/modelForm' ])
			        }
			    }
		    }
		}).state('app.datas.field', {
		    url : '/datas/model/field',
		    data : {
			    title : '字段'
		    },
		    params : {
			    modelId : null
		    },
		    views : {
			    "content@app" : {
			        controller : 'FieldCtrl',
			        templateUrl : "app/modules/datas/views/field.html",
			        resolve : {
				        deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/FieldCtrl', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/datas/directives/fieldForm' ])
			        }
			    }
		    }
		}).state('app.datas.modelForm', {
		    url : '/datas/model/modelForm',
		    data : {
			    title : '表单'
		    },
		    params : {
			    modelId : null
		    },
		    views : {
			    "content@app" : {
			        controller : 'ModelFormCtrl',
			        templateUrl : "app/modules/datas/views/model-form.html",
			        resolve : {
				        deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/ModelFormCtrl', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/forms/directives/editors/smartMarkdownEditor' ])
			        }
			    }
		    }
		}).state(
		        'app.datas.datasource',
		        {
		            url : '/datas/datasource',
		            data : {
			            title : '数据源'
		            },
		            views : {
			            "content@app" : {
			                controller : 'DatasourceCtrl',
			                templateUrl : "app/modules/datas/views/datasource.html",
			                resolve : {
				                deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/DatasourceCtrl', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/datas/directives/datasourceForm',
				                        'modules/forms/directives/input/dwSelectTree' ])
			                }
			            }
		            }
		        }).state(
		        'app.datas.data',
		        {
		            url : '/datas/data',
		            data : {
			            title : '数据'
		            },
		            views : {
			            "content@app" : {
			                controller : 'DataCtrl',
			                templateUrl : "app/modules/datas/views/data.html",
			                resolve : {
				                deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/DataCtrl', 'modules/widgets/directives/widgetGrid', 'bootstrap', 'modules/ui/directives/smartTreeview',
				                        'modules/forms/directives/editors/smartCkEditor', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/forms/directives/input/smartDatepicker',
				                        'modules/widgets/services/smallBoxMessage',
				                        'modules/services/makeSocket',
				                        'modules/datas/directives/dynamicSection', 'modules/datas/directives/selectMultiDatasourcePopover','modules/datas/directives/selectTagPopover' ,'modules/forms/directives/input/smartSelect2','modules/datas/directives/uploadHeaderPic' ])
			                }
			            }
		            }
		        }).state(
		        'app.datas.tag',
		        {
		            url : '/datas/tag',
		            data : {
			            title : '标签'
		            },
		            views : {
			            "content@app" : {
			                controller : 'TagCtrl',
			                templateUrl : "app/modules/datas/views/tag.html",
			                resolve : {
				                deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/TagCtrl', 'modules/widgets/directives/widgetGrid', 'bootstrap', 'modules/tables/directives/datatables/datatableCustomToolbar',
				                        'modules/datas/directives/tagForm' ])
			                }
			            }
		            }
		        }).state(
				        'preview',
				        {
				            url : '/preview',
				            data : {
					            title : '预览',
					            htmlId : 'extr-page'
				            },
				            views : {
				            	root : {
					                controller : 'PreViewCtrl',
					                templateUrl : "app/modules/datas/views/preview.html",
					                resolve : {
						                deps : $couchPotatoProvider.resolveDependencies([ 'modules/datas/controllers/PreViewCtrl','modules/services/resource' ])
					                }
					            }
				            }
				        });
	});

	module.run(function($couchPotato) {
		module.lazy = $couchPotato;
	});
	return module;
});