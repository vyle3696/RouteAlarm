package com.doan.thongbaodiemdung;

import com.doan.thongbaodiemdung.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.doan.thongbaodiemdung.Activity.MainActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
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
import java.util.List;

import static com.doan.thongbaodiemdung.Constants.FB_ACCOUNT;
import static com.doan.thongbaodiemdung.Constants.FB_FRIENDS;

public class SignIn extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "FacebookLogin";

    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

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
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            // Đăng nhập vào facebook lần đầu tiên
            @Override
            public void onSuccess(LoginResult loginResult) {
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
        if (isLoggedIn())
            LoginFacebookHandle();

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
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    private void LoginFacebookHandle()
    {
        Toast.makeText(getBaseContext(),"Đăng nhập Facebook thành công",Toast.LENGTH_LONG).show();
        TestFunction();
        Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
        SignIn.this.startActivity(mainIntent);
    }

    private void TestFunction()
    {
        UpdateAccountDatabase();
        UpdateFriendsDatabase();
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
                            avatarURL = mAuth.getCurrentUser().getPhotoUrl().toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Account account = new Account(id, name,avatarURL);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(FB_ACCOUNT).child(mAuth.getCurrentUser().getUid()).setValue(account);

                        Debug("Up account to db", "Successful");
                    }
                }
        ).executeAsync();
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
                                String avatarURL = "https" + "://graph.facebook.com/" + id + "/picture?width=64&height=64";
                                Account account = new Account(id, name, avatarURL);
                                ref.child(FB_FRIENDS).child(mAuth.getCurrentUser().getUid()).child(String.valueOf(i)).setValue(account);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Debug("Up friends to db", "Successful");
                    }
                }
        ).executeAsync();
    }
}


