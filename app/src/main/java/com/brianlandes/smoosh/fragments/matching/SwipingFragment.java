package com.brianlandes.smoosh.fragments.matching;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.fragments.BaseFragment;
import com.brianlandes.smoosh.fragments.ViewUserFragment;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.ImageUtils;
import com.brianlandes.smoosh.utils.LocationUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwipingFragment extends BaseFragment {

    public static final String TAG = SwipingFragment.class.getSimpleName();

    @BindView(R.id.swipe_view) SwipeFlingAdapterView flingContainer;

    ArrayList<Smoosher> smooshers = new ArrayList<Smoosher>();
    MyAdapter arrayAdapter;

    boolean showingUser = false;
    public boolean fromMainScreen = false;


    public SwipingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutResource = R.layout.fragment_swiping;
        View rootView = super.onCreateView(inflater,container,savedInstanceState);

        smooshers.clear();
        for( String uid: AssetUtils.nearbySmooshers ) {
            AddSmoosher(uid);
        }

        LocationUtils.QueryNearySmooshers(getActivity(),true);

        //choose your favorite adapter
        arrayAdapter = new MyAdapter(getActivity(),smooshers );

        AssetUtils.nearbySmooshersLoadedListeners.add( smoosherCallback );

        Log.d(TAG,"oncreateview");



        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                smooshers.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
//                Toast.makeText(MyActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
//                Toast.makeText(MyActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
            }

            @Override
            public void onScroll(float v) {

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
//                makeToast(MyActivity.this, "Clicked!");
                Smoosher smoosher = arrayAdapter.getItem(itemPosition);
                ViewUserFragment fragment = ViewUserFragment.with(smoosher)
                        .fromSwiping();
                showingUser = true;
                AssetUtils.ShowFragment(getActivity(),fragment);
            }
        });

        return rootView;
    }

    QuickCallback smoosherCallback = new QuickCallback() {
        @Override
        public void Activate(@Nullable Object... objects) {
            String uid = (String) objects[0];
            AddSmoosher(uid);
        }
    };



    public void AddSmoosher( String uid ) {
        Smoosher newSmoosher = SmoosherUtils.Load( uid );
        smooshers.add( newSmoosher );
        if ( arrayAdapter!=null )
            arrayAdapter.notifyDataSetChanged();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView" );
        AssetUtils.nearbySmooshersLoadedListeners.remove( smoosherCallback );
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            if ( fromMainScreen ) {
                fromMainScreen = false;
                return MoveAnimation.create(MoveAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
            } else
                return CubeAnimation.create(CubeAnimation.DOWN, enter, AppSettings.TRANSITION_DURATION);
        } else {
            if ( showingUser ) {
                showingUser=false;
                return CubeAnimation.create(CubeAnimation.UP, enter, AppSettings.TRANSITION_DURATION);
//            } else if ( goingBack ) {
//                goingBack=false;
//                return CubeAnimation.create(CubeAnimation.DOWN, enter, AppSettings.TRANSITION_DURATION);
            } else
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
        }
    }

    class MyAdapter extends ArrayAdapter<Smoosher> {

        public MyAdapter(@NonNull Context context, ArrayList<Smoosher> smooshers) {
            super(context, R.layout.cell_profile_card, smooshers);
        }

        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {

            Smoosher smoosher = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_profile_card, parent, false);
            }

            TextView helloText = (TextView)convertView.findViewById(R.id.helloText);

            SmoosherUtils.FillEditTextWithUserName(helloText,smoosher.uid);

            ImageView displayPicView = (ImageView)convertView.findViewById(R.id.imageView);

            if ( smoosher.photoStubsLoaded ) {
                ImageUtils.FillImageViewWithProfilePhoto(getContext(),displayPicView,smoosher.profilePhotos.get(0) );
            } else {
                smoosher.photoStubsLoadedListeners.add( new DisplayPhotoCallback(displayPicView,smoosher));
            }

            return convertView;
        }
    }

    class DisplayPhotoCallback implements QuickCallback {

        public ImageView imageView;
        public Smoosher smoosher;

        public DisplayPhotoCallback( ImageView imageView, Smoosher smoosher ) {
            this.imageView = imageView;
            this.smoosher = smoosher;
        }

        @Override
        public void Activate(@Nullable Object... objects) {
            ImageUtils.FillImageViewWithProfilePhoto(getContext(),imageView,smoosher.profilePhotos.get(0) );
//            smoosher.photoStubsLoadedListeners.remove(DisplayPhotoCallback.this);
        }
    }
}
