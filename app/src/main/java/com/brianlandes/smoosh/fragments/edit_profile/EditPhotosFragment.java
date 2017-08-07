package com.brianlandes.smoosh.fragments.edit_profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.brianlandes.smoosh.AppSettings;
import com.brianlandes.smoosh.R;
import com.brianlandes.smoosh.structures.ProfilePhoto;
import com.brianlandes.smoosh.structures.QuickCallback;
import com.brianlandes.smoosh.utils.AssetUtils;
import com.brianlandes.smoosh.utils.ImageUtils;
import com.brianlandes.smoosh.utils.StorageUtils;
import com.desai.vatsal.mydynamictoast.MyDynamicToast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.brianlandes.smoosh.utils.ImageUtils.FillImageViewWithUrl;

/**
 * Created by Brian on 8/3/2017.
 */

public class EditPhotosFragment extends Fragment implements View.OnClickListener {
    public final String TAG = EditPhotosFragment.class.getSimpleName();

    private int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    public static final int PICS_PER_ROW = 3;

//    public static int GridWidth = 1;
//    public static int GridHeight = 1;

    @BindView(R.id.gridView) RecyclerView gridView;
    @BindView(R.id.button_upload_image) Button uploadButton;

    private Unbinder unbinder;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mWrappedAdapter;
    GridAdapter adapter;

    public EditPhotosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_photos, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        uploadButton.setOnClickListener(this);

        mLayoutManager = new GridLayoutManager(getContext(), PICS_PER_ROW, GridLayoutManager.VERTICAL, false);

        // Setup D&D feature and RecyclerView
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

//        dragMgr.setDraggingItemShadowDrawable(
//                (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.trashcanopen));
        // Start dragging after long press
        dragMgr.setInitiateOnLongPress(true);
        dragMgr.setInitiateOnMove(false);
        dragMgr.setLongPressTimeout(400);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        dragMgr.setDragStartItemAnimationDuration(250);
        dragMgr.setDraggingItemAlpha(0.8f);
        dragMgr.setDraggingItemScale(1.3f);
        dragMgr.setDraggingItemRotation(15.0f);

//        dragMgr.attachRecyclerView(gridView);

        adapter = new GridAdapter();
        if ( AssetUtils.currentUser.photoStubsLoaded )
            FillPhotos();
        else {
            AssetUtils.currentUser.photoStubsLoadedListeners.add( photosLoadedCallback );
        }

        mWrappedAdapter = dragMgr.createWrappedAdapter(adapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        gridView.setLayoutManager(mLayoutManager);
        gridView.setAdapter(mWrappedAdapter);
        gridView.setItemAnimator(animator);

        dragMgr.attachRecyclerView(gridView);

        return rootView;
    }

    public void FillPhotos() {
        adapter.mItems.clear();
        for ( ProfilePhoto photo : AssetUtils.currentUser.profilePhotos ) {
            adapter.add(photo);
        }
        adapter.notifyDataSetChanged();
    }

    QuickCallback photosLoadedCallback = new QuickCallback() {
        @Override
        public void Activate(@Nullable Object... objects) {
            FillPhotos();
        }
    };

//    @Override
//    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        if (enter) {
//            return MoveAnimation.create(MoveAnimation.LEFT, enter, AppSettings.TRANSITION_DURATION);
//        } else {
//            return MoveAnimation.create(MoveAnimation.RIGHT, enter, AppSettings.TRANSITION_DURATION);
//        }
//    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.button_upload_image:
                UploadProfilePic();
                break;
        }
    }

    public void UploadProfilePic() {
        // Check for the read storage permission
        if (ContextCompat.checkSelfPermission( getContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            requestPermissions( new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            LaunchPicChooser();
        }
    }

    void LaunchPicChooser() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), PICK_IMAGE_REQUEST);
    }

    // Callback method from ActivityCompat.requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    LaunchPicChooser();
                } else {
                    // If you do not get permission, show a Toast
                    MyDynamicToast.errorMessage(getActivity(), getString(R.string.photos_permission_denied));
//                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG,"onActivityResult");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri sourceUri = data.getData();
            Uri destUri = ImageUtils.GetCacheImageUri(getContext());
            UCrop uCrop = UCrop.of(sourceUri, destUri)
                    .withAspectRatio(AppSettings.IMAGE_WIDTH, AppSettings.IMAGE_HEIGHT)
                    .withMaxResultSize(AppSettings.IMAGE_WIDTH, AppSettings.IMAGE_HEIGHT);
            // have to use startActivityForResult here instead of UCrop.start(activity) because we're in a fragment
            startActivityForResult(uCrop.getIntent(getContext()), UCrop.REQUEST_CROP);
        } else if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                final ProfilePhoto photo = AssetUtils.currentUser.AddPhoto(resultUri);
                adapter.add(photo);
//                adapter.add(resultUri);
                adapter.notifyDataSetChanged();



                StorageUtils.UploadPhoto(
                        getContext(),
                        resultUri,
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photo.storageUri = taskSnapshot.getDownloadUrl();
                        ImageUtils.PushProfilePhoto(photo);
                        adapter.holderTable.get(photo).imageView.setImageAlpha( 255 );
                    }
                } );
            } else {
//                Toast.makeText(this, "Some shit happened", Toast.LENGTH_SHORT).show();
            }


        } else if (resultCode == Activity.RESULT_OK && resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    static class MyViewHolder extends AbstractDraggableItemViewHolder {
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            float w = (float)AssetUtils.displayMetrics.widthPixels/(float)PICS_PER_ROW - 100;
            imageView.getLayoutParams().width = (int)w;
            imageView.getLayoutParams().height = (int)(w/AppSettings.IMAGE_WIDTH_HEIGHT_RATIO);
        }
    }

    public class GridAdapter extends RecyclerView.Adapter<MyViewHolder> implements DraggableItemAdapter<MyViewHolder> {

        List<ProfilePhoto> mItems;
        public HashMap<ProfilePhoto,MyViewHolder> holderTable;

        public GridAdapter() {
            setHasStableIds(true); // this is required for D&D feature.

            mItems = new ArrayList<>();
            holderTable = new HashMap<>();
        }

        public void add( ProfilePhoto photo ) {
            mItems.add( photo );
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).id; // need to return stable (= not change even after reordered) value
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_profile_photo, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final ProfilePhoto item = mItems.get(position);
            holderTable.put(item,holder);
            holder.imageView.setImageURI(null);
            if ( item.localUri!=null ) {
                holder.imageView.setImageURI(item.localUri);
                if ( item.storageUri==null )
                    holder.imageView.setImageAlpha( 60 );
            } else if ( item.storageUri!=null ) {
                FillImageViewWithUrl(
                        holder.imageView,
                        item.storageUri.toString(),
                        new QuickCallback() {
                            @Override
                            public void Activate(Object... objects) {
                                Bitmap bitmap = (Bitmap) objects[0];
                                if ( bitmap!=null ) {
                                    item.localUri = ImageUtils.SaveBitmapToCache(getContext(),bitmap);
                                }
                            }
                        });
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            ProfilePhoto movedItem = mItems.remove(fromPosition);
            mItems.add(toPosition, movedItem);
            notifyItemMoved(fromPosition, toPosition);

            AssetUtils.currentUser.profilePhotos.clear();
            AssetUtils.currentUser.profilePhotos.addAll( mItems );
            AssetUtils.currentUser.ReorderPhotos();
        }

        @Override
        public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
            ProfilePhoto item = mItems.get(position);
            return item.storageUri!=null;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }
    }
}
