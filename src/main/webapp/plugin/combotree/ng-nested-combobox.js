/**
 * Created by Maciek on 2014-06-29.
 */

angular.module('ui.nested.combobox', []).controller('NestedComboboxController', [ '$scope', '$element', '$attrs', function($scope, $element, $attrs) {
	'use strict';
	var that = this;
	this.isOpen = false;
	this.currentMember = $scope.currentMember;

	$scope.$watch('controlDisabled', function(value) {
		that.controlDisabled = value;
	});

	/*
	 * $element.on('blur', function (e) { //that.isOpen.status =
	 * !that.isOpen.status; that.isOpen = false; }); $element.on('focus',
	 * function (e) { //that.isOpen.status = !that.isOpen.status; that.isOpen =
	 * true; });
	 */

	this.toggleOpen = function() {

		if (that.controlDisabled === 'true') {
			this.isOpen.status = false;
			return false;
		}
		this.isOpen = !this.isOpen;
	};

	this.selectValue = function(event, member) {
		if (member.id === 'root') {
			member.name = event.currentTarget.innerText;
		}
		if($scope.changeEvent){
			$scope.changeEvent(member);
		}
		that.currentMember = member;
		$scope.currentMember = member;
	};
} ]).directive('nestedComboBox', function() {
	'use strict';

	return {
	    restrict : 'E',
	    controller : 'NestedComboboxController',
	    controllerAs : 'gs',
	    replace : true,
	    templateUrl : 'plugin/combotree/template/select-group.html',
	    scope : {
	        collection : '=',
	        currentMember : '=',
	        controlClass : '@',
	        controlDisabled : '@',
	        changeEvent : '='
	    },
	    link : function(scope, element, attrs, ctrl) {
		    scope.$watch('currentMember', function() {
			    ctrl.currentMember = scope.currentMember;
		    });
	    }
	};
});