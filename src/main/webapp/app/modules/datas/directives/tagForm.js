define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('tagForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/datas/directives/tag-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr) {
			    var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
				        title : {
				            required : true,
				            maxlength : 32,
				            remote : {
				                url : resource.server + '/datas/tags/validateTitle',
				                data : {
					                id : function() {
						                return scope.tag.id;
					                }
				                }
				            }
				        }

			        },
			        submitHandler : function() {
				        if (scope.tagDialog.isNew) {
					        scope.createTagRequest();
				        } else {
					        scope.saveTagRequest();
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
