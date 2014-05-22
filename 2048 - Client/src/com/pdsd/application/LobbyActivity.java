package com.pdsd.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class LobbyActivity extends Activity {
	public static List<String> playerNames = new LinkedList<String>();
	public boolean finished = false;
	PlayersAdapterListView playersAdaptorListView;
	// public ConnectionUtil connection = ConnectionUtil.getConnection();
	public ConnectionUtil connection;
	public LobbyActivity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		this.activity = this;
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				PlayersUpdaterJoin.finished = false;
				PlayersUpdaterJoin playersUpdater = new PlayersUpdaterJoin(
						connection, activity);
				playersUpdater.start();
			}
		}).start();*/

		TextView hostName = (TextView) findViewById(R.id.textView4);
		hostName.setText(getIntent().getStringExtra("<hostname>"));

		ListView playerNamesListView = (ListView) findViewById(R.id.listView2);
		playersAdaptorListView = new PlayersAdapterListView(this, playerNames);
		playerNamesListView.setAdapter(playersAdaptorListView);
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
//					
//					LobbyActivity.this.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							playersAdaptorListView.notifyDataSetChanged();
//						}
//					});
//				}
//			}
//		}).start();
	}

	public void startGame()
	{
		finished = true;
		PlayersUpdaterJoin.finished = true;
		Intent intent = new Intent(LobbyActivity.this,
				GameActivity.class).putExtra("HOST", "0");
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lobby, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.activity = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				PlayersUpdaterJoin.finished = false;
				PlayersUpdaterJoin playersUpdater = new PlayersUpdaterJoin(
						connection, activity);
				playersUpdater.start();
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

					LobbyActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							playersAdaptorListView.notifyDataSetChanged();
						}
					});
				}
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    Log.d("BACK BUTTON", "back button pressed");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = Constants.exitGameRequest.replace("*", HomeActivity.username);
				connection.sendData(data);
			}
		}).start();
	    
	}
	return super.onKeyDown(keyCode, event);

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finished = true;
		PlayersUpdaterJoin.finished = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finished = true;
		PlayersUpdaterJoin.finished = true;
	}
}