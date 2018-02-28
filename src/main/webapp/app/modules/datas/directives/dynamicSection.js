define([ 'modules/datas/module', 'modules/forms/common', 'jquery-maskedinput', 'jquery-form', 'jquery-validation', 'jquery-validation.messages_cn' ], function(module, formsCommon) {
	'use strict';
	return module.registerDirective('dynamicSection', function($compile, $sce) {
		return {
		    restrict : 'A',
		    compile : function(element, attrs) {
			    return function(scope, element, attrs) {
				    var html = $sce.trustAsHtml("<fieldset>" + scope.model.formCode + "</fieldset>").toString();
				    $(element).prepend($(html));
				    $compile(element.find('fieldset'))(scope);
				    var fields = _.select(scope.model.fields, function(field) {
					    return field.required == true;
				    });
				    var rule = {};
				    _.forEach(fields, function(f) {
					    if (f.dataLength != 0) {
						    rule[f.fieldCode] = {
							    required : true,
							    maxlength : f.dataLength
						    };
					    } else {
						    rule[f.fieldCode] = {
							    required : true
						    };
					    }
				    });
				    var validator = element.validate(angular.extend({
				        onfocusout : false,
				        rules : rule,
				        ignore : '',
				        errorPlacement : function(error, element) {
					        if (element.attr('id').startWith(ck)) {
						        error.insertAfter($(element).parent().children().last());
					        } else {
						        error.insertAfter(element);
					        }
				        },
				        submitHandler : function() {
					        var parent = scope.$parent;
					        if (parent.edit == 'create') {
						        parent.createDataRequest();
					        } else if (parent.edit == 'save') {
						        parent.saveDataRequest();
					        }
				        }
				    }, formsCommon.validateOptions));
			    };
		    }
		}
	});
});
