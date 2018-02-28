define(['angular','angular-couch-potato','angular-ui-router'], function (ng, couchPotato) {
	
    "use strict";
    
    var module = ng.module('app.manuscripts', ['ui.router','app.services']);
    couchPotato.configureApp(module);

    module.config(function ($stateProvider, $couchPotatoProvider) {
        $stateProvider
            .state('app.manuscripts', {
                abstract: true,
                data: {
                    title: '投稿设置'
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
            .state('app.manuscripts.organ', {
                url: '/manuscripts/organ',
                data: {
                    title: '单位管理'
                },
                views: {
                    "content@app": {
                        controller: 'OrganCtrl',
                        templateUrl: "app/modules/manuscripts/views/organ.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/manuscripts/controllers/OrganCtrl',
                                'modules/tables/directives/datatables/datatableCustomToolbar',
                                'modules/forms/directives/input/smartDatepicker',
                                'modules/manuscripts/directives/organForm'
                            ])
                        }
                    }
                }
            })
            .state('app.manuscripts.member', {
                url: '/manuscripts/member',
                data: {
                    title: '用户管理'
                },
                views: {
                    "content@app": {
                        controller: 'MemberCtrl',
                        templateUrl: "app/modules/manuscripts/views/member.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/manuscripts/controllers/MemberCtrl',
                                'modules/tables/directives/datatables/datatableCustomToolbar',
                                'modules/manuscripts/directives/memberForm'
                            ])
                        }
                    }
                }
            }).state('app.manuscripts.audit', {
                url: '/manuscripts/audit',
                data: {
                    title: '审稿分配'
                },
                params:{memberId:null,memberName:null},
                views: {
                    "content@app": {
                        controller: 'AuditAllotCtrl',
                        templateUrl: "app/modules/manuscripts/views/auditAllot.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/manuscripts/controllers/AuditAllotCtrl',
                                'modules/forms/directives/input/smartDuallistbox',
                                'modules/ui/directives/smartProgressbar',
                            ])
                        }
                    }
                }
            }).state('app.manuscripts.send', {
                url: '/manuscripts/send',
                data: {
                    title: '投稿栏目分配'
                },
                params:{memberId:null,memberName:null},
                views: {
                    "content@app": {
                        controller: 'SendAllotCtrl',
                        templateUrl: "app/modules/manuscripts/views/sendAllot.html",
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'modules/manuscripts/controllers/SendAllotCtrl',
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