define([ 'modules/settings/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('pluginForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/settings/directives/plugin-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
				        	fieldName : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/settings/plugins/validateFiledName',
				                    data : {
					                    id : function() {
						                    return scope.plugin.id;
					                    }
				                    }
				                }
				            },
				            plugName : {
				                required : true,
				                maxlength : 64
				            },
				            plugContent : {
					            required : true,
					            maxlength : 1024
				            },
				            jsDefinition : {
					            required : true
				            },
				            attr1 : {
					            required : false,
					            maxlength : 1024
				            }
				        },
				        submitHandler : function() {
					        if (scope.pluginDialog.isNew) {
						        scope.createpluginRequest();
					        } else {
						        scope.savepluginRequest();
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
