define([ 'modules/manuscripts/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('memberForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/manuscripts/directives/member-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
				        	loginName : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/manuscripts/members/validateLoginName',
				                    data : {
					                    id : function() {
						                    return scope.member.id;
					                    }
				                    }
				                }
				            },
				            phone : {
				                required : true,
				                number:true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/manuscripts/members/validatePhone',
				                    data : {
					                    id : function() {
						                    return scope.member.id;
					                    }
				                    }
				                }
				            },
				            email : {
				                required : false,
				                email:true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/manuscripts/members/validateEmail',
				                    data : {
					                    id : function() {
						                    return scope.member.id;
					                    }
				                    }
				                }
				            },
				            memberName : {
				                required : true,
				                maxlength : 64
				            },
				            organId : {
				                required : true
				            },
				            password : {
				                required : true,
				                maxlength : 64
				            },
				            memberType : {
					            required : true,
				            },
				            remark : {
					            required : false,
					            maxlength : 500
				            }
				        },
				        submitHandler : function() {
					        if (scope.memberDialog.isNew) {
						        scope.creatememberRequest();
					        } else {
						        scope.savememberRequest();
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
