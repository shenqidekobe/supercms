define([ 'modules/settings/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('jobTaskForm', function(resource) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/settings/directives/jobTask-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
			    		    taskTitle : {
					            required : true,
					            maxlength : 100
				            },
				        	taskName : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : resource.server + '/systems/tasks/validateTaskName',
				                    data : {
					                    id : function() {
						                    return scope.jobTask.id;
					                    }
				                    }
				                }
				            },
				            taskType : {
				                required : true,
				                maxlength : 64
				            },
				            taskExperess : {
					            required : true,
					            maxlength : 200,
					            remote : {
				                    url : resource.server + '/systems/tasks/validateTaskExperess',
				                    data : {
					                    id : function() {
						                    return scope.jobTask.id;
					                    }
				                    },
				                    message:"该时间表达式不合规范，请修正。"
				                }
				            },
				            taskTarget : {
					            required : true
				            }
				        },
				        submitHandler : function() {
					        if (scope.jobTaskDialog.isNew) {
						        scope.createjobTaskRequest();
					        } else {
						        scope.savejobTaskRequest();
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
