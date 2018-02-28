/**
 * 当前js只能满足于纯字符串的纯文字的文章(不能带有任何html标记的文章)，因为带有html标记的文章，分页时无法截取字符(会截取掉html标记)。
 * 如果要实现带有html标记的文章实现方案有：
 * 1、纯编辑创建文章的时候，直接每页每页的添加，后台直接按照编辑添加的文章生成多页。
 * 2、编辑创建文件的时候，在文章源码文件插入一个分页的标识符，然后前端根据标识符来进行分页。
 * 3、采取获取第一页，后续使用查看全文的方式进行处理。
 * 4、纯代码控制html标记强行进行分页，这种方式会超级的复杂，很可能会因为某个html标记处理不断导致页面显示不全。
 * */
$(function(){
	var obj=$("."+contentClass);
	var htmlText=obj.html();
	obj.html('');
	pagenation(htmlText, charSize); 
});
function pagenation(content,charSize) {  
    with (this) {  
	    this.defaultSize=500;//每页默认字符
        this.content = content;  
        this.contentLength = content.length;  
        this.pageSizeCount;  
        this.perpageLength = charSize==""?this.defaultSize:charSize;
        this.currentPage = 1;  
        this.regularExp =  /(\d+)/;
        this.divDisplayContent;  
        this.contentStyle = null;  
        this.strDisplayContent = "";  
        this.divDisplayPagenation;  
        this.strDisplayPagenation = "";   
        divDisplayContent = $("."+contentClass);
		
		if ($("."+pagerClass).length>0) {  
			divDisplayPagenation = $("."+pagerClass);
		}else {  
			try {  
				divDisplayPagenation = document.createElement("div");  
				divDisplayPagenation.id = "divPagenation";  
				divDisplayPagenation.className="pagerClass";  
				divDisplayPagenation=$(divDisplayPagenation);
				divDisplayContent.after(divDisplayPagenation);  
			}  
			catch (e) {  
				return false;  
			}  
		}  
        pagenation.initialize();
        
        return this; 
    }
};
pagenation.initialize = function() {  
    with (this) {  
	    if (contentLength <= perpageLength) {  
	        strDisplayContent = content;  
	        divDisplayContent.html(strDisplayContent);  
	        return null;  
	    }  
	    pageSizeCount = Math.ceil((contentLength / perpageLength));  
	    pagenation.goto(currentPage);  
	    pagenation.displayContent();  
   }   
};  
pagenation.displayPage = function() {  
    with (this) {  
        strDisplayPagenation = "";  
        if (currentPage && currentPage != 1)  
            strDisplayPagenation += '<a href="javascript:void(0)" href="javascript:void(0)" onclick="pagenation.previous()">上一页</a>  ';  
        else  
            strDisplayPagenation += "上一页  ";  
        for (var i = 1; i <= pageSizeCount; i++) {  
            if (i != currentPage)  
                strDisplayPagenation += '<a href="javascript:void(0)" href="javascript:void(0)" onclick="pagenation.goto(' + i + ');">' + i + '</a>  ';  
            else  
                strDisplayPagenation += i + "  ";  
        }  
        if (currentPage && currentPage != pageSizeCount)  
            strDisplayPagenation += '<a href="javascript:void(0)" href="javascript:void(0)" onclick="pagenation.next()">下一页</a>  ';  
        else  
            strDisplayPagenation += "下一页  ";  
            strDisplayPagenation += "共 " + pageSizeCount + " 页";
            divDisplayPagenation.html(strDisplayPagenation);  
    }   
};  
pagenation.previous = function() {  
    with (this) {  
        pagenation.goto(currentPage - 1);  
    }   
};  
pagenation.next = function() {  
    with (this) {  
        pagenation.goto(currentPage + 1);  
    }   
};  
pagenation.goto = function(iCurrentPage) {  
    with (this) {  
        if (regularExp.test(iCurrentPage)) {  
            currentPage = iCurrentPage;  
            //content.replace(/<[^>]+>/g,"");
            strDisplayContent = content.substr((currentPage - 1) * perpageLength, perpageLength);  
        }  
        else {  
            console.info("page parameter error!");  
        }  
        pagenation.displayPage();  
        pagenation.displayContent();  
    }   
};  
pagenation.displayContent = function() {  
    with (this) {  
        divDisplayContent.html(strDisplayContent);  
    }   
};