package com.brianlandes.smoosh.fragments.edit_profile;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserTabbedFragment extends Fragment {
    public final String TAG = EditUserTabbedFragment.class.getSimpleName();
    private Unbinder unbinder;
    CollectionPagerAdapter pagerAdapter;

    @BindView(R.id.tab_pager) ViewPager mViewPager;

    public EditUserTabbedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_user_tabbed, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        if ( pagerAdapter==null ) {
            pagerAdapter = new CollectionPagerAdapter(getActivity().getSupportFragmentManager());
            mViewPager.setAdapter(pagerAdapter);
        }

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
    }

    class CollectionPagerAdapter extends FragmentStatePagerAdapter {

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
//            args.putString(USER_UID, userUid );
            Fragment fragment;
            switch( position ) {
                case 0:
                    fragment = new EditPhotosFragment();
                    break;
                case 1:default:
                    fragment = new EditInfoFragment();
                    break;
                case 2:
                    fragment = new EditSettingsFragment();
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
