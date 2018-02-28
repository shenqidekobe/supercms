define([ 'modules/settings/module', 'lodash', 'datatables-responsive' ], function(module, _) {

	'use strict';

	module.registerController('ProduceLogsCtrl', function($scope,$filter, resource) {
		$scope.rowSelected = null;
		$scope.proLogs = null;
		
		$scope.logs={
			userId:'',
			produceType:'',
			produceResult:'',
			startTime:'',
			endTime:'',
			produceDesc:''
		}
		
		$scope.users=null;
		
		$scope.tableOptions = {
			    columns : [ {
				    data : 'produceType',
				    render : function(data, type, row) {
				    	var produceType="";
				    	switch (data) {
						case 'index':
							produceType="生成首页";
							break;
						case 'content,list':
							produceType="生成内容和列表页";
							break;
						case 'content':
							produceType="生成内容页";
							break;
						case 'list':
							produceType="生成列表页";
							break;
						case 'custom':
							produceType="生成自定义页";
							break;
						case 'history':
							produceType="历史列表页";
							break;
						case 'directive':
							produceType="执行指令";
							break;
						case 'method':
							produceType="执行方法";
							break;
						default:
							break;
						}
				    	return produceType;
			        }
			    },  {
				    data : 'produceResult',
				    render : function(data, type, row) {
				    	var produceResult="";
				    	switch (data) {
						case 'SUCCESS':
							produceResult="成功";
							break;
						case 'FAIL':
							produceResult="失败";
							break;
						case 'PART_SUCCESS':
							produceResult="部分成功";
							break;
						default:
							break;
						}
				    	return produceResult;
			        }
			    },{
				    data : 'produceCount'
			    },{
				    data : 'produceDesc'
			    }, {
			        data : 'produceDate',
			        render : function(data, type, row) {
				        return $filter('date')(data, 'yyyy-MM-dd HH:mm:ss');
			        }
			    }],
			    pageLength : 20,
			    ajax : function(data, callback, settings) {
			    	var data = _.extend(data, {
			    		userId : $scope.logs.userId,
			    		produceType : $scope.logs.produceType,
				        keys : $scope.logs.produceDesc,
				        produceResult : $scope.logs.produceResult,
				        startTime : $scope.logs.startTime,
				        endTime : $scope.logs.endTime
				    });
				    resource.produceLogsResource.datatable(data).$promise.then(function(logs) {
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
					$scope.proLogs  = data;
					$('#details_dialog').dialog('open');
				});
			}catch(e){}
		};
		
		// cancel form command handler
		$scope.cancelDetailFormHandler = function() {
			$('#details_dialog').dialog('close');
		}
		
		//查询日志列表
		$scope.searchLogsHandler = function(){
			$scope._refreshTable();
			//$('#logs_table').DataTable().ajax.reload(null, true);
		};
		
		//重置表单
		$scope.resetLogsHandler = function(){
			$scope.logs={
				userId:'',
				produceType:'',
				produceResult:'',
				startTime:'',
				endTime:'',
				produceDesc:''
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