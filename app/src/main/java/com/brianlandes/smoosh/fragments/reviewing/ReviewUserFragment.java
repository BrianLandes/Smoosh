package com.brianlandes.smoosh.fragments.reviewing;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;

import com.adroitandroid.chipcloud.Chip;
import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.fragments.BaseFragment;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.structures.Smoosher;
import com.brianlandes.smoosh.structures.Tag;
import com.brianlandes.smoosh.structures.TagList;
import com.brianlandes.smoosh.utils.RatingUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.brianlandes.smoosh.utils.TagUtils;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewUserFragment extends BaseFragment {

    public final String TAG = ReviewUserFragment.class.getSimpleName();

    Smoosher smoosher;

    @BindView(R.id.search_bar) EditText searchBar;
    @BindView(R.id.search_cloud) ChipCloud searchCloud;
    @BindView(R.id.top_cloud) ChipCloud topCloud;
    @BindView(R.id.pass_button) Button passButton;
    @BindView(R.id.like_button) Button likeButton;
    @BindView(R.id.add_tag_button) Button addButton;

    TagList topTags = new TagList();
    TagList searchTags = new TagList();

    public static ReviewUserFragment with(Smoosher smoosher) {
        ReviewUserFragment fragment = new ReviewUserFragment();
        fragment.smoosher = smoosher;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutResource = R.layout.fragment_review_user;
        View rootView = super.onCreateView(inflater,container,savedInstanceState);

        searchTags.changeListeners.add(new QuickCallback() {
            @Override
            public void Activate(@Nullable Object... objects) {
                searchCloud.removeAllViews();
                for( Tag tag : searchTags.Tags()) {
                    searchCloud.addChip(tag.phrase);
                }
            }
        });
        TagUtils.GetMostUsedTags(searchTags);
        searchBar.addTextChangedListener(searchBarWatcher);
        searchCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                Tag tag = searchTags.Get(i);
                int position = topTags.GetPosition(tag);
                if ( position == -1 )
                    topTags.Add( tag );
                else {
                    topCloud.setSelectedChip(position);
                }
                searchTags.Remove( tag );
            }

            @Override
            public void chipDeselected(int i) {
                Tag tag = searchTags.Get(i);
                int position = topTags.GetPosition(tag);
                if ( position != -1 ) {
                    Chip chip = (Chip) topCloud.getChildAt(position);
                    chip.deselect();
                }
//                    topCloud.setSelectedChip(position);
            }
        });

        topTags.changeListeners.add(new QuickCallback() {
            @Override
            public void Activate(@Nullable Object... objects) {
                // should only ever have to deal with TagList.Add()
                Tag tag = topTags.GetLast();
                topCloud.addChip(tag.phrase);
                topCloud.setSelectedChip(topCloud.getChildCount()-1);
                tag.selected = true;
            }
        });
        topCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                Tag tag = topTags.Get(i);
                tag.selected = true;
//                int position = searchTags.GetPosition(tag);
//                if ( position != -1 ) {
//                    searchCloud.setSelectedChip(position);
//                }
            }

            @Override
            public void chipDeselected(int i) {
                Tag tag = topTags.Get(i);
                tag.selected = false;
//                int position = searchTags.GetPosition(tag);
//                if ( position != -1 ) {
//                    Chip chip = (Chip) searchCloud.getChildAt(position);
//                    chip.deselect();
//                }
//                    topCloud.setSelectedChip(position);
            }
        });
        TagUtils.GetTagsFromLastReview(topTags,smoosher);

//        lastReviewTags.changeListeners.add(new QuickCallback() {
//            @Override
//            public void Activate(@Nullable Object... objects) {
//                lastReviewCloud.removeAllViews();
//                for( Tag tag : lastReviewTags.Tags()) {
//                    lastReviewCloud.addChip(tag.phrase);
//                }
//            }
//        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = searchBar.getText().toString();
                TagUtils.AddTag(phrase);
                TagUtils.GetTagsContaining(phrase.toLowerCase(),searchTags);
//                Log.d(TAG,"add button pressed: " + phrase );
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyReview();
                SmoosherUtils.LikeSmoosher(smoosher);
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyReview();
                SmoosherUtils.PassSmoosher(smoosher);
            }
        });

        return rootView;
    }

    public void ApplyReview() {
        ArrayList<String> tagKeys = new ArrayList<String>();
        for ( int i = 0; i < topTags.Size(); i ++ ) {
            Tag tag = topTags.Get(i);
            if ( tag.selected )
                tagKeys.add(tag.key);
        }
        RatingUtils.UpdateRating(smoosher,tagKeys );
    }

    TextWatcher searchBarWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            TagUtils.GetTagsContaining(s.toString().toLowerCase(),searchTags);
        }
    };

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return FlipAnimation.create(FlipAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
        } else {
            return MoveAnimation.create(MoveAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
        }
    }
}
