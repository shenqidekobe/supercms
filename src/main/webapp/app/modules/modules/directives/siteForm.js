define([ 'modules/modules/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('siteForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/modules/directives/site-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	jQuery.validator.addMethod("homeTemplate", function(value, element){
		    		if(scope.site.homeTemplateId){
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
				                    url : resource.server + '/modules/sites/validateTitle',
				                    data : {
					                    id : function() {
						                    return scope.site.id;
					                    }
				                    }
				                }
				            },
				            description : {
				                required : true,
				                maxlength : 64
				            },
				            testDomain : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/modules/sites/validateTestDomain',
				                    data : {
					                    id : function() {
						                    return scope.site.id;
					                    }
				                    }
				                }
				            },
				            productDomain : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/modules/sites/validateProductDomain',
				                    data : {
					                    id : function() {
						                    return scope.site.id;
					                    }
				                    }
				                }
				            },
				            dirName : {
				                required : true,
				                maxlength : 16,
				                remote : {
				                    url : resource.server + '/modules/sites/validateDirName',
				                    data : {
					                    id : function() {
						                    return scope.site.id;
					                    }
				                    }
				                }
				            },
				            homeTemplateId : {
					            homeTemplate : true
				            }
				        },
				        submitHandler : function() {
					        if (scope.siteDialog.isNew) {
						        scope.createSiteRequest();
					        } else {
						        scope.saveSiteRequest();
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
