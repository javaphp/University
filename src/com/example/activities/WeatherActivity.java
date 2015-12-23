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
		mWvWeather.getSettings().setJavaScriptEnabled(true);	//����JavaScript����
		mWvWeather.setWebChromeClient(new WebChromeClient());	//����JavaScript�Ի���
		mWvWeather.setWebViewClient(new WebViewClient());	//�������֪ͨ�������¼��������ʹ�øþ���룬��ʹ�����������������ҳ
		mWvWeather.loadUrl("http://www.weather.com.cn/weather1d/101280101.shtml");	//����Ĭ����ʾ������Ԥ����Ϣ
		mWvWeather.setInitialScale(57*4);	//����ҳ���ݷŴ�4��
	}
	
}
