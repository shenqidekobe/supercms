define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('menuForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/security/directives/menu-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
			    var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
			            title : {
			                required : true,
			                maxlength : 32,
			                remote : {
			                    url : resource.server + '/security/menus/validateTitle',
			                    data : {
				                    id : function() {
					                    return scope.menu.id;
				                    }
			                    }
			                }
			            },
			            uisref : {
			                required : true,
			                maxlength : 32
			            },
			            icon : {
			                maxlength : 32
			            },
			            lvl : {
			            	required : true,
			            	number: true
			            }
			        },
			        submitHandler : function() {
				        if (scope.menuDialog.isNew) {
					        scope.createMenuRequest();
				        } else {
					        scope.saveMenuRequest();
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
