define([ 'modules/manuscripts/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('SendAllotCtrl', function($scope, $state,$stateParams, $compile, $templateCache, $filter, resource,SmallBoxMessage) {
		$scope.all=[];
		$scope.dataSources=[];
		
		$scope.memberId=$stateParams.memberId;
		$scope.memberName=$stateParams.memberName;
		$scope.dataSourceItems=[];
		var currentSelectedIndex=0;
		
		var Data=function(serviceId,title,isAudit){
			this.serviceId=serviceId;
			this.title=title;
			this.isAudit=isAudit;
		}
		
		//读取所有的数据源
		$scope.loadDataSourceRequest = function() {
			new resource.membersResource.senddatasources({sendId:$scope.memberId}).$promise.then(function(data) {
				$scope.all=data;
				var j=0;
				for(var i=0;i<data.length;i++){
					var obj=data[i];
					var isAudit=obj.isAudit==1?true:false;
					var item=new Data(obj.id,obj.title,isAudit);
					if(obj.selected==true){
						//过滤得到已选的数据源
						$scope.dataSourceItems[currentSelectedIndex]=item;
						currentSelectedIndex++;
					}else{
						//过滤得到待选的数据源
						$scope.dataSources[j]=item;
						j++;
					}
				}
			});
		};
		//数组按索引删除
		Array.prototype.remove=function(dx) 
		{ 
		    if(isNaN(dx)||dx>this.length){return false;} 
		    for(var i=0,n=0;i<this.length;i++) 
		    { 
		        if(this[i]!=this[dx]) 
		        { 
		            this[n++]=this[i] 
		        } 
		    } 
		    this.length-=1 
		} 		
		//右移,通过数据绑定实现
		$scope.leftSelected=null;
		$scope.moveRight=function(){
			var leftSelected=$scope.leftSelected;
			for(var i in leftSelected){
				var sources=$scope.dataSources;
				var left=leftSelected[i];
				for(var j in sources){
					var data=sources[j];
					if(angular.isUndefined(data.serviceId)){
						return false;
					}
					if(data.serviceId==left.serviceId){
						data.isAudit=true;
						$scope.dataSourceItems[currentSelectedIndex]=data;//给右边已选的增加对象
						currentSelectedIndex++;
						
						$scope.dataSources.remove(j);//删除数据源数组中的选中对象
					}
				}
			}
		};
		//左移
		$scope.moveLeft=function(){
			var rightSelected=angular.element(".item-container.selected");
			for(var i=0;i<rightSelected.length;i++){
				var div=angular.element(rightSelected[i]);
				var right=div.attr("id");
				var rightIndex=div.attr("index");
				var title=div.text();
				var items=$scope.dataSourceItems;
				for(var j in items){
					var data=items[j];
					if(data.serviceId==right){
						var item=new Data(right,title,null);
						$scope.dataSources.push(item);
						
						$scope.dataSourceItems.remove(rightIndex);
						currentSelectedIndex--;
					}
				}
			}
			
		};
		//单机选中和非选中
		$scope.selected=function(event){
			if(event.target.nodeName=="INPUT"){
				return;
			}
			var element=angular.element(event.target);
			//element.toggleClass("selected");
			var css=element.attr("class");
			var selectClass="selected";
			if(css.indexOf("item-details")==-1){
				if(css.indexOf(selectClass)==-1){
					element.addClass(selectClass);
				}else{
					element.removeClass(selectClass);
				}
			}else{
				css=element.parent().attr("class");
				if(css.indexOf(selectClass)==-1){
					element.parent().addClass(selectClass);
				}else{
					element.parent().removeClass(selectClass);
				}
			}
			
			event.stopPropagation();
		};
		//是否需要审核
		$scope.auditHandler=function(event){
			var element=angular.element(event.target);
			console.info(element.attr("checked"));
		};
		//返回用户列表
		$scope.back=function(){
			$state.go("app.manuscripts.member");
		}
		//保存选择的数据
		$scope.saveSelected=function(){
			var json=angular.toJson($scope.dataSourceItems);
			console.info(json);
			var params={
				memberId:$scope.memberId,
				json:json
			}
			new resource.membersResource(params).$sendallot().then(function() {
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:"保存成功",
 	    			timeout:1000
 	    		});
				$scope.back();
			});
		}
		
		$scope.loadDataSourceRequest();
		
	})
	
});