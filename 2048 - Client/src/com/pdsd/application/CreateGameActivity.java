package com.pdsd.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class CreateGameActivity extends Activity {
	public static List<String> playerNames = new LinkedList<String>();
	public boolean finished = false;
	PlayersCreateAdapterListView playersCreateAdaptorListView;
	public ConnectionUtil connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		Log.d("test", "S-A APELAT ON CREATE");

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				connection = ConnectionUtil.getConnection();
//				PlayersUpdater.finished = false;
//				PlayersUpdater playersUpdater = new PlayersUpdater(connection);
//				playersUpdater.start();
//
//			}
//		}).start();

		ListView playerNamesListView = (ListView) findViewById(R.id.listView3);
		playersCreateAdaptorListView = new PlayersCreateAdapterListView(this, playerNames);
		playerNamesListView.setAdapter(playersCreateAdaptorListView);
		// playersCreateAdaptorListView = new
		// PlayersCreateAdapterListView(R.layout.activity_create_game,
		// R.id.game_players, this, playerNames);
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
//					CreateGameActivity.this.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							playersCreateAdaptorListView.notifyDataSetChanged();
//						}
//					});
//				}
//			}
//		}).start();

		
		playerNamesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

			}
		});

		Button startGame = (Button) findViewById(R.id.button10);
		startGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finished = true;
				PlayersUpdater.finished = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						String data = Constants.startGameRequest.replace("*", HomeActivity.username);
						connection.sendData(data);
					}
				}).start();
				
				
				Intent intent = new Intent(CreateGameActivity.this,
						GameActivity.class).putExtra("HOST", "1");
				startActivity(intent);
				finish();
				
				
			}
		});
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_game, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				
				if (connection == null) {
					Log.d ("test", "create connection e NULL");
				} else {
					Log.d ("test", "create connection nu e NULL");
				}
				
				PlayersUpdater.finished = false;
				PlayersUpdater playersUpdater = new PlayersUpdater(connection, CreateGameActivity.this);
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

					CreateGameActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							playersCreateAdaptorListView.notifyDataSetChanged();
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
		PlayersUpdater.finished = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finished = true;
		PlayersUpdater.finished = true;
	}
}