'use strict';

/**
 * @ngdoc overview
 * @name app [smartadminApp]
 * @description # app [smartadminApp]
 * 
 * Main module of the application.
 */

define([ 'angular', 'angular-couch-potato', 'angular-resource', 'angular-ui-router', 'angular-animate', 'angular-bootstrap', 'angular-sanitize', 'smartwidgets', 'notification', 'angular-shiro' ], function(ng, couchPotato) {

	//  create root module and load it's denpendency modules
	var app = ng.module('app', [ 'ngSanitize', 'scs.couch-potato', 'ngAnimate', 'ui.router', 'ui.bootstrap', 'angularShiro', 'app.misc', 'app.auth', 'app.layout', 'app.forms', 'app.tables', 'app.ui', 'app.widgets', 'app.services',
	        'app.dashboard', 'app.modules', 'app.templates', 'app.datas', 'app.settings', 'app.manuscripts', 'app.filemanager', 'app.security' ]);
	couchPotato.configureApp(app);

	// define app's constant
	app.constant("CTX", "http://127.0.0.1");
	app.constant("MakeFileWebSocket", "ws://127.0.0.1/endpoint/makeFileSocket");
	app.constant("MakeFileWebSocketSock", "http://127.0.0.1/endpoint/sockjs/makeFileSocket");
	app.value("PAGESIZE", 10);

	app.config([ 'angularShiroConfigProvider', function(config) {
		config.setAuthenticateUrl('/security/login');
		config.setFilter('/**', 'authc');

	} ]);

	// config $httpProvider
	app.config(function($provide, $httpProvider) {
		// define Request Interceptor
		$provide.factory('httpRequestInterceptor', function() {
			return {
				request : function(config) {
					return config;
				}
			};
		});
		// define Request or Response Error Interceptor
		$provide.factory('ErrorHttpInterceptor', function($q,$location) {
			var errorCounter = 0;
			function notifyError(rejection) {
				if(angular.isUndefined(rejection.data))
					return;
				$.bigBox({
				    title : rejection.status + ' ' + rejection.statusText,
				    content : rejection.data.message,
				    color : "#C46A69",
				    icon : "fa fa-warning shake animated",
				    number : ++errorCounter,
				    timeout : 6000
				});
			}
			return {
			    // 一个请求发送失败或者被拦截器拒绝了。请求异常拦截器会俘获那些被上一个请求拦截器中断的请求。它可以用来恢复请求或者有时可以用来撤销请求之前所做的配置，比如说关闭进度条，激活按钮和输入框什么之类的。
			    requestError : function(rejection) {
				    // show notification
				    notifyError(rejection);
				    // Return the promise rejection.
				    return $q.reject(rejection);
			    },
			    // 后台调用失败了。也有可能它被一个请求拦截器拒绝了，或者被上一个响应拦截器中断了。在这种情况下，响应异常拦截器可以帮助我们恢复后台调用。
			    responseError : function(rejection) {
				    // show notification
				    if(!angular.isUndefined(rejection.data)&&(rejection.data.message=="用户名不存在"||rejection.data.message=="密码不正确")){
				    	notifyError(rejection);
			    		$location.path('/login');
			    		return;
			    	}
				    notifyError(rejection);
				    // Return the promise rejection.
				    return $q.reject(rejection);
			    }
			};
		});
		$httpProvider.interceptors.push('ErrorHttpInterceptor');
		$httpProvider.interceptors.push('httpRequestInterceptor');
	});

	// app run
	app.run(function($couchPotato, $rootScope, $state, $stateParams, $templateCache, $location, $http, subject, usernamePasswordToken,$injector) {
		app.lazy = $couchPotato;
		$rootScope.$state = $state;
		$rootScope.$stateParams = $stateParams;
		$rootScope.logout = function() {
			$http.post('/security/logout').then(function() {
				localStorage.autoLogin=false;
				sessionStorage.isLogin=false;
				$location.path('/logout');
			});
		};
		console.info("localStorage.autoLogin="+localStorage.autoLogin+",sessionStorage.isLogin="+sessionStorage.isLogin);
		if(localStorage.autoLogin=='true'&&sessionStorage.isLogin!='true'&&$location.search().rn!='blank'){
			usernamePasswordToken.rememberMe = true;
			usernamePasswordToken.username = localStorage.cmsLocalUserName=='null'?"":localStorage.cmsLocalUserName;
			usernamePasswordToken.password = localStorage.cmsLocalPassword=='null'?"":localStorage.cmsLocalPassword;
			
			console.info("system starting auto login..."+angular.toJson(usernamePasswordToken));
			
			subject.login(usernamePasswordToken).then(function(){
				var user = $injector.get('User');
				user.loadMenus(function(){
					$location.path('/dashboard');
				});
			});
		}
		
		$rootScope.sessionId=$location.search().sessionId;
		
		if(!angular.isUndefined($rootScope.sessionId)){
			sessionStorage.isLogin=true;
		}
	});

	return app;
});
