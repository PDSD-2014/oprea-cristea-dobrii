package com.pdsd.application;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class StatisticsActivity extends Activity {
	public ConnectionUtil connection;
	public static String resultStatistics = null;
	public String getTotalGames(String message)
	{
		int count = message.indexOf("total_games=\"");
		int strLength = new String("total_games=\"").length();
		String totalGames = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return totalGames;
	}
	public String getHighScore(String message)
	{
		int count = message.indexOf("high_score=\"");
		int strLength = new String("high_score=\"").length();
		String highScore = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return highScore;
	}
	public String getTotalScore(String message)
	{
		int count = message.indexOf("total_score=\"");
		int strLength = new String("total_score=\"").length();
		String totalScore = message.substring(count+strLength,message.indexOf('"', count+strLength+1));
		return totalScore;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				String data = Constants.getStatisticsRequest.replace("*", HomeActivity.username);
				connection.sendData(data);
				while (true)
				{
					String answer = connection.receiveData();
					
					if (answer == null)
					{
						continue;
					}
					
					Log.d("test", "STATISTICS: " + answer);
					if (resultStatistics != null)
						answer = resultStatistics;
					
					Log.d("test", "answer = " + answer);
					
					if (answer.contains("result=1&total_games") )
					{
						final String total_games = getTotalGames(answer);
						final String high_score = getHighScore(answer);
						final String total_score = getTotalScore(answer);
						
						Log.d("test", "STATICICS back" + total_games + total_score + high_score);
						
						StatisticsActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Log.d("test", "STATICICS main" + total_games + total_score + high_score);
								TextView totalGamesView = (TextView) findViewById(R.id.textViewTotalGames);
								TextView highScoreView = (TextView) findViewById(R.id.textViewHighScore);
								TextView totalScoreView = (TextView) findViewById(R.id.textViewTotalScore);
								totalGamesView.setText("Total games: " + total_games);
								highScoreView.setText("High score: " + high_score);
								totalScoreView.setText("Total score: " + total_score);
								resultStatistics = null;
							}
						});
						break;
					}
				}
			}}).start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics, menu);
		return true;
	}
}