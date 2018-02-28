define([ 'modules/modules/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn'], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('sortForm', function(CTX) {
		return {
		    restrict : 'E',
		    replace : true,
		    templateUrl : 'app/modules/widgets/directives/sort-form.tpl.html',
		    scope : true,
		    link : function(scope,form,attr){
		    	scope.sort.sortName='';
		    	 var validator = form.validate(angular.extend({
				        onfocusout : false,
				        rules : {
				            sortName : {
				                required : true,
				                maxlength : 32,
				                remote : {
				                    url : CTX + '/systems/sort/verify',
				                    data : {
					                    id : function() {
						                    return scope.sort.id;
					                    },
					                    sortType:function(){
					                    	return scope.sort.sortType;
					                    }
				                    },
				                    cache:false,
							    	async: false,
					                delay: 1000,
					                message:'分类名已存在，请重新输入'
				                }
				            },
				        },
				        submitHandler : function() {
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
