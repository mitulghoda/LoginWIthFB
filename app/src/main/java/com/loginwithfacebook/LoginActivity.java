package com.loginwithfacebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


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
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import retrofit.RetrofitError;
import retrofit.client.Response;



public class LoginActivity extends  AppCompatActivity {


    TextView text_register, text_forgot_password, text_login;
    EditText edit_username, edit_password, edit_password_forgot;

    TextView login_twitter;

    private RadioGroup radio_login_type;
    private RadioButton radioIndividual, radioCompany;

    AlertDialog alertDialog;

    String fb_first_name, fb_last_name, fb_email, fbId, sFacebookProfilePicture;
    String google_email, google_firstname, google_id;
    private LinearLayout ll_fb;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    private TwitterAuthClient Twitterclient;
    //    private TwitterSession session;
    private TwitterLoginButton twitter_login_button;
    private String emailTwitter, idTwitter, nameTwitter, contactTwitter;



    private static final String TWITTER_KEY = "zjbTAX4faiIxbDv7dzowKrW5r";
    private static final String TWITTER_SECRET = "S9Wjlc65HRBrKyRasUEX7xYiT8uPmscxdlONaq7o5MwvzKJgtf";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;


    private ProgressDialog mProgressDialog;

    private String forgot_Email = "", device_id = "", refreshedToken = "";
    public int login_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Twitter Intialize
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        setContentView(R.layout.activity_main2);




        Twitterclient = new TwitterAuthClient();
//        session = Twitter.getSessionManager().getActiveSession();
        twitter_login_button = (TwitterLoginButton) findViewById(R.id.twitter_login_button);


        twitter_login_button.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
               /* Toast.makeText(LoginActivity.this, "SUC " + result, Toast.LENGTH_SHORT).show();*/
                //If login succeeds passing the Calling the login method and passing Result object
                TwitterSession session = result.data;
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(session, new Callback<String>() {
                    @Override
                    public void success(Result<String> result1) {
                        Toast.makeText(getApplicationContext(), result1.data.toString(), Toast.LENGTH_LONG).show();
                        emailTwitter = result1.data.toString();
                        login(result);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                        Log.e("EXCEPTION", "" + exception);
                    }
                });
                AccountService ac = Twitter.getApiClient(result.data).getAccountService();
                Log.e("AC", "" + ac);
                TwitterApiClient client = Twitter.getApiClient(result.data);
                Log.e("AC", "" + client);


//                session.getEmail().

            }


            @Override
            public void failure(TwitterException exception) {
//                Toast.makeText(getApplicationContext(), "Twitter Login Failed, Please try again", Toast.LENGTH_SHORT).show();

            }
        });

        //----------------------------------------------------------------------------------


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        //-------------------------------------------------------------------------------
        //************************   facebook LOGIN INTEGRATION
        //-------------------------------------------------------------------------------


        // Facebook Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());

        //  create a callback manager to handle login responses
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        // set up Profile Tracker for Profile Change and Set up Token Tracker for Access Token Changes.


        mTokenTracker.startTracking();
        mProfileTracker.startTracking();








        idMapping();



        setClick();


        // HIDE KEYBOARD


    }





    //-------------------------------------------------------------------------------
    //************************   ID Declaration
    //-------------------------------------------------------------------------------

    private void idMapping() {




        login_twitter = (TextView) findViewById(R.id.txt);




    }


    //-------------------------------------------------------------------------------
    //************************   ITEM CLICK DECLARATION
    //-------------------------------------------------------------------------------


    private void setClick() {
        // Twitter Login
        login_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                twitter_login_button.performClick();

            }
        });


    }



    //-------------------------------------------------------------------------------
    //************************   ON ACTIVITY RESULT CALLBACK
    //-------------------------------------------------------------------------------

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                Twitterclient.onActivityResult(requestCode, resultCode, data);




    }


    //-------------------------------------------------------------------------------
    //************************   TWITTER GET DATA
    //-------------------------------------------------------------------------------



    //The login function accepting the result object
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

                        //Creating an Intent

                    }
                });

    }


    //-------------------------------------------------------------------------------
    //************************   TWITTER LOGIN API CALL
    //-------------------------------------------------------------------------------




    private Map<String, String> getTwitterLoginDetail() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "registerUser");
        map.put("userId", "");
        map.put("provider", "twitter");
        map.put("provider_id", idTwitter);
        map.put("first_name", "" + nameTwitter);
        map.put("last_name", "");
        map.put("email", "" + emailTwitter);
        map.put("mobile_number", "");
        map.put("token", refreshedToken);
        map.put("device_id", device_id);
        Log.e("map", "Twitter Login" + map);
        return map;
    }


    //-------------------------------------------------------------------------------
    //************************   GOOGLE LOGIN CODE
    //-------------------------------------------------------------------------------








    private Map<String, String> getGoogleLoginDetail() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "registerUser");
        map.put("userId", "");
        map.put("provider", "google");
        map.put("provider_id", google_id);
        map.put("first_name", "" + google_firstname);
        map.put("last_name", "");
        map.put("email", "" + google_email);
        map.put("mobile_number", "");
        map.put("token", refreshedToken);
        map.put("device_id", device_id);
        Log.e("map", "G Login" + map);
        return map;
    }



    //-------------------------------------------------------------------------------
    //************************   FB LOGIN CODE
    //-------------------------------------------------------------------------------







    //-------------------------------------------------------------------------------------


    ///  -----------------------------------------------------------------------------------
    // ********************************  SHOW PROGRESS
    //-------------------------------------------------------------------------------------


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    ///  -----------------------------------------------------------------------------------
    // ********************************  COMPANY LOGIN API CALL
    //-------------------------------------------------------------------------------------

    private Map<String, String> getCompayLoginDetail() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "login");
        map.put("email", "" + edit_username.getText());
        map.put("user_type", "3");
        map.put("password", "" + edit_password.getText());
        map.put("token", refreshedToken);
        map.put("device_id", device_id);
        Log.e("map", "COMPANY LOGIN" + map);
        return map;
    }


    ///  -----------------------------------------------------------------------------------
    // ********************************  INDIVIDIAL LOGIN API CALL
    //-------------------------------------------------------------------------------------



    private Map<String, String> getLoginDetail() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "login");
        map.put("email", "" + edit_username.getText());
        map.put("user_type", "1");
        map.put("password", "" + edit_password.getText());
        map.put("token", refreshedToken);
        map.put("device_id", device_id);
        Log.e("map", "INDIVIDUAL LOGIN " + map);
        return map;
    }


    //-------------------------------------------------------------------------------------
    //***********************   FORGOT PASSWORD API CALL *********************************
    //-------------------------------------------------------------------------------------




    private Map<String, String> getForgotPassDetails() {
        Map<String, String> map = new HashMap<>();
        map.put("action", "forgetPassword");
        map.put("email", "" + forgot_Email);
        Log.e("map", "" + map);
        return map;
    }

}
// -----------------------------  END (FORGOT PASSWORD API CALL) -------------------------------------------------