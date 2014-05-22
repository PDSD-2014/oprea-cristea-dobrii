package com.pdsd.application;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		Button signIn = (Button) findViewById(R.id.button3);
		signIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					SigninListener signinListener = new SigninListener();
					new Thread(signinListener).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return true;
	}
	
	public class SigninListener implements Runnable {
		
		public SigninListener() throws IOException {
			
		}
		
		@Override
		public void run() {
			final EditText usernameEditText = (EditText) findViewById(R.id.editText1);
			final EditText passwordEditText = (EditText) findViewById(R.id.editText2);
			
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();

			if (username.length() == 0 || password.length() == 0) {
				SignInActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usernameEditText.setText("");
						passwordEditText.setText("");
						Toast.makeText(getApplicationContext(), (String)Constants.LOGIN_ERROR, Toast.LENGTH_LONG).show();
					}	
				});
				
				return;
			}
			
			final ConnectionUtil connection = ConnectionUtil.getConnection();
			
			String data = Constants.signInRequest.replace("*", username).replace("%", password);
			connection.sendData(data);

			String result = connection.receiveData();
			Log.d("RESULT", result);
			if (result.equals(Constants.OK)) {
				HomeActivity.username = username;
				
				Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			}
			else if (result.equals(Constants.NOK)) {
				SignInActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usernameEditText.setText("");
						passwordEditText.setText("");
						Toast.makeText(getApplicationContext(), (String)Constants.LOGIN_ERROR, Toast.LENGTH_LONG).show();
					}	
				});
			} else {
				SignInActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usernameEditText.setText("");
						passwordEditText.setText("");
						Toast.makeText(getApplicationContext(), (String)Constants.UNKNOWN_ERROR, Toast.LENGTH_LONG).show();
					}	
				});
			}
		}
	}
}