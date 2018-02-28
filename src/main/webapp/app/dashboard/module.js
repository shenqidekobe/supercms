define([
    'angular',
    'angular-couch-potato',
    'angular-ui-router',
    'angular-resource'
], function (ng, couchPotato) {
    'use strict';

    var module = ng.module('app.dashboard', [
        'ui.router',
        'ngResource'
    ]);

    module.config(function ($stateProvider, $couchPotatoProvider) {
        $stateProvider
            .state('app.dashboard', {
                url: '/dashboard',
                views: {
                    "content@app": {
                        controller: 'DashboardCtrl',
                        templateUrl: 'app/dashboard/dashboard.html',
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'dashboard/DashboardCtrl'
                            ])
                        },
                        controllerProvider : function($rootScope){
                            if($rootScope.$state.isLogin == false){
                                $rootScope.$state.go('login');
                            }
                            return function(){};
                        }
                    }
                },
                data:{
                    title: '控制台'
                }
            }) .state('app.index', {
                url: '/index',
                views: {
                    "content@app": {
                        controller: 'indexCtrl',
                        templateUrl: 'app/dashboard/index.html',
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'dashboard/indexCtrl'
                            ])
                        },
                    }
                },
                data:{
                    title: 'Dashboard'
                }
            });
    });

    couchPotato.configureApp(module);

    module.run(function($couchPotato, $rootScope, $state){
        module.lazy = $couchPotato;
    });

    return module;
});