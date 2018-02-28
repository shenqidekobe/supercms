define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('permissionForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/security/directives/permission-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
			    var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
			           
			        },
			        submitHandler : function() {
					    scope.assignPermissionRequest();
			        }
			    }, formsCommon.validateOptions));

			    scope.$on('resetForm', function() {
				    form.find('label').removeClass('state-error').removeClass('state-success');
				    form.find('em.invalid').remove();
			    });
		    }
		}
	});
});
