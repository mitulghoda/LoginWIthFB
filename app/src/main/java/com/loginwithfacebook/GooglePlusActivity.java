package com.loginwithfacebook;

import android.app.Dialog;
import android.app.PendingIntent;
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
import android.widget.Toast;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by abc on 22-06-17.
 */
public class GooglePlusActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private String sGooglePlusID, sGooglePlusFirstName, sGooglePlusLastName, sGooglePlusEmail, sGooglePlusDateOfBirth, sGooglePlusCurrentLocation, sGooglePlusPersonPhotoUrl, sGooglePlusPersonProfileURL, sGooglePlusGender;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to communicate with Google
    private GoogleApiClient mGoogleApiClient;
    TextView txt;
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private int mSignInError;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main3);
        idMapping();
        mGoogleApiClient = initializeGoogleApiClient();


        setOnClick();
    }

    private void setOnClick() {
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mGoogleApiClient.isConnecting()) {
                   UIUtil.showDialog(GooglePlusActivity.this);
                    googlePlusLogin();
                }
            }
        });
    }

    private void idMapping() {
        txt = (TextView) findViewById(R.id.txt);

    }

    private GoogleApiClient initializeGoogleApiClient() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, new Plus.PlusOptions.Builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE);

        return builder.build();
    }

    private void googlePlusLogin() {
        //    UIUtil.log("signInWithGplus");
        if (!mGoogleApiClient.isConnecting()) {
            mSignInProgress = STATE_SIGN_IN;
        } else {
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }



    }



    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        mSignInProgress = STATE_DEFAULT;
       // UIUtil.dismissDialog();
        getProfileInformation();
        // Indicate that the sign in process is complete.

    }


    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {


                Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                    @Override
                    public void onResult(People.LoadPeopleResult loadPeopleResult) {
                        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {

                            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                            try {
                                int count = personBuffer.getCount();

                                Toast.makeText(GooglePlusActivity.this, "00 " + count, Toast.LENGTH_SHORT).show();



                                for (int i = 0; i < count; i++) {

                                    //     Toast.makeText(MainActivity.this, "Friends = " + "Person " + i + " name: " + personBuffer.get(i).getDisplayName() + " - id: " + personBuffer.get(i).getId(), Toast.LENGTH_SHORT).show();

                                    Log.e("Get All Info", "Person " + i + " name: " + personBuffer.get(i).getDisplayName() + " - id: " +
                                                    personBuffer.get(i).getId() + " All Detail" + " " + personBuffer.get(i).getUrl()

                                            // FullName,Friends ID,Friend URL

                                        /*    + " " + personBuffer.get(i).getAboutMe()
                                            + " " + personBuffer.get(i).getBirthday()
                                            + " " + personBuffer.get(i).getBraggingRights()
                                            + " " + personBuffer.get(i).getUrl()
                                            + " " + personBuffer.get(i).getAgeRange()
                                            + " " + personBuffer.get(i).getNickname()
                                            + " " + personBuffer.get(i).getGender()
                                            + " " + personBuffer.get(i).getImage()
                                            + " " + personBuffer.get(i).getName()
                                            + " " + personBuffer.get(i).getCurrentLocation()*/);
                                }
                            } finally {

                                personBuffer.close();

                                //       Toast.makeText(MainActivity.this, "02 ", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Log.e("Get", "Error");

                            //       Toast.makeText(MainActivity.this, "03 ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                UIUtil.log("Person Detail" + currentPerson.toString());
                sGooglePlusLastName = currentPerson.getName().getFamilyName();
               // UIUtil.log("Google Plus Last Name" + sGooglePlusLastName);
                sGooglePlusFirstName = currentPerson.getName().getGivenName();
               UIUtil.log("Google Plus First Name" + sGooglePlusFirstName);
              //  Constants.SOCIAL_NAME = sGooglePlusFirstName + " " + sGooglePlusLastName;
                if (currentPerson.getGender() == 0) {
                    sGooglePlusGender = "Male";
                } else if (currentPerson.getGender() == 1) {
                    sGooglePlusGender = "Female";
                } else {
                    sGooglePlusGender = "Other";
                }
               UIUtil.log("Google Plus Gender" + sGooglePlusGender);
                sGooglePlusDateOfBirth = currentPerson.getBirthday();
                UIUtil.log("Google Plus Date of Birth" + sGooglePlusDateOfBirth);
                sGooglePlusCurrentLocation = currentPerson.getCurrentLocation();
                UIUtil.log("Google Plus Current Location" + sGooglePlusCurrentLocation);
                sGooglePlusID = currentPerson.getId();
             //   Constants.SOCIAL_GOOGLE_ID = sGooglePlusID;
                UIUtil.log("Google Plus ID" + sGooglePlusID);
                sGooglePlusPersonPhotoUrl = currentPerson.getImage().getUrl();
               /// UIUtil.log("Google Plus Person Photo URL" + sGooglePlusPersonPhotoUrl);
                sGooglePlusPersonProfileURL = currentPerson.getUrl();
           ///     UIUtil.log("Google Plus Profile URL" + sGooglePlusPersonProfileURL);
                sGooglePlusEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
              //  Constants.SOCIAL_EMAIL = sGooglePlusEmail;
            //    UIUtil.log("Google Plus Email" + sGooglePlusEmail);
                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                sGooglePlusPersonPhotoUrl = sGooglePlusPersonPhotoUrl.substring(0,
                        sGooglePlusPersonPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;
               UIUtil.log("Google Plus Person Photo URL" + sGooglePlusPersonPhotoUrl);

//                userGooglePlusRegistration();
              //  btn_googlePlus.setText("Log Out");

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
          UIUtil.log("onConnectionSuspended");
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
      //  UIUtil.log("onConnectionFailed: ConnectionResult.getErrorCode() = "
        //        + connectionResult.getErrorCode()));

        UIUtil.log("onConnectionFailed");
        UIUtil.dismissDialog();
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
           // UIUtil.log("API Unavailable.");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.

            // Store the ConnectionResult for later usage
            mSignInIntent = connectionResult.getResolution();
            mSignInError = connectionResult.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.

                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }


    private void resolveSignInError() {

    }

}


