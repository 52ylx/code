/**
 基本工具
 */
var lx = {
    /* 服务器根 */
    wwwroot: '',
    ajax: function(url,para,funcallback) {
        var that = this;
        var newurl = this.wwwroot + url;
        if(newurl.indexOf("?") >= 0) {
            newurl += "&token=" + $.cookie('token');
        }else{
            newurl += "?token=" + $.cookie('token');
        }
        $.ajax(newurl, {
            async: true,
            cache: false,
            dataType: "json",
            data: para,
            type: 'POST',
            success: function(data) {
                funcallback(data)
            }
        });
    },request: function(paras) {
        var url = location.href;
        var paraString = url.substring(url.indexOf("?") + 1, url.length).split("&");
        var paraObj = {}
        for (i = 0; j = paraString[i]; i++) {
            paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = decodeURIComponent(j.substring(j.indexOf("=") + 1, j.length));
        }
        var returnValue = paraObj[paras.toLowerCase()];
        if (typeof(returnValue) == "undefined") {
            return "";
        } else {
            return returnValue;
        }
    },isFunc: function (funcName) {
        try {
            if (typeof(eval(funcName))=="function") {
                return true;
            }
        } catch (e) {
        }
        return false;
    },toJSON:function(str){
        return eval('(' + str + ')');
    },replaceAll: function (str,FindText, RepText) {
        var regExp = new RegExp(FindText, "g");
        return str.replace(regExp, RepText);
    }

};
var curWwwPath = window.document.location.href;
var pathName = window.document.location.pathname;
if("/" == pathName){
    lx.wwwroot = window.document.location.href;
}else {
    var pos = curWwwPath.indexOf(pathName);
    lx.wwwroot = curWwwPath.substring(0, pos)+"/";
}

