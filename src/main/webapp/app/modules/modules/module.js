define(['angular',
    'angular-couch-potato',
    'angular-ui-router',
    'angular-hierarchical-selector'
], function (ng, couchPotato) {
    "use strict";
    var module = ng.module('app.modules', [
        'ui.router','app.services','hierarchical-selector'
    ]);
    couchPotato.configureApp(module);

    module.config(function ($stateProvider, $couchPotatoProvider) {
        $stateProvider
            .state('app.modules', {
                abstract: true,
                data: {
                    title: '模块'
                },
                resolve: {
                    deps: $couchPotatoProvider.resolveDependencies([
                        'modules/ui/directives/smartJquiDialogLauncher',
                        'modules/ui/directives/smartJquiDialog',
                        'modules/widgets/services/smallBoxMessage',
                        'modules/services/resource',
                        'modules/services/makeSocket'
                    ])
                }
            })
            .state('app.modules.site', {
                url: '/modules/site',
                data: {
                    title: '站点'
                },
                views: {
                    "content@app": {
                        controller: 'SiteCtrl',
                        templateUrl: "app/modules/modules/views/site.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/modules/controllers/SiteCtrl',
                                'modules/tables/directives/datatables/datatableCustomToolbar',
                                'modules/modules/directives/siteForm'
                            ])
                        }
                    }
                }
            })
            .state('app.modules.column', {
                url: '/modules/column',
                data: {
                    title: '栏目'
                },
                views: {
                    "content@app": {
                        controller: 'ColumnCtrl',
                        templateUrl: "app/modules/modules/views/column.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/modules/controllers/ColumnCtrl',
                                'modules/tables/directives/datatables/datatableTreetable',
                                'modules/modules/directives/columnForm',
                                'modules/forms/directives/input/dwSelectTree'
                            ])
                        }
                    }
                }
            })
              .state('app.modules.custom', {
                url: '/modules/custom',
                data: {
                    title: '自定义'
                },
                views: {
                    "content@app": {
                        controller: 'CustomCtrl',
                        templateUrl: "app/modules/modules/views/custom.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/modules/controllers/CustomCtrl',
                                'modules/tables/directives/datatables/datatableCustomToolbar',
                                'modules/modules/directives/customForm',
                                'modules/forms/directives/input/dwSelectTree'
                               
                            ])
                        }
                    }
                }
            })
             .state('app.modules.make', {
                url: '/modules/make',
                data: {
                    title: '批量更新'
                },
                views: {
                    "content@app": {
                        controller: 'MakeCtrl',
                        templateUrl: "app/modules/modules/views/make.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/modules/controllers/MakeCtrl',
                                'modules/forms/directives/input/dwSelectTree',
                                'modules/forms/directives/input/smartDatepicker',
                                'modules/widgets/directives/selectColumnOption',
                                'modules/modules/directives/makeProgressBar',
                                'modules/ui/directives/smartJquiTabs',
                                'modules/ui/directives/smartProgressbar',
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