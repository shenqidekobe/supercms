define([ 'modules/services/module', 'lodash'], function(module, _) {

	'use strict';
	
	module.registerFactory('resource', function($resource, $rootScope,CTX) {
		var server = CTX;
		
		// --------------
		// Menu Resource
		// --------------
		var menusResource = $resource(server + '/security/menus/:id', {
			id : "@id"
		}, {
			datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/security/menus?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable = angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    tree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/security/menus?tree',
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// --------------
		// User Resource
		// --------------
		var usersResource = $resource(server + '/security/users/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/security/users?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    },
		    datasources: {
		    	 method : 'get',
		    	 url : server + '/security/users?datasources',
		    	 isArray : true
		    },
		    assignDatasources : {
		    	method : 'post',
		    	url : server + '/security/users?assignDatasources'
		    }
		});
		
		// --------------
		// Role Resource
		// --------------
		var rolesResource = $resource(server + '/security/roles/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/security/roles?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    },
		    permissions: {
		    	 method : 'get',
		    	 url : server + '/security/roles?permissions',
		    	 isArray : true
		    },
		    assignPermissions : {
		    	method : 'post',
		    	url : server + '/security/roles?assignPermissions'
		    }
		});
		
		// --------------
		// Site Resource
		// --------------
		var sitesResource = $resource(server + '/modules/sites/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/modules/sites?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    publish : {
		        method : 'post',
		        url : server + '/modules/sites?publish',
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// -----------------
		// Column Resource
		// -----------------
		var columnsResource = $resource(server + '/modules/columns/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/modules/columns?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable = angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    siteColumn : {
		        method : 'get',
		        isArray : false,
		        url : server + '/modules/columns?siteColumn',
		    },
		    tree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/modules/columns?tree',
		    },
		    owntree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/modules/columns?owntree',
		    },
		    dataIds : {
		    	method : 'get',
		        isArray : true,
		        url : server + '/modules/columns?dataIds'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// ----------------
		// Custom Resource
		// ----------------
		var customsResource = $resource(server + '/modules/customs/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/modules/customs?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable = angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    sortsTree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/modules/customs?sortsTree',
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// ------------------
		// Template Resource
		// ------------------
		var templatesResource = $resource(server + '/templates/template/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : false,
		        url : server + '/templates/template/list',
		        transformResponse : function(response) {
	        	   var obj= angular.fromJson(response);
			       return obj;
		        }
		    },
		    queryByType: {
		    	 method : 'get',
			     isArray : true,
			     url : server + '/templates/template/findTemplateType',
		    },
		    create : {
			    method : 'post',
		    },
		    save : {
			    method : 'put',
		    }
		});
		
		// ------------------
		// Snippet Resource
		// ------------------
		var snippetsResource = $resource(server + '/templates/snippet/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : false,
		        url : server + '/templates/snippet/list',
		        transformResponse : function(response) {
	        	   var obj= angular.fromJson(response);
			       return obj;
		        }
		    },
		    create : {
			    method : 'post',
		    },
		    save : {
			    method : 'put',
		    }
		});
		
		// ----------------
		// Sort Resource
		// ----------------
		var sortsResource = $resource(server + '/systems/sort/:id', {
			id : "@id"
		}, {
			datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/systems/sort?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable = angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
			sorttree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/systems/sort?sortTree',
		        transformResponse : function(response) {
	        	   var obj= angular.fromJson(response);
			       return obj;
		        }
		    },
		    byType : {
		    	 method : 'get',
			        isArray : true,
			        url : server + '/systems/sort?byType',
			        transformResponse : function(response) {
		        	   var obj= angular.fromJson(response);
				       return obj;
			        }
		    },
		    sortsTree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/systems/sort?sortsTree',
		    },
		    create : {
			    method : 'post',
		    },
		    save : {
			    method : 'put',
		    }
		});
		
		// --------------
		// Model Resource
		// --------------
		var modelsResource = $resource(server + '/datas/models/:id', {
			id : "@id"
		}, {
			all : {
				method : 'get',
				isArray : true,
				url : server + '/datas/models',
			},
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/models?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    datasourceId : {
				method : 'get',
				url: server + '/datas/models'
			},
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// --------------
		// Field Resource
		// --------------
		var fieldsResource = $resource(server + '/datas/fields/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/fields?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    fields : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/fields?fields'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// --------------------
		// Datasource Resource
		// -------------------
		var datasourcesResource = $resource(server + '/datas/datasources/:id', {
			id : "@id"
		}, {
			all : {
				method : 'get',
				isArray : true,
				url : server + '/datas/datasources',
			},
			own : {
				method : 'get',
				isArray : true,
				url : server + '/datas/datasources?own',
			},
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/datasources?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    sortsTree : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/datasources?sortsTree',
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		// btach update service
		var batchResource = $resource(server + '/batchs/:id', {
			id : "@id"
		}, {
		    customs : {
		        method : 'get',
		        isArray : true,
		        url : server + '/batchs/customs',
		        transformResponse : function(response) {
			        return  angular.fromJson(response);
		        }
		    },
		    columns : {
		        method : 'get',
		        isArray : true,
		        url : server + '/batchs/columns',
		        transformResponse : function(response) {
			        return  angular.fromJson(response);
		        }
		    },
		    sites : {
		        method : 'get',
		        isArray : true,
		        url : server + '/batchs/sites',
		    },
		});
		
		// --------------------
		// Data Resource
		// -------------------
		var datasResource = $resource(server + '/datas/datas/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/datas?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    tags : {
		    	method : 'get',
		        isArray : true,
		        url : server + '/datas/datas?tags',
		    },
		    preview : {
		    	method : 'get',
		        url : server + '/datas/datas?preview',
		        transformResponse : function(response) {
		        	console.info(response);
			        return response;
		        }
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    },
		    remove : {
		    	method : 'delete',
		    	url : server + '/datas/datas'
		    },
		    dealTags: {
		    	 method : 'put',
		    	 url : server + '/datas/datas?dealTags'
		    },
		    stick: {
		    	 method : 'put',
		    	 url : server + '/datas/datas?stick'
		    },
		    unstick: {
		    	 method : 'put',
		    	 url : server + '/datas/datas?unstick'
		    },
		    submit : {
		    	 method : 'put',
		    	 url : server + '/datas/datas?submit'
		    },
		    pass1 : {
		    	 method : 'put',
		    	 url : server + '/datas/datas?pass1'
		    },
		    refuse1 : {
		    	 method : 'put',
		    	 url : server + '/datas/datas?refuse1'
		    },
		    pass2 : {
		    	 method : 'put',
		    	 url : server + '/datas/datas?pass2'
		    },
		    refuse2 : {
		    	 method : 'put',
		    	 url : server + '/datas/datas?refuse2'
		    },
		    copy: {
		    	 method : 'put',
		    	 url : server + '/datas/datas?copy'
		    },
		    move: {
		    	 method : 'put',
		    	 url : server + '/datas/datas?move'
		    },
		    ref: {
		    	 method : 'put',
		    	 url : server + '/datas/datas?ref'
		    }
		   
		});
		// --------------------
		// Tag Resource
		// -------------------
		var tagsResource = $resource(server + '/datas/tags/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/datas/tags?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    owns : {
		    	method : 'get',
		        isArray : true,
		        url : server + '/datas/tags?owns'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		});
		// plugin resource
		var pluginsResource = $resource(server + '/settings/plugins/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/settings/plugins?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		// jobTask resource
		var jobTasksResource = $resource(server + '/systems/tasks/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/systems/tasks?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    tasklogs : {
		        method : 'get',
		        isArray : false,
		        url : server + '/systems/tasks?tasklogs'
		    },
		    toggle : {
		        method : 'post',
		        url : server + '/systems/tasks?toggle'
		    },
		    tigger : {
		        method : 'post',
		        url : server + '/systems/tasks?tigger'
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		// systemLogs resource
		var systemLogsResource = $resource(server + '/systems/systemlogs/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/systems/systemlogs?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		// systemLogs resource
		var produceLogsResource = $resource(server + '/systems/producelogs/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/systems/producelogs?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		// params resource
		var paramsResource = $resource(server + '/systems/params/:id', {
			id : "@id"
		}, {
			find:{
				method:'get',
				isArray : false,
		        url : server + '/systems/params?find',
			},
		    all : {
		        method : 'get',
		        isArray : true
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		// params resource
		var indexsResource = $resource(server + '/systems/indexs/:id', {
			id : "@id"
		}, {
			find:{
				method:'get',
				isArray : false,
		        url : server + '/systems/params?find',
			},
		    all : {
		        method : 'get',
		        isArray : true
		    },
			execute:{
				method:'post',
		        url : server + '/systems/indexs?execute',
		        isArray : false,
			}
		});
		// manuscript organ resource
		var organsResource = $resource(server + '/manuscripts/organs/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/manuscripts/organs?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    level : {
		        isArray : true,
		        url : server + '/manuscripts/organs?level',
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		// manuscript member resource
		var membersResource = $resource(server + '/manuscripts/members/:id', {
			id : "@id"
		}, {
		    datatable : {
		        method : 'get',
		        isArray : true,
		        url : server + '/manuscripts/members?datatable',
		        transformResponse : function(response) {
			        $rootScope.datatable= angular.fromJson(response);
			        return $rootScope.datatable.data;
		        }
		    },
		    auditmembers : {
		        isArray : true,
		        url : server + '/manuscripts/members?auditMembers',
		        method : 'get'
		    },
		    auditdatasources : {
		        isArray : true,
		        url : server + '/manuscripts/members?auditDatasources',
		        method : 'get'
		    },
		    senddatasources : {
		        isArray : true,
		        url : server + '/manuscripts/members?sendDatasources',
		        method : 'get'
		    },
		    sendallot : {
		        url : server + '/manuscripts/members?sendAllot',
		        method : 'post'
		    },
		    auditallot : {
		        url : server + '/manuscripts/members?auditAllot',
		        method : 'post'
		    },
		    all : {
		        isArray : true,
		        method : 'get'
		    },
		    create : {
			    method : 'post'
		    },
		    save : {
			    method : 'put'
		    }
		});
		
		return {
			server,
			usersResource,
			menusResource,
			rolesResource,
			sitesResource,
			columnsResource,
			customsResource,
			templatesResource,
			snippetsResource,
			sortsResource,
			modelsResource,
			fieldsResource,
			datasourcesResource,
			datasResource,
			tagsResource,
			datasourcesResource,
			batchResource,
			pluginsResource,
			jobTasksResource,
			systemLogsResource,
			produceLogsResource,
			paramsResource,
			indexsResource,
			organsResource,
			membersResource
		};
	})
	
});
