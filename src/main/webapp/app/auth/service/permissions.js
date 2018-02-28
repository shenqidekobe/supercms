define(['auth/module', 'lodash'], function (module, _) {
	
	'use strict';
	 //将permissions存放到factory变量中,使之一直处于内存中,实现全局变量的作用
	 module.registerFactory('permissions', function ($rootScope) {
	    var permissionList;
	    return {
	      setPermissions: function(permissions) {
	         permissionList = permissions;
	         $rootScope.permissions=permissions;
	         //通过$broadcast广播事件,当权限发生变更的时候
	         $rootScope.$broadcast('permissionsChanged')
	      },
	      hasPermission: function (permission) {
	        permission = permission.trim();
	        return _.some(permissionList, function(item) {
	          if(_.isString(item.name))
	        	  return item.name.trim() == permission
	        });
	      }
	    }
    });
});