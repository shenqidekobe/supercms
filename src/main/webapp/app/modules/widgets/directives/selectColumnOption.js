-define([ 'modules/templates/module' ], function(module) {

	"use strict";

	/**
	 * 栏目树输出options
	 */
	module.registerDirective("selectColumnOption", function($compile) {
		return {
			restrict : 'AE',
		    replace: false,
		    scope : {
		        items : '='
		    },
		    link: function(scope, element, attrs){
        		scope.$watch('items', function() {
    			   element.empty();
    			   var columnHtml='';
    			   var i=0,k=0;
    			   var items=scope.items;
    			   var arrays=new Array(); 
    			   var getColumnAsHtml = function(items){
     				  var html='';
     				  $(items).each(function(idx,column){
     					   if($.inArray(column.title, arrays)!=-1){
     						  return true;
     					   }
     					    arrays[k]=column.title;
     					    k++;
     					    var str="";
		    				for(var op=0;op<(column.series-1);op++){
		    					str+="--";
		    				}
		    				//如果没有子元素，则验证其是否顶级元素
			    			 if(column.children && column.children.length == 0) {
			    				 html += _.template("<option value='<%=id%>'>"+str+"<%=title%></option>")(column);
			    			 }else{
			    				 //如果存在子元素，则验证其既存在子元素又存在父元素的情况
			    				 if(column.parentId==null){
			    					 html += _.template("<option value='<%=id%>'><%=title%></option>")(column);
			    				 }else{
			    					 html += _.template("<option value='<%=id%>'>"+str+"<%=title%></option>")(column);
			    				 }
			    				
			    				html += getColumnAsHtml(column.children);
			    			 }
  				    	  });
					  return html;
				   };
				   columnHtml+=getColumnAsHtml(items);
    			   var columnAsElem = angular.element(columnHtml);
 		    	   $compile(columnAsElem)(scope);
			 	   element.append(columnAsElem);
    		   });
		    }
		}
	});
})