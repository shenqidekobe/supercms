define(['modules/widgets/module'], function (module) {

    'use strict';

    module.registerDirective('hrefSortView', function ($rootScope) {
        return {
            restrict: 'AE',
            scope:{
            	query:'=query'
            },
            link: function (scope, element, attributes) {
                element.attr('href','javascript:void(0);');
                element.on('click', function(e){
                	$(".tree span").css("backgroundColor","");
                	var id=element.parent().attr("id");
                	if(id==null||id=="parent"){
                		id='';
                	}else{
                		element.parent().css("backgroundColor","#FF9900");
                	}
                	scope.query(id);
                    e.preventDefault();
                });
                element.on('contextmenu',function(event){
                	var sortType=$("#parent").attr("sorttype");
                	var sortId=element.parent().attr("id");
                	sortId=(sortId=="parent"?"":sortId);
    	        	var sortName=element.text();
    	        	var contextMenu=function(sortId,sortName,sortType){
    	        		 $.contextMenu({
    	            	        selector: '#'+element.parent().attr("id"), 
    	            	        autoHide:true,
    	            	        callback: function(key, options) {
    	            	        	//console.log("contextmenu callback params : "+sortId+"---"+sortName+"---"+sortType);
    	            	        	if(sortId==""&&(key=="edit"||key=="delete")){
    	            	        		return;
    	            	        	}
    	            	        	if(key=="add"){
    	            	        		$rootScope.sortOper={
    	            	        				parentId:sortId,
    	            	        				parentName:sortName,
    	            	        				sortType:sortType,
    	            	        				sortOper:"add",
    	            	        				random:Math.random()
    	            	        		}
    	            	        		$("#sort_dialog").dialog('open');
    	            	        	}else if(key=="edit"){
    	            	        		$rootScope.sortOper={
    	            	        				id:sortId,
    	            	        				parentId:null,
    	            	        				parentName:null,
    	            	        				sortType:sortType,
    	            	        				sortName:sortName,
    	            	        				sortOper:"edit",
    	            	        				random:Math.random()
    	            	        		}
    	            	        		$("#sort_dialog").dialog('open');
    	            	        	}else if(key=="delete"){
    	            	        		$rootScope.sortOper={
    	            	        				id:sortId,
    	            	        				sortOper:"remove",
    	            	        				random:Math.random()
    	            	        		}
    	            	        	}
    	            	        },
    	            	        items: {
    	            	            "add": {
    	            	            	name: "新增下级分类", 
    	            	            	icon: "add"},
    	            	            "edit": {
    	            	            	name: "修改分类", 
    	            	            	icon: "edit",
    	            	            	disabled:function(key,options){
    	            	            		return sortId=="";
    	            	            	}},
    	            	            "delete": {
    	            	            	name: "删除分类",
    	            	            	icon: "delete",
	            	            		disabled:function(key,options){
    	            	            		return sortId=="";
    	            	            	}},
    	            	        }
    	            	    });
    	        	}
    	        	contextMenu(sortId, sortName, sortType);
                	event.preventDefault();
                });
            }    
        }
    });
});
