define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('modelForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/datas/directives/model-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
			    var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
			            title : {
			                required : true,
			                maxlength : 32,
			                remote : {
			                    url : resource.server + '/datas/models/validateTitle',
			                    data : {
				                    id : function() {
					                    return scope.model.id;
				                    }
			                    }
			                }
			            },
			            tableCode : {
			                required : true,
			                number: true,
			                remote : {
			                    url : resource.server + '/datas/models/validateCode',
			                    data : {
				                    id : function() {
					                    return scope.model.id;
				                    }
			                    }
			                }
			            },
			            description : {
			                required : true,
			                maxlength : 64
			            }
			        },
			        submitHandler : function() {
				        if (scope.modelDialog.isNew) {
					        scope.createModelRequest();
				        } else {
					        scope.saveModelRequest();
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
