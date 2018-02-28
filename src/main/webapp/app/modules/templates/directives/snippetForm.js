define(['modules/templates/module','bootstrap-validator'], function(module){

    "use strict";


    module.registerDirective('bootstrapSnippetForm', function(CTX){

        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'app/modules/templates/directives/snippet-form.tpl.html',
            link: function(scope,form){
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
                    	snippetName : {
                            group : '.col-md-6',
                            validators : {
                                notEmpty : {
                                    message : '请输入片段名称'
                                },
                                stringLength : {
                                    max : 64,
                                    message : '片段名称最多输入64个字符'
                                }
                            }
                        },
                        snippetTag : {
                            group : '.col-md-6',
                            validators : {
                                notEmpty : {
                                    message : '请输入片段TAG'
                                },
                                stringLength : {
                                    max : 32,
                                    message : '片段TAG最多输入32个字符'
                                },
                                remote:{
                                	 url: CTX+'/templates/snippet/verify',//默认 snippetTag 参数
                                     type: "get",
                                     async: false,
                                     delay: 1000,
                                     data:{ id: function(validator){return $('#snippetForm :input[name="id"]').val();}},
                                     message: '该片段TAG已存在，请重新输入', 
                                },
                                
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
                        snippetCode : {
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
                  /*  var $form = $(e.target),
                    validator = $form.data('bootstrapValidator');
                	var sortName= validator.getFieldElements('sortName').val();
                	if(sortName==""){
                		 validator.getFieldElements('sortName').parent().addClass('has-feedback');
                		 validator.getFieldElements('sortName').parent().addClass('has-error');
                	     validator.disableSubmitButtons(false);
                		 e.preventDefault();
                	}*/
            		scope.$broadcast('createSnippet');//向下广播创建片段事件
                    e.preventDefault();
                });
            }

        }



    })


});