package com.doan.thongbaodiemdung;

import com.doan.thongbaodiemdung.Account;
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
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.internal.*;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

public class SignIn extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private DatabaseReference mDatabase;
    private String graphPath;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);

        FacebookOncreate();

        if (isLoggedIn()) {
            LoginFacebookHandle();
            Debug("Facebook login logged in", "Successfully");
            Debug("Permission: ", getAccessToken().getPermissions().toString());
            JsonHandle();
        }
        InitGoogleSignin();

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };

    }

    private void InitGoogleSignin()
    {
        findViewById(R.id.google_signin_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.google_signin_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(firebaseAuthListener);

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //Google signin handle
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getBaseContext(),"Đăng nhập Google thành công",Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
            SignIn.this.startActivity(mainIntent);
        } else {
            if(result.getStatus().getStatusCode() == 12501)
                Toast.makeText(getBaseContext(),"Vui lòng kết nối mạng để đăng nhập",Toast.LENGTH_LONG).show();
        }
    }

    //Google Sign in
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //Check if the account was logged in in last time
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    //Get Facebook Access token
    public AccessToken getAccessToken()
    {
        return AccessToken.getCurrentAccessToken();
    }

    //Hàm kế thừa từ google connection class để bắt sự kiện click google button
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_signin_button:
                signIn();
                break;
        }
    }

    //Init facebook api
    private void FacebookOncreate()
    {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Debug("Facebook login new","Successfully");
                Debug("Permission: ", getAccessToken().getPermissions().toString());
                handleFacebookAccessToken(loginResult.getAccessToken());
                LoginFacebookHandle();

            }

            @Override
            public void onCancel() {
                Toast.makeText(SignIn.this, "Đã đăng xuất", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(SignIn.this, "Vui lòng kết nối mạng để đăng nhập", Toast.LENGTH_LONG).show();
            }
        });

        loginButton.setReadPermissions("user_friends");
        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };
        accessTokenTracker.startTracking();

    }


    //Get json content from graph facebook
    private void JsonHandle()
    {
//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/1861013520819747/friends_using_app",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//
//                        //System.out.println("Response::" + String.valueOf(response.getJSONObject()));
//                        Log.e("Json", String.valueOf(response.getJSONObject()));
//                    }
//                }
//        ).executeAsync();

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + getAccessToken().getUserId() + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Log.e("Json", String.valueOf(response.getJSONObject()));
                    }
                }
        ).executeAsync();

        /*GraphRequest request = GraphRequest.newMeRequest(
                getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Insert your code here
                        Log.e("Json", String.valueOf(response.getJSONObject()));
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,friends");
        request.setParameters(parameters);
        request.executeAsync();
*/

    }

    //Login facebook thành công
    private void LoginFacebookHandle()
    {
            Toast.makeText(getBaseContext(),"Đăng nhập Facebook thành công",Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
            SignIn.this.startActivity(mainIntent);
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
                            Toast.makeText(getBaseContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Push account to firebase
    private void PushDatabase(Account account)
    {

    }

    private void Debug(String title, String content)
    {
        Log.e(title, content);
    }
}

