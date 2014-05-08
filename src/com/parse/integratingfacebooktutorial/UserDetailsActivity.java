package com.parse.integratingfacebooktutorial;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseUser;

public class UserDetailsActivity extends Activity {

	private ProfilePictureView userProfilePictureView;
	private TextView userNameView;
	private TextView userLocationView;
	private TextView userGenderView;
	private TextView userDateOfBirthView;
	private TextView userRelationshipView;
	private Button logoutButton;
	
	List<String> PERMISSIONS = Arrays.asList(Permissions.Page.MANAGE_PAGES);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.userdetails);

		userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
		userNameView = (TextView) findViewById(R.id.userName);
		userLocationView = (TextView) findViewById(R.id.userLocation);
		userGenderView = (TextView) findViewById(R.id.userGender);
		userDateOfBirthView = (TextView) findViewById(R.id.userDateOfBirth);
		userRelationshipView = (TextView) findViewById(R.id.userRelationship);

		logoutButton = (Button) findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogoutButtonClicked();
			}
		});

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
//			makeMeRequest();
			getFacebookPages();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			// Check if the user is currently logged
			// and show any cached content
			updateViewsWithProfileInfo();
		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}

//	private void makeMeRequest() {
//		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
//				new Request.GraphUserCallback() {
//					@Override
//					public void onCompleted(GraphUser user, Response response) {
//						if (user != null) {
//							// Create a JSON object to hold the profile info
//							JSONObject userProfile = new JSONObject();
//							try {
//								// Populate the JSON object
//								userProfile.put("facebookId", user.getId());
//								userProfile.put("name", user.getName());
//								if (user.getLocation().getProperty("name") != null) {
//									userProfile.put("location", (String) user
//											.getLocation().getProperty("name"));
//								}
//								if (user.getProperty("gender") != null) {
//									userProfile.put("gender",
//											(String) user.getProperty("gender"));
//								}
//								if (user.getBirthday() != null) {
//									userProfile.put("birthday",
//											user.getBirthday());
//								}
//								if (user.getProperty("relationship_status") != null) {
//									userProfile
//											.put("relationship_status",
//													(String) user
//															.getProperty("relationship_status"));
//								}
//
//								// Save the user profile info in a user property
//								ParseUser currentUser = ParseUser.getCurrentUser();
////								currentUser.put("profile", userProfile);
//								currentUser.saveInBackground();
//
//								// Show the user info
//								updateViewsWithProfileInfo();
//							} catch (JSONException e) {
//								Log.d(IntegratingFacebookTutorialApplication.TAG,
//										"Error parsing returned user data.");
//							}
//
//						} else if (response.getError() != null) {
//							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
//									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
//								Log.d(IntegratingFacebookTutorialApplication.TAG,
//										"The facebook session was invalidated.");
//								onLogoutButtonClicked();
//							} else {
//								Log.d(IntegratingFacebookTutorialApplication.TAG,
//										"Some other error: "
//												+ response.getError()
//														.getErrorMessage());
//							}
//						}
//					}
//				});
//		request.executeAsync();
//
//	}

	private void updateViewsWithProfileInfo() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.getString("facebookId") != null) {
					String facebookId = userProfile.get("facebookId")
							.toString();
					userProfilePictureView.setProfileId(facebookId);
				} else {
					// Show the default, blank user profile picture
					userProfilePictureView.setProfileId(null);
				}
				if (userProfile.getString("name") != null) {
					userNameView.setText(userProfile.getString("name"));
				} else {
					userNameView.setText("");
				}
				if (userProfile.getString("location") != null) {
					userLocationView.setText(userProfile.getString("location"));
				} else {
					userLocationView.setText("");
				}
				if (userProfile.getString("gender") != null) {
					userGenderView.setText(userProfile.getString("gender"));
				} else {
					userGenderView.setText("");
				}
				if (userProfile.getString("birthday") != null) {
					userDateOfBirthView.setText(userProfile.getString("birthday"));
				} else {
					userDateOfBirthView.setText("");
				}
				if (userProfile.getString("relationship_status") != null) {
					userRelationshipView.setText(userProfile
							.getString("relationship_status"));
				} else {
					userRelationshipView.setText("");
				}
			} catch (JSONException e) {
				Log.d(IntegratingFacebookTutorialApplication.TAG,
						"Error parsing saved user data.");
			}

		}

	}

	private void onLogoutButtonClicked() {
		// to clear all session information.
		ParseFacebookUtils.getSession().closeAndClearTokenInformation();
		// Log the user out
		ParseUser.logOut();

		// Go to the login view
		startLoginActivity();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	private void getFacebookPages() {
		Session activeSession = Session.getActiveSession();
		if (activeSession != null && activeSession.isOpened()) {
			// making request in order to get user Facebook pages.
			if (activeSession.getPermissions().contains(Permissions.Page.MANAGE_PAGES)) {
				Request facebookPageRequest = Request.newGraphPathRequest(activeSession, "me", new Callback() {
							@Override
							public void onCompleted(Response response) {
								if (response != null) {
									// response of getting Facebook pages request.
									if (response.getGraphObject() != null && response.getError() == null) {
										GraphObject responseGraphObject = response.getGraphObject();
										// Display Facebook Pages response.
										if (responseGraphObject.getInnerJSONObject() != null) {
											Toast.makeText(UserDetailsActivity.this, "Response from facebook of Pages request:\n\n" +responseGraphObject.getInnerJSONObject(), Toast.LENGTH_SHORT).show();
//											facebookResponse.setText("Response from facebook of Pages request:\n\n" +responseGraphObject.getInnerJSONObject());
										} else {
											Toast.makeText(UserDetailsActivity.this, "Response from facebook of Pages request:\n\nUnable to get facebook pages", Toast.LENGTH_SHORT).show();
										}
									} else {
										Toast.makeText(UserDetailsActivity.this, "Response from facebook of Pages request:\n\nUnable to get facebook pages", Toast.LENGTH_SHORT).show();
									}
								}
							}
						});
				
				Bundle params = facebookPageRequest.getParameters();
				// adding parameter of limit to making request.
				params.putString("fields", "accounts");
				params.putString("limit", "100");
				// execute album request.
				facebookPageRequest.executeAsync();

			} else {
				requestPagePermission(activeSession);
			}
		}
		Session.getActiveSession();
	}

	
	private Session.StatusCallback callback = new Session.StatusCallback() {

	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        if (exception != null) {
	            new AlertDialog.Builder(UserDetailsActivity.this).setMessage(exception.getMessage())
	                    .setPositiveButton("OK", null).show();
	        } else {
	        	UserDetailsActivity.this.onSessionStateChange(session, state, exception);
	        }
	    }
	};
	
	

	private boolean pendingAnnounce;	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {

		 if (state.isOpened()) {
		            if (pendingAnnounce && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
		                // Session updated with new permissions so try publishing once more.
		                pendingAnnounce = false;
		                //call your method here to publish something on facebook 
		                getFacebookPages();
		            }
		        }
		    }

		private void requestPagePermission(Session session) {
		    if (session != null) {
		        pendingAnnounce = true;
		        
		        Session fbSession = ParseFacebookUtils.getSession(); 
		        Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
		        newPermissionsRequest.setRequestCode(111);
		        fbSession.addCallback(callback);
		        fbSession.requestNewPublishPermissions(newPermissionsRequest);
		        
//		        Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(UserDetailsActivity.this, PERMISSIONS);
//
//		       
//		        Session mSession = Session.openActiveSessionFromCache(UserDetailsActivity.this);
//		       
//		        mSession.requestNewPublishPermissions(newPermissionsRequest);
		    }
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);

		    switch (requestCode) {
		    case 111:
		        Session session = Session.getActiveSession();
		        if (session != null) {
		            session.onActivityResult(this, requestCode, resultCode, data);
		            ParseFacebookUtils.getSession().onActivityResult(this, requestCode, resultCode, data);
		        }
		        break;
		    }
		}

}
