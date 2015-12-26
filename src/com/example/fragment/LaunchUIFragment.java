package com.example.fragment;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.activities.MainActivity;
import com.example.domain.Article;
import com.example.honrizontalscrollview.R;
import com.example.utils.MyConstant;
import com.example.view.RefreshableView;
import com.example.view.RefreshableView.PullToRefreshListener;
import com.example.viewholder.BaseViewHolder;

public class LaunchUIFragment extends Fragment {

	//private ListView listView;
	private List<Article> articles;
	
	private RefreshableView refreshableView;
	
	public LaunchUIFragment(List<Article> articles) {
		super();
		this.articles = articles;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_selection_launch, container, false);
		//ivLoading = (ImageView) rootView.findViewById(R.id.ivLoading);
		refreshableView = (RefreshableView) rootView.findViewById(R.id.refreshable_view);
		ListView listView = (ListView) rootView.findViewById(R.id.lvnews);
		ListViewAdapter listViewAdapter = new ListViewAdapter(articles, this.getActivity());
		listView.setAdapter(listViewAdapter);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {  
            @Override  
            public void onRefresh() {  
                try {  
                    Thread.sleep(3000);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
                refreshableView.finishRefreshing();  
            }  
        }, 0);  
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	

	public static class ListViewAdapter extends BaseAdapter{
		private List<Article> articleList;
		private Context context;
		private LayoutInflater layoutInflater;
		private static Bitmap bitmap;
		private static ImageView imageView;
		
		private static Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				imageView.setImageBitmap((Bitmap) msg.obj);
			};
		};
		
		public ListViewAdapter(List<Article> articleList, Context context) {
			super();
			this.articleList = articleList;
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return articleList.size();
		}

		@Override
		public Object getItem(int position) {
			return articleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			BaseViewHolder viewHolder = null;
			//if(convertView == null) {
				viewHolder = new BaseViewHolder();
				switch (articleList.get(position).getType()) {
				case MyConstant.HEAD_LINE_ARTICLE:
					convertView = layoutInflater.inflate(R.layout.item_head_line, null);
					viewHolder.imageView = (ImageView) convertView.findViewById(R.id.head_line_image);
					viewHolder.title = (TextView) convertView.findViewById(R.id.head_line_title);
					
					viewHolder.title.setText(articleList.get(position).getTitle());
					loadArticleImg(articleList.get(position).getImgUrl(), viewHolder.imageView);
					break;
				case MyConstant.COMMON_ARTICLE:
					convertView = layoutInflater.inflate(R.layout.item_common, null);
					viewHolder.imageView = (ImageView) convertView.findViewById(R.id.common_image);
					viewHolder.title = (TextView) convertView.findViewById(R.id.common_title);
					viewHolder.brief = (TextView) convertView.findViewById(R.id.common_brief);
					
					viewHolder.title.setText(articleList.get(position).getTitle());
					//viewHolder.imageView.setImageDrawable()
					loadArticleImg(articleList.get(position).getImgUrl(), viewHolder.imageView);
					viewHolder.brief.setText(articleList.get(position).getBrief());
					break;
				case MyConstant.ALBUM_ARTICLE:
					convertView = layoutInflater.inflate(R.layout.item_album, null);
					viewHolder.firstImage = (ImageView) convertView.findViewById(R.id.album_first_image);
					viewHolder.secondImage = (ImageView) convertView.findViewById(R.id.album_second_image);
					viewHolder.thirdImage = (ImageView) convertView.findViewById(R.id.album_third_image);
					break;
				case MyConstant.SMALL_HEAD_LINE_ARTICLE:
					convertView = layoutInflater.inflate(R.layout.item_small_head_line, null);
					viewHolder.imageView = (ImageView) convertView.findViewById(R.id.small_head_line_image);
					viewHolder.title = (TextView) convertView.findViewById(R.id.small_head_line_title);
					
					viewHolder.title.setText(articleList.get(position).getTitle());
					loadArticleImg(articleList.get(position).getImgUrl(), viewHolder.imageView);
				default:
					break;
				}
				convertView.setTag(viewHolder);
				//} else {
				//viewHolder = (BaseViewHolder) convertView.getTag();
			//}
	        
	        return convertView; 
		}
		
		private static void loadArticleImg(final String imgUrl, final ImageView thumb) {
			new Thread() {
				public void run() {
					try {
						String fullUrl = MyConstant.IMAG_BASE_PATH + imgUrl;
						byte[] data = getImage(fullUrl);
	                    if(data != null) {
	                    	bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	                    } 
						
						imageView = thumb;
						Message msg = new Message();
						msg.obj = bitmap; 
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		/**
	     * Get image from newwork
	     * @param path The path of image
	     * @return byte[]
	     * @throws Exception
	     */
	    public static byte[] getImage(String path) throws Exception{
	        URL url = new URL(path);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setConnectTimeout(5 * 1000);
	        conn.setRequestMethod("GET");
	        InputStream inStream = conn.getInputStream();
	        if(conn.getResponseCode() == 200){
	            return readStream(inStream);
	        }
	        return null;
	    }
	    public static byte[] readStream(InputStream inStream) throws Exception{
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024];
	        int len = 0;
	        while( (len=inStream.read(buffer)) != -1){
	            outStream.write(buffer, 0, len);
	        }
	        outStream.close();
	        inStream.close();
	        return outStream.toByteArray();
	    }
		
	}
	
}
