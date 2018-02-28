{
		"_comment": "IMPORTANT : go to the wiki page to know about options configuration https://github.com/simogeo/Filemanager/wiki/Filemanager-configuration-file",
    "options": {
        "culture": "zh-cn",
        "lang": "java",
        "theme": "flat-dark",
        "defaultViewMode": "grid",
        "autoload": true,
        "showFullPath": true,
        "showTitleAttr": false,
        "browseOnly": false,
        "showConfirmation": true,
        "showThumbs": true,
        "generateThumbnails": true,
        "searchBox": true,
        "listFiles": true,
        "fileSorting": "default",
        "chars_only_latin": false,
        "dateFormat": "Y M d H:i",
        "serverRoot": true,
        "baseUrl": false,
        "logger": false,
        "capabilities": ["select", "download", "rename", "delete", "replace"],
        "plugins": []
    },
    "security": {
        "allowFolderDownload": true,
        "allowChangeExtensions": false,
        "allowNoExtension": false,
        "uploadPolicy": "DISALLOW_ALL",
        "uploadRestrictions": [
            "css",
            "jpg",
            "jpe",
            "jpeg",
            "gif",
            "png",
            "svg",
            "txt",
            "pdf",
            "odp",
            "ods",
            "odt",
            "rtf",
            "doc",
            "docx",
            "xls",
            "xlsx",
            "ppt",
            "pptx",
            "csv",
            "ogv",
            "mp4",
            "webm",
            "m4v",
            "ogg",
            "mp3",
            "wav",
            "zip",
            "rar",
            "js"
        ]
    },
    "upload": {
        "multiple": true,
        "number": 10000,
        "overwrite": false,
        "imagesOnly": false,
        "fileSizeLimit": 16
    },
    "exclude": {
        "unallowed_files": [
            ".htaccess",
            "web.config"
        ],
        "unallowed_dirs": [
            "_thumbs",
            ".CDN_ACCESS_LOGS",
            "cloudservers"
        ],
        "unallowed_files_REGEXP": "/^\\./",
        "unallowed_dirs_REGEXP": "/^\\./"
    },
    "images": {
        "imagesExt": [
            "jpg",
            "jpe",
            "jpeg",
            "gif",
            "png",
            "svg"
        ],
        "resize": {
        	"enabled":true,
        	"maxWidth": 1280,
            "maxHeight": 1024
        }
    },
    "videos": {
        "showVideoPlayer": true,
        "videosExt": [
            "ogv",
            "mp4",
            "webm",
            "m4v"
        ],
        "videosPlayerWidth": 400,
        "videosPlayerHeight": 222
    },
    "audios": {
        "showAudioPlayer": true,
        "audiosExt": [
            "ogg",
            "mp3",
            "wav"
        ]
    },
    "pdfs": {
        "showPdfReader": true,
        "pdfsExt": [
            "pdf",
            "odp"
        ],
	    "pdfsReaderWidth": "640",
        "pdfsReaderHeight": "480"	
    },
    "edit": {
        "enabled": true,
        "lineNumbers": true,
        "lineWrapping": true,
        "codeHighlight": false,
        "theme": "elegant",
        "editExt": [
            "css",
            "html",
            "shtml"
        ]
    },
    "customScrollbar": {
    	"enabled": true,
    	"theme": "inset-2-dark",
    	"button": true
    },
    "extras": {
        "extra_js": [],
        "extra_js_async": true
    },
    "icons": {
        "path": "images/fileicons/",
        "directory": "_Open.png",
        "default": "default.png"
    },
    "url": "https://github.com/simogeo/Filemanager",
    "version": "2.0.0-dev"
}