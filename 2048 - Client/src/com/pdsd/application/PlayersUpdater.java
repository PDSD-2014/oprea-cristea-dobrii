package com.pdsd.application;

import android.app.Activity;
import android.util.Log;

public class PlayersUpdater extends Thread {
	public static boolean finished = false;
	public ConnectionUtil connection;
	public Activity caller;

	public PlayersUpdater(ConnectionUtil connection, Activity caller) {
		this.connection = connection;
		this.caller = caller;
	}

	@Override
	public void run() {

		Log.d("test", "in run");

		while (finished == false) {
			Log.d("test", "in while");
			final String playerNamesRequest;
			if (connection != null) {
				playerNamesRequest = connection.receiveData();
			} else {
				continue;
			}

			Log.d("test", "dupa if else");

			caller.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (playerNamesRequest == null) {
						Log.d("PLAYERS", "response is null");
					} else {
						Log.d("PLAYERS", playerNamesRequest);
						final String[] activities = playerNamesRequest
								.split("=");
						if (playerNamesRequest
								.contains(Constants.PLAYERS_LIST_REQUEST)
								&& activities.length == 1) {
							CreateGameActivity.playerNames.clear();
						} else {

							Log.d("test", "inainte de IF");
							if (activities[0]
									.equals(Constants.PLAYERS_LIST_REQUEST)) {
								Log.d("test", "dupa IF");

								synchronized (CreateGameActivity.playerNames) {
									CreateGameActivity.playerNames.clear();

									for (int i = 0; i < CreateGameActivity.playerNames
											.size(); i++) {
										Log.d("test",
												"playerName = "
														+ CreateGameActivity.playerNames
																.get(i));
									}

									String[] players = activities[1].split("&");

									for (int i = 0; i < players.length; i++) {
										Log.d("test",
												"dupa split: playerName = "
														+ players[i]);
									}

									for (int i = 0; i < players.length; i++) {
										CreateGameActivity.playerNames
												.add(players[i].split("\"")[1]);
										Log.d("PLAYERS",
												"host = "
														+ players[i]
																.split("\"")[1]);
									}
								}
							} else {
								Log.d("PLAYERS",
										"this is not a game list request");
							}
						}
					}
				}
			});
		}
	}
}