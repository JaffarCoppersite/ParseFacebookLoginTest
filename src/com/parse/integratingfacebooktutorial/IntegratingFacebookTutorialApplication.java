package com.parse.integratingfacebooktutorial;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class IntegratingFacebookTutorialApplication extends Application {

	static final String TAG = "MyApp";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "Qyipo2ilSw6uELDrgQUtyQt81BwsD0cu0yUxocYG", "MNAoGkXhTHsThMV7M3e8nlmMjAK5QYQO8Yeje6Im");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));
		
		ParseInstallation.getCurrentInstallation().saveInBackground();

	}

}
