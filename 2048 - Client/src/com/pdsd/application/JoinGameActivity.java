package com.pdsd.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class JoinGameActivity extends Activity {
	public static List<String> hostNames = new LinkedList<String>();
	public boolean finished = false;
	public HostsAdapterListView hostsAdaptorListView;
	public ConnectionUtil connection ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				connection = ConnectionUtil.getConnection();
//				HostsUpdater.finished = false;
//				HostsUpdater hostsUpdater = new HostsUpdater(connection);
//				hostsUpdater.start();
//			}
//		}).start();
		
		
		final ListView hostNamesListView = (ListView)findViewById(R.id.listView1);
		hostsAdaptorListView = new HostsAdapterListView(this, hostNames);
		finished = false;
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				while (finished == false) {
//					try {
//						Thread.sleep(Constants.SLEEP);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					
//					JoinGameActivity.this.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							hostsAdaptorListView.notifyDataSetChanged();
//						}	
//					});
//				}
//			}
//		}).start();
		
		hostNamesListView.setAdapter(hostsAdaptorListView);
		hostNamesListView.setOnItemClickListener(new OnItemClickListener() {
		    @Override 
		    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		    	final String hostName = hostNames.get(position);
		    	
		    	new Thread(new Runnable() {
					@Override
					public void run() {
						String data = Constants.joinGameRequest.replace("*", HomeActivity.username).replace("%", hostName);
						connection.sendData(data);
					}
				}).start();
		    	
		    	
				
				Intent intent = new Intent(JoinGameActivity.this, LobbyActivity.class).
		    								putExtra("<hostname>", hostName);
		        startActivity(intent);
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hosts, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				HostsUpdater.finished = false;
				HostsUpdater hostsUpdater = new HostsUpdater(connection, JoinGameActivity.this);
				hostsUpdater.start();
			}
		}).start();
		finished = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (finished == false) {
					try {
						Thread.sleep(Constants.SLEEP);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					JoinGameActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							hostsAdaptorListView.notifyDataSetChanged();
						}	
					});
				}
			}
		}).start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finished = true;
		HostsUpdater.finished = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finished = true;
		HostsUpdater.finished = true;
	}
}