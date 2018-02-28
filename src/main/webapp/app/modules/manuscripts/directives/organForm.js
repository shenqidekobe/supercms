define([ 'modules/manuscripts/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	
	'use strict';
	
	return module.registerDirective('organForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/manuscripts/directives/organ-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
				        	platformOrganId : {
				                required : false,
				                maxlength : 64,
				                remote : {
				                    url : resource.server + '/manuscripts/organs/validatePlatformOrganId',
				                    data : {
					                    id : function() {
						                    return scope.organ.id;
					                    }
				                    }
				                }
				            },
				            organName : {
				                required : true,
				                maxlength : 64
				            },
				            manuMaxCount : {
					            required : true,
					            number:true
				            },
				            abortTime : {
					            required : true
				            },
				            organLevel : {
				            	required : true
				            }
				        },
				        submitHandler : function() {
					        if (scope.organDialog.isNew) {
						        scope.createorganRequest();
					        } else {
						        scope.saveorganRequest();
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
