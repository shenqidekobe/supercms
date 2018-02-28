define([ 'modules/manuscripts/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('MemberCtrl', function($scope, $state,$rootScope, $compile, $templateCache, $filter, resource,SmallBoxMessage) {

		// ------------------
		// Data Definition
		// ------------------

		$scope.member = {
		    id : '',
		    organId:'',
		    loginName : '',
		    memberName : '',
		    password : '',
		    memberType : '',
		    phone : '',
		    email : '',
		    remark : ''
		};
		$scope.initialmember = angular.copy($scope.member);
		$scope.memberDialog = {
		    isNew : true,
		    icon : '',
		    title : ''
		};
		$scope.organs=null;
		$scope.rowSelected = null;
		$scope.sendSelected = null;
		$scope.auditSelected = null;
		$scope.memberType = '';
		$scope.tableOptions = {
		    columns : [ {
			    data : 'memberType',
			    render : function(data, type, row) {
			        return data=="SEND"?"投稿":"审稿";
		        }
		    }, {
			    data : 'memberName',
			    render : function(data, type, row) {
			        return '[' + row.id + '] ' + row.memberName;
		        }
		    }, {
		        data : 'organId',
		        render : function(data, type, row) {
			        return '[' + data + '] ' + row.organ.organName;
		        }
		    },  {
			    data : 'loginName'
		    },{
			    data : 'phone'
		    },{
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
				    memberType : $scope.memberType,
			    });
			    resource.membersResource.datatable(data).$promise.then(function(members) {
				    $scope.datatable.data = members;
				    callback($scope.datatable);
				    $scope.member = $scope.initialmember;
				    $scope.rowSelected = false;
				    $scope.sendSelected = false;
				    $scope.auditSelected = false;
			    });
		    }
		};

		// ------------------------------------
		// Resource Request Methods Definition
		
		// request create member
		$scope.loadOrganRequest = function() {
			new resource.organsResource.all().$promise.then(function(organs) {
				$scope.organs=organs;
			});
		};
		// ------------------------------------

		// request create member
		$scope.creatememberRequest = function() {
			new resource.membersResource($scope.member).$create().then(function() {
				$scope._refreshTable();
			});
		};

		// request save member
		$scope.savememberRequest = function() {
			$scope.member.$save().then(function() {
				$scope._refreshTable();
			});
		};

		// request remove member
		$scope.removememberRequest = function() {
			$scope.member.$remove().then(function() {
				$scope._refreshTable();
			});
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#members_table').DataTable();
			var tr = $(event.target).parent();
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			$scope.member = $scope.rowSelected = table.row('.highlight').data();
			try{
				if($scope.member.memberType=="SEND"){
					$scope.sendSelected=true;
					$scope.auditSelected=false;
				}if($scope.member.memberType=="AUDIT"){
					$scope.auditSelected=true;
					$scope.sendSelected=false;
				}
			}catch(e){}
		};

		// submit form command handler
		$scope.submitmemberFormHandler = function() {
			var form = angular.element(memberForm);
			form.submit();
		}

		// cancel form command handler
		$scope.cancelmemberFormHandler = function() {
			$('#member_dialog').dialog('close');
		}
		
		$scope.changeMemberHandler = function() {
			$('#members_table').DataTable().ajax.reload(null, true);
		};

		// create command handler
		$scope.creatememberHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.memberDialog.isNew = true;
			$scope.memberDialog.icon = 'fa fa-plus';
			$scope.memberDialog.title = '创建用户';
			$scope.member =  angular.copy($scope.initialmember);
		};

		// save command handler
		$scope.savememberHandler = function() {
			$scope.$broadcast('resetForm');
			$scope.memberDialog.isNew = false;
			$scope.memberDialog.icon = 'fa fa-edit';
			$scope.memberDialog.title = '修改用户';
			$scope.rowSelected.$get().then(function(data) {
				$scope.member = data;
			});
		};
		
		//投稿分配，进入另外一个页面
		$scope.sendAllotHandler = function(){
			$scope.cancelmemberFormHandler();
			$scope.rowSelected.$get().then(function(data) {
				if(data.memberType=="SEND"){
					$state.go('app.manuscripts.send',{memberId:data.id,memberName:data.memberName});
				}
			});
		}
		
		//审稿分配，进入另外一个页面
		$scope.auditAllotHandler = function(){
			$scope.cancelmemberFormHandler();
			$scope.rowSelected.$get().then(function(data) {
				if(data.memberType=="AUDIT"){
					$state.go('app.manuscripts.audit',{memberId:data.id,memberName:data.memberName});
				}
			});
		}

		// remove command handler
		$scope.removememberHandler = function() {
			SmallBoxMessage.confirm({
				title:"确认框",
				content:"您确定要删除该条数据吗？",
				callback:$scope.removememberRequest
			});
		};
		// refresh table after create or save or remove command
		$scope._refreshTable = function() {
			$('#member_dialog').dialog('close');
			$scope.$broadcast('refreshTable', {
				id : 'members_table'
			});
		};
		
		$scope.loadOrganRequest();

	})
});
