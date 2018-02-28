$(function(){
	hits.init();
});
var hits = {
	init : function() {	
			var obj=$("."+hitsCls);
			var siteId=obj.attr(sidAttr);
			var columnId=obj.attr(cidAttr);
			var recordId=obj.attr(ridAttr);
			var link=obj.attr(linkAttr);
			var cookieName="hits_"+columnId+"_"+recordId;
			var val=hits.getCookie(cookieName);
			if(cookieName!=val){
				hits.hitsFun(siteId,columnId,recordId,link,cookieName);
				return false;
			}
			
	},
	hitsFun : function(siteId,columnId,recordId,link,cookieName){
		if(link==''||link==undefined||link=='undefined'){
			link=document.location.toString();
		}
		$.ajax({
			url : domain + '/hits/save',
			data :{
				siteId:siteId,
				columnId:columnId,
				recordId:recordId,
				link:link,
				host:document.domain
			},
			type : 'POST',
			dataType : "JSONP",
			jsonp:"callback",
			error : function(e) {
				console.info("save hits is error."+e);
			},
			success : function(data) {
				if(data!='-1'&&data!='-2'){
					hits.setCookie(cookieName);
				}
			}
		});
	},
	setCookie : function(cookie){
		var date = new Date();
        var expiresDays = 30;                                    
        date.setTime(date.getTime() + expiresDays * 24 * 3600 * 1000);
        document.cookie=cookie+"="+cookie+";expires=" + date.toGMTString()+";path=/";
	},
	getCookie : function(cookie){
		var arrStr = document.cookie.split("; ");
	    for(var i = 0;i < arrStr.length;i ++){
	        var temp = arrStr[i].split("=");
	        if(temp[0] == cookie)return unescape(temp[1]);
	    }
	},
	delCookie : function(cookie){
		var date = new Date();
	    date.setTime(date.getTime() - 10000);
	    document.cookie = cookie + "="+cookie+"; expires=" + date.toGMTString();
	}
};
