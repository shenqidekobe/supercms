define([ 'auth/module' ], function(module) {

	'use strict';

	return module.registerFactory('User', function($http, $q, subject) {
		var loadMenus = function(fn){
			$http.get('/security/menus').then(function(response) {
				UserModel.username = response.data.username;
				UserModel.picture = response.data.picture;
				UserModel.menus = response.data.menus;
				dfd.resolve(UserModel)
				if(fn){
					fn();
				}
			});
		};
		var dfd = $q.defer();
		var UserModel = {
		    initialized : dfd.promise,
		    username : undefined,
		    picture : undefined,
		    menus : undefined,
		    loadMenus : loadMenus
		};
		loadMenus(null);
		return UserModel;
	});
});
