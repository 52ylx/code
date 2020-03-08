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
ddz.startup = function(w,h) {
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
};
ddz.showSplash = function() {
    this.stage.setFrameRate(0);
    if (this.splash) {
        this.stage.addChild(this.splash);
        this.stage.render()
    } else {
        this.splash = new Sprite;
        this.stage.addChild(this.splash);
        ddz.webSocket();
        // this.logo = new MyImg(this.images[5].image,null,(this.width-this.images[5].image.width)/2 ,20 ,100,100);
        // this.splash.addChild(this.logo);

        // var p = getPoker(2,'A',0,0,100,175,1);
        // this.splash.addChild(p);
        // var c = getPoker(5,'B',40,0,100,175,1);
        // this.splash.addChild(c);
        //[185, 34, 50, 28] //开始

        // var i = new MyImg(this.images[2].image,[15, 78, 98, 58],200,100,100,100,function(a) {
        //     if (a.type == "mousedown") {
        //         // TweenUtil.to(p, 500, {y: 300, x: 500});
        //         // ddz.splash.removeChild(ddz.i);
        //         p.mouseEnabled = true;
        //         p.rotation = 30;
        //         p.show();
        //     }
        // });
        // i.addImg(this.images[2].image,[185, 34, 50, 28],'auto','auto',25,25)
        // this.splash.addChild(i);
        // i.to({x:400,y:0,rotation:0},4000)
        // this.rePoker({xf:[[16,'F'],[15,'E'],[3,'B'],[2,'C'],[2,'A'],[2,'A'],[3,'B'],[2,'C'],[2,'A'],[2,'A'],[3,'B'],[2,'C'],[2,'A'],[2,'A'],[3,'B'],[2,'C']
        //     ,[2,'A'],[3,'B'],[2,'C'],[2,'A'],[2,'A'],[3,'B'],[2,'C'],[16,'F'],[15,'E']]
        //     ,yf:[[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E']]
        //     ,sf:[[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E']]
        //     ,zf:[[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E']]
            // ,bf:[[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E']]
        // });
        // this.rePoker({an:[[16,'F'],[15,'E'],[3,'B'],[2,'A'],[16,'F'],[15,'E']]})
        // this.anniu();
        // var i = TP.getNum("12312543",100,20,15,15,1);
        // this.splash.addChild(i);
        // var i = TP.getPlayer("DZ",100,120,50,50);
        // this.splash.addChild(i);
    }
};
ddz.set = function(ls,j,p){//数组,
    if (!ls || ls.length == 0) return;
    //[开始x,开始y,结束x,结束y,x间隔,y间隔],[牌高,宽,是否点击]
    var start = j[2]-j[0]>ls.length*j[4]?j[0]+(j[2]-j[0]-(ls.length-1)*j[4]-p[0])/2:j[0];
    var lie = Math.floor((j[2]-j[0])/j[4]);//获取横排放多少张
    var row = ls.length==lie?0:Math.floor(ls.length/lie);//放几排
    var list = [];
    for (var i in ls){
        var a = ls[i];
        var d = getPoker(a[0],a[1],start+(i%lie)*j[4], j[1]-p[1]-((row-Math.floor(i/lie))*j[5]) ,p[0],p[1],p[2],a[2]);
        this.splash.addChild(d);
        list.push(d);
    }
    return list;
}
ddz.anniu=function(){
    var i = new MyImg(this.images[2].image,[15, 78, 98, 58],this.width/2+20,this.height-220,80,60,function(a) {
        if (a.type == "mousedown") {
            // i.hide();
            var ls = [];
            for (var i in ddz.xf){
                if (ddz.xf[i].selected){
                    ls.push({num:ddz.xf[i].num,type:ddz.xf[i].type});
                }
            }
            console.log(ls)
            if (ddz.state == 2){//叫
                if (ls.length == 1 && ls[0].num==14){
                    ddz.socket.send(JSON.stringify({ls:ls}));
                }
            }else if (ddz.state == 3){//扣牌
                if (ls.length == 8){//反牌
                    ddz.socket.send(JSON.stringify({ls:ls}));
                }
            }else if (ddz.state == 4){//反牌
                if (ls.length == 2 && ls[0].type == ls[1].type && ls[0].num == ls[1].num && ls[0].num>=14){//反牌类型一样 大于等于 7
                    ddz.socket.send(JSON.stringify({ls:ls}));
                }
            }else if (ddz.state == 5){//开始出牌
                if (ls.length>0){
                    ddz.socket.send(JSON.stringify({ls:ls}));
                }
            }

        }
    });
    i.addImg(this.images[2].image,[185, 34, 50, 28],20,10,40,33)//开始
    this.splash.addChild(i);

    if (ddz.state == 4){//是否反地主
        var i = new MyImg(this.images[2].image,[15, 78, 98, 58],this.width/2+120,this.height-220,80,60,function(a) {
            if (a.type == "mousedown") {
                ddz.socket.send(JSON.stringify({ls:[]}));//不反
            }
        });
        i.addImg(this.images[2].image,[185, 34, 50, 28],20,10,40,33)//开始
        this.splash.addChild(i);
    }
}
ddz.rePoker = function(json) {
    this.splash.removeAllChildren();
    //正下方
    this.xf =this.set(json.xf,[80 ,this.height ,this.width-25 ,this.height ,36,40],[60,80,1]);
    this.set(json.bf,[this.width/2-150 ,this.height-150 ,(this.width/2)+150 ,this.height-200 ,25,40],[40,60,0]);
    this.set(json.yf,[(this.width/2)+170 ,this.height-200 ,this.width-30 ,this.height-200 ,25,40],[40,60,0])
    this.set(json.sf,[150 ,30+70 ,(this.width/2)+150 ,30+70 ,25,40],[40,60,0]);
    this.set(json.zf,[25 ,this.height-200 ,this.width/2-200 ,this.height-200 ,25,40],[40,60,0]);
    this.set(json.dp,[0,this.height/2-30 ,this.width ,this.height/2-30 ,25,40],[40,60,0])
    this.state = json.state;
    if (this.state && json.an){//显示按钮
        this.anniu();
    }
    if (json.zhu[0]){//显示主
        var i = getPoker(14,json.zhu[0],10, 10 ,50,70);//反牌
        if (json.zhu[1]){//地主叫 的
            i.addImg(ddz.images[1].image,poker[json.zhu[1]].frame,25,5)
        }
        this.splash.addChild(i);
        var i = TP.getNum(json.zhu[2],20,55,25,15);//倍数
        this.splash.addChild(i);
    }
    if (json.cp14){
        var ls = json.cp14;
        var zh = json.cp10;
        // var i = TP.getNum(0,100,20,15,15,1);
        var i = TP.getNum(zh[0][3],10,this.height-80,15,15,1);
        this.splash.addChild(i);
        var i = TP.getPlayer(zh[0][1]?"DZ":"GG",10,this.height-60,50,50);
        this.splash.addChild(i);
        if (ls[1][0]){
            var i = TP.getNum(zh[1][3],this.width-60,this.height/2-80,15,15,1);
            this.splash.addChild(i);
            var i = TP.getPlayer(zh[1][1]?"DZ":"GG",this.width-60,this.height/2-60,50,50);
            this.splash.addChild(i);
        }
        if (ls[2][0]){
            var i = TP.getNum(zh[2][3],100,10,15,15,1);
            this.splash.addChild(i);
            var i = TP.getPlayer(zh[2][1]?"DZ":"GG",100,30,50,50);
            this.splash.addChild(i);
        }
        if (ls[3][0]){
            var i = TP.getNum(zh[3][3],10,this.height/2-80,15,15,1);
            this.splash.addChild(i);
            var i = TP.getPlayer(zh[3][1]?"DZ":"GG",10,this.height/2-60,50,50);
            this.splash.addChild(i);
        }
        // if (ls[0][0]){//自己
        //     var i = TP.getNum("12312543",100,20,15,15,1);
        //     this.splash.addChild(i);
        //     var i = TP.getPlayer(ls[0][0]?"DZ":"GG",10,120,50,50);
        //     this.splash.addChild(i);
        // }
    }

    ddz.stage.render()
}

function changeSize(img,w){
    img.height = img.height*w/img.width;
    img.width = w;
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
    if (time||time>10){//有时间
        var i = time/10;
        this.t = true;//正在执行
        this.sx=(a.x-this.x)/i;
        this.sy=(a.y-this.y)/i;
        this.sr=(a.rotation-this.rotation)/i;
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
        },10);
    }else{
        for (var k in a){
            this[k]=a[k];
        }
        this.getStage().render();
    }
}

function getPoker(num,type,x,y,w,h,mouseEnabled,BM){
    if (BM){//背面
        return new MyImg(ddz.images[1].image,poker['H'].frame,x,y,w,h);
    }
    var m = new MyImg(ddz.images[1].image,poker['G'].frame,x,y,w,h,!mouseEnabled?'':function (a) {
        if (a.type == "mousedown") {
            m.selected = !m.selected;
            m.y += m.selected ? -15 : 15
            m.getStage().render()
        }
    });
    if (type == 'E'){
        num = 15;
    }else if (type == 'F'){
        num = 16;
    }

    m.num = num , m.type = type;
    var f = poker[num].frame;
    if (type == 'A' || type == 'C') {
        f= f.slice(0);
        f[0] -= 1;
        f[1] = 489
    }
    if (type == 'E' || type == 'F'){
        m.addImg(ddz.images[1].image,f,8,2,0,h-30);
    } else{
        m.addImg(ddz.images[1].image,f,8,2);
    }
    m.addImg(ddz.images[1].image,poker[type].frame,8,22);
    return m;
}

var poker = {
    2:{frame: [12, 465, 14, 21], offsetX: 0},
    3:{frame: [195, 465, 14, 21], offsetX: 0},
    4:{frame: [178, 465, 14, 21], offsetX: 0},
    5:{frame: [162, 465, 14, 21], offsetX: 0},
    6:{frame: [146, 465, 14, 21], offsetX: 0},
    7:{frame: [211, 465, 14, 21], offsetX: 0},
    8:{frame: [129, 465, 14, 21], offsetX: 0},
    9:{frame: [107, 465, 21, 21], offsetX: -1},
    10:{name: "J",frame: [89, 465, 14, 21], offsetX: 0},
    11:{name: "Q",frame: [68, 465, 18, 21], offsetX: -1},
    12:{name: "K",frame: [49, 465, 17, 21], offsetX: 0},
    13:{name: "A",frame: [29, 465, 17, 21], offsetX: -1},
    14:{name: "7",frame: [228, 465, 14, 21], offsetX: 0},
    15:{name: "小王", frame: [288, 73, 15, 100], offsetX: 0},
    16:{name: "大王", frame: [270, 73, 15, 100], offsetX: 0},
    A:{name: "方块", frame: [247, 179, 20, 24]},
    B:{name: "梅花", frame: [270, 179, 20, 24]},
    C:{name: "红桃", frame: [294, 179, 20, 24]},
    D:{name: "黑桃", frame: [318, 179, 20, 24]},
    E:{name: "小王",frame: [0, 0, 0, 0]},
    F:{name: "大王",frame: [0, 0, 0, 0]},
    G:{name: "正面",frame: [156, 12, 105, 145]},
    H:{name: "背面",frame: [22, 10, 105, 145]},
}
var TP ={
    0 :[0, 0, 19, 22],
    1:[19, 0, 13, 22],
    2:[32, 0, 15, 22],
    3:[47, 0, 15, 22],
    4:[62, 0, 15, 22],
    5:[77, 0, 15, 22],
    6:[92, 0, 16, 22],
    7:[108, 0, 16, 22],
    8:[124, 0, 15, 22],
    9:[139, 0, 16, 22],

    w0:[5, 25, 13, 20],
    w1:[18, 25, 11, 20],
    w2:[29, 25, 11, 20],
    w3:[40, 25, 11, 20],
    w4:[53, 25, 12, 20],
    w5:[64, 25, 11, 20],
    w6:[76, 25, 12, 20],
    w7:[88, 25, 12, 20],
    w8:[100, 25, 12, 20],
    w9:[112, 25, 12, 20],

    DZ:[216, 0, 87, 87],
    GG:[126, 0, 87, 87],
    MM:[305, 0, 87, 87],
}
TP.getNum = function(num,x,y,w,h,b){
    var s = num+'';
    var img = new MyImg(ddz.images[4].image,[0,0,0,0],x,y,s.length*w,h);
    for (var i=0;i<s.length;i++){
        img.addImg(ddz.images[4].image,this[(b?'w':'')+s[i]],((w)*i),0,w,h)
    }
    return img;
}
TP.getPlayer = function(s,x,y,w,h){
    return new MyImg(ddz.images[3].image,this[s],x,y,w,h);
}


ddz.webSocket= function () {

var userId=localStorage.getItem("d7_id");
userId = userId&&userId!="undefined"?userId:0;
// var userId = 0;
var socket = null;  // 判断当前浏览器是否支持WebSocket
if ('WebSocket' in window) {
    // socket = new WebSocket("ws://192.168.1.9:8080/LLWS/"+12);
    // socket = new WebSocket("ws://192.168.1.9:8080/LLWS/"+2);
    // socket = new WebSocket("ws://192.168.1.9:8080/LLWS/"+3);
    socket = new WebSocket("ws://192.168.1.9:8080/LLWS/"+userId);
    this.socket = socket;
} else {
    alert('该浏览器不支持本系统即时通讯功能，推荐使用谷歌或火狐浏览器！');
}

// 连接发生错误的回调方法
socket.onerror = function() {
    console.log("llws连接失败!");
};
// 连接成功建立的回调方法
socket.onopen = function(event) {
    console.log("llws连接成功!");
}

// 接收到消息的回调方法
socket.onmessage = function(res) {
    console.log("llws收到消息啦:" +res.data)
    res = eval("("+res.data+")");
    if (res.userId){
        userId = res.userId;
        localStorage.setItem("d7_id",userId);
    }else{
        ddz.rePoker(res);
    }
}
// 连接关闭的回调方法
socket.onclose = function() {
    console.log("llws关闭连接!");
    var idSetInterval = setInterval(function () {
        try {
            socket = new WebSocket("ws://192.168.1.9:8080/LLWS/"+userId);
            window.clearInterval(idSetInterval);
            ddz.socket = socket;
        }catch (e) {
            console.log("连接失败!"+e);
        }
    },3000);
}
// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function() {
    socket.close();
}


function request(variable){
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
}

}

