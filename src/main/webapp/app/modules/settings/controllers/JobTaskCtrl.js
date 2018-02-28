define([ 'modules/settings/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('JobTaskCtrl', function($scope, $rootScope, $compile, $templateCache, $filter, resource,SmallBoxMessage,PAGESIZE) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.jobTask = {
		    id : '',
		    taskTitle : '',
		    taskName : '',
		    taskState : 'Disable',
		    taskType : '',
		    taskExperess : '',
		    targets : '',
		    taskParams:'',
		};
		$scope.initialjobTask = angular.copy($scope.jobTask);
		$scope.jobTaskDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.column = false;
		$scope.rowSelected = null;
		$scope.openSelected = null;
		$scope.disableSelected = null;
		$scope.isEdit = null;
		$scope.targetList = null;
		$scope.taskType = "";
		$scope.taskState = "";
		$scope.tableOptions = {
		    columns : [ {
			    data : 'taskTitle'
		    },  {
			    data : 'taskGroup'
		    },{
			    data : 'taskName'
		    }, {
			    data : 'taskType',
			    render : function(data, type, row) {
			    	var typeName="";
			    	switch (data) {
					case 'RefreshSite':
						typeName="刷新站点首页";
						break;
					case 'RefreshColumn':
						typeName="刷新栏目";
						break;
					case 'RefreshColumnList':
						typeName="刷新栏目列表页";
						break;
					case 'RefreshColumnContent':
						typeName="刷新栏目内容";
						break;
					case 'RefreshCustom':
						typeName="刷新自定义页";
						break;
					case 'CollectTask':
						typeName="采集任务";
						break;
					case 'ResourceTask':
						typeName="内容中心任务";
						break;
					case 'RebuildDataIndex':
						typeName="重建数据索引";
						break;
					default:
						break;
					}
			    	return typeName;
		        }
		    }, {
			    data : 'taskState',
			    render : function(data, type, row) {
			        return data=="Disable"?"暂停中":"运行中";
		        }
		    }, {
		        data : 'createTime',
		        render : function(data, type, row) {
			        return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss');
		        }
		    }, {
		        data : 'updateTime',
		        render : function(data, type, row) {
			        return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss');
		        }
		    } ],
		    ajax : function(data, callback, settings) {
		    	var data = _.extend(data, {
		    		taskType : $scope.taskType,
		    		taskState : $scope.taskState
			    });
			    resource.jobTasksResource.datatable(data).$promise.then(function(jobTasks) {
				    $scope.datatable.data = jobTasks;
				    callback($scope.datatable);
				    $scope.jobTask = $scope.initialjobTask;
				    $scope.rowSelected = false;
				    $scope.openSelected = false;
				    $scope.disableSelected = false;
				    $scope.isEdit=false;
			    });
		    }
		};
		
		$scope.changeJobTaskHandler = function() {
			$('#jobTasks_table').DataTable().ajax.reload(null, true);
		};

		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request create jobTask
		$scope.createjobTaskRequest = function() {
			new resource.jobTasksResource($scope.jobTask).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save jobTask
		$scope.savejobTaskRequest = function() {
			$scope.jobTask.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove jobTask
		$scope.removejobTaskRequest = function() {
			$scope.jobTask.$remove().then(function() {
				$scope._refreshTable();
			});
		};
		
		// open command handler
		$scope.openJobTaskHandler = function(){
			$scope.canceljobTaskFormHandler();
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要开启该任务吗？",
				callback:$scope.openJobTaskRequest
			});
			
		};
		$scope.openJobTaskRequest = function(){
			new resource.jobTasksResource({id:$scope.jobTask.id,taskState:"Enable"}).$toggle().then(function() {
				SmallBoxMessage.tips({
					title:"提示",
					content:"开启成功.",
					timeout:3000
				});
				$scope._refreshTable();
			})
		};
		// disable command handler
		$scope.disableJobTaskHandler = function(){
			$scope.canceljobTaskFormHandler();
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要停止该任务吗？",
				callback:$scope.disableJobTaskRequest
			});
		};
		$scope.disableJobTaskRequest = function(){
			new resource.jobTasksResource({id:$scope.jobTask.id,taskState:"Disable"}).$toggle().then(function() {
				SmallBoxMessage.tips({
					title:"提示",
					content:"停用成功.",
					timeout:3000
				});
				$scope._refreshTable();
			})
		};
		// tigger command handler
		$scope.triggerJobTaskHandler = function(){
			$scope.canceljobTaskFormHandler();
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要立即触发该任务吗？",
				callback:$scope.triggerJobTaskRequest
			});
		};
		$scope.triggerJobTaskRequest = function(){
			$scope.canceljobTaskFormHandler();
			new resource.jobTasksResource({id:$scope.jobTask.id}).$tigger().then(function() {
				SmallBoxMessage.tips({
					title:"提示",
					content:"触发成功，后台任务已开始进行.",
					timeout:3000
				});
				$scope._refreshTable();
			})
		};
		//form type change to target
		$scope.changeTargetHandler = function(){
			$scope.targetList=null;
			var taskType=$scope.jobTask.taskType;
			$scope.switchTargets(taskType);
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#jobTasks_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.jobTask = $scope.rowSelected = table.row('.highlight').data();
			try{
				if($scope.jobTask.taskState=="Enable"){
					$scope.openSelected = false;
					$scope.disableSelected = true;
				}if($scope.jobTask.taskState=="Disable"){
					$scope.openSelected = true;
					$scope.disableSelected = false;
				}
			}catch(e){}
			
		};

		// submit form command handler
		$scope.submitjobTaskFormHandler = function() {
			var form = angular.element(jobTaskForm);
			form.submit();
		}

		// cancel form command handler
		$scope.canceljobTaskFormHandler = function() {
			$('#jobTask_dialog').dialog('close');
		}
		
		// cancel log command handler
		$scope.cancelLogFormHandler = function() {
			$('#log_dialog').dialog('close');
		}

		// create command handler
		$scope.createjobTaskHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.jobTaskDialog.isNew = true;
			$scope.jobTaskDialog.icon = 'fa fa-plus';
			$scope.jobTaskDialog.title = '创建任务';
			$scope.jobTask =  angular.copy($scope.initialjobTask);
			$scope.isEdit=false;
		};

		// save command handler
		$scope.savejobTaskHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.jobTaskDialog.isNew = false;
			$scope.jobTaskDialog.icon = 'fa fa-edit';
			$scope.jobTaskDialog.title = '修改任务';
			$scope.isEdit = true;
			$scope.rowSelected.$get().then(function(data) {
				$scope.jobTask = data;
				$scope.switchTargets(data.taskType);
			});
		};

		// remove command handler
		$scope.removejobTaskHandler = function() {
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要删除该条数据吗？",
				callback:$scope.removejobTaskRequest
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#jobTask_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'jobTasks_table'
			});
		};
		
		//多选下拉框回显处理
		$scope.isSelected = function(items,id){
			var resArr = items.split(",");
			if (resArr.indexOf(id) > -1) {
				return true;
			} else {
				return false;
			}
		};
		
		
		// type target loads
		$scope.switchTargets = function(taskType){
			switch (taskType) {
			case 'RefreshSite':
				$scope.column=false;
				$scope.loadSitesRequest();
				break;
			case 'RefreshColumn':
				$scope.column=true;
				$scope.loadColumnsRequest();
				break;
			case 'RefreshColumnList':
				$scope.column=true;
				$scope.loadColumnsRequest();
				break;
			case 'RefreshColumnContent':
				$scope.column=true;
				$scope.loadColumnsRequest();
				break;
			case 'RefreshCustom':
				$scope.column=false;
				$scope.loadCustomsRequest();
				break;
			case 'CollectTask':
				$scope.column=false;
				$scope.loadCollectsRequest();
				break;
			case 'ResourceTask':
				$scope.column=false;
				$scope.loadVideosRequest();
				break;
			case 'RebuildDataIndex':
				$scope.column=false;
				$scope.loadDataSourceRequest();
				break;
			default:
				$scope.column=false;
				break;
			}
		};
		//重定义targetList
		var redefineTargetList = function(data){
			var targets=$scope.jobTask.targets;
			if(angular.isUndefined(data.length)){
				for(var key in data){
			    	var array =data[key];
			    	if(array instanceof Array){
			    		for(var i=0;i<array.length;i++){
							var obj=array[i];
							obj.selected=false;
							for(var k in targets){
								if(targets[k]==obj.id){
									obj.selected=true;
								}
							}
						}
			    	}
				}
			}else{
				for(var i=0;i<data.length;i++){
					var obj=data[i];
					obj.selected=false;
					for(var k in targets){
						if(targets[k]==obj.id){
							obj.selected=true;
						}
					}
				}
			}
			
			return data;
		};
		//加载站点信息
		$scope.loadSitesRequest = function() {
			resource.batchResource.sites().$promise.then(function(data) {
				$scope.targetList = redefineTargetList(data);
			});
		};
		//加载栏目列表
		$scope.loadColumnsRequest = function() {
			resource.columnsResource.siteColumn().$promise.then(function(data) {
				$scope.targetList = redefineTargetList(data);
			});
		};
		//加载自定义列表
		$scope.loadCustomsRequest = function() {
			resource.batchResource.customs().$promise.then(function(data) {
				$scope.targetList = redefineTargetList(data);
			});
		};
		//加载数据源列表
		$scope.loadDataSourceRequest = function(){
			resource.datasourcesResource.all().$promise.then(function(data) {
				$scope.targetList = redefineTargetList(data);
			});
		};
		//加载采集列表
		$scope.loadCollectsRequest = function() {
		};
		//加载视频任务列表
		$scope.loadVideosRequest = function() {
		};
		
		
		
		
		
		//任务日志查询处理
		$scope.logsList=null;
		$scope.pager={
				pageth:1,
				rowCount:null,
				pageSize:PAGESIZE
		}
		$scope.findTaskLogHandler = function(){
			$scope.list(1);
		};
		$scope.list = function(pageth,taskId){
			taskId=taskId==null?$scope.jobTask.id:taskId;
			$scope.pager.pageth=angular.isUndefined(pageth)?$scope.pager.pageth:pageth;
	    	var data = {
	    		taskId : taskId,
	    		pageth:$scope.pager.pageth,
       		    pageSize:$scope.pager.pageSize 
		    };
		    resource.jobTasksResource.tasklogs(data).$promise.then(function(data) {
		    	$scope.logsList =data.result;
				$scope.pager.rowCount=data.totalCount;
		    });
		};
		

	})
});
