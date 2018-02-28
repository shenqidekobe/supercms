define(['modules/modules/module','lodash'],function(module,_){
	
	'use strict';
	
	module.registerController('MakeCtrl',function($scope,$modal,resource,makeSocket,SmallBoxMessage){
		$scope.sortsTree = {
			    data : null,
			    searchCurrentItem : null,
		};
		$scope.siteList =  null;
		$scope.columnList1 =null;
		$scope.columnList2 =null;
		$scope.customList =null;
		//加载站点信息
		$scope.loadSitesRequest = function() {
			resource.batchResource.sites().$promise.then(function(data) {
				$scope.siteList = data;
			});
		};
		//根据站点加载栏目信息
		$scope.loadColumnsRequest1 = function(siteId) {
			resource.batchResource.columns({siteId:siteId}).$promise.then(function(data) {
				$scope.columnList1 = data;
			});
		};
		//根据站点加载栏目信息
		$scope.loadColumnsRequest2 = function(siteId) {
			resource.batchResource.columns({siteId:siteId}).$promise.then(function(data) {
				$scope.columnList2 = data;
			});
		};
		//加载自定义页分类列表
		$scope.loadSortsAsTreeRequest = function() {
			resource.customsResource.sortsTree().$promise.then(function(data) {
				$scope.sortsTree.data = data;
				$scope.$watch('sortsTree.searchCurrentItem', function(item) {
					if (item) {
						//刷新分类下面的自定义页
						$scope.loadCustomsRequest(item.id);
					}
				});
			});
		};
		//加载自定义列表
		$scope.loadCustomsRequest = function(sortId) {
			resource.batchResource.customs({sortId:sortId}).$promise.then(function(data) {
				$scope.customList = data;
			});
		};
		
		$scope.changeSiteHandler1=function(){
			$scope.loadColumnsRequest1($scope.selectedItem1);
		};
        $scope.changeSiteHandler2=function(){
        	$scope.loadColumnsRequest2($scope.selectedItem2);
		};
		
		$scope.loadSortsAsTreeRequest();
		$scope.loadSitesRequest();
		
		//show hide simllBox
		$scope.progress={
				transitiongoal:0,
				title:'',
				justDesc:'',
				errorTitle:'',
				errorContent:''
		}
		$scope.complete=false;
		$scope.showBoxFlag=false;
		var box=$("#showBox");
		var bar=$(".progress-bar");
		var showBox=function(title){
			bar.attr('data-transitiongoal',0)
			$scope.progress.transitiongoal=0;
			$scope.progress.title=title;
			$scope.progress.justDesc='';
			$scope.progress.errorTitle='';
			$scope.progress.errorContent='';
			progressbar(0);
			
			$scope.showBoxFlag=true;
			$scope.complete=false;
			box.fadeIn(450);
			box.addClass('ajax-dropdown');
			
			
		};
		$scope.cancelBox=function(){
			bar.attr('data-transitiongoal',0)
			progressbar(0);
			$scope.showBoxFlag=false;
			$scope.complete=false;
			box.fadeOut(450);
			box.removeClass('ajax-dropdown');
		}
		var transition_delay=300;
		var refresh_speed=50;
		var progressbar=function(percent){
			var amount_max=100;
			bar.progressbar({
				transition_delay: transition_delay,
				refresh_speed:refresh_speed,
                display_text : 'fill',
                percent_format: function(percent) { return percent + '%'; },
                amount_format: function(percent, amount_max) { 
                	alert(percent);
                	return percent + ' / ' + amount_max;
                },
            });
		}
		
		//batch update page button event process
		var getDate=function(count){    
			var dd = new Date();    
			dd.setDate(dd.getDate()+count);
			var y = dd.getFullYear();    
			var m = dd.getMonth()+1;
			if(m<10){
				m="0"+m;
			}
			var d = dd.getDate();  
			if(d<10){
				d="0"+d;
			}
			return y+"-"+m+"-"+d;    
		};
		var defaultStartTime=getDate(0);
		var defaultEndTime=getDate(1);
		var defaultRepeatFlag=true;
		
		$scope.condition={
				startTime:defaultStartTime,
				endTime:defaultEndTime,
				repeatFlag:defaultRepeatFlag,
				columnIds1:null
		}
		//重置column form
		$scope.resetForm=function(){
			$scope.selectedItem1="";
			angular.element("#startTime").val(defaultStartTime);
			angular.element("#endTime").val(defaultEndTime);
			$scope.condition.repeatFlag=defaultRepeatFlag;
			$scope.condition.columnIds1=null;
		}
		//刷新自己的所有信息
		$scope.refreshCurrentUserAll=function(){
			$scope.currentUser=1;
			var message = 'SELF_INCREMENT#' + $scope.currentUser;
			showBox('快捷生成-刷新自己的所有信息');
			websocketRequest(message);
		}
		//刷新新增的最终页和列表页
		$scope.refreshNewInfo=function(){
			var message = 'SELF_INCREMENT#-1';
			showBox('快捷生成-刷新新增最终页和列表页');
			websocketRequest(message);
		}
		//根据所选栏目和时间生成最终页
		$scope.refreshContentPage=function(){
			var columnIds=$scope.condition.columnIds1;
			var startTime=angular.element("#startTime").val();
			var endTime=angular.element("#endTime").val();
			if(angular.isUndefined(columnIds)||columnIds==null){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择栏目.",
 	    			timeout:3000
 	    		});
				return false;
			}else if(startTime==""){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择开始时间.",
 	    			timeout:3000
 	    		});
				return false;
			}else if(endTime==""){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择结束时间.",
 	    			timeout:3000
 	    		});
				return false;
			}else if(startTime>endTime){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"开始时间不能大于结束时间.",
 	    			timeout:3000
 	    		});
				return false;
			}
			var message = "CONTENT#";
			message += columnIds;
			message += "#" +  $scope.condition.repeatFlag;
			message += "#" + startTime;
			message += "#" + endTime;
			showBox('快捷生成-刷新最终页');
			websocketRequest(message);
		}
		//根据所选栏目和时间生成列表页
		$scope.refreshListPage=function(){
			var columnIds=$scope.condition.columnIds1;
			var startTime=angular.element("#startTime").val();
			var endTime=angular.element("#endTime").val();
			if(angular.isUndefined(columnIds)||columnIds==null){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择栏目.",
 	    			timeout:3000
 	    		});
				return false;
			}else if(startTime>endTime){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"开始时间不能大于结束时间.",
 	    			timeout:3000
 	    		});
				return false;
			}
			var message = "LIST#";
			message += columnIds;
			message += "#" +  $scope.condition.repeatFlag;
			message += "#" + startTime;
			message += "#" + endTime;
			showBox('快捷生成-刷新列表页');
			websocketRequest(message);
		}
		//根据所选自定义页刷新
		$scope.refreshCustomPage=function(){
			var customIds=$scope.customIds;
			if(angular.isUndefined(customIds)){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择自定义页.",
 	    			timeout:3000
 	    		});
				return false;
			}
			var message = "CUSTOM#"+customIds;
			showBox('快捷生成-刷新自定义页');
			websocketRequest(message);
		}
		//根据所选站点刷新首页
		$scope.refreshIndexPage=function(){
			var siteIds=$scope.siteIds;
			if(angular.isUndefined(siteIds)){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择站点.",
 	    			timeout:3000
 	    		});
				return false;
			}
			var message = "INDEX#"+siteIds;
			showBox('快捷生成-刷新首页');
			websocketRequest(message);
		}
		//根据所选栏目刷新最终页以及列表页
		$scope.refreshContentAndListPage=function(){
			var columnIds=$scope.columnIds2;
			if(angular.isUndefined(columnIds)){
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"请选择栏目.",
 	    			timeout:3000
 	    		});
				return false;
			}
			var message = "LIST_CONTENT_INCREMENT#"+columnIds;
			showBox('快捷生成-刷新最终页和列表页');
			websocketRequest(message);
		}
		

		//websocket connection requestAndresponse
		makeSocket.initialize(function(msg){
			if (msg.warn) {
				$scope.progress.justDesc = msg.warn;
			} else {
				$scope.progress.justDesc = msg.title;
				// $scope.progress.transitiongoal=msg.percent;
				bar.attr('data-transitiongoal', msg.percent)
				progressbar(msg.percent);
				if (msg.isComplete == true) {
					// setTimeout(function() {
					$scope.complete = true;
					// },transition_delay);
				}
				if (msg.error) {
					$scope.progress.errorTitle = msg.title;
					$scope.progress.errorContent = msg.error;
				}
			}
		});
		var websocketRequest=function(message){
			 makeSocket.send(message);
		}
	  
		
	});
	
});