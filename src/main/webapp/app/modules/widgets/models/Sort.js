define([ 'modules/templates/module' ], function(module) {

	'use strict';

	return module.registerFactory('Sort', function($q,resource) {

		return  {
			getSort:function(type){
				var dfd = $q.defer();

				var SortModel = {
					initialized : dfd.promise,
					sorts : undefined
				};
				resource.sortsResource.sorttree({type:type}).$promise.then(function(data) {
					SortModel.sorts = data;
					dfd.resolve(SortModel)
				});
				return SortModel;
			}
		}

	});

});
