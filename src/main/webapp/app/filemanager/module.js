define([
    'angular',
    'angular-couch-potato',
    'angular-ui-router',
    'angular-resource'
], function (ng, couchPotato) {
    'use strict';

    var module = ng.module('app.filemanager', [
        'ui.router',
        'ngResource'
    ]);

    module.config(function ($stateProvider, $couchPotatoProvider) {
        $stateProvider
            .state('app.filemanager', {
                url: '/filemanager',
                views: {
                    "content@app": {
                        controller: 'FilemanagerCtrl',
                        templateUrl: 'app/filemanager/filemanager.html',
                        resolve: {
                            deps: $couchPotatoProvider.resolveDependencies([
                                'filemanager/FilemanagerCtrl'
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
                    title: '网盘'
                }
            });
    });

    couchPotato.configureApp(module);

    module.run(function($couchPotato, $rootScope, $state){
        module.lazy = $couchPotato;
    });

    return module;
});