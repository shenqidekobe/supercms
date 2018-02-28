define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('fieldForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/datas/directives/field-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
				            title : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/datas/fields/validateTitle',
				                    data : {
					                    id : function() {
						                    return scope.field.id;
					                    }
				                    }
				                }
				            },
				            fieldCode : {
				                required : true,
				                remote : {
				                    url : resource.server + '/datas/fields/validateCode',
				                    data : {
					                    code : function() {
						                    return scope.field.tableCode;
					                    }
				                    }
				                }
				            },
				            dataLength : {
				            	number : true
				            },
				            ordinal : {
				            	required : true,
				            	number : true
				            },
				            description : {
				                required : true,
				                maxlength : 64
				            }
				        },
				        submitHandler : function() {
					        if (scope.fieldDialog.isNew) {
						        scope.createFieldRequest();
					        } else {
						        scope.saveFieldRequest();
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
