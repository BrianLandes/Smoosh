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

import com.adroitandroid.chipcloud.ChipCloud;
import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.fragments.reviewing.ReviewUserFragment;
import com.brianlandes.smoosh.structures.ProfilePhoto;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.structures.Tag;
import com.brianlandes.smoosh.structures.TagList;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.ImageUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.brianlandes.smoosh.utils.TagUtils;
import com.labo.kaji.fragmentanimations.CubeAnimation;
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
public class ViewUserFragment extends BaseFragment {
    public final String TAG = ViewUserFragment.class.getSimpleName();

    private Smoosher smoosher;

    @BindView(R.id.carouselView) CarouselView carouselView ;
    @BindView(R.id.bio_edit_text) TextView bioEditText;
    @BindView(R.id.name_text_view) TextView nameEditText;
    @BindView(R.id.review_button) FloatingActionButton reviewButton;
    @BindView(R.id.tag_cloud) ChipCloud tagCloud;

    // animation controls
    boolean goingToReview = false;
    boolean comingFromSwipe = false;

    TagList tagList = new TagList();

    ReviewUserFragment reviewFragment;

    public static ViewUserFragment with(Smoosher smoosher) {
        ViewUserFragment fragment = new ViewUserFragment();
        fragment.smoosher = smoosher;
        return fragment;
    }

    public ViewUserFragment fromSwiping() {
        this.comingFromSwipe = true;
        return this;
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
        layoutResource = R.layout.fragment_view_user;
        View rootView = super.onCreateView(inflater,container,savedInstanceState);

        carouselView.setImageListener(imageListener);

        carouselView.getLayoutParams().height = (int)((float)AssetUtils.displayMetrics.widthPixels / AppSettings.IMAGE_WIDTH_HEIGHT_RATIO);

        if ( smoosher.photoStubsLoaded )
            FillPhotos();
        else
            smoosher.photoStubsLoadedListeners.add( photosLoadedCallback );

        SmoosherUtils.FillEditTextWithUserName(nameEditText, smoosher.uid );
        SmoosherUtils.FillEditTextWithUserBio(bioEditText, smoosher.uid );
        tagList = new TagList();
        TagUtils.GetUsersTags(smoosher, tagList);
        tagList.changeListeners.add(new QuickCallback() {
            @Override
            public void Activate(@Nullable Object... objects) {
                tagCloud.removeAllViews();
                for( Tag tag : tagList.Tags()) {
                    tagCloud.addChip(tag.phrase);
                }
//                Log.d(TAG, "Tag list size: " + Integer.toString(tagList.Size() ) + " : " + System.identityHashCode(tagList) );
            }
        });

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

    @Override public void onDestroyView() {
        super.onDestroyView();
        tagList.changeListeners.clear();

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
            if ( comingFromSwipe ) {
                comingFromSwipe = false;
                return CubeAnimation.create(CubeAnimation.UP, enter, AppSettings.TRANSITION_DURATION);
            } else
                return FlipAnimation.create(FlipAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
        } else {
            if ( goingToReview ) {
                goingToReview = false;
                return FlipAnimation.create(FlipAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
            } else if ( goingBack ) {
                goingBack = false;
                return CubeAnimation.create(CubeAnimation.DOWN, enter, AppSettings.TRANSITION_DURATION);
            } else
                return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
        }
    }

}
