package com.friendlyarm.serial.seaweather4shd.dao;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DBAdapter extends BaseAdapter{

	public List<? extends DBTable> mList;
    public  LayoutInflater mInflater;
    
    //public DBAdapter(){}
    
    public DBAdapter(List< ? extends DBTable> mList, Context context) {
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
    }
    
     

	@Override
    public int getCount() {
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
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
