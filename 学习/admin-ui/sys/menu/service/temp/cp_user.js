if (window.location.href.indexOf("/list.html")!=-1){
    /*list table标题*/
    list_content = `
        <div class="mt-20">
            <table class="table table-border table-bordered table-hover table-bg table-sort">
                <thead id = "thead">
                    <tr class="text-c">
                        <th width="25"><input type="checkbox" name="" value=""></th>
                        <th width="100">用户名</th>
                        <th width="40">密码</th>
                        <th width="40">医院ID</th>
                        <th width="40">医院名称</th>
                        <th width="70">状态</th>
                        <th width="100">操作</th>
                    </tr>
                </thead>
                <tbody id="tbody">
    
                </tbody>
            </table>
        </div>
    `;
    /*list js*/
    function get_tr(v){
        return `<tr class="text-c">
				<td><input type="checkbox" value="${v.id}" name="test"></td>
				<td>${v.name}</td>
				<td>${v.password}</td>
				<td>${v.hid}</td>
				<td>${v.h_name}</td>
				<td class="td-status"><span class="label ${v.status==0?'label-warning':'label-success'}  radius">${v.status==0?'已停用':'已启用'}</span></td>
				<td class="td-manage">
				    <a style="text-decoration:none" href="javascript:;" onClick="${v.status==0?'member_start':'member_stop'}(this,'${v.id}')"  title="${v.status==0?'启用':'停用'}"><i class="Hui-iconfont">${v.status==0?'&#xe615;':'&#xe631;'}</i></a>
				    <a title="编辑" href="javascript:;" onclick="member_edit('编辑','${v.id}','','510')" class="ml-5" style="text-decoration:none"><i class="Hui-iconfont">&#xe6df;</i></a>
				    <a title="删除" href="javascript:;" onclick="member_del(this,'${v.id}')" class="ml-5" style="text-decoration:none"><i class="Hui-iconfont">&#xe6e2;</i></a>
				</td>
			</tr>
        `;
    };
}



/*edit js*/
else if (window.location.href.indexOf("/edit.html")!=-1){
    /*edit 表单*/
    var edit_form = `
    <form action="" method="post" class="form form-horizontal" id="form-member-add">
		<div class="row cl">
			<label class="form-label col-xs-4 col-sm-3"><span class="c-red">*</span>用户名：</label>
			<div class="formControls col-xs-8 col-sm-9">
				<input type="text" class="input-text" value="" placeholder="" id="id" name="id">
			</div>
		</div>
		<div class="row cl">
			<label class="form-label col-xs-4 col-sm-3"><span class="c-red">*</span>医院名称：</label>
			<div class="formControls col-xs-8 col-sm-9"> <span class="select-box">
				<select name="dept" class="select" id="depts"></select>
				</span> </div>
		</div>
		<div class="row cl">
			<label class="form-label col-xs-4 col-sm-3"><span class="c-red">*</span>密码：</label>
			<div class="formControls col-xs-8 col-sm-9">
				<input type="password" class="input-text" value="" placeholder="" id="password" name="password">
			</div>
		</div>
		<div class="row cl">
			<div class="col-xs-8 col-sm-9 col-xs-offset-4 col-sm-offset-3">
				<input class="btn btn-primary radius" type="submit" value="&nbsp;&nbsp;提交&nbsp;&nbsp;">
			</div>
		</div>
        <input type='hidden' name="status" id='status' value ='1'/>
	</form>
    `;
    var user = "";
    function edit_back(data){
        $("#name").val(data.name);
        $("#password").val(data.password);
        $("#h_name").val(data.h_name);
        $("#status").val(data.status);
        user = data;
    }

    var edit_rules = {
        id:{
            required:true,
            minlength:2,
            maxlength:16
        },
        sex:{
            required:true,
        },
        password:{
            required:true,
        },
    };
    $(function () {
        lx.ajax('sys/list/dept?',function (data) {
            for (var i in data) {
                var m = data[i];
                if (i == 0 ||m.id == user.dept){
                    $("#deptName").val(m.name);
                }
                $("#depts").append(`<option value="${m.id}" ${m.id == user.dept?'selected':''}>${m.name}</option>`);
            }
        });

        $('#depts').change(function() {
            $("#deptName").val( $(this).children('option:selected').html());
        });

        lx.ajax('sys/list/role?',function (data) {
            for (var i in data) {
                var m = data[i];
                if (i == 0 ||m.id == user.role){
                    $("#roleName").val(m.name);
                }
                $("#roles").append(`<option value="${m.id}" ${m.id == user.role?'selected':''}>${m.name}</option>`);
            }
        });

        $('#roles').change(function() {
            $("#roleName").val( $(this).children('option:selected').html());
        });
    });
}
