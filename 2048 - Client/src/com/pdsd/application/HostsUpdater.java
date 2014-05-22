package com.pdsd.application;

import android.app.Activity;
import android.util.Log;

public class HostsUpdater extends Thread {
	public static boolean finished = false;
	public ConnectionUtil connection;
	public Activity caller;

	public HostsUpdater(ConnectionUtil connection, Activity caller) {
		this.connection = connection;
		this.caller = caller;
	}

	@Override
	public void run() {
		while (finished == false) {
			final String hostNamesRequest;
			if (connection != null)
				hostNamesRequest = connection.receiveData();
			else
				continue;

			caller.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if (hostNamesRequest == null) {
						Log.d("HOSTS", "response is null");

					} else {
						if (hostNamesRequest.contains("result=1&total_games")) {
							Log.d("test", "hontNamesRequest = "
									+ hostNamesRequest);
							StatisticsActivity.resultStatistics = new String(
									hostNamesRequest);
						}
						Log.d("HOSTS", hostNamesRequest);
						String[] activities = hostNamesRequest.split("=");
						if (hostNamesRequest
								.contains(Constants.GAMES_LIST_REQUEST)
								&& activities.length == 1) {
							JoinGameActivity.hostNames.clear();

						} else {

							if (activities[0]
									.equals(Constants.GAMES_LIST_REQUEST)) {
								synchronized (JoinGameActivity.hostNames) {
									JoinGameActivity.hostNames.clear();
									String[] hosts = activities[1].split("&");
									for (int i = 0; i < hosts.length; i++) {
										JoinGameActivity.hostNames.add(hosts[i]
												.split("\"")[1]);
										Log.d("HOSTS",
												"host = "
														+ hosts[i].split("\"")[1]);
									}
								}
							} else {
								Log.d("HOSTS",
										"this is not a game list request");
							}
						}
					}
				}
			});
		}
	}
}