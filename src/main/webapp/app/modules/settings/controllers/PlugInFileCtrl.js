define([ 'modules/settings/module', 'lodash' ], function(module, _) {

	'use strict';

	module.registerController('PlugInFileCtrl', function($scope,$stateParams,resource,SmallBoxMessage) {
		$scope.rootPath=$stateParams.pluginPath;
		$scope.url="app/filemanager/index.html?fileRoot=resource/plugin/"+$scope.rootPath;
	});
	
});