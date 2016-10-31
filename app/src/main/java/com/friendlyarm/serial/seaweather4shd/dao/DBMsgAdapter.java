package com.friendlyarm.serial.seaweather4shd.dao;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlyarm.serial.seaweather4shd.R;

import java.util.List;



/**
 * Created by zhangxin on 2016/4/26.
 */
public class DBMsgAdapter extends DBAdapter {
	public DBMsgAdapter(List<DBMsg> mList, Context context) {
		super(mList, context);
	}

	

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.db1, null);//没有缓存，再去创建

            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.db1_time);
            viewHolder.phoneNo = (TextView) view.findViewById(R.id.db1_pNo);
            viewHolder.content = (TextView) view.findViewById(R.id.db1_content);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        DBMsg mDBMsg = (DBMsg) mList.get(position); //position是从底部划入进界面的时候的位置

        viewHolder.time.setText(mDBMsg.time);
        viewHolder.phoneNo.setText(mDBMsg.phoneNo);
        viewHolder.content.setText(mDBMsg.content);

        return view;
    }

    class ViewHolder {  /*避免重复的findViewById的操作*/
        public TextView time;
        public TextView phoneNo;
        public TextView content;
    }
}
