define(['angular',
    'angular-couch-potato',
    'angular-ui-router'], function(ng, couchPotato){

    var module = angular.module('app.ui', ['ui.router']);

    couchPotato.configureApp(module);

    module.run(function($couchPotato){
        module.lazy = $couchPotato
    });

    return module;
});