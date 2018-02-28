define([ 'modules/datas/module' ], function(module) {

	'use strict';

	return module.registerDirective('uploadHeaderPic', function() {
		return {
		    restrict : 'A',
		    scope: true,
		    link : function(scope, element, attrs) {
			    element.change(function(e) {
				    var f = e.target.files[0];
				    if(f){
				    	this.parentNode.nextSibling.value=this.value;
				    	var reader = new FileReader();
				    	reader.onload = (function(theFile) {
				    		return function(e) {
				    			var span = document.createElement('span');
				    			span.innerHTML = [ '<img style="height:70px;border:1px solid #000;margin: 0 0 0 0" src="', e.target.result, '" title="', escape(theFile.name), '"/>' ].join('');
				    			$('#' + attrs.privewContainer).empty().append(span);
				    			$('#' + attrs.privewContainer).show();
				    		};
				    	})(f);
				    	reader.readAsDataURL(f);
				    }
			    });
		    }
		}
	});
});
