package com.loginwithfacebook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by abc on 22-06-17.
 */
public class FacebookActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private static final String FACEBOOK = "facebook";
    public String[] separated;
    String first_name,last_name,email,fbId,isfb,gender,dob,userType,sFacebookProfilePicture;
    TextView  txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        idMapping();
        printKeyHash();
        FacebookSdk.sdkInitialize(getApplicationContext());


        //  create a callback manager to handle login responses
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        // set up Profile Tracker for Profile Change and Set up Token Tracker for Access Token Changes.
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
        setOnClick();
    }

    private void setOnClick() {
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                } else {
                    facebookLogin();
                }

            }
        });
    }

    private void idMapping() {
        txt = (TextView)findViewById(R.id.txt);

    }
    public void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    FacebookActivity.this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_location"));
        LoginManager.getInstance().registerCallback(mCallbackManager, mFacebookCallback);

    }
    private void setupTokenTracker() {

//         To get the list of permissions associated with the current access token, call:
//         AccessToken.getCurrentAccessToken().getPermissions();
//
//        To get the list of declined permissions, call:
//        AccessToken.getCurrentAccessToken().getDeclinedPermissions();
//
//        For More Detail on Permission
//        https://developers.facebook.com/docs/facebook-login/android/permissions


        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i(FACEBOOK, "Profile Access Token Change " + currentAccessToken);


            }
        };
    }

    // track changes in the Current Profile.
    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.i(FACEBOOK, "Profile Tracker profile changed" + currentProfile);
                if (currentProfile != null) {
                    Log.i(FACEBOOK, "Profile" + currentProfile);
                    /*try {
                        if(currentProfile.getProfilePictureUri(256, 256)!=null) {
                            Uri uri=currentProfile.getProfilePictureUri(300, 300);
                            Define.fb_picture=uri.toString();
                            Log.i(FACEBOOK, "Profile Tracker FB Profile Picture" + Define.fb_picture);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Profile profile = Profile.getCurrentProfile();
        Log.i(FACEBOOK, "Profile Resume " + profile);

    }

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i(FACEBOOK, "profile onSuccess");

            // If login succeeds, the LoginResult parameter has the new AccessToken, and the most recently granted or declined permissions.
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                Log.i(FACEBOOK, "Profile Json Response" + object);
                                if (!object.getString("id").equals("")) {
                                    fbId = object.getString("id");
                                    Log.i(FACEBOOK, "Profile Facebook ID " + fbId);
                                }
                                if (object.getString("name") != "") {
                                    first_name = object.getString("name");
                                    Log.i(FACEBOOK, "Profile Name " + first_name);
                                }
                                if (object.getString("first_name") != "") {
                                    first_name = object.getString("first_name");
                                    Log.i(FACEBOOK, "Profile First Name " + first_name);
                                }
                                if (object.getString("last_name") != "") {
                                    last_name = object.getString("last_name");
                                    Log.i(FACEBOOK, "Profile Last Name " + last_name);
                                }
                                if (object.getString("email") != "") {
                                    email = object.getString("email");
                                    Log.i(FACEBOOK, "email " + last_name);
                                }
                                if (object.getString("gender") != "") {
                                    gender = object.getString("gender");
                                    Log.i(FACEBOOK, "Profile Gender " + gender);
                                }
                                if (object.getString("birthday") != "") {
                                    dob = object.getString("birthday");
                                    Log.i(FACEBOOK, "Profile dob " + dob);
                                }


                                try {
                                    if (object.getJSONObject("picture").getJSONObject("data").getString("url") != "") {
                                        sFacebookProfilePicture = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        Log.i(FACEBOOK, "Profile Picture URL in Graph : " + sFacebookProfilePicture);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


//                                try {
//                                    if (object.getString("email") != "") {
//                                        email = object.getString("email");
//                                        Log.i(FACEBOOK, "Profile in onSuccess " + email);
//                                    } else {
//                                        email = "";
//                                        Log.i(FACEBOOK, "Profile in onSuccess " + email);
//
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                try {
//                                    if (object.getJSONObject("picture").getJSONObject("data").getString("url") != "") {
//                                        sFacebookProfilePicture = object.getJSONObject("picture").getJSONObject("data").getString("url");
//                                        Log.i(FACEBOOK, "Profile Picture URL in Graph : " + sFacebookProfilePicture);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }

//                                try {
//                                    if (object.getJSONObject("location").getString("name") != "") {
//                                        sFacebookUserCity = object.getJSONObject("location").getString("name");
//                                        Log.i(FACEBOOK, "Profile City " + sFacebookUserCity);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }


                                // edt_mobile_number_email.setText(fb_gp_email);

                                if (separated != null) {
                                    if (separated[0] != null) {
                                        //  edt_password.setText(separated[0]);

                                    }
                                }
                                //callLogintAPI();
                                //callSignupAPI();


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();

                            }



                          //  goTOSignup();
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,name,email,gender,location,picture.width(256).height(256)");
            request.setParameters(parameters);
            request.executeAsync();



        }

        @Override
        public void onCancel() {
            Log.i(FACEBOOK, "Profile onCancel");

        }

        @Override
        public void onError(FacebookException e) {
            Log.i(FACEBOOK, "Profile onError " + e);


        }
    };
}
