define(['modules/widgets/module'], function (module) {

    'use strict';

    module.registerDirective('sortTreeview', function (Sort,$compile,$stateParams) {
        return {
            restrict: 'A',
            compile: function (element) {
            	return function (scope, element, attrs) {
            		scope.expanded = true;
            		scope.handleExpandedAll=function(event){
            			if(scope.expanded){
            				angular.element(event.target).parent().parent().find(">ul").hide();
            			}else{
            				angular.element(event.target).parent().parent().find(">ul").show();
            			}
            			scope.expanded=!scope.expanded;
            		}
            		scope.expandedChildren = true;
            		scope.handleExpandedChildren=function(event){
            			var $this=angular.element(event.target);
            			if(scope.expandedChildren){
            				$this.parent().next("ul").hide();
            			}else{
            				$this.parent().next("ul").show();
            			}
            			angular.element(event.target).toggleClass('fa-plus-circle', scope.expandedChildren).toggleClass('fa-minus-circle', !scope.expandedChildren);
            			scope.expandedChildren=!scope.expandedChildren;
            		}
            		var treeview=function(){
            			  element.empty();
	               		  var obj=Sort.getSort(attrs.sortType);
	               		  obj.initialized.then(function(){
	   	          			   var sortHtml='<ul role="tree" class="ng-isolate-scope">'+
	   	          				            '<li role="treeitem" class="ng-scope parent_li"><span id="parent" sortType="'+attrs.sortType+'" class="context-menu-sort">'+
	   	          				            '<i class="fa fa-lg fa-folder-open fa-minus-circle" ng-click="handleExpandedAll($event);"></i><a href-sort-view query=\"list\">所有分类</a></span>'+
	   	          				            '<ul role="group" class="smart-treeview-group ng-scope ng-isolate-scope">';
	   	          			   var getSortAsHtml = function(sorts){
	   	          				  var html='';
	       						  $(sorts).each(function(idx, sort){
	       			    			 if(sort.children && sort.children.length == 0) {
	       			    				if(sort.parent == null){
	       			    					html += _.template("<li role=\"treeitem\" class=\"ng-scope parent_li\"><span parentId='<%=parentId%>' id='<%=id%>' class=\"context-menu-sort\"><a href-sort-view query=\"list\"><%=sortName%></a></span></li>")(sort);
	       			    				}
	       			    			 }else{
	       			    				html += _.template("<li role=\"treeitem\" class=\"ng-scope parent_li\"><span parentId='<%=parentId%>' id='<%=id%>' class=\"context-menu-sort\"><i class=\"fa fa-lg fa-minus-circle\" ng-click=\"handleExpandedChildren($event);\" ></i><a href-sort-view query=\"list\"><%=sortName%></a></span>")(sort);
	       			    				html += '<ul role="group" class="smart-treeview-group ng-scope ng-isolate-scope">';
	       			    				html += getSortAsHtml(sort.children);
	       			    				html += '</ul>';
	       			    				html += "</li>";
	       			    			 }
	       				    	 });
	   	    						 return html;
	   	    					 };
	   	          			 sortHtml+=getSortAsHtml(obj.sorts);
	   	          			 sortHtml+="</ul></li></ul>";
	   	          			 var sortAsElem = angular.element(sortHtml);
	   	       		    	 $compile(sortAsElem)(scope);
	   	    				 element.append(sortAsElem);
	   	    				 //跳转回来记住之前选择的分类
	   	    				 $(".tree span").not("#parent").each(function(){
	   	    					 if($(this).attr("id")==$stateParams.searchSortId){
	   	    						 $(this).css("backgroundColor","#FF9900");
	   	    					 }
	   	                 	});
	               		});
            		}
            		treeview();
	               	scope.$watch("treeview",function(newObj,oldObj){
	               		//console.log("loading treeview params:"+newObj+" --- "+oldObj);
	               		if(newObj!=oldObj){
	               			treeview();
	               		}
	               	},true);
              	}
            }
        };
    });
});


