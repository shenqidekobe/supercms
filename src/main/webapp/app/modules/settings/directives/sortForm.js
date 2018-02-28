define([ 'modules/settings/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('sortForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/settings/directives/sort-form.tpl.html',
		    scope : true,
		    link : function(scope, form, attr){
		    	var validator = form.validate(angular.extend({
			        onfocusout : false,
			        rules : {
			        	sortName : {
			                required : true,
			                maxlength : 32,
			                remote : {
			                    url : resource.server + '/systems/sort/validateSortName',
			                    data : {
				                    id : function() {
					                    return scope.sort.id;
				                    },
				                    sortType : function() {
					                    return scope.sort.sortType;
				                    }
			                    }
			                }
			            },
			            sortType : {
			            	required : true
			            },
			            editParentId : {
			            	required : false
			            }
			        },
			        submitHandler : function() {
				        if (scope.sortDialog.isNew) {
					        scope.createSortRequest();
				        } else {
					        scope.saveSortRequest();
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
