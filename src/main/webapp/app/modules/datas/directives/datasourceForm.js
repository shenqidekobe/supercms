define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('datasourceForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/datas/directives/datasource-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
				            title : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/datas/datasources/validateTitle',
				                    data : {
					                    id : function() {
						                    return scope.datasource.id;
					                    }
				                    }
				                }
				            },
				            editSortId : {
				                required : true
				            },
				            modelId: {
				            	 required : true
				            },
				            origin: {
				            	 required : true
				            },
				            description: {
				            	 required : true
				            }
				            
				        },
				        submitHandler : function() {
					        if (scope.datasourceDialog.isNew) {
						        scope.createDatasourceRequest();
					        } else {
						        scope.saveDatasourceRequest();
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
