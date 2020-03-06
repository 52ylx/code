SlicedBitmap = function(a) {
    casual.DisplayObject.call(this);
    this.name = NameUtil.createUniqueName("SlicedBitmap");
    this.image = a;
    this.slices = [];
    this.pos = []
};
casual.inherit(SlicedBitmap, casual.DisplayObject);
SlicedBitmap.prototype.addSlice = function(a, b, c) {
    this.pos.push([b || 0, c || 0]);
    this.slices.push(a)
};
SlicedBitmap.prototype.clear = function() {
    this.slices.length = 0;
    this.pos.length = 0
};
SlicedBitmap.prototype.render = function(a) {
    for (var b = 0; b < this.slices.length; b++) {
        var c = this.slices[b];
        a.drawImage(this.image, c[0], c[1], c[2], c[3], this.pos[b][0], this.pos[b][1], c[2], c[3])
    }
};
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
UI = {
    images: null
};
UI.initNumber = function(a) {
    UI.number = a;
    UI.gold = {};
    UI.gold[0] = [0, 0, 19, 22];
    UI.gold[1] = [19, 0, 13, 22];
    UI.gold[2] = [32, 0, 15, 22];
    UI.gold[3] = [47, 0, 15, 22];
    UI.gold[4] = [62, 0, 15, 22];
    UI.gold[5] = [77, 0, 15, 22];
    UI.gold[6] = [92, 0, 16, 22];
    UI.gold[7] = [108, 0, 16, 22];
    UI.gold[8] = [124, 0, 15, 22];
    UI.gold[9] = [139, 0, 16, 22];
    UI.white = {};
    UI.white[0] = [5, 25, 13, 20];
    UI.white[1] = [18, 25, 11, 20];
    UI.white[2] = [29, 25, 11, 20];
    UI.white[3] = [40, 25, 11, 20];
    UI.white[4] = [53, 25, 12, 20];
    UI.white[5] = [64, 25, 11, 20];
    UI.white[6] = [76, 25, 12, 20];
    UI.white[7] = [88, 25, 12, 20];
    UI.white[8] = [100, 25, 12, 20];
    UI.white[9] = [112, 25, 12, 20]
};
UI.setNumber = function(a, b, c, d) {
    a.clear();
    c = c ? "": "0";
    b = b >= 10 ? b.toString() : c + b.toString();
    for (var e = c = 0; c < b.length; c++) {
        var f = d ? UI.white[b[c]] : UI.gold[b[c]];
        a.addSlice(f, e, 0);
        e += f[2] + 2
    }
    return a
};
UI.setBaseScoreAndRate = function(a, b, c) {
    if (UI.scoreText.visible = UI.baseScore.visible = UI.scoreRate.visible = c) {
        a && UI.setNumber(UI.baseScore, a, true, true);
        b && UI.setNumber(UI.scoreRate, b, true, true)
    }
};
UI.showBubble = function(a, b, c, d, e, f) {
    if (!UI.bubble) {
        UI.bubble = new Sprite;
        var g = new Bitmap(UI.images[2].image, [475, 250, 100, 75]);
        g.regX = 50;
        UI.bubble.addChild(g)
    }
    if (a) {
        for (UI.bubble.getChildAt(0).scaleX = c ? -1 : 1; UI.bubble.getNumChildren() > 1;) UI.bubble.removeChildAt(1);
        if (b == "noPlay") {
            a = new Bitmap(UI.images[2].image, [244, 5, 50, 26]);
            a.x = -25;
            a.y = 25;
            UI.bubble.addChild(a)
        } else if (b == "gamble0") {
            a = new Bitmap(UI.images[2].image, [121, 5, 50, 26]);
            a.x = -25;
            a.y = 25;
            UI.bubble.addChild(a)
        } else if (b == "gamble1") {
            a = new SlicedBitmap(UI.images[2].image);
            a.addSlice([185, 61, 16, 25]);
            a.addSlice([211, 92, 28, 28], 18);
            a.x = -23;
            a.y = 22;
            UI.bubble.addChild(a)
        } else if (b == "gamble2") {
            a = new SlicedBitmap(UI.images[2].image);
            a.addSlice([202, 61, 16, 25]);
            a.addSlice([211, 92, 28, 28], 18);
            a.x = -23;
            a.y = 22;
            UI.bubble.addChild(a)
        } else if (b == "gamble3") {
            a = new SlicedBitmap(UI.images[2].image);
            a.addSlice([219, 61, 16, 25]);
            a.addSlice([211, 92, 28, 28], 18);
            a.x = -23;
            a.y = 22;
            UI.bubble.addChild(a)
        }
        UI.bubble.x = e;
        UI.bubble.y = f;
        d.addChild(UI.bubble)
    } else UI.bubble.parent && UI.bubble.parent.removeChild(UI.bubble)
};
UI.initToolBar = function(a) {
    var b = new Sprite,
        c = [13, 320, 316, 120],
        d = new Bitmap(a, c);
    c = new Bitmap(a, c);
    d.scaleX = 1;
    c.scaleX = -1;
    c.x = c.width * 2;
    b.addChild(d);
    b.addChild(c);
    b.width = c.x;
    c = [206, 132, 47, 49];
    a = new Bitmap(a, c);
    a.x = (b.width >> 1) + 15;
    a.y = 9;
    b.addChild(a);
    a.visible = false;
    d = new SlicedBitmap(UI.number);
    d.x = a.x + a.width + 5;
    d.y = a.y + 4;
    b.addChild(d);
    d.visible = false;
    c = new SlicedBitmap(UI.number);
    c.x = a.x + a.width + 5;
    c.y = a.y + 26;
    b.addChild(c);
    c.visible = false;
    var e = new Sprite;
    e.x = a.x - 95;
    e.y = 6;
    b.addChild(e);
    UI.lastPokers = e;
    UI.scoreText = a;
    UI.scoreRate = d;
    UI.baseScore = c;
    UI.toolbar = b
};
UI.showWin = function(a, b) {
    if (a) {
        if (!UI.win) {
            var c = new MovieClip,
                d = new Bitmap(UI.images[8].image, [0, 0, 86, 90]);
            d.x = 270;
            d.y = 35;
            c.addFrame(new Frame(d));
            d = new Bitmap(UI.images[8].image, [0, 100, 86, 100]);
            d.x = 275;
            d.y = 20;
            c.addFrame(new Frame(d));
            d = new Bitmap(UI.images[6].image);
            c.addChild(d);
            c.x = b.getStageWidth() - d.width >> 1;
            c.y = 100;
            UI.win = c
        }
        b.addChild(UI.win)
    } else UI.win && UI.win.parent && UI.win.parent.removeChild(UI.win)
};
UI.showLose = function(a, b) {
    if (a) {
        if (!UI.lose) {
            var c = new MovieClip,
                d = new Bitmap(UI.images[9].image, [0, 0, 91, 42]);
            d.x = 210;
            d.y = 75;
            c.addFrame(new Frame(d));
            d = new Bitmap(UI.images[9].image, [0, 45, 73, 85]);
            d.x = 210;
            d.y = 20;
            c.addFrame(new Frame(d));
            d = new Bitmap(UI.images[7].image);
            c.addChild(d);
            c.x = b.getStageWidth() - d.width >> 1;
            c.y = 100;
            UI.lose = c
        }
        b.addChild(UI.lose)
    } else UI.lose && UI.lose.parent && UI.lose.parent.removeChild(UI.lose)
};
TweenUtil = function(a, b, c) {
    this.target = a;
    this.newProps = c;
    this.oldProps = {};
    for (var d in this.newProps) if (d == "onStart" || d == "onComplete" || d == "onUpdate") this[d] = this.newProps[d];
    else if (this.target[d] !== undefined) this.oldProps[d] = this.target[d];
    this.duration = b;
    this.interval = TweenUtil.interval;
    this.total = this.duration / this.interval;
    this.count = 0;
    this.timerID = null
};
TweenUtil.prototype.start = function() {
    TweenUtil.addTween(this);
    TweenUtil.activate();
    this.onStart && this.onStart()
};
TweenUtil.prototype.stop = function() {
    TweenUtil.removeTween(this)
};
TweenUtil.prototype._trigger = function() {
    this.count++;
    this.onUpdate && this.onUpdate();
    if (this.count >= this.total) {
        for (var a in this.newProps) this.target[a] = this.newProps[a];
        this._finish()
    } else for (a in this.newProps) this.target[a] += (this.newProps[a] - this.oldProps[a]) / this.total
};
TweenUtil.prototype._finish = function() {
    this.stop();
    this.onComplete && this.onComplete();
    this.newProps = this.oldProps = this.target = null
};
TweenUtil.timerID = null;
TweenUtil.interval = 1E3 / 30;
TweenUtil.tweens = [];
TweenUtil.addTween = function(a) {
    TweenUtil.tweens.indexOf(a) == -1 && TweenUtil.tweens.push(a)
};
TweenUtil.removeTween = function(a) {
    a = TweenUtil.tweens.indexOf(a);
    a != -1 && TweenUtil.tweens.splice(a, 1);
    TweenUtil.tweens.length == 0 && TweenUtil.deactivate()
};
TweenUtil.isActive = function() {
    return TweenUtil.timerID != null
};
TweenUtil.activate = function() {
    if (TweenUtil.timerID == null) TweenUtil.timerID = setInterval(TweenUtil._trigger, TweenUtil.interval)
};
TweenUtil.deactivate = function() {
    if (TweenUtil.timerID != null) {
        clearInterval(TweenUtil.timerID);
        TweenUtil.timerID = null
    }
};
TweenUtil._trigger = function() {
    for (var a = TweenUtil.tweens.length; --a >= 0;) TweenUtil.tweens[a]._trigger();
    TweenUtil.onTrigger && TweenUtil.onTrigger()
};
TweenUtil.onTrigger = null;
TweenUtil.to = function(a, b, c) { (new TweenUtil(a, b, c)).start()
};
TweenUtil.from = function(a, b, c) {
    a = new TweenUtil(a, b, c);
    b = a.oldProps;
    a.oldProps = a.newProps;
    a.newProps = b;
    for (var d in a.oldProps) if (a.target[d] !== undefined) a.target[d] = a.oldProps[d];
    a.start()
};
Poker = function(a, b) {
    Sprite.call(this);
    this.point = a;
    this.type = this.point == 16 ? Poker.JOKERS: this.point == 17 ? Poker.JOKERB: b;
    this.selected = false;
    this.init()
};
casual.inherit(Poker, Sprite);
Poker.prototype.init = function() {
    this.mouseChildren = false;
    var a = new Bitmap(Poker.image, Poker.foreground);
    this.addChild(a);
    var b = new Bitmap(Poker.image, Poker[this.point].frame);
    if (this.type == Poker.FANGKUAI || this.type == Poker.HONGTAO) {
        b.frame = b.frame.slice(0);
        b.frame[0] -= 1;
        b.frame[1] = 489
    }
    b.x = 10 + Poker[this.point].offsetX;
    b.y = 6;
    this.addChild(b);
    if (this.type != Poker.JOKERS && this.type != Poker.JOKERB) {
        var c = new Bitmap(Poker.image, this.type.frame);
        c.x = 8;
        c.y = 28;
        this.addChild(c)
    }
    b = new Bitmap(Poker.image, b.frame);
    b.scaleX = b.scaleY = -1;
    b.x = a.width - 12;
    b.y = a.height - 12;
    this.addChild(b);
    if (this.type != Poker.JOKERS && this.type != Poker.JOKERB) {
        b = new Bitmap(Poker.image, this.type.frame);
        b.scaleX = b.scaleY = -1;
        b.x = a.width - 10 + Poker[this.point].offsetX;
        b.y = a.height - 35;
        this.addChild(b)
    }
};
Poker.prototype.select = function(a) {
    if (! (!this.mouseEnabled || this.selected == a)) {
        this.selected = a;
        this.y += this.selected ? -15 : 15
    }
};
Poker.prototype.onMouseEvent = function(a) {
    if (a.type == "mousedown") {
        this.select(!this.selected);
        this.getStage().render()
    }
};
Poker.prototype.toString = function() {
    return "[Poker point=" + this.point + "]"
};
Poker.shuffle = function(a) {
    for (var b = 0,
             c = a.length; b < c; b++) {
        var d = Math.random() * (c - 1) >> 0,
            e = a[b];
        a[b] = a[d];
        a[d] = e
    }
    return a
};
Poker.newPack = function() {
    for (var a = [], b = 3; b <= 15; b++) {
        a.push(new Poker(b, Poker.FANGKUAI));
        a.push(new Poker(b, Poker.MEIHUA));
        a.push(new Poker(b, Poker.HONGTAO));
        a.push(new Poker(b, Poker.HEITAO))
    }
    a.push(new Poker(16));
    a.push(new Poker(17));
    return a
};
Poker.sortPoker = function(a, b) {
    return b.point - a.point
};
Poker.select = function(a, b) {
    for (var c = 0; c < a.length; c++) a[c].select(b)
};
Poker.image = null;
Poker.foreground = [156, 12, 105, 145];
Poker.background = [22, 10, 105, 145];
Poker.TOTAL = 54;
Poker.START = 3;
Poker.END = 17;
Poker.FANGKUAI = {
    name: "fangkuai",
    frame: [247, 179, 20, 24]
};
Poker.MEIHUA = {
    name: "meihua",
    frame: [270, 179, 20, 24]
};
Poker.HONGTAO = {
    name: "hongtao",
    frame: [294, 179, 20, 24]
};
Poker.HEITAO = {
    name: "heitao",
    frame: [318, 179, 20, 24]
};
Poker.JOKERS = {
    name: "jokers"
};
Poker.JOKERB = {
    name: "jokerb"
};
Poker[3] = {
    frame: [195, 465, 14, 21],
    offsetX: 0
};
Poker[4] = {
    frame: [178, 465, 14, 21],
    offsetX: 0
};
Poker[5] = {
    frame: [162, 465, 14, 21],
    offsetX: 0
};
Poker[6] = {
    frame: [146, 465, 14, 21],
    offsetX: 0
};
Poker[7] = {
    frame: [228, 465, 14, 21],
    offsetX: 0
};
Poker[8] = {
    frame: [211, 465, 14, 21],
    offsetX: 0
};
Poker[9] = {
    frame: [129, 465, 14, 21],
    offsetX: 0
};
Poker[10] = {
    frame: [107, 465, 21, 21],
    offsetX: -1
};
Poker[11] = {
    frame: [89, 465, 14, 21],
    offsetX: 0
};
Poker[12] = {
    frame: [68, 465, 18, 21],
    offsetX: -1
};
Poker[13] = {
    frame: [49, 465, 17, 21],
    offsetX: 0
};
Poker[14] = {
    frame: [29, 465, 17, 21],
    offsetX: -1
};
Poker[15] = {
    frame: [12, 465, 14, 21],
    offsetX: 0
};
Poker[16] = {
    frame: [288, 73, 15, 100],
    offsetX: 0
};
Poker[17] = {
    frame: [270, 73, 15, 100],
    offsetX: 0
};
ddz = {
    images: null,
    canvas: null,
    conntext: null,
    stage: null,
    width: 0,
    height: 0,
    players: [],
    user: null,
    logo: null,
    splash: null,
    pokerContainer: null,
    gambleContainer: null,
    controlContainer: null,
    backPokers: null,
    gambleScore: 0,
    gambleTurn: -1,
    gambleID: -1,
    gambleCount: 0,
    pokerX: 200,
    pokerY: 445,
    pokerMaxWidth: 700,
    pokerMaxGap: 60,
    lastThreePokers: null,
    playingTween: true,
    currentDiZhu: null,
    currentTurn: 0,
    currentTurnType: null,
    currentTurnPokers: null,
    currentTurnWinner: null,
    baseScore: 1,
    scoreRate: 1,
    turnTimerID: null,
    turnInterval: 1500,
    roundInterval: 3E3,
    enableSound: true,
    bgSound: null,
    winSound: null,
    loseSound: null,
    soundMP3: ["sounds/normal.mp3", "sounds/win.mp3", "sounds/lose.mp3"],
    soundOGG: ["sounds/normal.ogg", "sounds/win.ogg", "sounds/lose.ogg"]
};
ddz.startup = function(a) {
    this.images = [];
    a = a || ["images/bg.png", "images/poker.png", "images/button.png", "images/portrait.png", "images/number.png", "images/logo.png", "images/win.png", "images/lose.png", "images/hand1.png", "images/hand2.png"];
    var b = new ImageLoader(a);
    b.addEventListener("loaded", ddz.onLoaded);
    b.addEventListener("complete", ddz.onComplete);
    b.load();
    ddz.showProgress(0, a.length, a[0])
};
ddz.onLoaded = function(a) {
    ddz.showProgress(a.target.getLoaded(), a.target.getTotal(), a.params.src)
};
ddz.onComplete = function(a) {
    a.target.removeEventListener("loaded", ddz.onLoaded);
    a.target.removeEventListener("complete", ddz.onComplete);
    document.getElementById("status").innerHTML = "";
    ddz.images = a.params;
    UI.images = a.params;
    ddz.initGame()
};
ddz.showProgress = function(a, b, c) {
    var d = document.getElementById("status");
    d.innerHTML = "Loading resources, please wait...<br>";
    d.innerHTML += "(" + a + "/" + b + ") ";
    d.innerHTML += c.substring(c.lastIndexOf("/") + 1)
};
ddz.initGame = function() {
    if (navigator.userAgent.match(/ipad/i)) this.playingTween = false;
    this.canvas = document.getElementById("canvas");
    this.canvas.style.backgroundImage = "url(" + this.images[0].src + ")";
    this.canvas.style.backgroundPosition = "center center";
    this.canvas.oncontextmenu = function() {
        return false
    };
    this.context = this.canvas.getContext("2d");
    this.width = this.canvas.width;
    this.height = this.canvas.height;
    this.pokerY = this.height - 155;
    this.pokerX = this.width - this.pokerMaxWidth - 110;
    Poker.image = this.images[1].image;
    UI.initNumber(this.images[4].image);
    UI.initToolBar(this.images[2].image);
    UI.toolbar.x = this.width - UI.toolbar.width >> 1;
    this.stage = new Stage(this.context);
    // this.initButtons();
    // this.pokerContainer = new Sprite;
    // this.pokerContainer.name = "pokerContainer";
    // this.stage.addChild(this.pokerContainer);
    this.showSplash()
};
ddz.showSplash = function() {
    this.stage.setFrameRate(0);
    UI.showBubble(false);
    UI.showWin(false);
    UI.showLose(false);
    // this.controlContainer.visible = false;
    this.stage.removeChild(UI.toolbar);
    // this.pokerContainer.removeAllChildren();
    if (this.splash) {
        this.stage.addChild(this.splash);
        this.stage.render()
    } else {
        this.splash = new Sprite;
        this.stage.addChild(this.splash);
        if (this.playingTween) TweenUtil.onTrigger = function() {
            ddz.stage.render()
        };
        b = Poker.newPack();
        a = 0;
        for (c = b.length; a < c; a++) {
            var d = b[a];
            d.regX = 52;
            d.regY = 500;
            d.x = this.width >> 1;
            d.y = 470;
            d.scaleX = d.scaleY = 0.7;
            d.rotation = 360;
            d.mouseEnabled = false;
            this.splash.addChild(d);
            if (this.playingTween) TweenUtil.to(d, 500, {
                rotation: 295 + 130 * (a / (c - 1))
            });
            else d.rotation = 295 + 130 * (a / (c - 1))
        }
        a = new Bitmap(this.images[5].image);
        a.x = this.width - a.width >> 1;
        a.y = 500;
        this.logo = a;
        this.splash.addChild(a);
        TweenUtil.to(a, 1500, { y: 100,x:200 });

        var b = new SlicedBitmap(this.images[2].image);
        b.addSlice([15, 78, 98, 58]);
        b.addSlice([185, 34, 50, 28], 24, 10);
        var c = new Button(b);
        c.x = this.width - 100 >> 1;
        c.y = 365;
        this.startBtn = c;
        this.splash.addChild(this.startBtn)
        var z = this.logo;
        c.onMouseUp = function() {
            TweenUtil.to(z, 2500, { y: 300,x:500 });
        }
    }
};