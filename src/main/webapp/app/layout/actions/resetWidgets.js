define(['layout/module'], function (module) {

    "use strict";

    module.registerDirective('resetWidgets', function($state){

        return {
            restrict: 'A',
            link: function(scope, element){
                element.on('click', function(){
                    $.SmartMessageBox({
                        title : "<i class='fa fa-refresh' style='color:green'></i> 删除本地存储",
                        content : "是否重置所有保存的组件配置并删除本地存储？",
                        buttons : '[否][是]'
                    }, function(ButtonPressed) {
                        if (ButtonPressed == "是" && localStorage) {
                            localStorage.clear();
                            location.reload()
                        }
                    });

                });
            }
        }

    })

});
