define([ 'modules/datas/module', 'lodash' ], function(datasource, _) {

	'use strict';

	datasource.registerController('DataCtrl', function($http, $scope, $rootScope, $compile, $templateCache, $filter, $location,resource, $sce, subject,makeSocket,SmallBoxMessage) {
		var a = $scope;
		// ------------------
		// Data Definition
		// ------------------
		$scope.partialView = null;
		$scope.edit = 'create';
		$scope.tip = 'copy';
		$scope.model = null;
		$scope.columnId = null;
		$scope.datasource = null;
		$scope.tags = [];
		$scope.datasourceIds = [];
		$scope.columnsTree = {
		    data : null,
		    currentItem : null
		};
		$scope.datasourcesTree = {
		    data : null,
		    checkableData : null,
		    currentItem : null
		};
		$scope.sortsTree = {
			data : null
		};
		$scope.filter = {
		    view : 'columnTree',
		    datasourceId : '',
		    title : '',
		    startTime : '',
		    endTime : '',
		    checkStatus: 'PASS'
		}
		$scope.data = {
			datasource_id : ''
		};
		$scope.initialData = null;
		angular.copy($scope.data, $scope.initialData);
		$scope.rowSelected = null;
		$scope.tableOptions = {
			serverSide : false
		};
		$scope.assertTagSelected = function(tag) {
			return false;
		}
		// ------------------------------------
		// Resource Request Methods Definition
		// ------------------------------------

		// request columns as tree
		$scope.loadColumnsAsTreeRequest = function() {
			resource.columnsResource.owntree().$promise.then(function(data) {
				var siteMap = {}, columnMap = {};
				var fn = function(siteMap, columnMap, items) {
					for (var i = 0; i < items.length; i++) {
						var item = items[i];
						if (item.type == "SITE") {
							siteMap[item.id] = item;
						} else {
							columnMap[item.id] = item;
						}
						if (item.childrens && item.childrens.length != 0) {
							item['content'] = $('<span><i class="fa fa-lg fa-minus-circle"></i> ' + item.name + '</span>').data('item', item);
							item['expanded'] = false;
							fn(siteMap, columnMap, item.childrens);
						} else {
							item['content'] = $('<span><i class="icon-leaf"></i>' + item.name + '</span>').data('item', item);
							item['expanded'] = false;
						}
						item['children'] = [];
					}
				};
				fn(siteMap, columnMap, data);
				for ( var id in columnMap) {
					var val = columnMap[id];
					if (val.parentId) {
						columnMap[val.parentId]['children'][columnMap[val.parentId]['children'].length] = val;
					} else {
						siteMap[val.siteId]['children'][siteMap[val.siteId]['children'].length] = val;
					}
				}
				$scope.columnsTree.data = data;
			});
			$('#columnTree').on('click', 'span', function(e) {
				if (e.target.tagName == 'SPAN' && $(this).data('item').type == 'COLUMN') {
					$scope.columnsTree.currentItem = $(this).data('item');
					$scope.filter.datasourceId = $scope.columnsTree.currentItem.datasourceId;
					$scope.filter.title = null;
					$scope.filter.startTime = null;
					$scope.filter.endTime = null;
					$scope.filter.checkStatus = 'PASS';
					if ($scope.columnsTree.currentItem.valid) {
						$('#columnTree span').removeClass('active').removeClass('btn').removeClass('btn-default');
						$(this).addClass('active').addClass('btn').addClass('btn-default');
						$scope.loadModelRequest();
					}
				}
			});
		};

		// request datasources as tree
		$scope.loadDatasourcesAsTreeRequest = function() {
			resource.datasourcesResource.sortsTree().$promise.then(function(data) {
				$scope.sortsTree.data = data
				var orginSorsTreeData = _.clone(data, true);
				resource.datasourcesResource.own().$promise.then(function(data) {
					var map = {};
					for (var i = 0; i < data.length; i++) {
						var datasource = data[i];
						if (!map[datasource.sort.id]) {
							map[datasource.sort.id] = [];
						}
						map[datasource.sort.id].push(datasource);
					}
					var map1 = _.clone(map, true);
					var fn = function(sorts) {
						for (var j = 0; j < sorts.length; j++) {
							var sort = sorts[j];
							sort.type = 'SORT';
							var childrens = _.clone(sort.childrens);
							if (!map[sort.id]) {
								map[sort.id] = [];
							}
							for (var k = 0; k < map[sort.id].length; k++) {
								var datasource = map[sort.id][k];
								sort.childrens.push({
								    id : datasource.id,
								    name : datasource.title,
								    parentId : sort.id,
								    type : 'DATASOURCE',
								    children : [],
								    content : $('<span><i class="icon-leaf"></i>' + datasource.title + '</span>').data('item', datasource),
								    expanded : false
								});
							}
							if (sort.childrens && sort.childrens.length != 0) {
								sort.content = '<span><i class="fa fa-lg fa-minus-circle"></i> ' + sort.name + '</span>';
								sort.expanded = false;
							} else {
								sort.content = '<span><i class="icon-leaf"></i>' + sort.name + '</span>';
								sort.expanded = false;
							}
							sort.children = sort.childrens;
							if (childrens) {
								fn(childrens);
							}
						}
					}
					fn($scope.sortsTree.data);
					$scope.datasourcesTree.data = $scope.sortsTree.data;

					var fn1 = function(sorts) {
						for (var j = 0; j < sorts.length; j++) {
							var sort = sorts[j];
							sort.type = 'SORT';
							var childrens = _.clone(sort.childrens);
							if (!map1[sort.id]) {
								map1[sort.id] = [];
							}
							for (var k = 0; k < map1[sort.id].length; k++) {
								var datasource = map1[sort.id][k];
								sort.childrens.push({
								    id : datasource.id,
								    name : datasource.title,
								    parentId : sort.id,
								    type : 'DATASOURCE',
								    children : [],
								    content : $('<span><i class="icon-leaf"></i><input name="datasourceId" type="checkbox" value="' + datasource.id + '"/>' + datasource.title + '</span>').data('item', datasource),
								    expanded : false
								});
							}
							if (sort.childrens && sort.childrens.length != 0) {
								sort.content = '<span><i class="fa fa-lg fa-minus-circle"></i> ' + sort.name + '</span>';
								sort.expanded = false;
							} else {
								sort.content = '<span><i class="icon-leaf"></i>' + sort.name + '</span>';
								sort.expanded = false;
							}
							sort.children = sort.childrens;
							if (childrens) {
								fn1(childrens);
							}
						}
					}
					fn1(orginSorsTreeData);
					$scope.datasourcesTree.checkableData = orginSorsTreeData;

				});
			});
			$('#datasourceTree').on('click', 'span', function(e) {
				if (e.target.tagName == 'SPAN' && $(this).data('item')) {
					$('#datasourceTree span').removeClass('active').removeClass('btn').removeClass('btn-default');
					$(this).addClass('active').addClass('btn').addClass('btn-default');
					$scope.datasourcesTree.currentItem = $(this).data('item') ? $(this).data('item') : null;
					$scope.filter.datasourceId = $scope.datasourcesTree.currentItem ? $scope.datasourcesTree.currentItem.id : null;
					$scope.filter.title = null;
					$scope.filter.startTime = null;
					$scope.filter.endTime = null;
					$scope.filter.checkStatus = 'PASS';
					$scope.loadModelRequest();
				}
			});
		};

		// request load model
		$scope.loadModelRequest = function() {
			resource.modelsResource.datasourceId({
				datasourceId : $scope.filter.datasourceId
			}).$promise.then(function(model) {
				$scope.model = model;
				var columns = [ {
				    data : null,
				    class : 'details-control',
				    orderable : false,
				    defaultContent : ''
				} ];
				var o = _.find(model.fields, function(m) {
					return m.fieldCode == 'title';
				})
				if (!o) {
					columns.push({
					    data : null,
					    render : function() {
					    }
					});
				} else {
					columns.push({
					    data : "title",
					    render : function(data, type, row) {
						    if (row.stick == 1) {
							    return '<span style="color:red">[' + row.id + ']' + data + '</span>';
						    } else {
							    return '[' + row.id + ']' + data;
						    }
					    }
					});
				};
				columns.push({
				    data : 'check_status',
				    render : function(data, type, row) {
					    if(row.check_status == 'PASS'){
					    	return "通过审核";
					    }else if(row.check_status == 'ORIGIN'){
					    	return "未提交至一审";
					    }else if(row.check_status == 'FIRST_REFUSE'){
					    	return "一审拒绝";
					    }else if(row.check_status == 'FIRST'){
					    	return "提交至一审";
					    }else if(row.check_status == 'SECOND_REFUSE'){
					    	return "二审拒绝";
					    }else if(row.check_status == 'SECOND'){
					    	return "提交至二审";
					    }
				    }
				});
				columns.push({
				    data : 'create_time',
				    render : function(data, type, row) {
					    return $filter('date')(data, 'yyyy-MM-dd');
				    }
				});
				columns.push({
				    data : 'update_time',
				    render : function(data, type, row) {
					    return $filter('date')(data, 'yyyy-MM-dd');
				    }
				});
				columns.push({
				    data : null,
				    render : function(data, type, row) {
					    var passIcon = row.passed ? 'fa-circle' : 'fa-circle-o';
					    var passTooltip = row.passed ? '拒绝审核' : '通过审核';
					    var stickIcon = row.stick ? 'fa-level-up' : 'fa-level-down';
					    var stickTooltip = row.stick ? '取消置顶' : '置顶';
					    var edit = "<button id='save'  class='btn btn-xs btn-default' title='修改'><i class='fa fa-pencil'></i></button>";
					    var del = "<button id='del' class='btn btn-xs btn-default' title='删除'><i class='fa fa-times'></i></button>";
					    var tag = "<button id='tag' class='btn btn-xs btn-default' title='数据标签'><i class='fa fa-tags'></i></button>";
					    var stick = "<button id='stick' class='btn btn-xs btn-default' title='" + stickTooltip + "'><i class='fa " + stickIcon + "'></i></button>";
					    var pass = "<button class='btn btn-xs btn-default' title='" + passTooltip + "'><i class='fa " + passIcon + "'></i></button>";
					    var copy = "<button id='copy' class='btn btn-xs btn-default' title='复制到数据源'><i class='fa fa-copy'></i></button>";
					    var move = "<button id='move' class='btn btn-xs btn-default' title='移动到数据源'><i class='fa fa-cut'></i></button>";
					    var make = "<button id='make' class='btn btn-xs btn-default' title='生成相关的栏目内容页'><i class='fa fa-long-arrow-down'></i></button>";

					    var submit = "<button id='submit' class='btn btn-xs btn-default' title='提交至一审'><i class='fa  fa-thumbs-o-up'></i></button>";
					    var pass1 = "<button id='pass1' class='btn btn-xs btn-default' title='一审通过'><i class='fa  fa-thumbs-o-up'></i></button>";
					    var refuse1 = "<button id='refuse1' class='btn btn-xs btn-default' title='一审拒绝'><i class='fa fa-thumbs-o-down'></i></button>";
					    var pass2 = "<button id='pass2' class='btn btn-xs btn-default' title='二审通过'><i class='fa fa-thumbs-up'></i></button>";
					    var refuse2 = "<button id='refuse2' class='btn btn-xs btn-default' title='二审拒绝'><i class='fa  fa-thumbs-down'></i></button>";

					    var dynamicBtn = "";
					    var filterStatus = $scope.filter.checkStatus;
					    var rowStatus = row.checkStatus;
					    if(status == 'PASS'){
					    	dynamicBtn ='';
					    }else if(filterStatus == 'ORIGIN'){
					    		dynamicBtn = submit;
					    }else if(filterStatus == 'FIRST'){
					    		dynamicBtn = pass1 + refuse1;
					    } else if(filterStatus == 'SECOND'){
					    	dynamicBtn = pass2 + refuse2;
					    }
					    return edit + del + tag + stick + copy + move + make + dynamicBtn;
				    }
				});
				$scope.tableOptions = {
				    columns : columns,
				    serverSide : true,
				    ajax : function(data, callback, settings) {
					    var data = _.extend(data, {
					        datasourceId : $scope.filter.datasourceId,
					        title : $scope.filter.title,
					        startTime : $scope.filter.startTime,
					        endTime : $scope.filter.endTime,
					        checkStatus: $scope.filter.checkStatus
					    });
					    resource.datasResource.datatable(data).$promise.then(function(datas) {
						    var dataIds = _.map(datas, function(d) {
							    return d.id;
						    });
						    resource.columnsResource.dataIds({
							    dataId : dataIds.join(',')
						    }).$promise.then(function(columnDatas) {
							    var map = {};
							    _.forEach(columnDatas, function(columnData) {
								    if (!map[columnData.dataId]) {
									    map[columnData.dataId] = [];
								    }
								    map[columnData.dataId].push(columnData);
							    });
							    _.forEach(datas, function(data) {
								    data.columnDatas = map[data.id];
							    });
						    });

						    $scope.datatable.data = datas;
						    callback($scope.datatable);
						    angular.copy($scope.initialData, $scope.data);
						    $scope.rowSelected = false;
					    });
					    if ($scope.filter.datasourceId) {
						    resource.datasourcesResource.get({
							    'id' : $scope.filter.datasourceId
						    }).$promise.then(function(datasource) {
							    $scope.datasource = datasource;
						    });
					    }
				    }
				};
				$scope.partialView = null;
				$scope.partialView = '/app/modules/datas/views/partials/data-table.html?rnd=' + _.now();
			});
		};

		// request all tags
		$scope.loadTagsRequest = function() {
			resource.tagsResource.all().$promise.then(function(tags) {
				$scope.tags = tags;
			});
		}

		// request search data
		$scope.searchDataRequest = function() {
			$scope._refreshTable();
		};

		// request create data
		$scope.createDataRequest = function() {
			var formData = new FormData();
			formData.append('headerPic', $('#headerPic').prop('files')[0]);
			if ($('#headerPic').prop('files')[0]) {
				$http({
				    method : 'POST',
				    url : '/datas/datas/headerPic',
				    data : formData,
				    headers : {
					    'Content-Type' : undefined
				    },
				    transformRequest : angular.identity
				}).success(function(headerPic, status, headers, config) {
					$scope.data.headerPic = headerPic;
					new resource.datasResource($scope.data).$create().then(function() {
						$scope.partialView = '/app/modules/datas/views/partials/data-table.html?rnd=' + _.now();
					});
				});
			} else {
				new resource.datasResource($scope.data).$create().then(function() {
					$scope.partialView = '/app/modules/datas/views/partials/data-table.html?rnd=' + _.now();
				});
			}
		};

		// request save data
		$scope.saveDataRequest = function() {
			var formData = new FormData();
			formData.append('headerPic', $('#headerPic').prop('files')[0]);
			if ($('#headerPic').prop('files')[0]) {
				$http({
				    method : 'POST',
				    url : '/datas/datas/headerPic',
				    data : formData,
				    headers : {
					    'Content-Type' : undefined
				    },
				    transformRequest : angular.identity
				}).success(function(headerPic, status, headers, config) {
					$scope.data.headerPic = headerPic;
					$scope.data.$save().then(function() {
						$scope.partialView = '/app/modules/datas/views/partials/data-table.html?rnd=' + _.now();
					});
				});
			} else {
				$scope.data.$save().then(function() {
					$scope.partialView = '/app/modules/datas/views/partials/data-table.html?rnd=' + _.now();
				});
			}
		};

		// request remove data
		$scope.removeDataRequest = function() {
			$scope.data.$remove({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};

		// request stic data
		$scope.stickDataRequest = function() {
			$scope.data.$stick({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};

		// request unstick data
		$scope.unstickDataRequest = function() {
			$scope.data.$unstick({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};

		// request deal tag
		$scope.dealTagsRequest = function() {
			$scope.data.$dealTags({
			    datasourceId : $scope.datasource.id,
			    tagIds : (function() {
				    return _.map($('#select2 option:selected'), function(option) {
					    return $(option).val()
				    })
			    })()
			});
			$("#close-select-tag").click();
		};

		
		// request submit data
		$scope.submitRequest = function() {
			$scope.data.$submit({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};
		
		// request pass1 data
		$scope.pass1Request = function() {
			$scope.data.$pass1({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};

		// request refuse1 data
		$scope.refuse1Request = function() {
			$scope.data.$refuse1({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};
		// request pass2 data
		$scope.pass2Request = function() {
			$scope.data.$pass2({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};
		// request refuse2 data
		$scope.refuse2Request = function() {
			$scope.data.$refuse2({
			    id : $scope.data.id,
			    datasourceId : $scope.data.datasource_id
			}).then(function() {
				$scope._refreshTable();
			});
		};

		// request copy data to datasource
		$scope.copyRequest = function() {
			$scope.data.$copy({
			    datasourceId : $scope.datasource.id,
			    datasourceIds : $scope.datasourceIds
			});
			$("#close-select-multi-datasource").click();
		};
		// request move data to datasource
		$scope.moveRequest = function() {
			$scope.data.$move({
			    datasourceId : $scope.datasource.id,
			    datasourceIds : $scope.datasourceIds
			}).then(function() {
				$scope._refreshTable();
			});
			$("#close-select-multi-datasource").click();
		};
		// request put ref to datasource
		$scope.refRequest = function() {
			$scope.data.$ref({
			    columnId : $scope.columnId,
			    datasourceId : $scope.datasource.id,
			    datasourceIds : $scope.datasourceIds
			});
			$("#close-select-multi-datasource").click();
		};

		// ---------------------------------
		// Event Handler Methods Definition
		// ---------------------------------

		// select row command handler
		$scope.selectTableRowHandler = function(event) {
			var table = $('#datas_table').DataTable();
			var tr = $(event.target).closest('tr');
			if (tr.hasClass('highlight')) {
				tr.removeClass('highlight');
			} else {
				table.$('tr.highlight').removeClass('highlight');
				tr.addClass('highlight');
			}
			if (table.row(tr).data()) {
				$scope.data = table.row(tr).data();
			}
		};

		// search command handler
		$scope.searchDataHandler = function() {
			$scope.searchDataRequest();
		};

		// create command handler
		$scope.createDataHandler = function() {
			angular.copy($scope.initialData, $scope.data);
			$scope.edit = 'create';
			$scope.partialView = '/app/modules/datas/views/partials/data-edit.html?rnd=' + _.now();
		};

		// save command handler
		$scope.saveDataHandler = function() {
			$scope.edit = 'save';
			$scope.partialView = '/app/modules/datas/views/partials/data-edit.html?rnd=' + _.now();
		};

		$scope.submitHandler = function() {
			$scope.data.datasource_id = $scope.filter.datasourceId;
			$.each($('#data_form').find('textarea[id^=ck]'), function(ck) {
				var name = $(this).attr('name');
				$scope.data[name] = CKEDITOR.instances['ck' + name].getData();
				$(this).val(CKEDITOR.instances['ck' + name].getData());
			});
			var form = angular.element(data_form);
			form.submit();
		};
		$scope.returnHandler = function() {
			$scope.partialView = '/app/modules/datas/views/partials/data-table.html?rnd=' + _.now();
		};

		// remove command handler
		$scope.removeDataHandler = function() {
			$.SmartMessageBox({
			    title : "<i class='fa fa-refresh' style='color:green'></i> 删除数据源",
			    content : "是否删除选中的数据源？",
			    buttons : '[否][是]'
			}, function(ButtonPressed) {
				if (ButtonPressed == "是") {
					$scope.removeDataRequest();
				}
			});
		};
		// refresh table after create or save or remove
		// command
		$scope._refreshTable = function() {
			$scope.filter.startTime = $('#startTime').val();
			$scope.filter.endTime = $('#endTime').val();
			$scope.$broadcast('refreshTable', {
				id : 'datas_table'
			});
		};

		// ------------------
		// Data Initializing
		// ------------------
		$scope.loadColumnsAsTreeRequest();
		$scope.loadDatasourcesAsTreeRequest();
		$scope.loadTagsRequest();
		
		//makeSocket init
		makeSocket.initialize(function(msg){
			var tips="生成成功";
			if (msg.warn) {
				tips= msg.warn;
			} else {
				if (msg.error) {
					tips = msg.error;
				}
			}
			SmallBoxMessage.tips({
    			title:"提示",
    			content:tips,
    			timeout:3000
    		});
		});

		(function() {
			// save button handler
			$('#content').on('click', 'button[id=save]', function() {
				$scope.saveDataHandler();
			});
			// delete button handler
			$('#content').on('click', 'button[id=del]', function() {
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> 删除数据",
				    content : "是否删除选中的数据？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						$scope.removeDataRequest();
					}
				});
			});
			// stick button handler
			$('#content').on('click', 'button[id=stick]', function() {
				var tmp = $scope.data.stick == 0 ? '置顶' : '取消置顶';
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> " + tmp + "数据",
				    content : "是否" + tmp + "选中的数据？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						if ($scope.data.stick == 0) {
							$scope.stickDataRequest();
						} else if ($scope.data.stick == 1) {
							$scope.unstickDataRequest();
						}
					}
				});
			});
			// tag button handler
			$('#content').on('click', 'button[id=tag]', function() {
				var offset = $(this).offset();
				$("#select-tag-popover").offset({
				    top : offset.top + 17,
				    left : offset.left - 114,
				}).trigger('click');
			});
			// copy button handler
			$('#content').on('click', 'button[id=copy]', function() {
				$scope.tip = 'copy';
				var offset = $(this).offset();
				$("#select-multi-datasource-popover").offset({
				    top : offset.top,
				    left : offset.left,
				}).trigger('click').next().find('#tipTitle').html('复制到数据源');
			});
			// move button handler
			$('#content').on('click', 'button[id=move]', function() {
				$scope.tip = 'move';
				var offset = $(this).offset();
				$("#select-multi-datasource-popover").offset({
				    top : offset.top,
				    left : offset.left,
				}).trigger('click').next().find('#tipTitle').html('移动到数据源');
			});
			// makeFile button handler
			$('#content').on('click', 'button[id=make]', function(e) {
				e.preventDefault();
				$scope.tip = 'make';
				var id = $scope.data.id;
			    var datasourceId = $scope.data.datasource_id;
				var message = "CONTENT_ALONE#"+id+"#"+datasourceId;
				makeSocket.send(message);
			});
			// ref button handler
			$('#content').on('click', 'a[id=ref]', function(e) {
				e.preventDefault();
				$scope.tip = 'ref';
				var offset = $(this).offset();
				$("#select-multi-datasource-popover").offset({
				    top : offset.top,
				    left : offset.left,
				}).trigger('click').next().find('#tipTitle').html('作为引用添加到数据源');
				$scope.columnId = $(this).attr('columnid');
			});
			// preview button handler
			$('#content').on('click', 'a[id=preview]', function(e) {
				e.preventDefault();
				$scope.tip = 'preview';
				var id = $scope.data.id;
			    var datasourceId = $scope.data.datasource_id;
				var columnId = $(this).attr('columnid');
				window.open(resource.server+"/datas/datas?preview&id="+id+"&datasourceId="+datasourceId+"&columnId="+columnId+"&sessionId="+$location.search().sessionId);
			});
			$('#content').on('click', 'button[id=submit]', function(e) {
				e.preventDefault();
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> 审核数据",
				    content : "是否提交选中的数据至一审？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						$scope.submitRequest();
					}
				});
			});
			$('#content').on('click', 'button[id=pass1]', function(e) {
				e.preventDefault();
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> 审核数据",
				    content : "是否一审通过选中的数据？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						$scope.pass1Request();
					}
				});
			});
			$('#content').on('click', 'button[id=refuse1]', function(e) {
				e.preventDefault();
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> 审核数据",
				    content : "是否一审拒绝选中的数据？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						$scope.refuse1Request();
					}
				});
			});
			$('#content').on('click', 'button[id=pass2]', function(e) {
				e.preventDefault();
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> 审核数据",
				    content : "是否二审通过选中的数据？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						$scope.pass2Request();
					}
				});
			});
			$('#content').on('click', 'button[id=refuse2]', function(e) {
				e.preventDefault();
				$.SmartMessageBox({
				    title : "<i class='fa fa-refresh' style='color:green'></i> 审核数据",
				    content : "是否二审拒绝选中的数据？",
				    buttons : '[否][是]'
				}, function(ButtonPressed) {
					if (ButtonPressed == "是") {
						$scope.refuse2Request();
					}
				});
			});
			// select datasources save button handler
			$('#content').on('click', '#confirm-select-multi-datasource', function() {
				$scope.datasourceIds = [];
				$('#select-multi-datasource-popover').next().find('input:checkbox').each(function(index, item) {
					if ($(item).prop('checked')) {
						$scope.datasourceIds.push($(item).val());
						$(item).prop("checked", false);
					}
				});
				if ($scope.tip == 'copy') {
					$scope.copyRequest();
				} else if ($scope.tip == 'move') {
					$scope.moveRequest();
				} else if ($scope.tip == 'ref') {
					$scope.refRequest();
				}
			});
			// select datasources close button handler
			$('#content').on('click', '#close-select-multi-datasource', function() {
				$("#select-multi-datasource-popover").next().find('input:checkbox').each(function(index, item) {
					$(item).prop("checked", false);
				});
				$("#select-multi-datasource-popover").trigger('click');
			});
			$('#content').on('click', '#confirm-select-tag', function() {
				$scope.dealTagsRequest();
			});
			// select tags close button handler
			$('#content').on('click', '#close-select-tag', function() {
				$("#select-tag-popover").trigger('click');
			});
		})();
		$scope.$watch('filter.checkStatus', function(checkStatus){
			$scope._refreshTable();
		});
	})
});
