define([ 'angular', 'angular-couch-potato' ], function(ng, couchPotato) {

	"use strict";

	var module = ng.module('app.services', ['ngResource']);

	couchPotato.configureApp(module);

	module.config(function($stateProvider, $couchPotatoProvider) {

	});

	module.run(function($couchPotato) {
		module.lazy = $couchPotato;
	});

	return module;
});