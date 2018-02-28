define([ 'modules/modules/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('columnForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/modules/directives/column-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
		    	jQuery.validator.addMethod("homeTemplate", function(value, element){
		    		if(scope.column.homeTemplateId){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}, "必选字段");
		    	jQuery.validator.addMethod("contentTemplate", function(value, element){
		    		if(scope.column.contentTemplateId){
		    			return true;
		    		}else{
		    			return false;
		    		}
		    	}, "必选字段");
		    	jQuery.validator.addMethod("datasource", function(value, element){
		    		if(scope.column.datasourceId){
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
			                    url : resource.server + '/modules/columns/validateTitle',
			                    data : {
				                    id : function() {
					                    return scope.column.id;
				                    }
			                    }
			                }
			            },
			            description : {
			                required : true,
			                maxlength : 64
			            },
			            editParentId : {
				            required : true
			            },
			            dirName : {
			                required : true,
			                maxlength : 16,
			                remote : {
			                    url : resource.server + '/modules/columns/validateDirName',
			                    data : {
				                    id : function() {
					                    return scope.column.id;
				                    }
			                    }
			                }
			            },
			            datasourceId : {
				            datasource : true
			            },
			            homeTemplateId : {
			            	homeTemplate : true
			            },
			            contentTemplateId : {
			            	contentTemplate : true
			            }
			        },
			        submitHandler : function() {
				        if (scope.columnDialog.isNew) {
					        scope.createColumnRequest();
				        } else {
					        scope.saveColumnRequest();
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
