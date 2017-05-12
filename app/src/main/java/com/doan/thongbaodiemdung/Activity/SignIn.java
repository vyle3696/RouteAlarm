package com.doan.thongbaodiemdung.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Data.DatabaseHelper;
import com.doan.thongbaodiemdung.Data.FirebaseHandle;
import com.doan.thongbaodiemdung.Data.Route;
import com.doan.thongbaodiemdung.Other.Account;
import com.doan.thongbaodiemdung.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.doan.thongbaodiemdung.Other.Constants.FB_ACCOUNT;
import static com.doan.thongbaodiemdung.Other.Constants.FB_FRIENDS;
import static com.doan.thongbaodiemdung.Other.Constants.ID;

public class SignIn extends AppCompatActivity implements
        View.OnClickListener {

    public String userID;

    private static final String TAG = "FacebookLogin";

    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null)
        Debug("mAuth: ", mAuth.toString());
        InitFacebookLogin();

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void InitFacebookLogin()
    {
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_friends"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            // Đăng nhập vào facebook lần đầu tiên
            @Override
            public void onSuccess(LoginResult loginResult) {

                LoginButton loginButton1 = (LoginButton) findViewById(R.id.login_button);
                loginButton1.setVisibility(View.INVISIBLE);



                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                LoginFacebookHandle();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
        if (isLoggedIn()) {
            LoginButton loginButton1 = (LoginButton) findViewById(R.id.login_button);
            loginButton1.setVisibility(View.INVISIBLE);
            LoginFacebookHandle();
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginFacebookHandle();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void LoginFacebookHandle()
    {
        Toast.makeText(getBaseContext(), getResources().getText(R.string.login_success),Toast.LENGTH_LONG).show();
        UpdateDatabse();
    }

    private void UpdateDatabse()
    {
        UpdateAccountDatabase();
    }

    private AccessToken getAccessToken()
    {
        return AccessToken.getCurrentAccessToken();
    }

    private void Debug(String title, String text)
    {
        Log.e(title, text);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;

    }

    private void UpdateAccountDatabase()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getAccessToken().getUserId(),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        String name = "", id = "", avatarURL = "";
                        try {
                            name = jsonObject.getString("name");
                            id =jsonObject.getString("id");
                            userID = id;

                            if(mAuth.getCurrentUser() != null)
                                avatarURL = mAuth.getCurrentUser().getPhotoUrl().toString();

                            //update friend database
                            UpdateFriendsDatabase();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Account account = new Account(id, name, avatarURL);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        try {
                            ref.child(FB_ACCOUNT).child(id).child("name").setValue(name);

                            ref.child(FB_ACCOUNT).child(id).child("avatarURL").setValue(avatarURL);

                            savePreference(name, avatarURL);

                            FirebaseHandle.getInstance().setUserID(id);

                            //Khoi chay mainActivity
                            Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(mainIntent);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }
        ).executeAsync();
    }

    private void savePreference(String name, String avatarURL) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!name.equals(""))
            editor.putString("name", name);
        editor.putString("avatarURL", avatarURL);

        editor.apply();
    }

    private void UpdateFriendsDatabase()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getAccessToken().getUserId() + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        List<String> list = new ArrayList<String>();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        JSONArray array = new JSONArray();
                        try {
                            array = jsonObject.getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for(int i = 0 ; i < array.length() ; i++){
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                String name = obj.getString("name");
                                String id = obj.getString("id");
                                String avatarURL = "https" + "://graph.facebook.com/" + id + "/picture";
                                Account account = new Account(id, name, avatarURL);
                                if(mAuth.getCurrentUser() != null) {
                                    ref.child(FB_ACCOUNT).child(userID).child(FB_FRIENDS).child(id)
                                            .child(ID).setValue(id);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).executeAsync();
    }

    public static void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();


            }
        }).executeAsync();
    }
}


