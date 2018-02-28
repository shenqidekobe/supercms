define([ 'angular', 'angular-couch-potato', 'angular-ui-router' ], function(ng, couchPotato) {
	"use strict";
	var module = ng.module('app.security', [ 'ui.router', 'app.services' ]);
	couchPotato.configureApp(module);

	module.config(function($stateProvider, $couchPotatoProvider) {
		$stateProvider.state('app.security', {
		    abstract : true,
		    data : {
			    title : '安全中心'
		    },
		    resolve : {
			    deps : $couchPotatoProvider.resolveDependencies([ 'modules/ui/directives/smartJquiDialogLauncher', 'modules/ui/directives/smartJquiDialog', 'modules/services/resource' ])
		    }
		}).state('app.security.user', {
		    url : '/security/user',
		    data : {
			    title : '用户管理'
		    },
		    views : {
			    "content@app" : {
			        controller : 'UserCtrl',
			        templateUrl : "app/modules/security/views/user.html",
			        resolve : {
				        deps : $couchPotatoProvider.resolveDependencies([ 'modules/security/controllers/UserCtrl', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/security/directives/userForm','modules/security/directives/assignDatasourceForm','modules/forms/directives/input/checklistModel','modules/ui/directives/smartTreeview' ])
			        }
			    }
		    }
		}).state('app.security.role', {
		    url : '/security/role',
		    data : {
			    title : '角色管理'
		    },
		    views : {
			    "content@app" : {
			        controller : 'RoleCtrl',
			        templateUrl : "app/modules/security/views/role.html",
			        resolve : {
				        deps : $couchPotatoProvider.resolveDependencies([ 'modules/security/controllers/RoleCtrl', 'modules/tables/directives/datatables/datatableCustomToolbar', 'modules/security/directives/roleForm','modules/security/directives/permissionForm','modules/forms/directives/input/checklistModel' ])
			        }
			    }
		    }
		}).state('app.security.menu', {
		    url : '/security/menu',
		    data : {
			    title : '菜单管理'
		    },
		    views : {
			    "content@app" : {
			        controller : 'MenuCtrl',
			        templateUrl : "app/modules/security/views/menu.html",
			        resolve : {
				        deps : $couchPotatoProvider.resolveDependencies([ 'modules/security/controllers/MenuCtrl',  'modules/tables/directives/datatables/datatableTreetable', 'modules/security/directives/menuForm','modules/forms/directives/input/dwSelectTree' ])
			        }
			    }
		    }
		})
	});

	module.run(function($couchPotato) {
		module.lazy = $couchPotato;
	});
	return module;
});