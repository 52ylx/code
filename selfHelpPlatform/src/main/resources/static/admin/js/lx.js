/**
 基本工具
 */
var lx = {
    /* 服务器根 */
    wwwroot: ''
    /* 回到顶部  */
    ,returntop:function(){
        $("html,body").animate({scrollTop:0},1000);
    }
    ,/* json to string */
    O2String: function(O) {
        try {
            var j = JSON.stringify(O);
            return j;
        } catch (e) {
            var S = [];
            var J = "";
            if (Object.prototype.toString.apply(O) === '[object Array]') {
                for (var i = 0; i < O.length; i++)
                    S.push(this.O2String(O[i]));
                J = '[' + S.join(',') + ']';
            } else if (Object.prototype.toString.apply(O) === '[object Date]') {
                J = "new Date(" + O.getTime() + ")";
            } else if (Object.prototype.toString.apply(O) === '[object RegExp]' || Object.prototype.toString.apply(O) === '[object Function]') {
                J = O.toString();
            } else if (Object.prototype.toString.apply(O) === '[object Object]') {
                for (var i in O) {
                    O[i] = typeof(O[i]) == 'string' ? '"' + O[i] + '"' : (typeof(O[i]) === 'object' ? this.O2String(O[i]) : O[i]);
                    S.push(i + ':' + O[i]);
                }
                J = '{' + S.join(',') + '}';
            }
            return J;
        }
    },
    ajax: function(url, funcallback,data) {
        this.ajaxasync(true,url,funcallback,data);
    },ajaxasync: function(async,url, funcallback,data) {
        var that = this;
        var newurl = this.wwwroot + url;
        if(newurl.indexOf("?") >= 0) {
            newurl += "&token=" + $.cookie('token');
        }else {
            newurl += "?token=" + $.cookie('token');
        }
        $.ajax(newurl, {
            async: async,
            cache: false,
            dataType: "json",
            data: data,
            type: 'POST',
            processData: true,
            layerIndex: -1,
            beforeSend: function () {
                this.layerIndex = layer.load(0, {shade: [0.5, '#666c7f']});
            },
            complete: function () {
                layer.close(this.layerIndex);
            },
            success: function(data) {
                if(that.login(data)){
                    funcallback(data);
                }
            }
        });
    }
    ,login_open:false
    ,login:function(data){
        if (data.success == '0'){//报错了
            layer.alert(data.msg,{icon:5})
            return false;
        } else if (data.success != '9') {//登录失效了
            return true;
        }
        var that = this;
        if (!that.login_open){
            that.login_open = true;
            layer.prompt({title: '登录',area: ['250px', '240px'], formType: 0, btn: ['确定','刷新'],btn2:function () {
                changeCode();
                return false;
            }}, function(name, index){
                lx.ajax('/admin/login?login_name='+name+'&PassWord='+$("#layui-layer-content_pass").val()+'&code='+$("#layui-layer-content_code").val(),function(data) {
                    if ("success" == data.result) {
                        $.cookie('token',data.token);
                        $.cookie('user',JSON.stringify(data));
                    }
                    location.replace(location.href);
                });
                that.login_open = false;
                layer.close(index);
            });
            $(".layui-layer-content").append(`
            <input type="password" id="layui-layer-content_pass" class="layui-layer-input" placeholder="密码" value="" style="margin-top: 6px;">
            <input type="text" id="layui-layer-content_code" class="layui-layer-input" placeholder="验证码" value="0000" style="margin-top: 6px;background:url('${lx.wwwroot+"/login/code"}') no-repeat scroll right center transparent;">
            <script>
                function changeCode() {
                    document.getElementById("layui-layer-content_code").style.backgroundImage="url("+lx.wwwroot+"/login/code?t=" + new Date().getTime()+")";
                }
                document.onkeydown=function(e){
              var a=e||window.event;//加这个火狐下不会报 event is  undefind
                if (a.keyCode == 13){
                   $(".layui-layer-btn0").click();
                }
             }
            </script>
            `);
        }
        return false;
    },
    sys_service: function(data, funcallback) {
        var newurl = this.wwwroot + 'sys/service';
        this.ajax(newurl,funcallback,data);
    }
    /* 分析地址参数等 */
    ,request: function(paras) {
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
    },guid:function() {
        function S4() {
            return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
        }
        return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    },full: function () {
        var index = parent.layer.getFrameIndex(window.name);
        parent.layer.full(index);
    }

};
//获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
var curWwwPath = window.document.location.href;
//获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
var pathName = window.document.location.pathname;
if("/" == pathName){
    lx.wwwroot = window.document.location.href;
}else {
    var pos = curWwwPath.indexOf(pathName);
//获取主机地址，如： http://localhost:8083
    var localhostPaht = curWwwPath.substring(0, pos);
//获取带"/"的项目名，如：/uimcardprj
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    lx.wwwroot = (localhostPaht)+"/";
}
var layui_fun={
    arrayToTree: function(treeArr, pid,func) {
        let temp = [];
        if(treeArr){
            treeArr.forEach((item, index) => {
                if (item.pid == pid) {
                    if(func){
                        func(item);
                    }
                    var ls = layui_fun.arrayToTree(treeArr, item.id,func);
                    if (ls.length > 0) {
                        item.children = ls;
                    }
                    temp.push(item);
                }
            });
        }
        return temp;
    }
}

//对表格的封装
if($("#table_temp_id").size()>0){
    var temp_fun = {
        //密码框
        password_fun:function (d) {
            return `******`;
        }
        //单选
        ,checkbox_fun:function (d) {
            return `<input type="checkbox" name="${this.field}" lay-filter="checkbox" lay-skin="switch" lay-text="${this.lay_text}" ${d[this.field] == 1 ? ' checked' : ''}>`;
        }
        //单选
        ,switch_fun:function (d) {
            return `<input type="checkbox" name="${this.field}" value="${d[this.data_id]}" lay-filter="switch_fun" lay-skin="switch" lay-text="${this.lay_text}" ${d[this.field] == this.check ? ' checked' : ''}>`;
        }
        //日期 format
        ,date_fun:function (d) {
            return ` <input type="text" name="${this.field}" format ="${this.format}"  class="layui-input layui-input-date" value="${layui.util.toDateString(d[this.field],this.format)}" >`
        }
        //下拉选 data
        ,select_fun:function (d){
            var options = "";
            for(var i in this.data){
                var v = this.data[i];
                options +=`<option value="${v.id}" ${d[this.field] == v.id ? ' selected="selected"' : ''} >${v.name}</option>`;
            }
            return '<a lay-event="type"></a><select name="'+this.field+'" lay-filter="select" lay-search><option value="">请选择</option>' + options + '</select>';
        }
        //联表操作 data
        ,link_func:function (d) {
            var k = this.data[d[this.field]];
            return k?k:'未知';
        }
        ,sex_func:function (d) {
            return d[this.field]==1?`<button class="layui-btn layui-btn-xs">${this.t1}</button>`: `<button class="layui-btn layui-btn-primary layui-btn-xs" lay-event="add_p">${this.t2}</button>`;
        }
        ,redio_func:function (d) {
            return this['t'+d[this.field]]?`<button class="layui-btn layui-btn-xs">${this['t'+d[this.field]]}</button>`:`<button class="layui-btn layui-btn-xs">未知</button>`;
        }
    }
    window.dataObj = {
        //表格
        table_temp:{
            elem:"#table_temp_id"
            ,url:''
            //,height:600
            ,parseData: function(res){ //res 即为原始返回的数据
                if (lx.login(res)) {
                    res.rows = dataObj.rows_filter(res.rows);//执行过滤
                    // var lv = $("#table_temp_search_id").val();//获取搜索框
                    // if (lv){
                    //     res.rows = res.rows.filter(function (v) {
                    //         return JSON.stringify(v).indexOf(lv) != -1;
                    //     });
                    // }
                    return res;
                }
            }
            ,response: {
                statusName: 'success' //规定数据状态的字段名称，默认：code
                ,statusCode: 1 //规定成功的状态码，默认：0
                ,msgName: 'msg' //规定状态信息的字段名称，默认：msg
                ,countName: 'count' //规定数据总数的字段名称，默认：count
                ,dataName: 'rows' //规定数据列表的字段名称，默认：data
            }
            ,toolbar:true
            ,cols:[[]]
            ,page:!0
            ,limit:10
            ,limits:[5,10,15,20,25,30,50,100,200,500,1000,10000]
            ,text:"对不起，加载出现异常！"
        }
        ,rows_filter:function (rows) { return rows; }
        ,search:function () {
            layui.table.reload('table_temp_id', {where: {'find':$("#table_temp_search_id").val()}});
        }
        ,id:'id'
        ,del:function (data) {
            layer.msg('调用删除');
        }
        ,delAll:function (checkData) {
            layer.msg('调用删除全部');
        }
        ,edit:function (data) {
            layer.msg('调用修改');
        }
        ,edit_area:['80%','90%']
        ,add_content:''
        ,add_area:['80%','90%']
        ,del_success:function () {
            layui.table.reload('table_temp_id');
            layer.msg('已执行');
        }
        ,batchdel: function(){
            var checkStatus = layui.table.checkStatus('table_temp_id')
                ,checkData = checkStatus.data; //得到选中的数据
            if(checkData.length === 0){
                return layer.msg('请选择数据');
            }
            dataObj.delAll(checkData);
        }
        ,add: function(){
            layui.layer.open({
                type: 2
                ,title: '添加'
                ,content: dataObj.add_content
                ,maxmin: true
                ,area: dataObj.add_area
                ,btn: ['确定', '取消']
                ,yes: function(index, layero){
                    //点击确认触发 iframe 内容中的按钮提交
                    var submit = layero.find('iframe').contents().find("#layuiadmin-app-form-submit");
                    submit.click();
                }
            });
        }
    }
    layui.config({
        base: '/sys/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'contlist', 'table'], function(){
        var table = layui.table,form = layui.form;

        if(dataObj.table_temp.url.indexOf("?") >= 0) {
            dataObj.table_temp.url += "&token=" + $.cookie('token');
        }else {
            dataObj.table_temp.url += "?token=" + $.cookie('token');
        }
        table.render(dataObj.table_temp);
        //监听搜索
        form.on('submit(table_temp_id)', function(data){
            if (dataObj.table_temp.page) {
                dataObj.table_temp.page = {curr: 1};
            }
            dataObj.table_temp.where = data.field;
            table.render(dataObj.table_temp);
        });
        //监听工具条 和单元格
        table.on('tool(table_temp_id)', function(obj) {
            var data = obj.data;
            if (obj.event === 'del') {
                layer.confirm('确认删除?', function (index) { dataObj.del(data); });
            } else {
                if(dataObj[obj.event+"_func"]){
                    dataObj[obj.event+"_func"](data);
                }else{
                    layer.open({
                        type: 2
                        ,title: '修改'
                        ,content: dataObj[obj.event](data)
                        ,maxmin: true
                        ,area: dataObj[obj.event+'_area']
                        ,btn: ['确定', '取消']
                        ,yes: function(index, layero){
                            //点击确认触发 iframe 内容中的按钮提交
                            var submit = layero.find('iframe').contents().find("#layuiadmin-app-form-submit");
                            submit.click();
                        }
                    });
                }
            }
        });

        $('.layui-btn.layuiadmin-btn-list').on('click', function(){
            var type = $(this).data('type');
            dataObj[type] ? dataObj[type].call(this) : '';
        });


        //监听地址选择操作
        layui.form.on('select(select)', function (data) {
            var elem = $(data.elem);
            var trElem = elem.parents('tr');
            var tableData = layui.table.cache['table_temp_id'];
            // 更新到表格的缓存数据中，才能在获得选中行等等其他的方法中得到更新之后的值
            tableData[trElem.data('index')][elem.attr('name')] = data.value;
        });

    });
}
if($('#formTest1').size()>0){//edit
    window.dataForm ={};
    dataForm.flist =[];
    $(function(){
        $.each(dataForm.flist, function (index, v) {
            if(v.hide){//隐藏
                $("#formTest1").append(`<input type='hidden' name="${v.field}" value ='${v.val}'/>`);
                return;
            }
            var html =
                `<div class="layui-form-item">
                <label class="layui-form-label">${v.verify?`<i class="layui-icon layui-icon-star" style="font-size: 1rem; color: #FF0000;"></i> `:''} ${v.title}</label>
                <div class="layui-input-block">`;
            if (v.radio_ls){//单选
                $.each(v.radio_ls, function (index,v1) {
                    html+=`<input type="radio" name="${v.field}" value="${v1.val}" title="${v1.name}" ${v.disabled?'disabled':''}  ${v.id?'id="'+ v.id+v1.val+'"':''} ${v.checkval==v1.val?'checked':''}>`;
                });
            }else if(v.select){//下拉选
                if (v.select.url){
                    lx.ajaxasync(false,v.select.url,function (data) {
                        v.select.ls = data.rows;
                    })
                }
                html+=`<select name="${v.field}" ${v.disabled?'disabled':''}  ${v.id?'id="'+ v.id+'"':''} lay-verify="${v.verify}"  lay-search>`;
                if(v.select.option){
                    html +=v.select.option;
                }else{
                    html += '<option></option>';
                }
                $.each(v.select.ls, function (index, item) {
                    html+=`<option value="${item[v.select.id]}">${item[v.select.name]}</option>`;// 下拉菜单里添加元素
                });
                html+=`</select>`;
            }else if(v.img){
                html+=`
                    <div class="layui-inline" id="imgTest${v.id}">
                        <button type="button" class="layui-btn" id="imgTestbtn${v.id}"><i class="layui-icon">&#xe67c;</i>上传图片</button>
                        <img src="" width="${v.width}px" height="${v.height}px">
                    </div>
                    `;
            }else{
                html+=`<input type="${v.type? v.type:'text'}" name="${v.field}" ${v.disabled?'disabled':''}  ${v.id?'id="'+ v.id+'"':''} lay-verify="${v.verify}" placeholder="请输入" autocomplete="off" class="layui-input ${v.cls?v.cls:''}">`;
            }
            html+=`</div></div>`;
            $("#formTest1").append(html);
        });
        layui.form.render();

        dataForm.id = lx.request(dataForm.primaryKey);
        layui.config({
            base: '/sys/layuiadmin/' //静态资源所在路径
        }).extend({
            index: 'lib/index' //主入口模块
        }).use(['index', 'form','upload'], function(){
            var $ = layui.$
                ,form = layui.form;
            //监听提交
            form.on('submit(layuiadmin-app-form-submit)', dataForm.submit_func);


            layui.use('upload', function(){
                var upload = layui.upload;
                $.each(window.dataForm.flist, function (index, v) {
                    if(v.img){
                        upload.render({
                            elem: '#imgTestbtn'+ v.id //绑定元素
                            //,size: 30 //最大允许上传的文件大小
                            ,acceptMime: 'image/*'
                            ,auto: false //选择文件后不自动上传
                            ,choose: function(obj){
                                obj.preview(function(index, file, result){
                                    //使用压缩
                                    dealImage(result, 500,10, function(base64){
                                        $("#imgTest"+ v.id+" img").attr('src',base64);
                                    });
                                });
                            }
                        });
                    }
                });
            });


        })
        if(dataForm.id){
            dataForm.enableID_func();
        }
    });
}

$(function(){
    if($(".start_end").size()>0){lay('.start_end').each(function(){layui.laydate.render({elem: this,trigger: 'click',range: true});});}
    if($(".start_end_time").size()>0){lay('.start_end_time').each(function(){layui.laydate.render({elem: this,trigger: 'click',type: 'datetime',range: true,ready: function(date){
        $(".layui-laydate-footer [lay-type='datetime'].laydate-btns-time").click();
        $(".laydate-main-list-1 .layui-laydate-content li ol li:last-child").click();
        $(".layui-laydate-footer [lay-type='date'].laydate-btns-time").click();
    }});});}
    if($(".s_data").size()>0){lay('.s_data').each(function(){layui.laydate.render({elem: this,trigger: 'click'});});}
    if($(".s_datatime").size()>0){lay('.s_datatime').each(function(){layui.laydate.render({elem: this,trigger: 'click',type: 'datetime'});});}
});

//压缩方法
function dealImage(base64, w,size, callback) {
    var newImage = new Image();
    var quality = 0.99;    //压缩系数0-1之间
    newImage.src = base64;
    newImage.setAttribute("crossOrigin", 'Anonymous');	//url为外域时需要
    var imgWidth, imgHeight;
    newImage.onload = function () {
        imgWidth = this.width;
        imgHeight = this.height;
        var canvas = document.createElement("canvas");
        var ctx = canvas.getContext("2d");
        if (Math.max(imgWidth, imgHeight) > w) {
            if (imgWidth > imgHeight) {
                canvas.width = w;
                canvas.height = w * imgHeight / imgWidth;
            } else {
                canvas.height = w;
                canvas.width = w * imgWidth / imgHeight;
            }
        } else {
            canvas.width = imgWidth;
            canvas.height = imgHeight;
            quality = 0.99;
        }
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.drawImage(this, 0, 0, canvas.width, canvas.height);
        var base64 = canvas.toDataURL("image/jpeg", quality); //压缩语句
        // 如想确保图片压缩到自己想要的尺寸,如要求在50-150kb之间，请加以下语句，quality初始值根据情况自定
         while (base64.length / 1024 > size) {
         	quality -= 0.01;
         	base64 = canvas.toDataURL("image/jpeg", quality);
         }
        callback(base64);//必须通过回调函数返回，否则无法及时拿到该值
    }
}
