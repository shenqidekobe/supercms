define(['modules/forms/module', 'lodash', 'bootstrap-duallistbox'], function (module, _) {

    'use strict';

    /**
     * update 2015-8-27
     * 修改动态加载option，单个移动的时候全部移动的BUG，采用传递items在指令里动态拼接option的方式进行处理
     * itemName表示items的显示名称
     * */
    return module.registerDirective('smartDuallistbox', function () {
        return {
            restrict: 'A',
            scope:{
            	items:'=',
            	itemName:'@'
            },
            link: function (scope,tElement, tAttributes) {
	            scope.$watch('items', function() {
	            	 var items=scope.items;
	            	 var alreadyItems=scope.alreadyItems;
	            	 var op="";
	            	 $(items).each(function(idx, item){
	            		 var selected="";
            			 if(item.selected==true){
            				 selected="selected='selected'";
            			 }
	            		 op+= _.template("<option value='<%=id%>' "+selected+"><%="+scope.itemName+"%></option>")(item);
	            	 });
	            	 if(op!=""){
	            		 tElement.append(op);
	            		 tElement.removeAttr('smart-duallistbox data-smart-duallistbox');
	                     var aOptions = _.pick(tAttributes, ['nonSelectedFilter']);

	                     var options = _.extend(aOptions, {
	                         nonSelectedListLabel: '可选列表',
	                         selectedListLabel: '已选列表',
	                         filterTextClear: '显示所有',
	                         filterPlaceHolder: 'Filter',
	                         moveSelectedLabel: '移动选中',
	                         moveAllLabel: '移动所有',
	                         removeSelectedLabel: '移除选中',
	                         removeAllLabel:'全部移除',
	                         preserveSelectionOnMove: 'moved',
	                         //helperSelectNamePostfix:'',
	                         moveOnSelect: false
	                     });

	                     tElement.bootstrapDualListbox(options);
	            	 }
	            	
            	})
               
            }
        }
    });
});
