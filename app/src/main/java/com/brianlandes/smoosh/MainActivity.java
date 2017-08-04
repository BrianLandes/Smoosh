package com.brianlandes.smoosh;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brianlandes.smoosh.fragments.MainScreenFragment;
import com.brianlandes.smoosh.fragments.edit_profile.EditProfileFragment;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 123;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            ContinueToMainScreen();
        } else {
            // not signed in
            SignIn( null );
        }
    }

    public void SignIn( View view ) {
        startActivityForResult( // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public void ContinueToMainScreen() {
//        Intent intent = new Intent( this, MainScreenActivity.class );
//        startActivity( intent );

        MainScreenFragment mainScreenFragment = new MainScreenFragment();

        fragmentManager.beginTransaction()
                .add( R.id.main_container, mainScreenFragment )
                .commit();

        // TODO: on initial registration take them to the edit profile page

        SmoosherUtils.LoadCurrent();

//        finish();
    }

    public void ClickEditProfile(View view) {
        EditProfileFragment editScreenFragment = new EditProfileFragment();

        fragmentManager.beginTransaction()
                .replace( R.id.main_container, editScreenFragment )
                .addToBackStack(null)
                .commit();

        Log.d(TAG, "ClickEditProfile" );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(intent);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                ContinueToMainScreen();
                return;
            } else {
                // Sign in failed
                // TODO: take them to a different screen so they can retry signing in
                // TODO: replace the snackbars with DynamicToasts
                if (response == null) {
                    // User pressed back button
                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
        }
    }

    public void showSnackbar( int message ) {
        Snackbar.make( findViewById( R.id.main_container ), message, Snackbar.LENGTH_LONG).show();
    }
}
