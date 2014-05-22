package com.pdsd.application;

import android.util.Log;

public class PlayersUpdaterJoin extends Thread {
	public static boolean finished = false;
	public ConnectionUtil connection;
	public LobbyActivity caller;

	public PlayersUpdaterJoin(ConnectionUtil connection, LobbyActivity caller) {
		this.connection = connection;
		this.caller = caller;
	}

	@Override
	public void run() {
		while (finished == false) {
			final String playerNamesRequest = connection.receiveData();

			Log.d("PLAYERS", playerNamesRequest);

			if (playerNamesRequest == null) {
				Log.d("PLAYERS", "response is null");
				continue;
			}
			if (playerNamesRequest.contains(Constants.GAME_STARTED)) {
				finished = true;
				caller.startGame();
				continue;
			}

			if (playerNamesRequest.contains(Constants.GAME_CLOSED_BY_HOST)) {
				Log.d("FINISH", "finished");
				caller.finish();
				continue;
			}
			caller.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String[] activities = playerNamesRequest.split("=");
					if (playerNamesRequest
							.contains(Constants.PLAYERS_LIST_REQUEST)
							&& activities.length == 1) {
						LobbyActivity.playerNames.clear();

					} else {

						if (activities[0]
								.equals(Constants.PLAYERS_LIST_REQUEST)) {
							synchronized (LobbyActivity.playerNames) {
								LobbyActivity.playerNames.clear();
								String[] players = activities[1].split("&");
								for (int i = 0; i < players.length; i++) {
									LobbyActivity.playerNames.add(players[i]
											.split("\"")[1]);
									Log.d("PLAYERS",
											"host = "
													+ players[i].split("\"")[1]);
								}
							}
						} else {
							Log.d("PLAYERS", "this is not a game list request");
						}
					}

				}
			});
		}
	}
}