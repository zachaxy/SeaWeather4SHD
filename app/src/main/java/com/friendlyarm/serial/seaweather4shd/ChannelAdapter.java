package com.friendlyarm.serial.seaweather4shd;


import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter{
	
	private List<String> mNameList;
	private List<String> mFreqList;
	
	private LayoutInflater mInflater;
	
	public ChannelAdapter(Context context, List<String> nameList,List<String> freqList){
		mInflater = LayoutInflater.from(context);
		mNameList = nameList;
		mFreqList = freqList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mNameList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view;
		view = mInflater.inflate(R.layout.channels_list_item, null);	
		TextView name = (TextView) view.findViewById(R.id.channel_index);
        TextView freq= (TextView) view.findViewById(R.id.channel_freq);
        
        name.setText(mNameList.get(position));
        freq.setText(mFreqList.get(position));
		return view;
	}

}
