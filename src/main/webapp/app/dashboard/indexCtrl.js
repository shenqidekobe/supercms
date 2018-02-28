define(['dashboard/module'], function (module) {

    'use strict';

    module.registerController('indexCtrl', function ($scope, $http) {
    	$http.get('api/user.json').then(function(response){
	    });
    });

});
