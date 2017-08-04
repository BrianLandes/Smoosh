package com.brianlandes.smoosh.fragments.edit_profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.utils.DatabaseUtils;
import com.brianlandes.smoosh.utils.SmoosherUtils;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.brianlandes.smoosh.utils.SmoosherUtils.FillEditTextWithUserBio;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditInfoFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = EditInfoFragment.class.getSimpleName();

    private Unbinder unbinder;

    @BindView(R.id.save_button) Button saveButton;
    @BindView(R.id.editText) EditText editText;
    @BindView(R.id.name_edit_text) EditText nameEditText;

    public EditInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_info, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        saveButton.setOnClickListener(this);

        SmoosherUtils.FillEditTextWithUserName(nameEditText, null );
        SmoosherUtils.FillEditTextWithUserBio(editText, null );

        return rootView;
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.save_button:
                UploadBio();
                break;
        }
    }

    public void UploadBio() {
        String text = editText.getText().toString();
        if ( text.length() <= getResources().getInteger( R.integer.max_bio_length ) )
            DatabaseUtils.SetUserBio(text);
        else
            MyDynamicToast.errorMessage(getContext(),getString(R.string.bio_too_long));

        String name = nameEditText.getText().toString();
        if ( name.length() < getResources().getInteger( R.integer.min_name_length ) )
            MyDynamicToast.errorMessage(getContext(),getString(R.string.name_too_short));
        else if ( name.length() > getResources().getInteger( R.integer.max_name_length ) )
            MyDynamicToast.errorMessage(getContext(),getString(R.string.name_too_long));
        else
            DatabaseUtils.SetUserName(name);
    }
}
