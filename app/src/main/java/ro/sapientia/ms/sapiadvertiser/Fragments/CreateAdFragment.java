package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import ro.sapientia.ms.sapiadvertiser.Model.Advertisement;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.AdImageAdapter;
import ro.sapientia.ms.sapiadvertiser.Utils.FragmentManager;
import ro.sapientia.ms.sapiadvertiser.Utils.PathParser;


public class CreateAdFragment extends Fragment {

    public static final int OPEN_DOCUMENT_CODE  = 2;
    public static final int REQUEST_FOR_STORAGE = 1;

    private static final String TAG = CreateAdFragment.class.getSimpleName();
    private ArrayList<String> uploadableImages;
    private ArrayList<Uri> imagesUri;

    private ConstraintLayout mConstraintLayout;
    private ViewPager viewPager;
    private EditText mTitle,mShortDesc,mLongDesc,mLocation;
    private ProgressBar mprogressBar;
    private Advertisement advertisement;
    private ImageView leftButton,rightButton,addImageButton;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference advertismentRef = FirebaseStorage.getInstance().getReference().child("AdvertismentPictures/");


    public CreateAdFragment() {
        // Required empty public constructor
    }

    public static CreateAdFragment newInstance() {
        CreateAdFragment fragment = new CreateAdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_ad, container, false);
        initWidgets(view);
        uploadableImages = new ArrayList<>();
        viewPager.setAdapter(new AdImageAdapter(getActivity(),uploadableImages));
        advertisement = new Advertisement();
        imagesUri = new ArrayList<>();
        handleViewpagerNavigation();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initWidgets(View view){

        viewPager = view.findViewById(R.id.picked_image_pager);
        addImageButton = view.findViewById(R.id.add_image_logo);
        mTitle = view.findViewById(R.id.title_of_adv);
        mShortDesc = view.findViewById(R.id.short_of_adv);
        mLongDesc = view.findViewById(R.id.long_of_adv);
        mLocation = view.findViewById(R.id.location_of_adv);
        mprogressBar = view.findViewById(R.id.progress);
        mConstraintLayout = view.findViewById(R.id.container);
        leftButton = view.findViewById(R.id.swipe_left);
        rightButton = view.findViewById(R.id.swipe_right);
        Button uploadButton = view.findViewById(R.id.upload_advert);
        checkPermissions();
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO if(NetworkUtils.isNetworkAvailable)
                if(!checkFormData()){
                    Toast.makeText(getContext(),"Correct the mistakes!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(imagesUri.isEmpty()){
                    Toast.makeText(getContext(),"Select at least an image",Toast.LENGTH_LONG).show();
                    return;
                }
                //TODO if(NetworkUtils.isNetworkAvailable)
                uploadImagesToStorage();
            }
        });
    }

    private void selectImageFromGallery(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_DOCUMENT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_DOCUMENT_CODE && resultCode == Activity.RESULT_OK){
            Uri imageUri;
            if(data != null){
                imageUri = data.getData();
                if(imageUri != null){
                    String s = PathParser.getPathFromUri(Objects.requireNonNull(getActivity()),imageUri);
                    uploadableImages.add(s);
                }
                viewPager.setAdapter(new AdImageAdapter(getActivity(),uploadableImages));
                imagesUri.add(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageFromGallery();
                } else {
                    addImageButton.setClickable(false);
                    addImageButton.setFocusable(false);
                    Toast.makeText(getActivity(),"Permission denied",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void uploadImagesToStorage(){

        final String key = database.getReference("Advertisements").push().getKey();
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mConstraintLayout.setAlpha(0.2F);
        mprogressBar.setVisibility(View.VISIBLE);
        for(Uri imgUri : imagesUri){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(),imgUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                byte[] arr = outputStream.toByteArray();
                final StorageReference picRef = advertismentRef.child(String.valueOf(System.currentTimeMillis()) + ".jpg");
                final UploadTask uploadTask = picRef.putBytes(arr);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure",e);
                        mprogressBar.setVisibility(View.INVISIBLE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        Toast.makeText(getContext(),"Image Uploading failed!",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.e(TAG,"Succesfully uploaded files!");
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                Log.e(TAG,"Trigger point here!!!");
                                // Continue with the task to get the download URL
                                return picRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    advertisement.getImages().put(String.valueOf(System.currentTimeMillis()),downloadUri.toString());
                                    advertisement.setAdID(key);
                                    advertisement.setCreatorID(FirebaseAuth.getInstance().getUid());
                                    advertisement.setTitle(mTitle.getText().toString());
                                    advertisement.setShortDescription(mShortDesc.getText().toString());
                                    advertisement.setLongDescription(mLongDesc.getText().toString());
                                    advertisement.setNumOfViews(0);
                                    advertisement.setLocation(mLocation.getText().toString());
                                    advertisement.setTimeStamp(System.currentTimeMillis());
                                    advertisement.setIsReported(false);
                                    advertisement.setIsDeleted(false);
                                    database.getReference("advertismenets").child(Objects.requireNonNull(key)).setValue(advertisement);
                                    Log.e(TAG,"Success: " + downloadUri.toString());
                                    Toast.makeText(getContext(),"Advertisement successfully posted!",Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e(TAG,"Failure!",task.getException());
                                    Toast.makeText(getContext(),"Posting unsuccessful! Try again",Toast.LENGTH_LONG).show();
                                }
                                Log.e(TAG,String.valueOf(advertisement.getImages().values().size()));
                                mprogressBar.setVisibility(View.INVISIBLE);
                                mConstraintLayout.setAlpha(1F);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                FragmentManager manager= new FragmentManager(getContext());
                                manager.executeTransaction(ListFragment.newInstance("all"),R.id.frame_layout,"home",false);
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        mprogressBar.setProgress((int)progress);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkFormData(){

        boolean isWellFormed = false;
        if(TextUtils.isEmpty(mTitle.getText())){
            mTitle.setError("Title is required!");
        }
        if(TextUtils.isEmpty(mShortDesc.getText())){
            mShortDesc.setError("Provide Short Description!");
        }
        if(TextUtils.isEmpty(mLongDesc.getText())){
            mLongDesc.setError("Provide longer description");
        }
        if(TextUtils.isEmpty(mLocation.getText())){
            mLocation.setError("Location needed!");
        }
        else{
            isWellFormed = true;
        }
        return isWellFormed;
    }

    private void handleViewpagerNavigation(){

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tab = viewPager.getCurrentItem();
                if(tab > 0){
                    tab--;
                    viewPager.setCurrentItem(tab);
                }
                else{
                    viewPager.setCurrentItem(tab);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tab = viewPager.getCurrentItem();
                tab++;
                viewPager.setCurrentItem(tab);
            }
        });
    }

    private void checkPermissions(){

        int req = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),Manifest.permission.READ_EXTERNAL_STORAGE);
        if (req != PackageManager.PERMISSION_GRANTED/*=0*/){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE/*ide manifest.permission.R_E_S kellene */},REQUEST_FOR_STORAGE);
        }
    }
}
