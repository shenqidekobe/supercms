define([ 'modules/services/module', 'lodash','sockjs'], function(module, _,sockjs) {

	'use strict';
	
	module.registerFactory('makeSocket', function($rootScope,MakeFileWebSocket,MakeFileWebSocketSock,SmallBoxMessage) {
		//websocket connection requestAndresponse
		var WebSocketObj = {};
		WebSocketObj.socket = null;
		WebSocketObj.socketState=-1;
		WebSocketObj.callback = null;
		WebSocketObj.connect = (function(callback) {
			WebSocketObj.callback=callback;
			var socketAddress=MakeFileWebSocket;
			if ('WebSocket' in window) {
				WebSocketObj.socket = new WebSocket(MakeFileWebSocket);
			} else if ('MozWebSocket' in window) {
				WebSocketObj.socket = new MozWebSocket(MakeFileWebSocket);
			} else {
				WebSocketObj.socket = new SockJS(MakeFileWebSocketSock);
				socketAddress=MakeFileWebSocketSock;
			}
			WebSocketObj.socket.onopen = function(evnt) {
				WebSocketObj.socketState=1;
				console.log('WebSocket connection opened...');
			};
			WebSocketObj.socket.onmessage = function(evnt) {
				console.log("Callback Message: " + evnt.data);
	    		var msg = $.parseJSON(evnt.data);
	    		WebSocketObj.callback(msg);
			};
			WebSocketObj.socket.onerror = function(evnt) {
				WebSocketObj.socketState=-1;
				console.log('WebSocket connection error...'+evnt);
				SmallBoxMessage.tips({
 	    			title:"连接异常",
 	    			content:"websocket服务连接失败,请检查服务地址是否正确.当前服务地址："+socketAddress,
 	    			timeout:15000
 	    		});
			};
			WebSocketObj.socket.onclose = function(evnt) {
				WebSocketObj.socketState=0;
				console.log('WebSocket connection closed...');
			}
		});
		WebSocketObj.initialize = function(callback) {
			WebSocketObj.connect(callback);
	    };
	    WebSocketObj.send = function(message) {
    		if( WebSocketObj.socketState!=1){
    			WebSocketObj.initialize(function(){
    			});
    			if(WebSocketObj.socketState!=1){
    				SmallBoxMessage.tips({
     	    			title:"连接未打开",
     	    			content:"websocket连接未打开,请刷新页面或者重新登录。",
     	    			timeout:15000
     	    		});
    				return false;
    			}
    		}
    		WebSocketObj.socket.send(message);
	    };
	    return WebSocketObj;
	});
	
});