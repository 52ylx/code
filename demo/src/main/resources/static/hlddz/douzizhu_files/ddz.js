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
TweenUtil = function(a, b, c) {//相对自己
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
GroupType = function(a, b) {
    if (a) GroupType[a] = this;
    this.name = a;
    this.numPokers = b
};
GroupType.prototype.toString = function() {
    return this.name
};
new GroupType("\u5355\u5f20", 1);
new GroupType("\u5bf9\u5b50", 2);
new GroupType("\u53cc\u738b", 2);
new GroupType("\u4e09\u5f20", 3);
new GroupType("\u70b8\u5f39", 4);
new GroupType("\u4e09\u5e26\u4e00", 4);
new GroupType("\u4e94\u5f20\u987a\u5b50", 5);
new GroupType("\u4e09\u5e26\u4e8c", 5);
new GroupType("\u516d\u5f20\u987a\u5b50", 6);
new GroupType("\u4e09\u8fde\u5bf9", 6);
new GroupType("\u56db\u5e26\u4e8c", 6);
new GroupType("\u4e8c\u8fde\u98de\u673a", 6);
new GroupType("\u4e03\u5f20\u987a\u5b50", 7);
new GroupType("\u516b\u5f20\u987a\u5b50", 8);
new GroupType("\u56db\u8fde\u5bf9", 8);
new GroupType("\u4e8c\u8fde\u98de\u673a\u5e26\u7fc5\u8180", 8);
new GroupType("\u4e5d\u5f20\u987a\u5b50", 9);
new GroupType("\u4e09\u8fde\u98de\u673a", 9);
new GroupType("\u5341\u5f20\u987a\u5b50", 10);
new GroupType("\u4e94\u8fde\u5bf9", 10);
new GroupType("\u4e8c\u8fde\u98de\u673a\u5e26\u4e8c\u5bf9", 10);
new GroupType("\u5341\u4e00\u5f20\u987a\u5b50", 11);
new GroupType("\u5341\u4e8c\u5f20\u987a\u5b50", 12);
new GroupType("\u516d\u8fde\u5bf9", 12);
new GroupType("\u56db\u8fde\u98de\u673a", 12);
new GroupType("\u4e09\u8fde\u98de\u673a\u5e26\u7fc5\u8180", 12);
new GroupType("\u4e03\u8fde\u5bf9", 14);
new GroupType("\u4e94\u8fde\u98de\u673a", 15);
new GroupType("\u4e09\u8fde\u98de\u673a\u5e26\u4e09\u5bf9", 15);
new GroupType("\u516b\u8fde\u5bf9", 16);
new GroupType("\u56db\u8fde\u98de\u673a\u5e26\u7fc5\u8180", 16);
new GroupType("\u4e5d\u8fde\u5bf9", 18);
new GroupType("\u516d\u8fde\u98de\u673a", 18);
new GroupType("\u5341\u8fde\u5bf9", 20);
new GroupType("\u4e94\u8fde\u98de\u673a\u5e26\u7fc5\u8180", 20);
Rule = {};
Rule.compare = function(a, b) {
    var c = Rule.getType(a),
        d = Rule.getType(b);
    switch (d) {
        case GroupType.\u5355\u5f20:
        case GroupType.\u5bf9\u5b50:
        case GroupType.\u4e09\u5f20:
        case GroupType.\u70b8\u5f39:
        case GroupType.\u4e09\u5e26\u4e00:
        case GroupType.\u4e09\u5e26\u4e8c:
        case GroupType.\u56db\u5e26\u4e8c:
        case GroupType.\u4e94\u5f20\u987a\u5b50:
        case GroupType.\u516d\u5f20\u987a\u5b50:
        case GroupType.\u4e03\u5f20\u987a\u5b50:
        case GroupType.\u516b\u5f20\u987a\u5b50:
        case GroupType.\u4e5d\u5f20\u987a\u5b50:
        case GroupType.\u5341\u5f20\u987a\u5b50:
        case GroupType.\u5341\u4e00\u5f20\u987a\u5b50:
        case GroupType.\u5341\u4e8c\u5f20\u987a\u5b50:
        case GroupType.\u4e09\u8fde\u5bf9:
        case GroupType.\u56db\u8fde\u5bf9:
        case GroupType.\u4e94\u8fde\u5bf9:
        case GroupType.\u516d\u8fde\u5bf9:
        case GroupType.\u4e03\u8fde\u5bf9:
        case GroupType.\u516b\u8fde\u5bf9:
        case GroupType.\u4e5d\u8fde\u5bf9:
        case GroupType.\u5341\u8fde\u5bf9:
        case GroupType.\u4e8c\u8fde\u98de\u673a:
        case GroupType.\u4e8c\u8fde\u98de\u673a\u5e26\u7fc5\u8180:
        case GroupType.\u4e8c\u8fde\u98de\u673a\u5e26\u4e8c\u5bf9:
        case GroupType.\u4e09\u8fde\u98de\u673a:
        case GroupType.\u4e09\u8fde\u98de\u673a\u5e26\u7fc5\u8180:
        case GroupType.\u4e09\u8fde\u98de\u673a\u5e26\u4e09\u5bf9:
        case GroupType.\u56db\u8fde\u98de\u673a:
        case GroupType.\u56db\u8fde\u98de\u673a\u5e26\u7fc5\u8180:
        case GroupType.\u4e94\u8fde\u98de\u673a:
        case GroupType.\u4e94\u8fde\u98de\u673a\u5e26\u7fc5\u8180:
        case GroupType.\u516d\u8fde\u98de\u673a:
            if (c == d) return a[0].point > b[0].point;
            if (c == GroupType.\u53cc\u738b || c == GroupType.\u70b8\u5f39) return true
    }
    return false
};
Rule.getType = function(a) {
    a.sort(Poker.sortPoker);
    var b = a.length;
    switch (b) {
        case 1:
            return GroupType.\u5355\u5f20;
        case 2:
            if (Rule.isSame(a, 2)) return GroupType.\u5bf9\u5b50;
            else if (a[0].point == 17 && a[1].point == 16) return GroupType.\u53cc\u738b;
            break;
        case 3:
            if (Rule.isSame(a, 3)) return GroupType.\u4e09\u5f20;
            break;
        case 4:
            if (Rule.isSame(a, 4)) return GroupType.\u70b8\u5f39;
            else if (Rule.isTripleLink(a)) return GroupType.\u4e09\u5e26\u4e00;
            break;
        case 5:
            if (Rule.isStraight(a)) return GroupType.\u4e94\u5f20\u987a\u5b50;
            else if (Rule.isTripleLink(a)) return GroupType.\u4e09\u5e26\u4e8c;
            break;
        case 6:
            if (Rule.isStraight(a)) return GroupType.\u516d\u5f20\u987a\u5b50;
            else if (Rule.isPairLink(a)) return GroupType.\u4e09\u8fde\u5bf9;
            else if (Rule.isSame(a, 4)) {
                for (var c = -1,
                         d = 0,
                         e = 0; e < b - 1; e++) if (a[e].point == a[e + 1].point) {
                    d++;
                    if (d >= 3) c = e - 2
                } else d = 0;
                if (c > 0) {
                    b = a.splice(c, 4);
                    a.unshift.apply(a, b)
                }
                return GroupType.\u56db\u5e26\u4e8c
            } else if (Rule.isTripleLink(a)) return GroupType.\u4e8c\u8fde\u98de\u673a;
            break;
        case 7:
            if (Rule.isStraight(a)) return GroupType.\u4e03\u5f20\u987a\u5b50;
            break;
        case 8:
            if (Rule.isStraight(a)) return GroupType.\u516b\u5f20\u987a\u5b50;
            else if (Rule.isPairLink(a)) return GroupType.\u56db\u8fde\u5bf9;
            else if (Rule.isTripleLink(a)) return GroupType.\u98de\u673a\u5e26\u7fc5\u8180;
            break;
        case 9:
            if (Rule.isStraight(a)) return GroupType.\u4e5d\u5f20\u987a\u5b50;
            else if (Rule.isTripleLink(a)) return GroupType.\u4e09\u8fde\u98de\u673a;
            break;
        case 10:
            if (Rule.isStraight(a)) return GroupType.\u5341\u5f20\u987a\u5b50;
            else if (Rule.isPairLink(a)) return GroupType.\u4e94\u8fde\u5bf9;
            else if (Rule.isTripleLink(a)) return GroupType.\u4e8c\u8fde\u98de\u673a\u5e26\u4e8c\u5bf9;
            break;
        case 11:
            if (Rule.isStraight(a)) return GroupType.\u5341\u4e00\u5f20\u987a\u5b50;
            break;
        case 12:
            if (Rule.isStraight(a)) return GroupType.\u5341\u4e8c\u5f20\u987a\u5b50;
            else if (Rule.isPairLink(a)) return GroupType.\u516d\u8fde\u5bf9;
            else {
                a = Rule.isTripleLink(a);
                if (a == 3) return GroupType.\u4e09\u8fde\u98de\u673a\u5e26\u7fc5\u8180;
                else if (a == 4) return GroupType.\u56db\u8fde\u98de\u673a
            }
            break;
        case 14:
            if (Rule.isPairLink(a)) return GroupType.\u4e03\u8fde\u5bf9;
            break;
        case 15:
            if (Rule.isTripleLink(a)) return GroupType.\u4e94\u8fde\u98de\u673a;
            else if (Rule.isTripleLink(a)) return GroupType.\u4e09\u8fde\u98de\u673a\u5e26\u4e09\u5bf9;
            break;
        case 16:
            if (Rule.isPairLink(a)) return GroupType.\u516b\u8fde\u5bf9;
            else if (Rule.isTripleLink(a)) return GroupType.\u56db\u8fde\u98de\u673a\u5e26\u7fc5\u8180;
            break;
        case 18:
            if (Rule.isPairLink(a)) return GroupType.\u4e5d\u8fde\u5bf9;
            else if (Rule.isTripleLink(a)) return GroupType.\u516d\u8fde\u98de\u673a;
            break;
        case 20:
            if (Rule.isPairLink(a)) return GroupType.\u5341\u8fde\u5bf9;
            else if (Rule.isTripleLink(a)) return GroupType.\u4e94\u8fde\u98de\u673a\u5e26\u7fc5\u8180
    }
    return false
};
Rule.isSame = function(a, b) {
    var c, d = false;
    for (c = 0; c < b - 1; c++) if (a[c].point == a[c + 1].point) d = true;
    else {
        d = false;
        break
    }
    if (d) return true;
    for (c = a.length - 1; c > a.length - b; c--) if (a[c].point == a[c - 1].point) d = true;
    else {
        d = false;
        break
    }
    return d
};
Rule.isStraight = function(a) {
    if (a[0].point >= 15) return false;
    for (var b = 0; b < a.length - 1; b++) if (a[b].point != a[b + 1].point + 1) return false;
    return true
};
Rule.isPairLink = function(a) {
    if (a[0].point >= 15) return false;
    for (var b = 0; b < a.length - 2; b += 2) if (a[b].point != a[b + 1].point || a[b].point != a[b + 2].point + 1 || a[b + 2].point != a[b + 3].point) return false;
    return true
};
Rule.isTripleLink = function(a) {
    for (var b = 0,
             c = a.length,
             d = 2; d < c; d++) a[d - 2].point == a[d].point && a[d - 1].point == a[d].point && b++;
    if (b == 0) return false;
    var e = Rule.sortTripleLink(a.slice());
    if (b > 1) for (d = 0; d < b * 3 - 3; d += 3) if (e[d].point == 15 || e[d].point != e[d + 3].point + 1) return false;
    d = c - b * 3;
    if (d == 0 || d == b) return true;
    else if (d == b * 2) {
        d = 0;
        for (var f = b * 3 - 1; f < c - 1; f++) e[f].point == e[f + 1].point && d++;
        if (b != d) return false
    } else return false;
    for (d = 0; d < c; d++) a[d] = e[d];
    return b
};
Rule.sortTripleLink = function(a) {
    var b, c = 0,
        d = a.length,
        e = false,
        f = 0,
        g = null,
        h = [];
    for (b = 2; b < d; b++) if (a[b].point == a[b - 1].point && a[b].point == a[b - 2].point) {
        if (f == 0) {
            f++;
            h.push(a[b])
        } else {
            g = a[b];
            for (var i = b; i > 0; i--) {
                e = a[i];
                a[i] = a[i - 1];
                a[i - 1] = e
            }
            c++
        }
        e = true
    } else {
        f = 0;
        e || (c = b - 1)
    }
    for (b = 0; b < h.length; b++) for (f = 0; f < d; f++) if (a[f].point == h[b].point) if (a[f] == g) g = null;
    else {
        e = a[f - c];
        a[f - c] = a[f];
        a[f] = e
    }
    d = a.slice(a.length - c, a.length);
    d.sort(Poker.sortPoker);
    for (b = a.length - c; b < a.length; b++) a[b] = d[b - a.length + c];
    return a
};
AI = {
    singleRate: 1,
    singleLowRate: 0.5,
    kingRate: 3,
    pairRate: 3,
    pairLowRate: 2,
    tripleRate: 4,
    tripleLowRate: 3.5,
    straightRate: 5,
    bombRate: 6,
    twoKingScore: 120
};
AI.gamble = function(a) {
    a = a.pokerScore;
    return a < 220 ? 1 : a >= 220 && a <= 280 ? 2 : 3
};
AI.selectPoker = function(a, b, c) {
    if (!b) return AI.autoSelectPoker(a);
    var d = AI.findPokerByType(a, b);
    if (d && d.length > 0) for (var e = d.length - 1; e >= 0; e--) {
        var f = d[e];
        f instanceof Array || (f = [f]);
        if (b.numPokers == f.length) {
            if (b == GroupType.\u4e09\u5e26\u4e00 || b == GroupType.\u4e09\u5e26\u4e8c) f = Rule.sortTripleLink(f);
            if (b == GroupType.\u4e09\u5e26\u4e00) if (a.single.length > 0) {
                var g = a.single[a.single.length - 1];
                if (g.point < f[3].point) f[3] = g
            } else {
                if (f[3].point > 12) {
                    g = a.pokers[a.pokers.length - 1];
                    if (g.point != f[0].point && g.point != a.pokers[a.pokers.length - 4].point) if (g.point < f[3].point) f[3] = g
                }
            } else if (b == GroupType.\u4e09\u5e26\u4e8c) {
                g = a.pair[a.pair.length - 1];
                if (g[0].point < f[3].point) {
                    f[3] = g[0];
                    f[4] = g[1]
                }
            }
            g = false;
            if (g = c ? Rule.compare(f, c) : true) {
                Poker.select(f, true);
                return true
            }
        }
    }
    return false
};
AI.autoSelectPoker = function(a) {
    AI.analyzePlayerPokers(a);
    var b, c;
    if (Rule.getType(a.pokers)) Poker.select(a.pokers, true);
    else {
        c = AI.findPokerByType(a, GroupType.\u5341\u4e8c\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u5341\u4e00\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u5341\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e5d\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u516b\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e03\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u516d\u5f20\u987a\u5b50);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e94\u5f20\u987a\u5b50);
        if (c && c.length > 0) for (b = c.length - 1; b >= 0; b--) if (c[b][0].point < 13) {
            Poker.select(c[b], true);
            return true
        }
        c = AI.findPokerByType(a, GroupType.\u5341\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e5d\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u516b\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e03\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u516d\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e94\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u56db\u8fde\u5bf9);
        if (!c || !c.length) c = AI.findPokerByType(a, GroupType.\u4e09\u8fde\u5bf9);
        if (c && c.length > 0) for (b = c.length - 1; b >= 0; b--) if (c[b][0].point <= 10) {
            Poker.select(c[b], true);
            return true
        }
        b = a.triple.length;
        if (b >= 2) {
            c = a.triple[b - 1];
            if (c[0].point <= 10) {
                if (a.single.length) var d = a.single[a.single.length - 1];
                if (a.pair.length) var e = a.pair[a.pair.length - 1];
                if (d) if (e && e[0].point != c[0].point && e[0].point < 10 && e[0].point <= d.point - 2) c = c.concat(e);
                else c.push(d);
                else if (e && e[0].point != c[0].point && e[0].point < 10) c = c.concat(e);
                Poker.select(c, true);
                return true
            }
        }
        b = a.single.length;
        if (b > 0) {
            c = a.single[b - 1];
            if (c.point < 10) {
                c.select(true);
                return true
            }
        }
        b = a.pair.length;
        if (b > 0) {
            c = a.pair[b - 1];
            if (c[0].point < 10) {
                Poker.select(c, true);
                return true
            }
        }
        b = a.single.length;
        if (b > 0) {
            c = a.single[b - 1];
            c.select(true);
            return true
        }
        b = a.pair.length;
        if (b > 0) {
            c = a.pair[b - 1];
            Poker.select(c, true);
            return true
        }
        b = a.triple.length;
        if (b > 0) {
            if ((c = AI.findPokerByType(a, GroupType.\u4e09\u5e26\u4e00)) && c.length && Rule.getType(c[c.length - 1]) != GroupType.\u70b8\u5f39) {
                Poker.select(c[c.length - 1], true);
                return true
            }
            if ((c = AI.findPokerByType(a, GroupType.\u4e09\u5e26\u4e8c)) && c.length) {
                Poker.select(c[c.length - 1], true);
                return true
            }
            c = a.triple[b - 1];
            Poker.select(c, true);
            return true
        }
        b = a.bomb.length;
        if (b > 0) {
            c = a.bomb[b - 1];
            Poker.select(c, true);
            return true
        } (c = AI.findPokerByType(a, GroupType.\u53cc\u738b)) && c.length && Poker.select(c[0], true);
        return true
    }
};
AI.findPokerByType = function(a, b) {
    var c, d = b.numPokers,
        e = a.pokers.length;
    if (d > e) return null;
    if (b == GroupType.\u5355\u5f20) return a.single;
    else if (b == GroupType.\u5bf9\u5b50) return a.pair;
    else if (b == GroupType.\u4e09\u5f20) return a.triple;
    else if (b == GroupType.\u70b8\u5f39) return a.bomb;
    var f, g = [];
    for (c = d - 1; c < e; c++) {
        f = a.pokers.slice(c - d + 1, c + 1);
        if (Rule.getType(f) == b) {
            g.push(f);
            c += d - 1
        }
    }
    return g
};
AI.scorePlayerPokers = function(a) {
    var b, c, d = 0;
    for (b = 0; b < a.single.length; b++) {
        c = a.single[b].point;
        d += c >= 16 ? AI.kingRate * c: c >= 10 ? AI.singleRate * c: AI.singleLowRate * c
    }
    if (a.single[0].point == 17 && a.single[0].point == 16) d += AI.twoKingScore;
    for (b = 0; b < a.pair.length; b++) {
        c = a.pair[b][0].point;
        d += c >= 10 ? AI.pairRate * c: AI.pairLowRate * c
    }
    for (b = 0; b < a.triple.length; b++) {
        c = a.triple[b][0].point;
        d += c >= 10 ? AI.tripleRate * c: AI.tripleLowRate * c
    }
    for (b = 0; b < a.bomb.length; b++) {
        c = a.bomb[b][0].point;
        d += AI.bombRate * c
    }
    a.pokerScore = d;
    trace("pokerScore:", a.name, d);
    return d
};
AI.analyzePlayerPokers = function(a) {
    var b = [],
        c = [],
        d = [],
        e,
        f,
        g = a.pokers,
        h = g.length;
    for (e = 0; e < h; e++) {
        f = g[e].point;
        var i = false;
        if (e == 0) i = h == 1 ? true: g[e + 1].point != f;
        else if (e == h - 1) i = g[e - 1].point != f;
        else if (g[e - 1].point != f && g[e + 1].point != f) i = true;
        i && b.push(g[e])
    }
    for (e = 1; e < h; e++) if (g[e - 1].point == g[e].point) {
        c.push([g[e - 1], g[e]]);
        e++
    }
    for (e = 2; e < h; e++) {
        f = g[e].point;
        if (g[e - 2].point == f && g[e - 1].point == f) if (! (e + 1 < h && g[e + 1].point == f)) if (! (e >= 3 && g[e - 3].point == f)) {
            d.push([g[e - 2], g[e - 1], g[e]]);
            e += 2
        }
    }
    for (e = 3; e < h; e++) {
        f = g[e].point;
        if (g[e - 3].point == f && g[e - 2].point == f && g[e - 1].point == f) {
            c.push([g[e - 3], g[e - 2], g[e - 1], g[e]]);
            e += 3
        }
    }
    a.single = b;
    a.pair = c;
    a.triple = d;
    a.bomb = []
};
Player = function(a, b) {
    Sprite.call(this);
    this.id = a;
    this.name = b;
    this.score = 0;
    this.role = Player.NORMAL;
    this.sex = Player.MALE;
    this.pokers = null;
    this.single = [];
    this.pair = [];
    this.triple = [];
    this.bomb = [];
    this.pokerScore = 0;
    this.lastPlayedPokers = this.playingPokers = this.playingType = null;
    this.init()
};
casual.inherit(Player, Sprite);
Player.prototype.init = function() {
    var a = new Bitmap(Player.image, Player.PORTRAIT_FRAME);
    this.mouseChildren = false;
    this.addChild(a);
    this.frame = a;
    var b = new SlicedBitmap(UI.number);
    b.x = a.x + 38;
    b.y = a.y + a.height;
    this.addChild(b);
    this.count = b
};
Player.prototype.reset = function() {
    this.single = [];
    this.pair = [];
    this.triple = [];
    this.bomb = [];
    this.pokerScore = 0;
    this.lastPlayedPokers = this.playingPokers = this.playingType = this.pokers = null
};
Player.prototype.getSelectedPokers = function() {
    for (var a = [], b = 0; b < this.pokers.length; b++) this.pokers[b].selected && a.push(this.pokers[b]);
    return a
};
Player.prototype.deletePokers = function(a) {
    for (var b = 0; b < a.length; b++) {
        var c = a[b],
            d = this.pokers.indexOf(c);
        d >= 0 && this.pokers.splice(d, 1);
        c.mouseEnabled = false;
        d = this.single.indexOf(c);
        d >= 0 && this.single.splice(d, 1);
        for (d = 0; d < this.pair.length; d++) {
            var e = this.pair[d];
            if (e[0] == c) e[0] = null;
            else if (e[1] == c) e[1] = null;
            if (!e[0] || !e[1]) {
                this.pair.splice(d, 1);
                break
            }
        }
        for (d = 0; d < this.triple.length; d++) {
            e = this.triple[d];
            if (e[0] == c) e[0] = null;
            else if (e[1] == c) e[1] = null;
            else if (e[2] == c) e[2] = null;
            if (!e[0] || !e[1] || !e[2]) {
                this.triple.splice(d, 1);
                break
            }
        }
        for (d = 0; d < this.bomb.length; d++) {
            e = this.bomb[d];
            if (e[0] == c) e[0] = null;
            else if (e[1] == c) e[1] = null;
            else if (e[2] == c) e[2] = null;
            else if (e[3] == c) e[3] = null;
            if (!e[0] || !e[1] || !e[2] || !e[3]) {
                this.bomb.splice(d, 1);
                break
            }
        }
    }
};
Player.prototype.clearLastPokers = function() {
    if (this.lastPlayedPokers) {
        for (var a = 0; a < this.lastPlayedPokers.length; a++) {
            var b = this.lastPlayedPokers[a];
            b.parent && b.parent.removeChild(b)
        }
        this.lastPlayedPokers = null
    }
};
Player.prototype.setPortrait = function(a) {
    if (this.portrait) {
        this.portrait.frame = a;
        this.portrait.width = a[2];
        this.portrait.height = a[3]
    } else {
        this.portrait = new Bitmap(Player.image, a);
        this.portrait.x = 9;
        this.portrait.y = 9;
        this.addChild(this.portrait)
    }
};
Player.prototype.toString = function() {
    return this.name
};
Player.prototype.addGlow = function() {
    if (!Player.glow) {
        Player.glow = new Bitmap(Player.image, Player.PORTRAIT_GLOW);
        Player.glow.x = -19;
        Player.glow.y = -21
    }
    this.addChildAt(Player.glow, 0)
};
Player.image = null;
Player.PORTRAIT_FRAME = [8, 2, 115, 115];
Player.PORTRAIT_GLOW = [2, 120, 155, 155];
Player.DIZHU = 0;
Player.NORMAL = 1;
Player.MALE = "male";
Player.FEMALE = "female";
Player.ROBOT_NAME_MALE = ["\u9ccc\u62dc", "\u97e6\u5c0f\u5b9d", "\u5965\u7279\u66fc"];
Player.ROBOT_NAME_FEMALE = ["\u8c82\u8749", "\u51e4\u59d0"];
Player.PORTRAIT_DIZHU = [[216, 0, 87, 87]];
Player.PORTRAIT_M = [[126, 0, 87, 87]];
Player.PORTRAIT_W = [[305, 0, 87, 87]];
Player.getRandomName = function() {
    return Player.ROBOT_NAMES[Math.floor(Math.random() * Player.ROBOT_NAMES.length)]
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
    Player.image = this.images[3].image;
    UI.initNumber(this.images[4].image);
    UI.initToolBar(this.images[2].image);
    UI.toolbar.x = this.width - UI.toolbar.width >> 1;
    this.stage = new Stage(this.context);
    this.initButtons();
    this.pokerContainer = new Sprite;
    this.pokerContainer.name = "pokerContainer";
    this.stage.addChild(this.pokerContainer);
    this.initPlayers();
    this.user.name = "Alex";
    this.user.sex = Player.MALE;
    this.showSplash()
};
ddz.showSplash = function() {
    this.stage.setFrameRate(0);
    UI.showBubble(false);
    UI.showWin(false);
    UI.showLose(false);
    this.controlContainer.visible = false;
    this.stage.removeChild(UI.toolbar);
    this.pokerContainer.removeAllChildren();
    for (var a = 0; a < this.players.length; a++) {
        var b = this.players[a];
        UI.setNumber(b.count, 0);
        b.setPortrait([0, 0, 1, 1])
    }
    Player.glow && Player.glow.parent && Player.glow.parent.removeChild(Player.glow);
    for (var c in this.players) this.players[c].visible = false;
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
        a.y = 190;
        this.logo = a;
        this.splash.addChild(a);
        this.logo.render = function(e) {
            e.shadowColor = "#333";
            e.shadowOffsetX = 0;
            e.shadowOffsetY = 15;
            e.shadowBlur = 50;
            Bitmap.prototype.render.call(this, e)
        };
        this.splash.addChild(this.startBtn)
    }
};
ddz.startGame = function() {
    this.scoreRate = this.baseScore = 1;
    this.currentTurnWinner = this.currentTurnPokers = this.currentTurnType = this.currentDiZhu = null;
    this.stage.removeChild(this.splash);
    UI.lastPokers.removeAllChildren();
    UI.setBaseScoreAndRate(null, null, false);
    for (var a in this.players) {
        this.players[a].visible = true;
        this.players[a].reset()
    }
    this.stage.addChild(UI.toolbar);
    this.deal(Poker.shuffle(Poker.newPack()));
    this.backPokers = [];
    for (a = 0; a < 9; a++) {
        var b = new Bitmap(Poker.image, Poker.background);
        b.x = this.width - 73 >> 1;
        b.y = 200;
        this.stage.addChildAt(b, 0);
        this.backPokers.push(b);
        if (this.playingTween) if (a < 3) {
            b.visible = false;
            b.alpha = 0.5;
            b.index = a == 0 ? 6 : a == 1 ? 12 : 17;
            setTimeout(TweenUtil.to, 1E3 * a, b, 200, {
                alpha: 0,
                x: 0,
                y: 100,
                onStart: function() {
                    this.target.visible = true
                },
                onComplete: function() {
                    UI.setNumber(ddz.players[2].count, this.target.index);
                    this.target.parent.removeChild(this.target)
                }
            })
        } else {
            if (a < 6) {
                b.visible = false;
                b.alpha = 0.5;
                b.index = a == 3 ? 6 : a == 4 ? 12 : 17;
                setTimeout(TweenUtil.to, 1E3 * (a - 3) + 300, b, 200, {
                    alpha: 0,
                    x: ddz.width,
                    y: 100,
                    onStart: function() {
                        this.target.visible = true
                    },
                    onComplete: function() {
                        UI.setNumber(ddz.players[1].count, this.target.index);
                        this.target.parent.removeChild(this.target)
                    }
                })
            }
        } else {
            if (a < 6) b.visible = false;
            UI.setNumber(ddz.players[1].count, 17);
            UI.setNumber(ddz.players[2].count, 17)
        }
    }
    a = 0;
    for (b = this.user.pokers.length; a < b; a++) {
        var c = this.user.pokers[a];
        c.name = "poker" + a;
        c.x = this.width - 73 >> 1;
        c.y = 200;
        c.index = a + 1;
        this.pokerContainer.addChild(c);
        if (this.playingTween) {
            c.visible = false;
            setTimeout(TweenUtil.to, 150 * a, c, 200, {
                x: this.pokerX + a * Math.min(this.pokerMaxWidth / b, this.pokerMaxGap) >> 0,
                y: this.pokerY,
                onStart: function() {
                    UI.setNumber(ddz.players[0].count, this.target.index);
                    this.target.visible = true
                }
            })
        } else {
            c.x = this.pokerX + a * Math.min(this.pokerMaxWidth / b, this.pokerMaxGap) >> 0;
            c.y = this.pokerY;
            UI.setNumber(ddz.players[0].count, 17)
        }
    }
    if (this.playingTween) TweenUtil.onTrigger = function() {
        if (!TweenUtil.isActive() && ddz.user.pokers[ddz.user.pokers.length - 1].y == ddz.pokerY) {
            TweenUtil.onTrigger = null;
            ddz.backPokers[6].x += 40;
            ddz.backPokers[8].x -= 40;
            ddz.gamble()
        }
        ddz.stage.render()
    };
    else {
        ddz.backPokers[6].x += 40;
        ddz.backPokers[8].x -= 40;
        ddz.gamble()
    }
    this.pokerContainer.mouseEnabled = false
};
ddz.deal = function(a) {
    var b, c, d;
    for (b = 0; b < 3; b++) {
        d = this.players[b];
        c = b * 17;
        c = a.slice(c, c + 17);
        c.sort(Poker.sortPoker);
        d.pokers = c;
        AI.analyzePlayerPokers(d);
        AI.scorePlayerPokers(d)
    }
    this.lastThreePokers = a.slice(51)
};
ddz.gamble = function() {
    if (this.gambleCount >= this.players.length) {
        this.baseScore = this.gambleScore || 1;
        var a = this.gambleID;
        this.gambleCount = 0;
        this.gambleID = this.gambleTurn = -1;
        this.gambleScore = 0;
        this.stage.removeChild(this.gambleContainer);
        if (a >= 0) {
            TweenUtil.onTrigger = function() {
                if (!TweenUtil.isActive()) {
                    TweenUtil.onTrigger = null;
                    ddz.backPokers = null
                }
                ddz.stage.render()
            };
            if (this.playingTween) for (var b = 6; b < 9; b++) TweenUtil.to(this.backPokers[b], 200, {
                alpha: 0,
                x: this.width - 140 >> 1,
                y: 0,
                onComplete: function() {
                    this.target.parent.removeChild(this.target)
                }
            });
            else for (b = 6; b < 9; b++) this.backPokers[b].parent.removeChild(this.backPokers[b]);
            this.setDiZhu(this.players[a])
        } else this.startGame()
    } else {
        this.gambleTurn = this.gambleTurn < 2 ? ++this.gambleTurn: 0;
        this.gambleTurn == 0 ? this.stage.addChild(this.gambleContainer) : this.doGamble(this.players[this.gambleTurn], AI.gamble(this.players[this.gambleTurn]))
    }
};
ddz.doGamble = function(a, b) {
    trace("Gamble:", a, b);
    if (b > this.gambleScore) {
        this.gambleID = a.id;
        this.gambleScore = b
    } else b = 0;
    if (this.gambleTurn == 0) this.stage.removeChild(this.gambleContainer);
    else if (this.gambleTurn == 1) UI.showBubble(true, "gamble" + b, true, this.stage, this.width - 200, 150);
    else this.gambleTurn == 2 && UI.showBubble(true, "gamble" + b, false, this.stage, 200, 150);
    this.stage.render();
    this.gambleCount++;
    setTimeout(casual.delegate(this.gamble, this), this.turnInterval)
};
ddz.setDiZhu = function(a) {
    a.role = Player.DIZHU;
    a.pokers = a.pokers.concat(this.lastThreePokers);
    a.pokers.sort(Poker.sortPoker);
    UI.setNumber(a.count, a.pokers.length);
    UI.lastPokers.removeAllChildren();
    for (var b = 0,
             c = this.lastThreePokers.length; b < c; b++) {
        var d = casual.copy(this.lastThreePokers[b]);
        d.scaleX = d.scaleY = 0.4;
        d.x = b * 20;
        d.mouseEnabled = false;
        UI.lastPokers.addChild(d)
    }
    if (a == this.user) {
        b = 0;
        for (c = a.pokers.length; b < c; b++) {
            d = a.pokers[b];
            d.name = "poker" + b;
            d.x = this.pokerX + b * Math.min(this.pokerMaxWidth / c, this.pokerMaxGap) >> 0;
            d.y = this.pokerY;
            this.pokerContainer.addChildAt(d, b)
        }
    }
    for (b = 0; b < this.players.length; b++) {
        c = this.players[b];
        if (c == a) c.setPortrait(Player.PORTRAIT_DIZHU[0]);
        else if (c.sex == Player.MALE) c.setPortrait(Player.PORTRAIT_M[0]);
        else c.sex == Player.FEMALE && c.setPortrait(Player.PORTRAIT_W[0])
    }
    UI.setBaseScoreAndRate(this.baseScore, this.scoreRate, true);
    this.pokerContainer.mouseEnabled = true;
    UI.showBubble(false);
    this.currentDiZhu = a;
    this.currentTurn = this.players.indexOf(a);
    this.nextTurn(false)
};
ddz.nextTurn = function(a) {
    if (a) if (this.currentTurn == 2) this.currentTurn = 0;
    else this.currentTurn++;
    if (this.currentTurnWinner && this.currentTurnWinner.id == this.currentTurn) {
        this.currentTurnPokers = this.currentTurnType = null;
        trace("NewTurn:", this.currentTurnWinner, this.currentTurnWinner.pokers)
    }
    this.players[this.currentTurn].addGlow();
    this.turnTimerID && clearTimeout(this.turnTimerID);
    this.turnTimerID = setTimeout(casual.delegate(this.runTurn, this), this.turnInterval)
};
ddz.runTurn = function() {
    var a = this.players[this.currentTurn];
    if (a == this.user) {
        this.controlContainer.visible = true;
        this.stage.render()
    } else {
        UI.showBubble(false);
        if (this.currentTurnWinner && a.role == this.currentTurnWinner.role && this.currentTurnType) {
            var b = AI.findPokerByType(this.currentDiZhu, this.currentTurnType);
            if (!b || b.length == 0 && a.pokers.length > this.currentTurnWinner.pokers.length) {
                this.skip(a);
                return
            }
        }
        AI.selectPoker(a, this.currentTurnType, this.currentTurnPokers);
        if (a.getSelectedPokers().length > 0) this.playPoker(a);
        else {
            if (this.currentTurnType == GroupType.\u5355\u5f20) if (a.pokers[0] > this.currentTurnPokers[0]) if (a.pokers.length >= 3 && a.pokers[2] != a.pokers[0]) {
                a.pokers[0].select(true);
                this.playPoker(a);
                return
            }
            this.skip(a)
        }
    }
};
ddz.playPoker = function(a) {
    var b, c, d;
    c = a.getSelectedPokers();
    b = Rule.getType(c);
    d = c.length;
    if (b) {
        a.clearLastPokers();
        a.playingType = b;
        a.playingPokers = c;
        trace("PlayPoker:", a, b, c);
        this.currentTurnType = b;
        this.currentTurnPokers = c;
        this.currentTurnWinner = a;
        a.lastPlayedPokers = a.playingPokers;
        a.playingPokers = null;
        a.deletePokers(c);
        if (a == this.user) {
            for (var e = 0; e < d - 1; e++) {
                var f = this.pokerContainer.getChildIndex(c[e]);
                this.pokerContainer.getChildIndex(c[e + 1]) < f && this.pokerContainer.setChildIndex(c[e + 1], f)
            }
            if (this.playingTween) TweenUtil.onTrigger = function() {
                if (!TweenUtil.isActive()) {
                    ddz.controlContainer.visible = false;
                    ddz.arrangePokers()
                }
                ddz.stage.render()
            };
            f = ddz.width - 73 >> 1;
            for (e = 0; e < d; e++) {
                var g = c[e];
                if (this.playingTween) TweenUtil.to(g, 200, {
                    x: f + (e - d * 0.5) * 20,
                    y: 220,
                    scaleX: 0.7,
                    scaleY: 0.7
                });
                else {
                    g.x = f + (e - d * 0.5) * 20;
                    g.y = 220;
                    g.scaleX = g.scaleY = 0.7
                }
            }
            if (!this.playingTween) {
                this.controlContainer.visible = false;
                this.arrangePokers()
            }
        } else {
            if (a.id == 1) for (e = d - 1; e >= 0; e--) {
                f = c[e];
                f.scaleX = f.scaleY = 0.7;
                f.x = this.width - 240 - e * 20;
                f.y = 130;
                this.pokerContainer.addChild(f)
            } else if (a.id == 2) for (e = 0; e < d; e++) {
                f = c[e];
                f.scaleX = f.scaleY = 0.7;
                f.x = 170 + e * 20;
                f.y = 130;
                this.pokerContainer.addChild(f)
            }
            this.stage.render()
        }
        if (b == GroupType.\u53cc\u738b || b == GroupType.\u70b8\u5f39) {
            this.scoreRate *= 2;
            UI.setBaseScoreAndRate(null, this.scoreRate, true)
        }
        UI.setNumber(a.count, a.pokers.length);
        a.pokers.length <= 0 ? setTimeout(casual.delegate(this.win, this), this.turnInterval, a) : this.nextTurn(true)
    }
};
ddz.skip = function(a) {
    trace("SkipPoker:", a.name, this.currentTurnType);
    if (this.currentTurn == 0) this.controlContainer.visible = false;
    if (this.currentTurn == 1) UI.showBubble(true, "noPlay", true, this.stage, this.width - 200, 150);
    else this.currentTurn == 2 && UI.showBubble(true, "noPlay", false, this.stage, 200, 150);
    a.clearLastPokers();
    this.stage.render();
    this.nextTurn(true)
};
ddz.win = function(a) {
    trace("Winner:", a);
    for (var b in this.players) if (b == a || b.role == a.role) b.score += b.role == Player.DIZHU ? this.baseScore * this.scoreRate * 2 : this.baseScore * this.scoreRate;
    else b.score -= this.baseScore * this.scoreRate;
    b = this.user == a || a.role == this.user.role;
    if (this.enableSound) if (b && this.winSound) this.winSound._element.play();
    else this.loseSound && this.loseSound._element.play();
    a.role == Player.DIZHU ? UI.showWin(true, this.stage) : UI.showLose(true, this.stage);
    this.stage.setFrameRate(8);
    this.turnTimerID && clearTimeout(this.turnTimerID);
    this.turnTimerID = null;
    setTimeout(casual.delegate(this.showSplash, this), 5E3)
};
ddz.arrangePokers = function() {
    for (var a, b = 0,
             c = this.user.pokers.length; b < c; b++) {
        a = this.user.pokers[b];
        a.x = this.pokerX + b * Math.min(this.pokerMaxWidth / c, this.pokerMaxGap) >> 0;
        a.y = this.pokerY
    }
};
ddz.initPlayers = function() {
    var a = new Player;
    a.x = 30;
    a.y = this.height - 145;
    a.id = 0;
    a.visible = false;
    this.stage.addChild(a);
    this.players.push(a);
    this.user = a;
    a = new Player;
    a.x = this.width - 125;
    a.y = 10;
    a.id = 1;
    a.visible = false;
    a.sex = Player.MALE;
    a.name = Player.ROBOT_NAME_MALE[0];
    a.score = 1E3;
    this.stage.addChild(a);
    this.players.push(a);
    a = new Player;
    a.x = 10;
    a.y = 10;
    a.id = 2;
    a.visible = false;
    a.sex = Player.FEMALE;
    a.name = Player.ROBOT_NAME_FEMALE[0];
    a.score = 1E3;
    this.stage.addChild(a);
    this.players.push(a)
};
ddz.initButtons = function() {
    var a = Button.prototype.onMouseEvent;
    Button.prototype.onMouseEvent = function(j) {
        a.call(this, j);
        this.alpha = j.type == "mouseout" ? 1 : 0.8;
        this.getStage().render()
    };
    var b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([185, 34, 50, 28], 24, 10);
    var c = new Button(b);
    c.x = this.width - 100 >> 1;
    c.y = 365;
    this.startBtn = c;
    var d = 410;
    this.gambleContainer = new Sprite;
    this.gambleContainer.y = this.pokerY - 70;
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([185, 2, 50, 28], 25, 10);
    var e = new Button(b);
    e.x = d;
    this.gambleContainer.addChild(e);
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([185, 61, 16, 25], 26, 10);
    b.addSlice([211, 92, 28, 28], 46, 10);
    var f = new Button(b);
    f.x = d + 120;
    this.gambleContainer.addChild(f);
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([202, 61, 16, 25], 26, 10);
    b.addSlice([211, 92, 28, 28], 46, 10);
    var g = new Button(b);
    g.x = d + 240;
    this.gambleContainer.addChild(g);
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([219, 61, 16, 25], 26, 10);
    b.addSlice([211, 92, 28, 28], 46, 10);
    var h = new Button(b);
    h.x = d + 360;
    this.gambleContainer.addChild(h);
    this.controlContainer = new Sprite;
    this.controlContainer.y = this.pokerY - 70;
    this.stage.addChild(this.controlContainer);
    d = 410;
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([142, 178, 50, 26], 24, 10);
    var i = new Button(b);
    i.x = d;
    this.controlContainer.addChild(i);
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([142, 151, 50, 26], 24, 10);
    var k = new Button(b);
    k.x = d + 120;
    this.controlContainer.addChild(k);
    this.resetBtn = k;
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 78, 98, 58]);
    b.addSlice([142, 123, 50, 26], 24, 10);
    var l = new Button(b);
    l.x = d + 240;
    this.controlContainer.addChild(l);
    b = new SlicedBitmap(this.images[2].image);
    b.addSlice([15, 4, 98, 58]);
    b.addSlice([125, 94, 50, 28], 24, 10);
    b = new Button(b);
    b.x = d + 360;
    this.controlContainer.addChild(b);
    e.onMouseUp = function() {
        ddz.doGamble(ddz.players[0], 0)
    };
    f.onMouseUp = function() {
        ddz.doGamble(ddz.players[0], 1)
    };
    g.onMouseUp = function() {
        ddz.doGamble(ddz.players[0], 2)
    };
    h.onMouseUp = function() {
        ddz.doGamble(ddz.players[0], 3)
    };
    b.onMouseUp = function() {
        var j = ddz.user.getSelectedPokers();
        if (j && j.length > 0) if (ddz.currentTurnPokers) Rule.compare(j, ddz.currentTurnPokers) && ddz.playPoker(ddz.user);
        else Rule.getType(j) && ddz.playPoker(ddz.user)
    };
    l.onMouseUp = function() {
        AI.selectPoker(ddz.user, ddz.currentTurnType, ddz.currentTurnPokers)
    };
    i.onMouseUp = function() {
        ddz.skip(ddz.user)
    };
    k.onMouseUp = function() {
        Poker.select(ddz.user.getSelectedPokers(), false)
    };
    c.onMouseUp = function() {
        ddz.startGame()
    }
};