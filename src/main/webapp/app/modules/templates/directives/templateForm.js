define(['modules/templates/module','bootstrap-validator'], function(module){

    "use strict";


    /**
     * 由于template分index、list、content、custom四种类型，编辑模版的时候需要获取每种模版的分类
     * 而嵌套指令又无法编译ng-switch指令(导致无法验证sortId字段)，所以通过传递参数的方式
     * */
    module.registerDirective('bootstrapTemplateForm', function($http){

        return {
            restrict: 'E',
            replace: false,
            templateUrl: 'app/modules/templates/directives/template-form.tpl.html',
            link: function(scope, form){
                form.bootstrapValidator({
                	excluded :[':disabled',  ':hidden',':not(:visible)'],
                    feedbackIcons : {
                        valid : 'glyphicon glyphicon-ok',
                        invalid : 'glyphicon glyphicon-remove',
                        validating : 'glyphicon glyphicon-refresh'
                    },
                    submitHandler: function(validator, form, submitButton) {
                    },
                    onSuccess : function(form){
                    },
                    fields : {
                    	templateName : {
                            group : '.col-md-6',
                            validators : {
                                notEmpty : {
                                    message : '请输入模版名称'
                                },
                                stringLength : {
                                    max : 64,
                                    message : '模版名称最多输入64个字符'
                                }
                            }
                        },
                        sortId : {
                        	group : '.col-md-6',
                            validators : {
                                notEmpty : {
                                    message : '请选择一个分类'
                                }
                            }
                        },
                        templateCode : {
                            validators : {
                            	notEmpty : {
                                    message : '请输入模版代码'
                                },
                            }
                        }
                    }
                    
                }).on('error.form.bv', function(e) {
                    e.preventDefault();
                }).on('success.form.bv', function(e) {
                    /*var $form = $(e.target),
                    validator = $form.data('bootstrapValidator');
                    alert(validator.getFieldElements('templateName').val());*/
                	scope.$broadcast('createTemplate');//向下广播创建片段事件
                    e.preventDefault();
                });
            }

        }



    })


});