package com.loginwithfacebook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import io.fabric.sdk.android.Fabric;

/**
 * Created by abc on 22-06-17.
 */
public class TwiiterActivity extends AppCompatActivity {
    //This is your KEY and SECRET
    //And it would be added automatically while the configuration


    //Tags to send the username and image url to next activity using intent
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE_URL = "image_url";

    //Twitter Login Button
    TwitterLoginButton twitterLoginButton;
    private static final String TWITTER_KEY = " anYJqPFcsNhUkPSyhpusQOcJr";
    private static final String TWITTER_SECRET = "qQZITj3JsKdRIYYYpNemkcnlRy5Vcci68YWAqyM8vPNqCHAb5T";

    // Twiiter Declaration
    private long sTwiiterId;
    private String sTwitterUserName, sTwitterEmail, sTwiiterFullName, sTwitterProfileImage, sTwitterLocation;
    private int iTwitterFriends, GetDetail;
    private TwitterSession twitterSession;
    private TwitterAuthClient authClient;
    TextView  txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig));

        setContentView(R.layout.activity_main2);

        idMapping();
        authClient = new TwitterAuthClient();

        if (Twitter.getSessionManager().getActiveSession() != null) {
            //  btn_twitter.setText("Log Out");
        } else {
            //  btn_twitter.setText("Log In");
        }

        setOnClick();
    }

    private void setOnClick() {

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                //If login succeeds passing the Calling the login method and passing Result object
                login(result);
            }

            @Override
            public void failure(TwitterException exception) {
                //If failure occurs while login handle it here
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Twitter.getSessionManager().getActiveSession() != null) {
                    twitterLogOut(TwiiterActivity.this);
                  //  UIUtil.toast(context, "You are successfully logged out .");
                    //            dialog.dismiss();
                } else {
                    authClient.authorize(TwiiterActivity.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                        @Override
                        public void success(Result<TwitterSession> result) {
                      //      UIUtil.log("Response" + result);
                            sTwitterUserName = result.data.getUserName();
                            Log.e("Twitter User Name ","" + sTwitterUserName);
                          //  Constants.SOCIAL_NAME = sTwitterUserName;
                            sTwiiterId = result.data.getUserId();
                            Log.e("Twitter User ID " ,""+ sTwiiterId);
                          //  Constants.SOCIAL_TWITTER_ID = String.valueOf(sTwiiterId);

                            twitterSession = Twitter.getSessionManager().getActiveSession();
                            TwitterAuthToken authToken = twitterSession.getAuthToken();
                            String token = authToken.token;
                            String secret = authToken.secret;
                            sTwiiterId = twitterSession.getId();
                       //     UIUtil.log("Twitter User Session ID Check " + sTwiiterId);

                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                            twitterApiClient.getAccountService().verifyCredentials(false, false, new com.twitter.sdk.android.core.Callback<User>() {
                                @Override
                                public void success(Result<User> result) {
                                    sTwiiterFullName = result.data.name;
                                 //   UIUtil.log("Twitter User Full Name " + sTwiiterFullName);
                                    sTwitterProfileImage = result.data.profileImageUrl;
                                 //   UIUtil.log("Twitter User Profile Image " + sTwitterProfileImage);
                                    sTwitterLocation = result.data.location;
                                //    UIUtil.log("Twitter User Location " + sTwitterLocation);
                                    sTwitterEmail = result.data.email;
                                 //   UIUtil.log("Twitter User Email " + sTwitterEmail);
                                    iTwitterFriends = result.data.friendsCount;
                                 //   UIUtil.log("Twitter User Friends " + iTwitterFriends);

                                    GetDetail = result.data.followersCount;
                                 //   UIUtil.log("Twitter GetDetail " + GetDetail);



                                }

                                @Override
                                public void failure(TwitterException e) {

                                }
                            });
                            //           getUserEmail();
//                                userTwitterRegistration();
                            //            btn_twitter.setText("Log out");
                        }

                        @Override
                        public void failure(TwitterException e) {
                         //   UIUtil.log("Twiiter Exception" + e.getMessage());
                        }
                    });
                }



            }
        });
    }

    private void idMapping() {

        txt = (TextView)findViewById(R.id.txt);


    }
    public void twitterLogOut(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
        //  btn_twitter.setText("Log In");
    }

    public void login(Result<TwitterSession> result) {

//Creating a twitter session with result's data
        TwitterSession session = result.data;

        //Getting the username from session
        final String username = session.getUserName();

        //This code will fetch the profile image URL
        //Getting the account service of the user logged in
        Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void failure(TwitterException e) {
                        //If any error occurs handle it here
                    }

                    @Override
                    public void success(Result<User> userResult) {
                        //If it succeeds creating a User object from userResult.data
                        User user = userResult.data;

                        //Getting the profile image url
                        String profileImage = user.profileImageUrl.replace("_normal", "");

                        Log.d("done","name-->"+username + "url-->"+profileImage);
                        // Toast.makeText(this,"name-->"+username + "url-->"+profileImage,Toast.LENGTH_LONG).show();

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Adding the login result back to the button
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }
}
