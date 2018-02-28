define([ 'modules/datas/module', 'lodash' ], function(model, _) {

	'use strict';

	model.registerController('PreViewCtrl', function($scope,$location,resource) {
		var search=$location.search();
		$scope.html = null;
		/*resource.datasResource.preview({id:search.id,datasourceId:search.datasourceId,columnId:search.columnId}).$promise.then(function(data) {
			console.info(data);
			$scope.html=data;
		});*/
		$.ajax({
			url:'http://127.0.0.1/datas/datas?preview',
			data:{
				id:search.id,datasourceId:search.datasourceId,columnId:search.columnId
			},
			dataType:"html",
			success:function(data){
				$scope.html=data;
			}
		});
	});
	
});
