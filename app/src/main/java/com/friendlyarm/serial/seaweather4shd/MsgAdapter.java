package com.friendlyarm.serial.seaweather4shd;


import java.util.List;



import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.friendlyarm.serial.seaweather4shd.tools.BytesUtil;
import com.friendlyarm.serial.seaweather4shd.tools.Param;

public class MsgAdapter extends BaseAdapter {
	private List<Msg> mList;
	private LayoutInflater mInflater;

	public MsgAdapter(Context context, List<Msg> list) {
		mInflater = LayoutInflater.from(context);
		mList = list;
	}

	@Override
	public int getCount() {
		// 该list中,指定最多显示20条内容
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view;
		ViewHolder viewHolder;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.item, null);// 没有缓存，再去创建

			viewHolder = new ViewHolder();

			viewHolder.imageView = (ImageView) view.findViewById(R.id.msg_img);
			viewHolder.content = (TextView) view.findViewById(R.id.msg_content);
			viewHolder.time = (TextView) view.findViewById(R.id.msg_time);

			view.setTag(viewHolder);

		} else {
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}

		Msg msg = mList.get(position); // position是从底部划入进界面的时候的位置

		viewHolder.imageView.setImageResource(msg.mMsgImg);
		viewHolder.content.setText(msg.mMsgContent);
		viewHolder.time.setText(formatTimeInRight(msg.mMsgTime));

		return view;
	}

	class ViewHolder {
		public ImageView imageView;
		public TextView content;
		public TextView time;
	}
	
	public void updataView(int posi, ListView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
            View view = listView.getChildAt(posi - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();
            
            //这一步只是在界面显示上暂时显示为已读状态
            //但是存在的问题是只要一刷新界面(界面滑动),就会重新从list中绘制布局.
            //if(view.)
           // ((ImageView) view.findViewById(R.id.msg_img)).setImageResource(R.drawable.msg_read);


            //这一步将真实数据修改,以后再刷新的时候,就是实际的已读状态了
            Msg msg = mList.get(posi);
            if(msg.mMsgImg == R.drawable.msg_unread){
            	Log.d("###","短信未读");
            	msg.mMsgImg = R.drawable.msg_read;
            	((ImageView) view.findViewById(R.id.msg_img)).setImageResource(R.drawable.msg_read);
            	ContentValues value1 = new ContentValues();
            	value1.put("img", msg.mMsgImg);
            	Param.db.update("tmpMSG",value1,"time = ?",new String[]{msg.mMsgTime});
             }else if(msg.mMsgImg == R.drawable.business_msg_unread){
            	Log.d("###","商务信息未读");
            	msg.mMsgImg = R.drawable.business_msg_read;
            	((ImageView) view.findViewById(R.id.msg_img)).setImageResource(R.drawable.business_msg_read);
            	ContentValues value2 = new ContentValues();
            	value2.put("img", msg.mMsgImg);
            	Param.db.update("tmpMSG",value2,"time = ?",new String[]{msg.mMsgTime});
            }
            msg.isRead = true;
            mList.set(posi, msg);
        } 
        
        
	
    }
		private String formatTimeInRight(String s){
			char[] c = s.toCharArray();
			s = "接收时间: "+ BytesUtil.formatTime(c);
        	return s;
        }

}
