ImageLoader = function(a) {
    casual.EventDispatcher.call(this);
    this.resources = a;
    this.images = [];
    this._loaded = 0
};
casual.inherit(ImageLoader, casual.EventDispatcher);
ImageLoader.load = function(a, b) { (new ImageLoader(a, b)).load()
};
ImageLoader.prototype.load = function() {
    if (this._loaded > this.resources.length - 1) this.dispatchEvent({
        type: "complete",
        target: this,
        params: this.images
    });
    else {
        var a = new Image;
        a.onload = casual.delegate(this._loadHandler, this);
        a.src = this.resources[this._loaded]
    }
};
ImageLoader.prototype._loadHandler = function(a) {
    this.images.push({
        src: this.resources[this._loaded],
        image: a.target
    });
    this.dispatchEvent({
        type: "loaded",
        target: this,
        params: a.target
    });
    this._loaded++;
    this.load()
};
ImageLoader.prototype.getLoaded = function() {
    return this.images.length
};
ImageLoader.prototype.getTotal = function() {
    return this.resources.length
};
ImageLoader.prototype.isLoaded = function() {
    return this.images.length == this.resources.length
};
ddz = {
    images: [],
    poker:[],
    canvas: null,
    conntext: null,
    stage: null,
    width: 0,
    height: 0,
    rem:0,
    splash: null,
    playingTween:true
};
ddz.startup = function(w,h,func_init) {
    this.func_init = func_init;
    this.width = w;
    this.rem=w/100;
    this.height = h;
    var a = ["images/bg.png", "images/poker.png", "images/button.png", "images/portrait.png", "images/number.png", "images/logo.png", "images/win.png", "images/lose.png", "images/hand1.png", "images/hand2.png"];
    var b = new ImageLoader(a);
    b.addEventListener("complete", ddz.onComplete);//加载完成
    b.load();//开始加载图片
};
ddz.onComplete = function(a) {
    a.target.removeEventListener("complete", ddz.onComplete);
    ddz.images = a.params;
    ddz.initGame();//开始加载
};
ddz.initGame = function() {
    if (navigator.userAgent.match(/ipad/i)) this.playingTween = false;
    this.canvas = document.getElementById("canvas");
    this.canvas.style.backgroundImage = "url(" + this.images[0].src + ")";
    this.canvas.style.backgroundPosition = "center center";
    this.canvas.oncontextmenu = function() {return false};
    this.context = this.canvas.getContext("2d");
    this.context.fillRect(0, 0, this.width, this.height);
    this.canvas.width = this.width;
    this.canvas.height = this.height;
    this.stage = new Stage(this.context);
    this.showSplash()
    this.func_init();//调用初始化完成方法
};
function sleep(millisecond) {
    return new Promise(resolve => {
        setTimeout(() => {
            resolve()
        }, millisecond)
    })
}
ddz.showSplash = function() {
    this.stage.setFrameRate(0);
    if (this.splash) {
        this.stage.addChild(this.splash);
        this.stage.render()
    } else {
        this.splash = new Sprite;
        this.stage.addChild(this.splash);
        this.xfsplash = new Sprite;
        this.stage.addChild(this.xfsplash);
        // var ls = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,[26,0,1,1],[52,0,1,0],[53,1]]
        // var  ls = [0,[1,1,0],[2,0,0],[3,1,1],[4,0,0]];
        // ddz.setDX(ls,0);
        //     j=[60 ,this.height ,this.width-25 ,this.height ,34,40],p=[90,110,1];

                // for (var i in json.msg[27]){
        //     var v = json.msg[27][i];
        //     if (v.user == userId){
        //         this.wz = Number(i);//获取自己的位置
        //         this.room = v.room;//房间号
        //         break;
        //     }
        // }
        // for (var i in json.msg[27]){
        //     var v = json.msg[27][i];
        //     var numI = Number(i);
        //     var z = (numI+this.wz)%room;
        //     $("#user"+z).html(v.username.substring(0,2));
        //     $("#user_"+z).html(json.msg[20+numI]);
        //     if (numI == json.msg[26]){//当前操作者
        //         $("#user"+z).addClass("layui-btn-danger");
        //     }
        // }


        // var json  = [
        //     [[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E'],[16,'F'],[15,'E']]
        //     ,[[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E'],[16,'F'],[15,'E']]
        //     ,[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18]
        //     ,[0,1,2,3,4,5,6,7,8]
        // ]
        // this.set(ls,[10 ,this.height-50 ,this.width-35 ,this.height ,30,50],[70,120,1],this.xfsplash,500);
        // this.set(json[3],[10,this.height/2 ,this.width-30 ,0 ,17,30],[40,60,0],this.splash)
        // this.set(json[3],[10,this.height/2-100 ,this.width/2-10 ,0 ,17,30],[40,60,0],this.splash)
        // this.set(json[3],[10,this.height/2-200 ,this.width-30 ,0 ,17,30],[40,60,0],this.splash)
        // this.set(json[3],[this.width/2+10 ,this.height/2-100 ,this.width-10 ,0 ,17,30],[40,60,0],this.splash)
    }
};
ddz.initInfo = function(cp){//初始化牌
    for (var i in cp[27]){//获取自己的位置和房间号
        var v = cp[27][i];
        if (v.user == userId){
            this.room = cp[28];//房间人数
            this.roomID = cp[29];//房间id
            this.wz = Number(i);//获取自己的位置
            break;
        }
    }
    for (var i in cp[27]){//显示其他人的信息
        var v = cp[27][i];
        var numI = Number(i);
        var z = (numI+this.room-this.wz)%this.room;
        $("#user"+this.room+z).html(v.username.substring(0,2));//显示名字
    }
}
//更新其他人出的牌 所有人积分积分
ddz.updateInfo = function (cp){
    for (var i in cp[27]){//显示其他人的 积分，状态
        var v = cp[27][i];
        var numI = Number(i);//当前几号信息
        var z = (numI+this.room-this.wz)%this.room;//自己位置
        $("#user_"+this.room+z).html(cp[20+numI]);//积分
        $("#user"+this.room+z).removeClass("layui-btn-warm");
        $("#user"+this.room+z).removeClass("layui-btn-danger");
        if (numI == cp[26]){//当前操作者
            $("#user"+this.room+z).addClass("layui-btn-danger");
        }else if (numI == cp[7]){//叫主
            $("#user"+this.room+z).addClass("layui-btn-warm");
        }
    }
    // this.splash.removeAllChildren();//清楚几组出牌
    if (this.room == 4){
        this.set(cp[10+this.wz]      ,[10             ,this.height/2      ,this.width-30   ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+1)%4],[this.width/2+10,this.height/2-100  ,this.width-30   ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+2)%4],[10             ,this.height/2-200  ,this.width-30   ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+3)%4],[10             ,this.height/2-100  ,this.width/2-10 ,0 ,17,30],[40,60,0],this.splash)
    }else{//5人间
        this.set(cp[10+this.wz]      ,[10             ,this.height/2      ,this.width-30   ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+1)%5],[this.width/2+10,this.height/2-100  ,this.width-30   ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+2)%5],[this.width/2+10,this.height/2-200  ,this.width-30   ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+3)%5],[10             ,this.height/2-200  ,this.width/2-10 ,0 ,17,30],[40,60,0],this.splash)
        this.set(cp[10+(this.wz+4)%5],[10             ,this.height/2-100  ,this.width/2-10 ,0 ,17,30],[40,60,0],this.splash)
    }

}
function getDX(a,zhu){
    if (a.length>1) a = a[0];
    var t = Math.floor(a/13);//类型
    if (t == 4) a+=500 //王
    else if(t == zhu) a+=100;//主
    if (a%13==5) a+=200;//7
    return a;
}
ddz.setDX = function(ls,zhu){
    ddz.xfsplash.removeAllChildren();
    ls.sort(function (a,b) {
       return getDX(b,zhu)-getDX(a,zhu);
    });
    ddz.xf=ddz.set(ls,[10 ,ddz.height-50 ,ddz.width-35 ,ddz.height ,30,50],[70,120,1],ddz.xfsplash);//显示自己的牌
}
ddz.set = function(ls,j,p,splash){//数组,
    if (!ls || ls.length == 0) return;
    //[开始x,开始y,结束x,结束y,x间隔,y间隔],[牌高,宽,是否点击]
    var start = j[2]-j[0]>ls.length*j[4]?j[0]+(j[2]-j[0]-(ls.length-1)*j[4]-p[0])/2+5:j[0];
    var lie = Math.floor((j[2]-j[0])/j[4]);//获取横排放多少张
    var row = Math.floor(ls.length/lie)-(ls.length%lie==0?1:0);//放几排
    var list = [];
    for (var i in ls){
        var a = ls[i];//a[0] 牌 a[1]默认选中 1选中 a[2] 不能点击 0能点
        var d = getPoker(a.length>1?a[0]:a,start+(i%lie)*j[4], j[1]-p[1]-((row-Math.floor(i/lie))*j[5]) ,p[0],p[1],p[2] && !a[2]);
        if (a[1]){//选中
            d.y-=15;
            d.selected = true;
        }
        splash.addChild(d);
        this.stage.render();
        list.push(d);
    }
    return list;
}
ddz.asyncSet =async function(ls,j,p,splash,time,func){
    if (!ls || ls.length == 0) return;
    //[开始x,开始y,结束x,结束y,x间隔,y间隔],[牌高,宽,是否点击]
    var start = j[2]-j[0]>ls.length*j[4]?j[0]+(j[2]-j[0]-(ls.length-1)*j[4]-p[0])/2+5:j[0];
    var lie = Math.floor((j[2]-j[0])/j[4]);//获取横排放多少张
    var row = Math.floor(ls.length/lie)-(ls.length%lie==0?1:0);//放几排
    for (var i in ls){
        var a = ls[i];//a[0] 牌 a[1]默认选中 1选中 a[2] 不能点击 0能点
        var d = getPoker(a.length>1?a[0]:a,start+(i%lie)*j[4], j[1]-p[1]-((row-Math.floor(i/lie))*j[5]) ,p[0],p[1],p[2] && !a[2]);
        if (a[1]){//选中
            d.y-=15;
            d.selected = true;
        }
        if (time){//等待
            this.typeNum[Math.floor(a/13)]+=1;
            if (a%13==5){
                $("#btn"+Math.floor(a/13)).removeAttr("disabled");//可以点击按钮
                $("#btn"+Math.floor(a/13)).removeClass("layui-bg-gray");//改变颜色
            }
            $("#btn_0").html(this.typeNum[0]);
            $("#btn_1").html(this.typeNum[1]);
            $("#btn_2").html(this.typeNum[2]);
            $("#btn_3").html(this.typeNum[3]);

            await sleep(time);
        }
        splash.addChild(d);
        this.stage.render();
    }
    if (time){
        func();//执行排序
    }
}

MyImg = function(img,frame,x,y,w,h,onMouseEvent){
    Sprite.call(this);
    this.x = x || 0;
    this.y = y || 0;
    var b = new Bitmap(img,frame);
    b.width = this.width = w||b.width ;
    b.height = this.height = h||b.height;
    this.addChild(b);//扑克牌
    this.onMouseEvent = onMouseEvent;
}
casual.inherit(MyImg, Sprite);
MyImg.prototype.addImg = function(img,frame,x,y,w,h){
    var m;
    if (x=='auto') m = new MyImg(img,frame,(this.width-w)/2,(this.height-h)/2,w,h);
    else m = new MyImg(img,frame,x,y,w,h);
    m.onMouseEvent = this.onMouseEvent;
    this.addChild(m);
}
MyImg.prototype.hide=function(){
    if (this.hideBoo) return;
    this.hideBoo = true;
    this.oldx = this.x,this.oldy = this.y;
    this.x = ddz.width;
    this.y = ddz.height;
    this.getStage().render()
}
MyImg.prototype.show=function(){
    this.hideBoo = false;
    this.x = this.oldx,this.y = this.oldy;
    this.getStage().render()
}
MyImg.prototype.remove= function(a){
    a.removeChild(this);
}
MyImg.prototype.to = function(a,time){
    if (time||time>50){//有时间
        var i = time/50;
        this.t = true;//正在执行
        this.sx=(a.x-this.x)/i||0;
        this.sy=(a.y-this.y)/i||0;
        this.sr=(a.rotation-this.rotation)/i||0;
        var self = this;
        var int=window.setInterval(function () {
            if ( i--<=0){
                window.clearInterval(int);
                self.t = false;
            }
            self.x+=self.sx;
            self.y+=self.sy;
            self.rotation+=self.sr;
            self.getStage().render();
        },50);
    }else{
        for (var k in a){
            this[k]=a[k];
        }
        this.getStage().render();
    }
}

function getPoker(num,x,y,w,h,mouseEnabled,BM){
    // if (BM){//背面
    //     return new MyImg(ddz.images[1].image,poker[26].frame,x,y,w,h);
    // }
    var m = new MyImg(ddz.images[1].image,poker[25].frame,x,y,w,h,!mouseEnabled?'':function (a) {
        if (a.type == "mousedown") {
            m.selected = !m.selected;
            m.y += m.selected ? -15 : 15
            m.getStage().render();
            window.paimousedown();//牌被点击了
        }
    });

    m.num = num; m.type = Math.floor(num/13);//0 方块 1 梅花 2红桃 3 黑桃 4王
    var f = poker[m.num%13].frame;
    if (m.type == 0 || m.type == 2) {
        f= f.slice(0);
        f[0] -= 1;
        f[1] = 489
    }else if (m.type == 4){
        f = poker[13+m.num%13].frame;
    }
    if (m.type == 4){//王
        m.addImg(ddz.images[1].image,f,5,2,w/4,h*0.7);
    } else{
        m.addImg(ddz.images[1].image,f,5,2,w/4,h/4);
    }
    m.addImg(ddz.images[1].image,poker[20+m.type].frame,5,h/4+3,w/4,w/4);
    return m;
}

var poker = {
    0:{name :"2",frame: [12, 465, 14, 21], offsetX: 0},
    1:{name :"3",frame: [195, 465, 14, 21], offsetX: 0},
    2:{name :"4",frame: [178, 465, 14, 21], offsetX: 0},
    3:{name :"5",frame: [162, 465, 14, 21], offsetX: 0},
    4:{name :"6",frame: [146, 465, 14, 21], offsetX: 0},
    5:{name: "7",frame: [228, 465, 14, 21], offsetX: 0},
    6:{name :"8",frame: [211, 465, 14, 21], offsetX: 0},
    7:{name :"9",frame: [129, 465, 14, 21], offsetX: 0},
    8:{name :"10",frame: [107, 465, 21, 21], offsetX: -1},
    9:{name: "J",frame: [89, 465, 14, 21], offsetX: 0},
    10:{name: "Q",frame: [68, 465, 18, 21], offsetX: -1},
    11:{name: "K",frame: [49, 465, 17, 21], offsetX: 0},
    12:{name: "A",frame: [29, 465, 17, 21], offsetX: -1},

    13:{name: "小王", frame: [288, 73, 15, 100], offsetX: 0},
    14:{name: "大王", frame: [270, 73, 15, 100], offsetX: 0},
    20:{name: "方块", frame: [247, 179, 20, 24]},
    21:{name: "梅花", frame: [270, 179, 20, 24]},
    22:{name: "红桃", frame: [294, 179, 20, 24]},
    23:{name: "黑桃", frame: [318, 179, 20, 24]},
    24:{name: "王", frame: [0,0,0,0]},
    25:{name: "正面",frame: [156, 12, 105, 145]},
    26:{name: "背面",frame: [22, 10, 105, 145]},
}
