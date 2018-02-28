define(['modules/widgets/module'],function(module){
	
	"use strict";
	
	 module.registerDirective('paginationWidget', function(){
	        return {
	            restrict: 'E',
	            replace: true,
	            scope:{
	            	pager:'=pagerObject',
	            	query:'=query'
	            },
	           // templateUrl: 'app/modules/widgets/directives/pagination-widget.tp.html',
	            controller:function($scope,$element){
	            	$scope.createHtml=function(){
	            		var maxPage =  Math.ceil($scope.pager.rowCount/ $scope.pager.pageSize) ;
	                    var pageth = $scope.pager.pageth;
	                    var str = '<ul class="pagination">';
	                    if(maxPage>1){
	                    	if(maxPage > 10){
		                        if(pageth > 3){//minPage + 2
		                        	str += '<li><a href-void><i class="fa fa-arrow-left"></i></a></li>';
		                        	str += '<li><a href-void>1</a></li>';
		                        	str += '<li><a href="javascripe:void(0);">...</a></li>';
		                        }
		                        for(var i= pageth <=2?1:pageth -2 ;i<= (pageth >= maxPage-2?maxPage:pageth +2) ;i++ ){
		                            if(i == 1){
		                                if(pageth == 1){
		                                    str += '<li class=\'disabled\'><a href-void><i class="fa fa-arrow-left"></i></a></li>';
		                                    str += '<li class=\'active\'><a href-void>'+i+'</a></li>';
		                                }else{
		                                    str += '<li><a href-void><i class="fa fa-arrow-left"></i></a></li>';
		                                    str += '<li><a href-void>'+i+'</a></li>';
		                                }
		                            }else if(i == maxPage){
		                                if(pageth == maxPage){
		                                    str += '<li class=\'active\'><a href-void>'+i+'</a></li>';
		                                    str += '<li class=\'disabled\'><a href-void><i class="fa fa-arrow-right"></i></a></li>';
		                                }else{
		                                    str += '<li><a href-void>'+i+'</a></li>';
		                                    str += '<li><a href-void><i class="fa fa-arrow-right"></i></a></li>';
		                                }
		                            }else{
		                                if(pageth == i){
		                                    str +=  '<li class=\'active\'><a href-void>'+i+'</a></li>';
		                                }else{
		                                    str += '<li><a href-void>'+i+'</a></li>';
		                                }
		                            }
		                        }
		                        if(pageth < maxPage - 2){
		                            str += '<li><a href="javascripe:void(0);">...</a></li>';
		                            str += '<li><a href-void>'+maxPage+'</a></li>';
		                            str += '<li><a href-void><i class="fa fa-arrow-right"></i></a></li>';
		                        }
		                    }else{
		                        for(var i=1 ; i<=maxPage ; i++){
		                            if(i == 1){
		                                if(pageth == 1){
		                                	str += '<li class=\'disabled\'><a href-void><i class="fa fa-arrow-left"></i></a></li>';
		                                	str += '<li class=\'active\'><a href-void>'+i+'</a></li>';
		                                }else{
		                                	str += '<li><a href-void><i class="fa fa-arrow-left"></i></a></li>';
		                                	str += '<li><a href-void>'+i+'</a></li>';
		                                }
		                            }else if(i == maxPage){
		                                if(pageth == maxPage){
		                                	str += '<li class=\'active\'><a href-void>'+i+'</a></li>';
		                                    str += '<li class=\'disabled\'><a href-void><i class="fa fa-arrow-right"></i></a></li>';
		                                }else{
		                                	str += '<li><a href-void>'+i+'</a></li>';
		                                    str += '<li><a href-void><i class="fa fa-arrow-right"></i></a></li>';
		                                }
		                            }else{
		                                if(pageth == i){
		                                	str += '<li class=\'active\'><a href-void>'+i+'</a></li>';
		                                }else{
		                                	str += '<li><a href-void>'+i+'</a></li>';
		                                }
		                            }
		                        }
		                    }
	                    }
	            		str += '</ul>';
	                    $element.html(str);
	                    $scope.bindEvent();
	            	};
	            	$scope.bindEvent=function(){
	            		$element.find('a').on('click',function(){
	            			var text=$(this).html();
	            			var pageth=$scope.pager.pageth;
	            			if(text=='...'||$(this).parent().attr("class")=='disabled'){
	            				return;
	            			}else if(text=='<i class="fa fa-arrow-left"></i>'){
	            				$scope.pager.pageth=pageth-1;
	            			}else if(text=='<i class="fa fa-arrow-right"></i>'){
	            				$scope.pager.pageth=pageth+1;
	            			}else{
	            				$scope.pager.pageth = parseInt(text);
	            			}
	            			$scope.query();
	            			$scope.createHtml();
	            		});
	            	}
	            	$scope.createHtml();
	            	$scope.$watch('pager.rowCount',function(){
	            		$scope.createHtml();
	            	});
	            }
	        }
	 });
	
})