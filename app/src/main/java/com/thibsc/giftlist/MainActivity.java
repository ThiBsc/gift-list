package com.thibsc.giftlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MAIN_ACTIVITY";

    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private TabLayout tabLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.mainViewPager);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), getBaseContext());
        viewPager.setAdapter(pageAdapter);

        tabLayout = findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(viewPager);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //Log.w(TAG, "DisplayName: " + account.getDisplayName() + " id: " + account.getId());
        //updateUI(account);
        if (account == null) {
            startSignInActivity();
        } else {
            if (mAuth.getCurrentUser() == null){
                // Here, google can't be null
                firebaseAuthWithGoogle(account);
            }
            //firebaseAuthWithGoogle(account);
        }
    }

    private void startSignInActivity(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuth.getCurrentUser() != null){
            mAuth.signOut();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        if (acct.isExpired()){
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "isExpired: " + task.getResult().isExpired());
                    }
                }
            });
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("CHECK FIREBASE_USER", user.getDisplayName());
                            //updateUI(user);
                            createUserDocument();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void createUserDocument(){
        // Create the user document to firebase
        // Due to the security rules in the firebase configuration, if the document exists, it does nothing
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Map<String, Object> user = new HashMap<>();
        user.put("display_name", firebaseUser.getDisplayName());
        user.put("creation", Calendar.getInstance().getTime().toString());

        db.collection("users").document(firebaseUser.getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                } else {
                    Log.d(TAG, "Error writing document!"+ task.getException().getMessage());
                }
            }
        });
    }
}
