package com.pdsd.application;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlayersAdapterListView extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private List<String> playerNames;
		
	public PlayersAdapterListView (Activity context, List<String> playerNames) {
		this.playerNames = playerNames;
		layoutInflater = (LayoutInflater)context.getLayoutInflater();
	}

	@Override
	public int getCount() {
		return playerNames.size();
	}

	@Override
	public Object getItem(int arg0) {
		return playerNames.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = layoutInflater.inflate(R.layout.activity_lobby_players, parent, false);
		
		TextView textView = (TextView)convertView.findViewById(R.id.lobby_players);
		textView.setText(playerNames.get(position));
		
		return convertView;
	}
}