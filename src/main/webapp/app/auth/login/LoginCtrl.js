define([ 'auth/module' ], function(module) {

	"use strict";

	module.registerController('LoginCtrl', function($scope,$location,subject, usernamePasswordToken, $injector) {
		$scope.usernamePasswordToken =usernamePasswordToken;
		//自动登陆标识，默认为true
		$scope.autoLogin=localStorage.autoLogin=='null'?true:localStorage.autoLogin=="false"?false:true;
		usernamePasswordToken.rememberMe = true;
		usernamePasswordToken.username = localStorage.cmsLocalUserName=='null'?"":localStorage.cmsLocalUserName;
		usernamePasswordToken.password = localStorage.cmsLocalPassword=='null'?"":localStorage.cmsLocalPassword;
		
		$scope.login = function() {
			if($scope.usernamePasswordToken.username==""||$scope.usernamePasswordToken.password==""){
				return;
			}
			localStorage.autoLogin=$scope.autoLogin;
			localStorage.cmsLocalUserName=$scope.usernamePasswordToken.username;
			localStorage.cmsLocalPassword=$scope.usernamePasswordToken.password;
			subject.login($scope.usernamePasswordToken).then(function(){
				var user = $injector.get('User');
				user.loadMenus(function(){
					sessionStorage.isLogin=true;
					$location.path('/dashboard');
				});
			});
		};
		$('body').keydown(function(event) {
			if (event.keyCode == 13){
				$scope.login();
				event.preventDefault();
			}
		});
	})
});
