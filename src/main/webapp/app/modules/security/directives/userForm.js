define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('userForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/security/directives/user-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
			    var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
			            name : {
			                required : true,
			                maxlength : 32,
			                remote : {
			                    url : resource.server + '/security/users/validateUsername',
			                    data : {
				                    id : function() {
					                    return scope.user.id;
				                    }
			                    }
			                }
			            },
			            username : {
			                required : true,
			                maxlength : 32
			            },
			            password : {
			                required : true,
			                minlength : 6,
			                maxlength : 32
			            },
			            repeat : {
			            	equalTo: "#password"
			            },
			            email : {
			                required : true,
			                maxlength : 32
			            },
			            telephone : {
			                required : true,
			                maxlength : 11
			            },
			            roleId : {
			            	 required : true,
			            },
			            description : {
				            maxlength : 64
			            }
			        },
			        submitHandler : function() {
				        if (scope.userDialog.isNew) {
					        scope.createUserRequest();
				        } else {
					        scope.saveUserRequest();
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
