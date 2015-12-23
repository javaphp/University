package com.example.activities;

import com.example.honrizontalscrollview.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WeatherActivity extends Activity {
	private WebView mWvWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		initView();
	}

	private void initView() {
		mWvWeather = (WebView) findViewById(R.id.wvWeather);
		mWvWeather.getSettings().setJavaScriptEnabled(true);	//设置JavaScript可用
		mWvWeather.setWebChromeClient(new WebChromeClient());	//处理JavaScript对话框
		mWvWeather.setWebViewClient(new WebViewClient());	//处理各种通知和请求事件，如果不使用该句代码，将使用内置浏览器访问网页
		mWvWeather.loadUrl("http://www.weather.com.cn/weather1d/101280101.shtml");	//设置默认显示的天气预报信息
		mWvWeather.setInitialScale(57*4);	//放网页内容放大4倍
	}
	
}
