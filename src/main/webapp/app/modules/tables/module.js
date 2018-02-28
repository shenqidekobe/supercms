define([ 'angular', 'angular-couch-potato' ], function(ng, couchPotato) {

	"use strict";

	var module = ng.module('app.tables', [ 'ui.router' ]);

	couchPotato.configureApp(module)

	module.config(function() {

	});

	module.run(function($couchPotato) {
		module.lazy = $couchPotato;
	});
	return module;

});
