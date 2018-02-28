$(function(){
	praise.init();
});
var praise = {
	init : function() {	
		$("."+zanCls).bind("click",function(){
			var obj=$(this);
			var siteId=obj.attr(sidAttr);
			var columnId=obj.attr(cidAttr);
			var recordId=obj.attr(ridAttr);
			var link=obj.attr(linkAttr);
			var num=obj.text();
			var cookieName=columnId+"_"+recordId;
			var val=praise.getCookie(cookieName);
			if(cookieName==val){
				//obj.attr("disabled", true);
				//alert('该文章已点赞');
				return false;
			}else{
				praise.praiseFun(siteId,columnId,recordId,link,cookieName,num);
				return false;
			}
		});
		$("."+numCls).each(function(){
			var obj=$(this);
			var columnId=obj.attr(cidAttr);
			var recordId=obj.attr(ridAttr);
			praise.numFun(columnId, recordId, obj);
		});
	},
	praiseFun : function(siteId,columnId,recordId,link,cookieName,num){
		if(link==''||link==undefined||link=='undefined'){
			link=document.location.toString();
		}
		$.ajax({
			url : domain + '/praise/save',
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
			error : function() {
			},
			success : function(data) {
				if(data=='-1'){
					alert('点赞失败');
				}else if(data=="-2"){
					alert('非法请求');
				}else{
					$("."+numCls).each(function(){
						var obj=$(this);
						var num_cid=obj.attr(cidAttr);
						var num_rid=obj.attr(ridAttr);
						var cusNum=parseInt(num)+1;
						if(num_cid==columnId&&num_rid==recordId){
							obj.text(cusNum);
						}
					});
					praise.setCookie(cookieName);
				}
			}
		});
	},
	numFun : function(columnId,recordId,obj){
		$.ajax({
			url : domain + '/praise/num',
			data :{
				recordId:recordId,
				host:document.domain
			},
			type : 'POST',
			dataType : "JSONP",
			jsonp:"callback",
			error : function() {
			},
			success : function(data) {
				if(data=='-1'){
					alert('加载失败');
				}else if(data=="-2") {
					alert('非法请求');
				}else{
					obj.text(data);
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
