package com.brianlandes.smoosh.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.fragments.reviewing.ReviewUserFragment;
import com.brianlandes.smoosh.structures.ProfilePhoto;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.ImageUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewUserFragment extends Fragment {
    public final String TAG = ViewUserFragment.class.getSimpleName();
    private Unbinder unbinder;

    private Smoosher smoosher;

    @BindView(R.id.carouselView) CarouselView carouselView ;
    @BindView(R.id.bio_edit_text) TextView bioEditText;
    @BindView(R.id.name_text_view) TextView nameEditText;
    @BindView(R.id.review_button) FloatingActionButton reviewButton;

    // animation controls
    boolean goingToReview = false;

    ReviewUserFragment reviewFragment;

    public static ViewUserFragment with(Smoosher smoosher) {
        ViewUserFragment fragment = new ViewUserFragment();
        fragment.smoosher = smoosher;
        return fragment;
    }

    public ViewUserFragment() {
        // Required empty public constructor
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            final ProfilePhoto photo = smoosher.profilePhotos.get(position);
            ImageUtils.FillImageViewWithProfilePhoto(getContext(),imageView,photo);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View rootView = inflater.inflate(R.layout.fragment_view_user, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        carouselView.setImageListener(imageListener);

        carouselView.getLayoutParams().height = (int)((float)AssetUtils.displayMetrics.widthPixels / AppSettings.IMAGE_WIDTH_HEIGHT_RATIO);

        if ( smoosher.photoStubsLoaded )
            FillPhotos();
        else
            smoosher.photoStubsLoadedListeners.add( photosLoadedCallback );

        SmoosherUtils.FillEditTextWithUserName(nameEditText, smoosher.uid );
        SmoosherUtils.FillEditTextWithUserBio(bioEditText, smoosher.uid );

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ( reviewFragment==null )
                    reviewFragment = ReviewUserFragment.with(smoosher);
                goingToReview = true;
                AssetUtils.ShowFragment(getActivity(),reviewFragment);
            }
        });

        return rootView;
    }

    public void FillPhotos() {
        carouselView.setPageCount(smoosher.profilePhotos.size());
    }

    QuickCallback photosLoadedCallback = new QuickCallback() {
        @Override
        public void Activate(@Nullable Object... objects) {
            FillPhotos();
        }
    };

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
        } else {
            if ( goingToReview )
                return FlipAnimation.create(FlipAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
            else
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
        }
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
