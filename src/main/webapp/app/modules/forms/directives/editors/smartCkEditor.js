define([ 'modules/forms/module', 'ckeditor' ], function(module) {

	'use strict';

	module.registerDirective('smartCkEditor', function($filter) {
		return {
		    restrict : 'A',
		    compile : function(tElement) {
			    tElement.removeAttr('smart-ck-editor data-smart-ck-editor');

			    CKEDITOR.replace(tElement.attr('id'), {
			        toolbar : [ [ 'Styles', 'Format', 'Font', 'FontSize' ], 
			                [ 'Bold', 'Italic', 'Underline', 'StrikeThrough', '-', 'Undo', 'Redo', '-', 'Cut', 'Copy', 'Paste', 'Find', 'Replace', '-', 'Outdent', 'Indent', '-', 'Print' ], 
			                [ 'NumberedList', 'BulletedList', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ], [ 'Image', 'Table', '-', 'Link', 'Flash', 'Smiley', 'TextColor', 'BGColor', 'Source','Maximize' ] ],
			        height : '400px',
			        startupFocus : true,
			        filebrowserBrowseUrl: '/plugin/ckfinder/ckfinder.html',
			        filebrowserImageBrowseUrl: '/plugin/ckfinder/ckfinder.html?Type=Images&start=Images:/' + $filter('date')(new Date(),'yyyy-MM-dd')  +'/',
			        filebrowserUploadUrl: '/plugin/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Files',
			        filebrowserImageUploadUrl: '/plugin/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Images&currentFolder=/'+ $filter('date')(new Date(),'yyyy-MM-dd')  +'/',
			        filebrowserWindowWidth : '1000',
			        filebrowserWindowHeight : '700',
			        
			    });
		    }
		}
	});
});
