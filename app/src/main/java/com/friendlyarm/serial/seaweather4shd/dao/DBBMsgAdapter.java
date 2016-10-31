package com.friendlyarm.serial.seaweather4shd.dao;


import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.friendlyarm.serial.seaweather4shd.R;

public class DBBMsgAdapter extends DBAdapter{

	public DBBMsgAdapter(List<DBBMsg> mList, Context context) {
		super(mList, context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.db2, null);//没有缓存，再去创建

            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.db2_time);
            viewHolder.content = (TextView) view.findViewById(R.id.db2_content);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        DBBMsg mDBBMsg = (DBBMsg) mList.get(position); //position是从底部划入进界面的时候的位置

        viewHolder.time.setText(mDBBMsg.time);
        viewHolder.content.setText(mDBBMsg.content);

        return view;
    }

    class ViewHolder {  /*避免重复的findViewById的操作*/
        public TextView time;
        public TextView content;
    }

}
