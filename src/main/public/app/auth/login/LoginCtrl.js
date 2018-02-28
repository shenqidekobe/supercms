define(['auth/module'], function (module) {

    "use strict";

    module.registerController('LoginCtrl', function ($scope, $state, GooglePlus, User, ezfb) {

       $scope.login = function(){
    	 console.log(11);  
       };
    })
});
