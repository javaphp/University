package com.example.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.domain.Article;
import com.example.fragment.CommonUIFragment;
import com.example.fragment.LaunchUIFragment;
import com.example.honrizontalscrollview.R;
import com.example.utils.HttpUtil;
import com.example.utils.MyConstant;
import com.example.view.GifView;
import com.example.view.SyncHorizontalScrollView;
import com.example.viewholder.BaseViewHolder;
import com.example.viewholder.HeadLineItem;

public class MainActivity extends FragmentActivity implements OnClickListener {

	public static final String ARGUMENTS_NAME = "arg";
	private RelativeLayout rl_nav;
	private SyncHorizontalScrollView mHsv;
	private RadioGroup rg_nav_content;
	private ImageView iv_nav_indicator;
	private ImageView iv_nav_left;
	private ImageView iv_nav_right;
	private ViewPager mViewPager;
	private Button mOptions;   //标题栏的选项按钮
	private int indicatorWidth;
	public static String[] tabTitle = { "头条", "娱乐", "热点", "体育", "广州",
										"财经", "科技", "段子", "图片", "轻松一刻"};	//标题
	private LayoutInflater mInflater;
	private TabFragmentPagerAdapter mAdapter;
	private int currentIndicatorLeft = 0;
	
	private ProgressDialog progress;
	private GifView mIvLoading;
	private Handler handler;
	
	
	
	public List<Article> mArticles = new ArrayList<Article>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.main_title_bar);
		
		//progress=ProgressDialog.show(MainActivity.this,"加载中", "正在加载数据,请稍候...");
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//mArticles = new ArrayList<Article>();
				Log.i("internet", "handlerMessage:hi");
            	JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(msg.obj.toString());
					
					for(int i = 0; i<jsonArray.length(); i++) {
						JSONObject item = jsonArray.getJSONObject(i);
						int id = item.getInt("id");
						String title = item.getString("title");
						String brief = item.getString("abstract");
						String content = item.getString("content");
						String imgUrl = item.getString("img");
						int type = item.getInt("type");
						Article article = new Article(id, title, brief, content, imgUrl, type);
						mArticles.add(article); 
					}
					Log.i("internet", "mArticle:"+mArticles.size() + "size");
					mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mArticles);
					mViewPager.setAdapter(mAdapter);
					mIvLoading.setVisibility(View.GONE);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		loadArticles();
		
		findViewById();
		initView();
		
		setListener();
	}
	
	private void loadArticles() {
		new Thread() {
			public void run() {
				try {
					String url = MyConstant.BASE_PATH + "articleapis";
					String gift = HttpUtil.requestByGet(url); 
					Message msg = new Message();
					Log.i("internet", gift + "hi");
					Log.i("internet", "hi");
					msg.obj = gift; 
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnOption:
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	private void setListener() {
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				
				if(rg_nav_content!=null && rg_nav_content.getChildCount()>position){
					((RadioButton)rg_nav_content.getChildAt(position)).performClick();
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		
		rg_nav_content.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				if(rg_nav_content.getChildAt(checkedId)!=null){
					
					TranslateAnimation animation = new TranslateAnimation(
							currentIndicatorLeft ,
							((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft(), 0f, 0f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(100);
					animation.setFillAfter(true);
					
					//执行位移动画
					iv_nav_indicator.startAnimation(animation);
					
					mViewPager.setCurrentItem(checkedId);	//ViewPager 跟随一起 切换
					
					//记录当前 下标的距最左侧的 距离
					currentIndicatorLeft = ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft();
					
					mHsv.smoothScrollTo(
							(checkedId > 1 ? ((RadioButton) rg_nav_content.getChildAt(checkedId)).getLeft() : 0) - ((RadioButton) rg_nav_content.getChildAt(2)).getLeft(), 0);
				}
			}
		});
	}

	private void initView() {
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		indicatorWidth = dm.widthPixels / 4;
		
		LayoutParams cursor_Params = iv_nav_indicator.getLayoutParams();
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		iv_nav_indicator.setLayoutParams(cursor_Params);
		
		mHsv.setSomeParam(rl_nav, iv_nav_left, iv_nav_right, this);
		
		//获取布局填充器
		mInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

		//另一种方式获取
//		LayoutInflater mInflater = LayoutInflater.from(this);  
		
		initNavigationHSV();
		
		mOptions.setOnClickListener(this);
		
		
//		mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mArticles);
//		mViewPager.setAdapter(mAdapter);
	}

	private void initNavigationHSV() {
		
		rg_nav_content.removeAllViews();
		
		for(int i=0;i<tabTitle.length;i++){
			
			RadioButton rb = (RadioButton) mInflater.inflate(R.layout.nav_radiogroup_item, null);
			rb.setId(i);
			rb.setText(tabTitle[i]);
			rb.setTextColor(0xffb2b2b2);
			rb.setLayoutParams(new LayoutParams(indicatorWidth,
					LayoutParams.MATCH_PARENT));
			
			rg_nav_content.addView(rb);
		}
		
	}

	private void findViewById() {
		
		rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);
		
		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);
		
		rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);
		
		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
		iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);
		
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		
		mOptions = (Button) findViewById(R.id.btnOption);
		mIvLoading = (GifView) findViewById(R.id.ivLoading);
		mIvLoading.setMovieResource(R.drawable.loading);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter{
		private List<Article> articles;
		
		public TabFragmentPagerAdapter(FragmentManager fm, List<Article> articles) {
			super(fm);
			this.articles = articles;
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = null;
			ft = new LaunchUIFragment(articles);
			/*
			switch (arg0) {
			case 0:
				ft = new LaunchUIFragment();
				break;

			default:
				ft = new CommonUIFragment();
				
				Bundle args = new Bundle();
				args.putString(ARGUMENTS_NAME, tabTitle[arg0]);
				ft.setArguments(args);
				
				break;
			}*/
			return ft;
		}

		@Override
		public int getCount() {
			
			return tabTitle.length;
		}
		
	}

	
}
