package com.brianlandes.smoosh;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.brianlandes.smoosh.fragments.MainScreenFragment;
import com.brianlandes.smoosh.fragments.edit_profile.EditProfileFragment;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        ContinueToMainScreen();
    }


    public void ContinueToMainScreen() {
//        Intent intent = new Intent( this, MainScreenActivity.class );
//        startActivity( intent );

        MainScreenFragment mainScreenFragment = new MainScreenFragment();

        fragmentManager.beginTransaction()
                .add( R.id.main_container, mainScreenFragment )
                .commit();

        // TODO: on initial registration take them to the edit profile page

        AssetUtils.currentUser = new Smoosher();

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
}
