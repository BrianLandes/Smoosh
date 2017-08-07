package com.brianlandes.smoosh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.brianlandes.smoosh.fragments.MainScreenFragment;
import com.brianlandes.smoosh.fragments.ViewUserFragment;
import com.brianlandes.smoosh.fragments.edit_profile.EditUserTabbedFragment;
import com.brianlandes.smoosh.fragments.matching.MatchFragment;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.DatabaseUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.github.florent37.awesomebar.AwesomeBar;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.brianlandes.smoosh.fragments.SignInFragment;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 123;
    private static final int REQUEST_LOCATION_PERMISSION = 1024;

    FragmentManager fragmentManager;
    private FusedLocationProviderClient mFusedLocationClient;

    @BindView(R.id.bar)
    AwesomeBar bar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        AssetUtils.init(this);
        if (savedInstanceState == null) {

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

            bar.addOverflowItem("Edit Profile");

            bar.displayHomeAsUpEnabled(false);
        }
    }

    public void SignIn(View view) {
        startActivityForResult( // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
    }

    public void SignOut(View view) {
        drawerLayout.closeDrawer(Gravity.START);
        DatabaseUtils.Reset();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        ShowSignInScreen();
                    }
                });
    }

    public void ContinueToMainScreen() {
//        Intent intent = new Intent( this, MainScreenActivity.class );
//        startActivity( intent );

        bar.setVisibility(View.VISIBLE);

        MainScreenFragment mainScreenFragment = new MainScreenFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.main_container, mainScreenFragment)
                .commit();

        // TODO: on initial registration take them to the edit profile page

        SmoosherUtils.LoadCurrent();

        TryGetLocation();
//        finish();
    }

    public void ShowSignInScreen() {
        bar.setVisibility(View.GONE);
        SignInFragment mainScreenFragment = new SignInFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.main_container, mainScreenFragment)
                .commit();

        clearBackStack();
    }

    private void clearBackStack() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void ClickEditProfile(View view) {
//        EditPhotosFragment editScreenFragment = new EditPhotosFragment();
        EditUserTabbedFragment editScreenFragment = new EditUserTabbedFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, editScreenFragment)
                .addToBackStack(null)
                .commit();
    }

    public void ClickViewProfile(View view) {
        ViewUserFragment fragment = ViewUserFragment.with(AssetUtils.currentUser);
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void ClickMatching(View view) {
        MatchFragment fragment = new MatchFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
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
                    ShowSignInScreen();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar(R.string.no_internet_connection);
                    ShowSignInScreen();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar(R.string.unknown_error);
                    ShowSignInScreen();
                    return;
                }
            }

            showSnackbar(R.string.unknown_sign_in_response);
            ShowSignInScreen();
        }
    }

    public void showSnackbar(int message) {
        Snackbar.make(findViewById(R.id.main_container), message, Snackbar.LENGTH_LONG).show();
    }

    public void TryGetLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            GetLocation();
        }
    }

    // Callback method from ActivityCompat.requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length == 2
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    GetLocation();
                } else {
                    // If you do not get permission, show a Toast
                    MyDynamicToast.errorMessage(this, getString(R.string.location_permission_denied));
                }
                break;
            }
        }
    }

    public void GetLocation() {
        Log.d(TAG, "GetLocation");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "GetLocation: didn't have permission");
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG, location.toString());
                        } else {
                            Log.d(TAG, "GetLocation: location was null");
                            mLocationRequest = LocationRequest.create();
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                            mLocationRequest.setInterval(10000);
                            mLocationRequest.setFastestInterval(5000);
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    new LocationCallback() {
                                        @Override
                                        public void onLocationResult(LocationResult locationResult) {
                                            for (Location location : locationResult.getLocations()) {
                                                // Update UI with location data
                                                // ...
                                                Log.d(TAG, "RequestLocationUpdates: " + location.toString());
                                            }

                                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    ActivityCompat#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for ActivityCompat#requestPermissions for more details.
                                                Log.d(TAG, "GetLocation: didn't have permission");
                                                return;
                                            }


                                        };
                                    },
                                    null /* Looper */);
                        }
                    }
                });




    }
}
