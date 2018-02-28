define(['filemanager/module'], function (module) {

    'use strict';

    module.registerController('FilemanagerCtrl', function ($scope, $http, subject) {
    	var username = subject.authenticationInfo.principal;
    	$scope.src = 'app/filemanager/index.html?fileRoot=resource/custom/' + username + "/";
    	$scope.showUserDir = function(){
    		$scope.src = 'app/filemanager/index.html?fileRoot=resource/custom/' + username + "/";
    	};
    	$scope.showSiteDir = function(){
    		$scope.src = 'app/filemanager/index.html?fileRoot=sites/';
    	}
    });

});
