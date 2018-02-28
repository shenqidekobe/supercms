define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('roleForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/security/directives/role-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
			    var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
			            name : {
			                required : true,
			                maxlength : 32,
			                remote : {
			                    url : resource.server + '/security/roles/validateName',
			                    data : {
				                    id : function() {
					                    return scope.role.id;
				                    }
			                    }
			                }
			            },
			            description : {
				            maxlength : 64
			            }
			        },
			        submitHandler : function() {
				        if (scope.roleDialog.isNew) {
					        scope.createRoleRequest();
				        } else {
					        scope.saveRoleRequest();
				        }
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
