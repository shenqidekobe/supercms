define(['angular','angular-couch-potato','angular-ui-router'], function (ng, couchPotato) {
    "use strict";
    
    var module = ng.module('app.settings', ['ui.router','app.services']);
    couchPotato.configureApp(module);

    module.config(function ($stateProvider, $couchPotatoProvider) {
        $stateProvider
            .state('app.settings', {
                abstract: true,
                data: {
                    title: '系统设置'
                },
                resolve: {
                    deps: $couchPotatoProvider.resolveDependencies([
                        'modules/ui/directives/smartJquiDialogLauncher',
                        'modules/ui/directives/smartJquiDialog',
                        'modules/widgets/services/smallBoxMessage',
                        'modules/services/resource'
                    ])
                }
            })
             .state('app.settings.task', {
                url: '/settings/task',
                data: {
                    title: '任务管理'
                },
                views: {
                    "content@app": {
                        controller: 'JobTaskCtrl',
                        templateUrl: "app/modules/settings/views/task.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/settings/controllers/JobTaskCtrl',
                                'modules/tables/directives/datatables/datatableCustomToolbar',
                                'modules/settings/directives/jobTaskForm',
                                'modules/widgets/directives/paginationWidget'
                            ])
                        }
                    }
                }
            })
            .state('app.settings.plugin', {
                url: '/settings/plugin',
                data: {
                    title: '插件管理'
                },
                views: {
                    "content@app": {
                        controller: 'PlugInCtrl',
                        templateUrl: "app/modules/settings/views/plugin.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/settings/controllers/PlugInCtrl',
                                'modules/tables/directives/datatables/datatableCustomToolbar',
                                'modules/settings/directives/plugInForm'
                            ])
                        }
                    }
                }
            })
            .state("app.settings.pluginfile",{
            	url:'/settings/pluginfile',
            	data:{
            		title:"插件文件管理"
            	},
            	params:{
            		pluginPath:null
            	},
            	views:{
            		"content@app":{
            			controller:'PlugInFileCtrl',
            			templateUrl:"app/modules/settings/views/pluginFile.html",
            			resolve:{
            				deps:$couchPotatoProvider.resolveDependencies([
            				    'modules/settings/controllers/PlugInFileCtrl'])
            			}
            		}
            	}
            	
            })
            .state("app.settings.sort",{
            	url:'/settings/sort',
            	data:{
            		title:"分类管理"
            	},
            	views:{
            		"content@app":{
            			controller:'SortManageCtrl',
            			templateUrl:"app/modules/settings/views/sorts.html",
            			resolve:{
            				deps:$couchPotatoProvider.resolveDependencies([
            				    'modules/settings/controllers/SortManageCtrl',
            				    'modules/tables/directives/datatables/datatableTreetable',
                                'modules/settings/directives/sortForm',
                                'modules/forms/directives/input/dwSelectTree'
            				   ])
            			}
            		}
            	}
            })
            .state("app.settings.systemlogs",{
            	url:'/settings/systemlogs',
            	data:{
            		title:"系统日志"
            	},
            	views:{
            		"content@app":{
            			controller:'SystemLogsCtrl',
            			templateUrl:"app/modules/settings/views/systemLogs.html",
            			resolve:{
            				deps:$couchPotatoProvider.resolveDependencies([
            				    'modules/settings/controllers/SystemLogsCtrl',
            				    'modules/tables/directives/datatables/datatableCustomToolbar',
            				    'modules/forms/directives/input/smartDatepicker',
            				    'modules/ui/directives/smartProgressbar',
            				    ])
            			}
            		}
            	}
            })
            .state("app.settings.producelogs",{
            	url:'/settings/producelogs',
            	data:{
            		title:"生成日志"
            	},
            	views:{
            		"content@app":{controller:'ProduceLogsCtrl',
	            		templateUrl:'app/modules/settings/views/produceLogs.html',
	            		resolve:{
	            			deps:$couchPotatoProvider.resolveDependencies([
	            			    'modules/settings/controllers/ProduceLogsCtrl',
	            			    'modules/tables/directives/datatables/datatableCustomToolbar',
	            			    'modules/forms/directives/input/smartDatepicker',
	            			   ])
	            		}
            		}
            	}
            })
            .state("app.settings.params",{
            	url:'/settings/params',
            	data:{
            		title:"系统参数"
            	},
            	views:{
            		"content@app":{controller:'ParamsCtrl',
	            		templateUrl:'app/modules/settings/views/params.html',
	            		resolve:{
	            			deps:$couchPotatoProvider.resolveDependencies([
	            			    'modules/settings/controllers/ParamsCtrl',
	            			   ])
	            		}
            		}
            	}
            })
             .state("app.settings.indexs",{
            	url:'/settings/indexs',
            	data:{
            		title:"重建数据索引"
            	},
            	views:{
            		"content@app":{controller:'IndexsCtrl',
	            		templateUrl:'app/modules/settings/views/indexs.html',
	            		resolve:{
	            			deps:$couchPotatoProvider.resolveDependencies([
	            			    'modules/settings/controllers/IndexsCtrl',
	            			   ])
	            		}
            		}
            	}
            })
    });

    module.run(function ($couchPotato) {
        module.lazy = $couchPotato;
    });
    return module;
});