define([ 'modules/templates/module' ], function(module) {

	"use strict";

	/**
	 * 由于嵌套指令、select无法提前ng-model的值、所以通过传递参数的方式来绑定
	 * */
	module.registerDirective("selectSortOption", function(Sort, $compile,$stateParams) {
		return {
			restrict : 'AE',
		    replace: false,
			compile: function(element){
            	return function (scope, element, attrs) {
            		var sortId=$stateParams.sortId;
            		scope.sortType=attrs.sortType;
            		var object=attrs.obj;
            		if(angular.isUndefined(scope.sortType)){
            			scope.sortType=$stateParams.templateType;
            			object="template";
            			scope.sortType=object+"_"+scope.sortType;
            		}
            		scope.createOption=function(){
                		  var obj=Sort.getSort(scope.sortType);
                		  obj.initialized.then(function(){
    	        			   var sortHtml='<option value="">选择分类</option>';
    	        			   var i=0;
    	        			   var getSortAsHtml = function(sorts){
    	        				  var html='';
    	  						  $(sorts).each(function(idx, sort){
    	  							var selected="";
    	  							var str="";
	  			    				for(var op=0;op<i;op++){
	  			    					str+="--";
	  			    				}
	  			    				if(sort.id==sortId){
	  			    					selected="selected='selected'";
	  			    				}
    	  			    			 if(sort.children && sort.children.length == 0) {
    	  			    				if(!angular.isUndefined(sort.parent)){
    	  			    					i=0;
    	  			    					html += _.template("<option value='<%=id%>' "+selected+"><%=sortName%></option>")(sort);
    	  			    				}else{
    	  			    					html += _.template("<option value='<%=id%>' "+selected+">"+str+"<%=sortName%></option>")(sort);
    	  			    				}
    	  			    			 }else{
    	  			    				html += _.template("<option value='<%=id%>' "+selected+">"+str+"<%=sortName%></option>")(sort);
    	  			    				i++;
    	  			    				html += getSortAsHtml(sort.children);
    	  			    			 }
    	  				    	 });
    	  						 return html;
    	  					   };
    	        			   sortHtml+=getSortAsHtml(obj.sorts);
    	        			   var sortAsElem = angular.element(sortHtml);
    	     		    	   $compile(sortAsElem)(scope);
    	  				 	   element.append(sortAsElem);
            		      });
            		}
            		scope.createOption();  
            	}
		   }
		}
		
	});
})