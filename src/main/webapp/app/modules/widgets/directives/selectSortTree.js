define([ 'modules/templates/module' ], function(module) {

	"use strict";

	module.registerDirective("selectSortTree", function(Sort, $compile) {
		return {
			restrict : 'AE',
			replace:true,
			compile: function(element){
            	return function (scope, element, attrs) {
            		scope.sortType=attrs.sortType;
            		scope.createTree=function(){
            			  element.empty();
                		  var obj=Sort.getSort(scope.sortType);
                		  var object=attrs.obj;
                		  obj.initialized.then(function(){
    	        			   var sortHtml='<ul id="sortTree" data-smart-jqui-menu>';
    	        			   var getSortAsHtml = function(sorts){
    	        				  var html='';
    	  						  $(sorts).each(function(idx, sort){
    	  			    			 if(sort.children && sort.children.length == 0) {
    	  			    				if(sort.parent == null){
    	  			    					html += _.template("<li id='<%=id%>'><a href-sort obj='"+object+"'><%=sortName%></a></li>")(sort);
    	  			    				}
    	  			    			 }else{
    	  			    				html += _.template("<li id='<%=id%>'><a href-sort obj='"+object+"'><%=sortName%></a>")(sort);
    	  			    				html += '<ul>';
    	  			    				html += getSortAsHtml(sort.children);
    	  			    				html += '</ul>';
    	  			    				html += "</li>";
    	  			    			 }
    	  				    	 });
    	  						 return html;
    	  					   };
    	        			   sortHtml+=getSortAsHtml(obj.sorts);
    	        			   sortHtml+="</ul>";
    	        			   var sortAsElem = angular.element(sortHtml);
    	     		    	   $compile(sortAsElem)(scope);
    	  				 	   element.append(sortAsElem);
            		      });
            		}
            		scope.$watch('sortType',function(){
            			console.info("sortType...");
            			scope.createTree();
	                });
            		  
            	}
		   }
		}
		
	});
})