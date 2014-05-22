package com.pdsd.application;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity {
	public static List<String> hostNames = new LinkedList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		Button signUp = (Button) findViewById(R.id.button4);
		signUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					SignupListener signupListener = new SignupListener();
					new Thread(signupListener).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}
	
	public class SignupListener implements Runnable {
		
		public SignupListener() throws IOException {
			
		}
		
		@Override
		public void run() {
			final EditText usernameEditText = (EditText) findViewById(R.id.editText3);
			final EditText passwordEditText = (EditText) findViewById(R.id.editText4);
			final EditText confirmPasswordEditText = (EditText) findViewById(R.id.editText5);
			
			String username = usernameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String confirmPassword = confirmPasswordEditText.getText().toString();

			if (username.length() == 0 || password.length() == 0 || 
					confirmPassword.length() == 0 || !password.equals(confirmPassword)) {
				SignUpActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usernameEditText.setText("");
						passwordEditText.setText("");
						confirmPasswordEditText.setText("");
						Toast.makeText(getApplicationContext(), (String)Constants.SIGNUP_ERROR, Toast.LENGTH_LONG).show();
					}	
				});
				
				return;
			} 

			final ConnectionUtil connection = ConnectionUtil.getConnection();
			
			String data = Constants.signUpRequest.replace("*", username).replace("%", password);
			connection.sendData(data);

			String result = connection.receiveData();
			
			if (result.equals(Constants.OK)) {
				HomeActivity.username = username;
				
				Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			}
			else if (result.equals(Constants.NOK)) {
				SignUpActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usernameEditText.setText("");
						passwordEditText.setText("");
						confirmPasswordEditText.setText("");
						Toast.makeText(getApplicationContext(), (String)Constants.SIGNUP_ERROR, Toast.LENGTH_LONG).show();
					}	
				});
			} else {
				SignUpActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						usernameEditText.setText("");
						passwordEditText.setText("");
						confirmPasswordEditText.setText("");
						Toast.makeText(getApplicationContext(), (String)Constants.UNKNOWN_ERROR, Toast.LENGTH_LONG).show();
					}	
				});
			}
		}
	}
}