define([ 'modules/modules/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('customForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/modules/directives/custom-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
		    	jQuery.validator.addMethod("customTemplate", function(value, element){
		    		if(scope.custom.customTemplateId){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}, "必选字段");
			    var validator = form.validate(angular.extend({
			    	ignore: [],
			        onfocusout : false,
			        rules : {
			            title : {
			                required : true,
			                maxlength : 32,
			                remote : {
			                    url : resource.server + '/modules/customs/validateTitle',
			                    data : {
				                    id : function() {
					                    return scope.custom.id;
				                    }
			                    }
			                }
			            },
			            description : {
			                required : true,
			                maxlength : 64
			            },
			            editSortId : {
			                required : true
			            },
			            location : {
			                required : true,
			                maxlength : 48,
			                remote : {
			                    url : resource.server + '/modules/customs/validateLocation',
			                    data : {
				                    id : function() {
					                    return scope.custom.id;
				                    }
			                    }
			                }
			            },
			            customTemplateId : {
			            	customTemplate : true
			            },
			        },
			        submitHandler : function() {
				        if (scope.customDialog.isNew) {
					        scope.createCustomRequest();
				        } else {
					        scope.saveCustomRequest();
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
