package com.pdsd.application;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HostsAdapterListView extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private List<String> hostNames;
		
	public HostsAdapterListView (Activity context, List<String> hostNames) {
		this.hostNames = hostNames;
		layoutInflater = (LayoutInflater)context.getLayoutInflater();
	}

	@Override
	public int getCount() {
		return hostNames.size();
	}

	@Override
	public Object getItem(int arg0) {
		return hostNames.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.activity_hosts, parent, false);
		
		TextView textView = (TextView)convertView.findViewById(R.id.textView3);
		textView.setText(hostNames.get(position));
		
		return convertView;
	}
}