package com.example.f;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainActivity extends Activity {
	private WebView webView;
	private Button 跳转, go, back, 刷新, 主页;
//	private View beijing;
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideBottomUIMenu();//隐藏状态栏及虚拟按键
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl("http://192.168.1.9:8080/hlddz/test1.html");
//		webView.loadUrl("https://www.sogou.com");
//		go = (Button) findViewById(R.id.go);
		刷新 = (Button) findViewById(R.id.刷新);
//		back = (Button) findViewById(R.id.back);
//		主页 = (Button) findViewById(R.id.主页);
//		beijing = findViewById(R.id.beijing);
//		go.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if (webView.canGoForward()) {
//					webView.goForward();
//				}
//			}
//		});
//		back.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if (webView.canGoBack()) {
//					webView.goBack();
//				}
//			}
//		});
		刷新.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				webView.reload();
			}
		});
//		主页.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				webView.loadUrl("https://www.sogou.com");
//			}
//		});

	


		webView.setWebViewClient(new WebViewClient() {
			// 当点击链接时,覆盖窗口
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				if (url.startsWith("http:") || url.startsWith("https:")) {
					view.loadUrl(url);// 加载url
					return false;
				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
					return true;
				}
			}
		});

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);// 启用JS脚本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        webSettings.setDomStorageEnabled(true);//支持缓存
		// 这里可以有很多设置
//		webSettings.setSupportZoom(true); // 支持缩放
//		webSettings.setBuiltInZoomControls(true); // 启用内置缩放装置

		webView.setWebChromeClient(new WebChromeClient() {
			// 当WebView进度改变时更新窗口进度
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				// 自己实现
				super.onProgressChanged(view, newProgress);
			}
		});

	}
	//主动控制返回键
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		 	if (keyCode == KeyEvent.KEYCODE_BACK){  
	                return isCosumenBackKey();
	        }
	        return false;
	}
	private boolean isCosumenBackKey() {
	    // 这儿做返回键的控制，如果自己处理返回键逻辑就返回true，如果返回false,代表继续向下传递back事件，由系统去控制
		webView.goBack();
	   return true;
	}

	private void hideBottomUIMenu() { 
		Window window = getWindow();  
		//隐藏标题栏  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //隐藏状态栏  
        //定义全屏参数  
        int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;  
        //设置当前窗体为全屏显示  
        window.setFlags(flag, flag);
        //隐藏虚拟按键 start
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
//        window.setAttributes(params);
      //隐藏虚拟按键 end
	}


//js中加入暂停方法
//    function onPause() {
//        !audioObj.paused && audioObj.pause()
//    }
//    function onResume() {
//        audioObj.paused && audioObj.play()
//    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
        webView.pauseTimers();
        webView.loadUrl("javascript:onPause()");
    }

        @Override
        public void onResume() {
            super.onResume();
            webView.resumeTimers();
            webView.onResume();
            webView.loadUrl("javascript:onResume()");
        }


        @Override
        protected void onDestroy() {
            webView.destroy();
            webView = null;
            super.onDestroy();
        }
//        暂停完毕
}
