package com.pdsd.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends Activity {
	public ConnectionUtil connection;
	public static String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				connection = ConnectionUtil.getConnection();
//				HostsUpdater.finished = false;
//				HostsUpdater hostsUpdater = new HostsUpdater(connection);
//				hostsUpdater.start();
//			}}).start();
		
		Button createGame = (Button) findViewById(R.id.button6);
		createGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						String data = Constants.createGameRequest.replace("*", username);
						connection.sendData(data);
					}
				}).start();
				
				HostsUpdater.finished = true;
				Intent intent = new Intent(HomeActivity.this, CreateGameActivity.class);
		        startActivity(intent);
			}
		});
		
		Button joinGame = (Button) findViewById(R.id.button7);
		joinGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, JoinGameActivity.class);
		        startActivity(intent);
			}
		});
		
		Button statisticsButton = (Button) findViewById(R.id.button8);
		statisticsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, StatisticsActivity.class);
		        startActivity(intent);
			}
		});
		
		Button signOut = (Button) findViewById(R.id.button9);
		signOut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String data = Constants.signOutRequest.replace("*", username);
				connection.sendData(data);
				connection.closeConnection();
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    Log.d("BACK BUTTON", "back button pressed");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String data = Constants.signOutRequest.replace("*", username);
				connection.sendData(data);
				connection.closeConnection();
			}
		}).start();
	    
	}
	return super.onKeyDown(keyCode, event);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		HostsUpdater.finished = true;
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		HostsUpdater.finished = true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				HostsUpdater.finished = false;
				HostsUpdater hostsUpdater = new HostsUpdater(connection, HomeActivity.this);
				hostsUpdater.start();
			}}).start();
	}
}