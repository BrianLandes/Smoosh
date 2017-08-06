package com.brianlandes.smoosh;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.brianlandes.smoosh.fragments.MainScreenFragment;
import com.brianlandes.smoosh.fragments.ViewUserFragment;
import com.brianlandes.smoosh.fragments.edit_profile.EditPhotosFragment;
import com.brianlandes.smoosh.fragments.edit_profile.EditUserTabbedFragment;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.github.florent37.awesomebar.AwesomeBar;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 123;

    FragmentManager fragmentManager;

    @BindView(R.id.bar)AwesomeBar bar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        AssetUtils.init(this);
        if ( savedInstanceState==null ) {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // already signed in
                ContinueToMainScreen();
            } else {
                // not signed in
                SignIn(null);
            }

            bar.setOnMenuClickedListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.START);
                }
            });

            bar.addOverflowItem( "Edit Profile" );

            bar.displayHomeAsUpEnabled(false);
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
//        EditPhotosFragment editScreenFragment = new EditPhotosFragment();
        EditUserTabbedFragment editScreenFragment = new EditUserTabbedFragment();
        fragmentManager.beginTransaction()
                .replace( R.id.main_container, editScreenFragment )
                .addToBackStack(null)
                .commit();
    }

    public void ClickViewProfile(View view) {
        ViewUserFragment fragment = ViewUserFragment.with(AssetUtils.currentUser);
        fragmentManager.beginTransaction()
                .replace( R.id.main_container, fragment )
                .addToBackStack(null)
                .commit();
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
