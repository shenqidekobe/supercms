define(["modules/graphs/module","morris"],function(a){"use strict";return a.registerDirective("morrisDonutGraph",function(){return{restrict:"E",replace:!0,template:'<div class="chart no-padding"></div>',link:function(a,b){Morris.Donut({element:b,data:[{value:70,label:"foo"},{value:15,label:"bar"},{value:10,label:"baz"},{value:5,label:"A really really long label"}],formatter:function(a){return a+"%"}})}}})});