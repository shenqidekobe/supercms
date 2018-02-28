define([ 'modules/tables/module', 
         'datatables', 
         'datatables-responsive',
         'datatables-colvis',
         'datatables-bootstrap',
         'datatables-treetable' ], function(module) {

	'use strict';

	return module.registerDirective('datatableTreetable', function($compile) {
		return {
		    restrict : 'A',
		    scope : {
			    tableOptions : '='
		    },
		    link : function(scope, element, attributes) {
			    var options = {
			        dom : "X<'dt-toolbar'>t<'dt-toolbar-footer'<'col-sm-6 hidden-xs'i><'col-sm-6 col-xs-12'p>>",
			        language : {
			            processing : "处理中...",
			            lengthMenu : "显示 _MENU_ 项结果",
			            zeroRecords : "没有匹配结果",
			            info : "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
			            infoEmpty : "显示第 0 至 0 项结果，共 0 项",
			            infoFiltered : "(由 _MAX_ 项结果过滤)",
			            infoPostFix : "",
			            search : "搜索:",
			            url : "",
			            emptyTable : "表中数据为空",
			            loadingRecords : "载入中...",
			            infoThousands : ",",
			            paginate : {
			                first : "首页",
			                previous : "上页",
			                next : "下页",
			                last : "末页"
			            },
			            aria : {
			                sortAscending : ": 以升序排列此列",
			                sortDescending : ": 以降序排列此列"
			            }
			        },
			        ordering : false,
			        searching : false,
			        lengthChange : false,
			        serverSide : true,
			        smartResponsiveHelper : null,
			        retrieve : true,
			        preDrawCallback : function() {
				        if (!this.smartResponsiveHelper) {
					        this.smartResponsiveHelper = new ResponsiveDatatablesHelper(element, {
					            tablet : 1024,
					            phone : 480
					        });
				        }
			        },
			        rowCallback : function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
				        this.smartResponsiveHelper.createExpandIcon(nRow);
			        },
			        drawCallback : function(oSettings) {
				        this.smartResponsiveHelper.respond();
			        },
			        oTreeTable: {
			        	showExpander: true,
			        	expandable: true,
			        	force: true,
			        	initialState : 'expanded',
			        	fnPreInit: function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
			        		if(iDisplayIndex==0){
			        			$(nRow).attr('data-tt-id', 1);
			        		}else{
			        			$(nRow).attr('data-tt-id', 2);
			        			$(nRow).attr('data-tt-parent-id', 1);
			        		}
			        	}
			        }
			    };
			    
			    if (attributes.tableOptions) {
				    options = angular.extend(options, scope.tableOptions)
			    }

			    var _dataTable;

			    var childFormat = element.find('.smart-datatable-child-format');
			    if (childFormat.length) {
				    var childFormatTemplate = childFormat.remove().html();
				    element.on('click', childFormat.data('childControl'), function() {
					    var tr = $(this).closest('tr');

					    var row = _dataTable.row(tr);
					    if (row.child.isShown()) {
						    // This row is already open - close it
						    row.child.hide();
						    tr.removeClass('shown');
					    } else {
						    // Open this row
						    var childScope = scope.$new();
						    childScope.d = row.data();
						    var html = $compile(childFormatTemplate)(childScope);
						    row.child(html).show();
						    tr.addClass('shown');
					    }
				    })
			    }

			    _dataTable = $(element).DataTable(options);

			    scope.$on('refreshTable', function(event, args) {
			    	if(_dataTable.table().node().id == args.id){
			    		_dataTable.ajax.reload(null, true);
			    	}
			    });
		    	/**
			    $("#columns_table").treetable({
				    expandable : true
			    });*/
		    }
		}
	});
});
