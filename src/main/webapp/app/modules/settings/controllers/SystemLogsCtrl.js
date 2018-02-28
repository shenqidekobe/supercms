define([ 'modules/settings/module', 'lodash', 'datatables-responsive' ], function(module, _) {

	'use strict';

	module.registerController('SystemLogsCtrl', function($scope,$filter, resource) {
		$scope.moduleLogs="system_module";//默认查询系统操作日志
		$scope.rowSelected = null;
		$scope.sysLogs = null;
		$scope.logs={
			userId:'',
			operType:'',
			operId:'',
			operDesc:'',
			startTime:'',
			endTime:'',
			module:$scope.moduleLogs
		}
		
		var systemModule={"create": "创建","save": "编辑","remove":"删除","disable": "禁用","enbale": "启用","trigger":"触发",
				         "allotSecurity":"权限分配","allotDataSource":"数据源分配","allotData":"数据分配",
				         "allotSend":"投稿分配","allotAudit": "审稿分配","copy": "复制","move":"移动","ref": "引用",
				         "stick": "置顶","unstick":"取消置顶","dealTags":"打标签","dataReview":"数据审核"};
		
		var loginModule={"login": "登录","logout": "登出"};
		
		var manuscriptModule={"create": "创建","save": "编辑","remove":"删除","audit":"审核","recall": "撤回"};
		
		$scope.logTypes=systemModule;
		
		$scope.users=null;
		
		$scope.tableOptions = {
			    columns : [ {
				    data : 'operation'
			    },  {
				    data : 'operType',
				    render : function(data, type, row) {
				    	var operType="";
				    	switch (data) {
						case 'create':
							operType="新增";
							break;
						case 'save':
							operType="编辑";
							break;
						case 'remove':
							operType="删除";
							break;
						case 'login':
							operType="登录";
							break;
						case 'logout':
							operType="登出";
							break;
						case 'disable':
							operType="禁用";
							break;
						case 'enable':
							operType="启用";
							break;
						case 'trigger':
							operType="触发";
							break;
						case 'recall':
							operType="撤回";
							break;
						case 'allotSend':
							operType="投稿分配";
							break;
						case 'allotAudit':
							operType="审稿分配";
							break;
						case 'allotSecurity':
							operType="权限分配";
							break;
						case 'allotDataSource':
							operType="数据源分配";
							break;
						case 'allotData':
							operType="数据分配";
							break;
						case 'copy':
							operType="复制";
							break;
						case 'move':
							operType="移动";
							break;
						case 'ref':
							operType="引用";
							break;
						case 'stick':
							operType="置顶";
							break;
						case 'unstick':
							operType="取消置顶";
							break;
						case 'dealTags':
							operType="打标签";
							break;
						case 'dataReview':
							operType="数据审核";
							break;
						default:
							break;
						}
				    	return operType;
			        }
			    },{
				    data : 'operId'
			    },{
				    data : 'operIp'
			    },{
				    data : 'operDesc'
			    }, {
			        data : 'operTime',
			        render : function(data, type, row) {
				        return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss');
			        }
			    } ],
			    pageLength : 20,
			    ajax : function(data, callback, settings) {
			    	var data = _.extend(data, {
			    		userId : $scope.logs.userId,
			    		operType : $scope.logs.operType,
			    		operId : $scope.logs.operId,
				        keys : $scope.logs.operDesc,
				        moduleLog : $scope.logs.module,
				        startTime : $scope.logs.startTime,
				        endTime : $scope.logs.endTime,
				        });
				    resource.systemLogsResource.datatable(data).$promise.then(function(logs) {
					    $scope.datatable.data = logs;
					    callback($scope.datatable);
				    });
			    }
			};
			
		
		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#logs_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.rowSelected = table.row('.highlight').data();
			try{
				$scope.rowSelected.$get().then(function(data) {
					$scope.sysLogs  = data;
					$('#details_dialog').dialog('open');
				});
			}catch(e){}
		};
		
		// cancel form command handler
		$scope.cancelDetailFormHandler = function() {
			$('#details_dialog').dialog('close');
		}
		
		//查询日志列表
		$scope.searchLogsHandler = function(module){
			if(!angular.isUndefined(module)){
				$scope.logs.module=module;
			}
			switch ($scope.logs.module) {
			case 'system_module':
				$scope.logTypes=systemModule;
				$scope.loadUsersRequest();
				break;
			case 'system_login':
				$scope.logTypes=loginModule;
				$scope.loadUsersRequest();
				break;
			case 'manuscript':
				$scope.logTypes=manuscriptModule;
				$scope.loadMembersRequest();
				break;
			case 'manuscript_login':
				$scope.logTypes=loginModule;
				$scope.loadMembersRequest();
				break;
			default:
				break;
			}
			$scope._refreshTable();
			//$('#logs_table').DataTable().ajax.reload(null, true);
		};
		
		//重置表单
		$scope.resetLogsHandler = function(){
			$scope.logs={
				userId:'',
				operId:'',
				operDesc:'',
				startTime:'',
				endTime:'',
				module:$scope.moduleLogs
			}
			$('#startTime').val('');
			$('#endTime').val('');
		};
		
		//获取所有的系统用户信息
		$scope.loadUsersRequest = function() {
			resource.usersResource.all().$promise.then(function(data) {
				$scope.users = data;
			});
		};
		
		//获取所有的投稿用户信息
		$scope.loadMembersRequest = function() {
			resource.membersResource.all().$promise.then(function(data) {
				for(var i=0;i<data.length;i++){
					var obj=data[i];
					obj.name=obj.memberName;
				}
				$scope.users = data;
			});
		};
		
		$scope._refreshTable = function() {
			$scope.logs.startTime = $('#startTime').val();
			$scope.logs.endTime = $('#endTime').val();
			$scope.$broadcast('refreshTable', {
				id : 'logs_table'
			});
		};
		$scope.loadUsersRequest();
		
	});
	
});