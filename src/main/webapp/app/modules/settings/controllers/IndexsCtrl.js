define([ 'modules/settings/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('IndexsCtrl', function($scope, resource,SmallBoxMessage) {
		$scope.executeRequest = function(){
			new resource.indexsResource.execute().$promise.then(function(rsp){
				console.info(rsp);
				SmallBoxMessage.tips({
 	    			title:"提示",
 	    			content:rsp.value,
 	    			timeout:5000
 	    		});
			});
		};

	})
});
