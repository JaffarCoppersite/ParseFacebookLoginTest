package com.parse.integratingfacebooktutorial;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	private Button loginButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			showUserDetailsActivity();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("public_profile","user_friends");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					if(err != null) {
						Toast.makeText(LoginActivity.this, "Error code: "+ err.getCode()+"\nError msg: "+err.getMessage(), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(LoginActivity.this, "User returned from parse is null.", Toast.LENGTH_SHORT).show();
					}
					
					
					Log.d(IntegratingFacebookTutorialApplication.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(IntegratingFacebookTutorialApplication.TAG,
							"User signed up and logged in through Facebook!");
					if(user.getObjectId() == null) {
						Toast.makeText(LoginActivity.this, "User object id is null", Toast.LENGTH_SHORT).show();
						return;
					}
					showUserDetailsActivity();
				} else {
					if(user.getObjectId() == null) {
						Toast.makeText(LoginActivity.this, "User object id is null", Toast.LENGTH_SHORT).show();
						return;
					}
					showUserDetailsActivity();
				}
			}
		});
	}

	private void showUserDetailsActivity() {
		Intent intent = new Intent(this, UserDetailsActivity.class);
		startActivity(intent);
	}
}
