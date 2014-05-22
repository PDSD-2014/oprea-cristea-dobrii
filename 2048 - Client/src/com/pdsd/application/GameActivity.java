package com.pdsd.application;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {

	private int[][] matrix = new int[4][4];
	private int direction;
	public boolean turn = false;
	ArrayList<TextView> tilesArray;
	public ConnectionUtil connection;
	public static String strMatrix = null;
	TextView tile0;
	TextView tile1;
	TextView tile2;
	TextView tile3;
	TextView tile4;
	TextView tile5;
	TextView tile6;
	TextView tile7;
	TextView tile8;
	TextView tile9;
	TextView tile10;
	TextView tile11;
	TextView tile12;
	TextView tile13;
	TextView tile14;
	TextView tile15;
	public boolean finished = false;
	TextView personalScoreTV;
	TextView totalScoreTV;
	ImageView turnImageView;

	int personalScore = 0;
	int totalScore = 0;
	int currentScore = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		configureView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	void configureView() {
		tilesArray = new ArrayList<TextView>();

		tile0 = (TextView) findViewById(R.id.textView0);
		tile1 = (TextView) findViewById(R.id.textView1);
		tile2 = (TextView) findViewById(R.id.textView2);
		tile3 = (TextView) findViewById(R.id.textView3);
		tile4 = (TextView) findViewById(R.id.textView4);
		tile5 = (TextView) findViewById(R.id.textView5);
		tile6 = (TextView) findViewById(R.id.textView6);
		tile7 = (TextView) findViewById(R.id.textView7);
		tile8 = (TextView) findViewById(R.id.textView8);
		tile9 = (TextView) findViewById(R.id.textView9);
		tile10 = (TextView) findViewById(R.id.textView10);
		tile11 = (TextView) findViewById(R.id.textView11);
		tile12 = (TextView) findViewById(R.id.textView12);
		tile13 = (TextView) findViewById(R.id.textView13);
		tile14 = (TextView) findViewById(R.id.textView14);
		tile15 = (TextView) findViewById(R.id.textView15);

		tile0.setText("");
		tile1.setText("");
		tile2.setText("");
		tile3.setText("");
		tile4.setText("");
		tile5.setText("");
		tile6.setText("");
		tile7.setText("");
		tile8.setText("");
		tile9.setText("");
		tile10.setText("");
		tile11.setText("");
		tile12.setText("");
		tile13.setText("");
		tile14.setText("");
		tile15.setText("");

		tilesArray.add(tile0);
		tilesArray.add(tile1);
		tilesArray.add(tile2);
		tilesArray.add(tile3);
		tilesArray.add(tile4);
		tilesArray.add(tile5);
		tilesArray.add(tile6);
		tilesArray.add(tile7);
		tilesArray.add(tile8);
		tilesArray.add(tile9);
		tilesArray.add(tile10);
		tilesArray.add(tile11);
		tilesArray.add(tile12);
		tilesArray.add(tile13);
		tilesArray.add(tile14);
		tilesArray.add(tile15);

		personalScoreTV = (TextView) findViewById(R.id.persScoreValue);
		totalScoreTV = (TextView) findViewById(R.id.totalScoreValue);

		personalScoreTV.setText("0");
		totalScoreTV.setText("0");

		turnImageView = (ImageView) findViewById(R.id.imageView);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		direction = -1;

		View view = getWindow().getDecorView().findViewById(
				android.R.id.content);

		view.setOnTouchListener(new OnSwipeTouchListener(this) {
			public void onSwipeTop() {
				direction = 0;
				move();
			}

			public void onSwipeRight() {
				direction = 1;
				move();
			}

			public void onSwipeBottom() {
				direction = 2;
				move();
			}

			public void onSwipeLeft() {
				direction = 3;
				move();
			}
		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				connection = ConnectionUtil.getConnection();
				String newString;
				Bundle extra = getIntent().getExtras();
				if (extra == null) {
					newString = null;
					Log.d("ERROR", "no extra");
				} else {
					newString = extra.getString("HOST");
				}
				if (newString.compareTo("1") == 0) {
					generateRandomTile();
					generateRandomTile();
					
					
					//////////////////
//					matrix[0][0] = 10;
//					matrix[0][1] = 11;
//					matrix[0][2] = 12;
//					matrix[0][3] = 13;
//					matrix[1][0] = 14;
//					matrix[1][1] = 15;
//					matrix[1][2] = 16;
//					matrix[1][3] = 17;
//					matrix[2][0] = 18;
//					matrix[2][1] = 19;
//					matrix[2][2] = 20;
//					matrix[2][3] = 21;
//					matrix[3][0] = 22;
					//////////////////
					
					writeMatrixOnBoard();
					sendData(stringifyMatrix() + "\"");
					turn = true;
					
					GameActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							turnImageView.setImageResource(android.R.drawable.presence_online);
						}
					});
					
					while (!finished)
					{
						destringifyMatrix(receiveData());
						
						writeMatrixOnBoard();
					}

				} else {
					GameActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							turnImageView.setImageResource(android.R.drawable.presence_busy);
						}
					});
					while (!finished)
					{
						destringifyMatrix(receiveData());
					
						writeMatrixOnBoard();
					}
				}
			}
		}).start();

	}
	
	public String stringifyMatrix() {
		String result = new String();
		result+="matrix?user=\""+HomeActivity.username+"\"&matrix=\"";
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				result += matrix[i][j] + "@";
			}
		
		result += currentScore+"@";
		totalScore+=currentScore;
		currentScore = 0;
		
		result += "pers=";
		
		result += personalScore+"@";
		
		updateScores();
		
		return result;
	}

	public void destringifyMatrix(String message) {

		String[] res = message.split("@");
		if (res.length >= 12) {
			int count = 1;
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++) {
					matrix[i][j] = Integer.parseInt(res[count]);
					count++;
				}
			
			int score = Integer.parseInt(res[count]);
			totalScore+=score;
			count++;
			count++;
			
			if (count == res.length - 1 && res[count].contains("Game over"))
			{
				finished = true;
				
				GameActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Game over!!!",
								Toast.LENGTH_LONG).show();
					}
				});
				Log.d("test", "S-A TERMINAT!!!");
			}
		}
		
		updateScores();

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    Log.d("BACK BUTTON", "back button pressed");
		new Thread(new Runnable() {
			@Override
			public void run() {
				
			}
		}).start();
	    
	}
	
	Log.d("test", "finished = " + finished);
	
	if (finished == true)
		return super.onKeyDown(keyCode, event);
	else
		return false;
	}
	public String receiveData() {
		String res = null;
		
		while (!finished)
		{
		 
		 
		 res = connection.receiveData();
		 Log.d("test", "receive data = " + res);
		 
		 if (res.contains("matrix"))
			break;
		 if (res.contains("Your turn"))
		 {
			turn = true;
			GameActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					turnImageView.setImageResource(android.R.drawable.presence_online);
				}
			});
			continue;
		 }
		}
		
		return res;
	}

	public void sendData(String message) {
		connection.sendData(message);
	}

	private void move() {

		boolean tilesMoved = false;
		if (turn == false)
			return ;
		switch (direction) {
		case (0):
			tilesMoved = moveUp();
			break;
		case (1):
			tilesMoved = moveRight();
			break;
		case (2):
			tilesMoved = moveDown();
			break;
		case (3):
			tilesMoved = moveLeft();
			break;
		default:
			break;
		}

		if (!tilesMoved)
			return;
 
		turn = false;
		GameActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				turnImageView.setImageResource(android.R.drawable.presence_busy);
			}
		});
		
		
		generateRandomTile();
		writeMatrixOnBoard();
		updateScores();
		
		
		
		if (checkGameOver()) {
			GameActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Game over!!!",
							Toast.LENGTH_LONG).show();
				}
			});
			finished = true;
			sendData(stringifyMatrix()+"Game over@" + "\"");
		}
		else
			sendData(stringifyMatrix() + "\"");
	}

	private boolean moveDown() {
		boolean isNextRow;
		boolean tilesMoved = false;

		int i, j;

		for (j = 0; j < 4; j++) {
			boolean[] united = new boolean[4];
			do {
				isNextRow = true;
				for (i = 3; i > 0; i--) {
					if ((matrix[i][j] == 0) && (matrix[i - 1][j] != 0)) {
						matrix[i][j] = matrix[i - 1][j];
						matrix[i - 1][j] = 0;
						isNextRow = false;
						tilesMoved = true;
					} else if ((matrix[i][j] != 0)
							&& (matrix[i][j] == matrix[i - 1][j]) && !united[i]
							&& !united[i - 1]) {
						matrix[i][j] += matrix[i - 1][j];
						personalScore += matrix[i][j];
						currentScore += matrix[i][j];
						matrix[i - 1][j] = 0;
						united[i] = true;
						isNextRow = false;
						tilesMoved = true;
					}
				}
			} while (!isNextRow);
		}

		return tilesMoved;
	}

	private boolean moveUp() {
		boolean isNextRow;
		boolean tilesMoved = false;

		int i, j;

		for (j = 0; j < 4; j++) {
			boolean[] united = new boolean[4];
			do {
				isNextRow = true;
				for (i = 0; i < 3; i++) {
					if ((matrix[i][j] == 0) && (matrix[i + 1][j] != 0)) {
						matrix[i][j] = matrix[i + 1][j];
						matrix[i + 1][j] = 0;
						isNextRow = false;
						tilesMoved = true;
					} else if ((matrix[i][j] != 0)
							&& (matrix[i][j] == matrix[i + 1][j]) && !united[i]
							&& !united[i + 1]) {
						matrix[i][j] += matrix[i + 1][j];
						personalScore += matrix[i][j];
						currentScore += matrix[i][j];
						matrix[i + 1][j] = 0;
						united[i] = true;
						isNextRow = false;
						tilesMoved = true;
					}
				}
			} while (!isNextRow);
		}

		return tilesMoved;
	}

	private boolean moveRight() {
		boolean isNextRow;
		boolean tilesMoved = false;

		int i, j;

		for (i = 0; i < 4; i++) {
			boolean[] united = new boolean[4];
			do {
				isNextRow = true;
				for (j = 3; j > 0; j--) {
					if ((matrix[i][j] == 0) && (matrix[i][j - 1] != 0)) {
						matrix[i][j] = matrix[i][j - 1];
						matrix[i][j - 1] = 0;
						isNextRow = false;
						tilesMoved = true;
					} else if ((matrix[i][j] != 0)
							&& (matrix[i][j] == matrix[i][j - 1]) && !united[j]
							&& !united[j - 1]) {
						matrix[i][j] += matrix[i][j - 1];
						personalScore += matrix[i][j];
						currentScore += matrix[i][j];
						matrix[i][j - 1] = 0;
						united[j] = true;
						isNextRow = false;
						tilesMoved = true;
					}
				}
			} while (!isNextRow);
		}

		return tilesMoved;
	}

	public boolean moveLeft() {
		boolean isNextRow;
		boolean tilesMoved = false;

		int i, j;

		for (i = 0; i < 4; i++) {
			boolean[] united = new boolean[4];
			do {
				isNextRow = true;
				for (j = 0; j < 3; j++) {
					if ((matrix[i][j] == 0) && (matrix[i][j + 1] != 0)) {
						matrix[i][j] = matrix[i][j + 1];
						matrix[i][j + 1] = 0;
						isNextRow = false;
						tilesMoved = true;
					} else if ((matrix[i][j] != 0)
							&& (matrix[i][j] == matrix[i][j + 1]) && !united[j]
							&& !united[j + 1]) {
						matrix[i][j] += matrix[i][j + 1];
						personalScore += matrix[i][j];
						currentScore += matrix[i][j];
						matrix[i][j + 1] = 0;
						united[j] = true;
						isNextRow = false;
						tilesMoved = true;
					}
				}
			} while (!isNextRow);
		}

		return tilesMoved;
	}

	private boolean checkGameOver() {
		int i, j;
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				if (matrix[i][j] == 0)
					return false;
				else if ((i != 0) && (matrix[i][j] == matrix[i - 1][j]))
					return false;
				else if ((i != 3) && (matrix[i][j] == matrix[i + 1][j]))
					return false;
				else if ((j != 0) && (matrix[i][j] == matrix[i][j - 1]))
					return false;
				else if ((j != 3) && (matrix[i][j] == matrix[i][j + 1]))
					return false;
			}
		}
		return true;
	}

	private void generateRandomTile() {
		boolean done = false;
		Random rand;
		while (!done) {
			rand = new Random();
			int i = (int) (rand.nextInt(10000) % 4);
			rand = new Random();
			int j = (int) (rand.nextInt(10000) % 4);

			if (matrix[i][j] == 0) {
				rand = new Random();
				int chance = rand.nextInt() % 5;

				if (chance == 0) {
					matrix[i][j] = 4;
				} else {
					matrix[i][j] = 2;
				}

				done = true;
			}
		}
	}

	private void writeMatrixOnBoard() {
		GameActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int i;
				for (i = 0; i < 16; i++) {
					TextView currentTV = (TextView) tilesArray.get(i);

					int n = i / 4;
					int m = i % 4;
					int value = matrix[n][m];

					if (value > 0) {
						String valueStr = "" + value;
						currentTV.setText(valueStr);
					} else {
						currentTV.setText("");
					}
				}
			}
		});
	}

	private void updateScores() {
		GameActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String persScoreStr = "" + personalScore;
				String totalScoreStr = "" + totalScore;

				personalScoreTV.setText(persScoreStr);
				totalScoreTV.setText(totalScoreStr);
			}
		});
	}
}