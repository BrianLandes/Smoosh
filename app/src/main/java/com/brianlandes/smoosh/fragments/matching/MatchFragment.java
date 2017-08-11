package com.brianlandes.smoosh.fragments.matching;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.LocationUtils;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchFragment extends Fragment {
    public final String TAG = MatchFragment.class.getSimpleName();
    private Unbinder unbinder;

    CollectionPagerAdapter pagerAdapter;

    @BindView(R.id.tab_pager) ViewPager mViewPager;

    public MatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_match, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        if ( pagerAdapter==null ) {
            pagerAdapter = new CollectionPagerAdapter(getActivity().getSupportFragmentManager());
            mViewPager.setAdapter(pagerAdapter);
        }

        LocationUtils.QueryNearySmooshers(getActivity(),true);

        AssetUtils.nearbySmooshersLoadedListeners.add( nearbySmooshersCallback );

        return rootView;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
        } else {
            return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
        }
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        AssetUtils.nearbySmooshers.remove(nearbySmooshersCallback );
    }

    QuickCallback nearbySmooshersCallback = new QuickCallback() {
        @Override
        public void Activate(@Nullable Object... objects) {
            Log.d( TAG, "A new smoosher was added nearby" );
        }
    };

    class CollectionPagerAdapter extends FragmentStatePagerAdapter {

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
//            args.putString(USER_UID, userUid );
            Fragment fragment = new Fragment();
            switch( position ) {
                case 0:
                    fragment = new SwipingFragment();
                    break;
                case 1:default:
//                    fragment = new SwipingFragment();
                    break;
                case 2:
//                    fragment = new SwipingFragment();
                    break;

            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch( position ) {
                case 0:
                default:
                    return getResources().getString(R.string.photos);
                case 1:
                    return getResources().getString(R.string.bio);
                case 2:
                    return getResources().getString(R.string.settings);
            }
        }
    }
}
