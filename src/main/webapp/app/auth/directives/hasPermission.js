define(['auth/module'], function(module){
    "use strict";

    //has-permission属性校验permission的name,如果当前用户有则显示,没有则隐藏.
    return module.registerDirective('hasPermission', function(permissions){
    	 return {
    		    link: function(scope, element, attrs) {
    		      if(!_.isString(attrs.hasPermission))
    		         throw "hasPermission value must be a string";
    		      var value = attrs.hasPermission.trim();
    		      var notPermissionFlag = value[0] === '!';
    		      if(notPermissionFlag) {
    		        value = value.slice(1).trim();
    		      }
    		      function toggleVisibilityBasedOnPermission() {
    		        var hasPermission = permissions.hasPermission(value);
    		        if(hasPermission && !notPermissionFlag || !hasPermission && notPermissionFlag)
    		          element.show();
    		        else
    		          element.hide();
    		      }
    		      toggleVisibilityBasedOnPermission();
    		      scope.$on('permissionsChanged', toggleVisibilityBasedOnPermission);
    		    }
    		 };
    })
});