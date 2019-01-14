package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.content.pm.PackageManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ro.sapientia.ms.sapiadvertiser.Model.User;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.PathParser;

import static android.app.Activity.RESULT_OK;


public class ProfileUpdateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private static final int REQ_PERMISSION=0;
    private static final String TAG = "ProfileUploadFragment";
    private Uri imgUri;
    private EditText mUserName;
    private EditText mUserPhoneNum;
    private EditText mUserEmail;
    private Button mSaveButton;
    private Button mUploadButton;
    private ImageView image;
    private Bitmap b;

    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;
    private ArrayList<String> pathArray;
    private StorageReference mStorageRef; //valami ma'gikus import valahova
    private String filePath;
    private final int PICK_IMAGE_REQUEST=71;
    private User myUser;
    //firebase
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseStorage storage;

    StorageReference storageReference;
    private StorageReference userRef = FirebaseStorage.getInstance().getReference().child("UserProfilePictures/");

    FirebaseDatabase database =FirebaseDatabase.getInstance();



    private OnFragmentInteractionListener mListener;

    public ProfileUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileUpdateFragment newInstance(String param1, String param2) {
        ProfileUpdateFragment fragment = new ProfileUpdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "oncreate view before inflate");
        View view = inflater.inflate(R.layout.fragment_profile_update, container, false);
        Log.d(TAG, "oncreate view after inflate");
        //firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init view

        mSaveButton = view.findViewById(R.id.saveButton);
        mUploadButton = view.findViewById(R.id.btnUpload);
        image = view.findViewById(R.id.profile_image);

        mProgressDialog = new ProgressDialog(getContext());
        pathArray = new ArrayList<>();
        mUserName = view.findViewById(R.id.profileName);
        mUserPhoneNum = view.findViewById(R.id.phoneNum);
        mUserEmail = view.findViewById(R.id.email);


        // Inflate the layout for this fragment

        getUserInfo();

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionRequest();
                Log.d(TAG, "onClick: Uploading Image button");
                chooseImage();

                Log.d(TAG, "uploadbutton end");
                loadImageFromStorage();
            }

        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                Log.d(TAG, mUserEmail.getText().toString());

                Log.d(TAG, "onclicksave");

                myUser = new User(mUserName.getText().toString(), mUserEmail.getText().toString(), mUserPhoneNum.getText().toString());

                Log.d(TAG, myUser.toString());

                final String key = database.getReference("Users").push().getKey();
                Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                Log.d(TAG, "ittvan");



                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), imgUri);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    byte[] arr = outputStream.toByteArray();
                    final StorageReference picRef = userRef.child(String.valueOf(System.currentTimeMillis()) + ".jpg");
                    final UploadTask uploadTask = picRef.putBytes(arr);
                    //mProgressBar.setVisibility(View.VISIBLE);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure", e);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            Toast.makeText(getContext(), "Image Uploading failed!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.e(TAG, "Succesfully uploaded files!");
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw Objects.requireNonNull(task.getException());
                                    }
                                    Log.e(TAG, "Trigger point here!!!");
                                    // Continue with the task to get the download URL
                                    return picRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        myUser.setProfilePic(downloadUri.toString());
                                        database.getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(myUser);
                                        Log.d(TAG, "Success: " + downloadUri.toString());
                                        Toast.makeText(getContext(), "User successfully updated!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.d(TAG, "Failure!", task.getException());
                                        Toast.makeText(getContext(), "Unsuccesfull ", Toast.LENGTH_LONG).show();
                                    }
                                    Log.e(TAG, String.valueOf(myUser.getProfilePic()));
                                    //mProgressBar.setVisibility(View.INVISIBLE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //mProgressBar.setProgress((int) progress);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void permissionRequest(){
        int req = ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if (req != PackageManager.PERMISSION_GRANTED/*=0*/){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE/*ide manifest.permission.R_E_S kellene */},REQ_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d (TAG,"permission ok");
        }else {
        Log.d(TAG,"permission filed");
        }
    }


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
        Log.d(TAG,"chooseimage() OK");
    }
    private void uploadImagesToStorage(){

        final String key = database.getReference("Advertisements").push().getKey();
        Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //mConstraintLayout.setAlpha(0.2F);
        image.setAlpha(0.2F);


    }
    private void loadImageFromStorage()
    {
        try{
            File f=new File(filePath, "");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            image.setImageBitmap(b);
        }catch (FileNotFoundException e){
            Log.e(TAG, "loadImageFromStorage: FileNotFoundException: " + e.getMessage() );
        }

    }

    public void imageUpload(){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+UUID.randomUUID().toString());
            StorageTask<UploadTask.TaskSnapshot> u =
                    ref.putFile(Uri.parse(filePath))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext() ,"Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(),"Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,e.getMessage());
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded"+(int)progress+"%");
                                }
                            });
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {

            if(data != null){
                imgUri = data.getData();
                if(imgUri != null){
                    filePath = PathParser.getPathFromUri(getContext(),imgUri);
                    Log.d(TAG,filePath);
                    Glide.with(getView())
                            .load(new File(filePath)).apply(RequestOptions.circleCropTransform())
                            .into(image);
                }

            }
        }
    }
    private void getUserInfo(){
        database.getReference().child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myUser=dataSnapshot.getValue(User.class);
                mUserEmail.setText(Objects.requireNonNull(myUser).getEmail());
                mUserPhoneNum.setText(myUser.getPhoneNum());
                mUserName.setText(myUser.getName());
                if (getActivity() == null || getActivity().isDestroyed()){
                    return;
                }
                Glide.with(Objects.requireNonNull(getContext())).load(myUser.getProfilePic()).apply(RequestOptions.circleCropTransform()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"adatot nem tudtuk lekerni",databaseError.toException());
            }
        });

    }

}
