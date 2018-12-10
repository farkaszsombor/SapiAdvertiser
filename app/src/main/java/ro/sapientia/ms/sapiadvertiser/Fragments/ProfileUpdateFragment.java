package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import ro.sapientia.ms.sapiadvertiser.Fragments.ProfileUpdateFragment;
import ro.sapientia.ms.sapiadvertiser.Model.User;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.PathParser;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileUpdateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUpdateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private static final String TAG = "ProfileUploadActivity";
    private EditText mUserName;
    private EditText mUserPhoneNum;
    private EditText mUserEmail;
    private Button mSaveButton;
    private Button mUploadButton;
    private de.hdodenhof.circleimageview.CircleImageView image;

    private ProgressDialog mProgressDialog;
    private ArrayList<String> pathArray;
    private StorageReference mStorageRef; //valami ma'gikus import valahova
    private String filePath;
    private final int PICK_IMAGE_REQUEST=71;

    //fireabase
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseStorage storage;
    StorageReference storageReference;

    DatabaseReference mUserDatabase;


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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_update,container,false);
        //firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init view
        mUserName=view.findViewById(R.id.profileName);
        mUserPhoneNum = view.findViewById(R.id.phoneNum);
        mUserEmail = view.findViewById(R.id.email);
        mSaveButton = view.findViewById(R.id.saveButton);
        mUploadButton = view.findViewById(R.id.btnUpload);
        image=view.findViewById(R.id.profile_image);
        mProgressDialog = new ProgressDialog(getContext());
        pathArray = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Inflate the layout for this fragment


        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Uploading Image.");
                mProgressDialog.setMessage("Uploading Image...");
                mProgressDialog.show();

                //get the signed in user
                FirebaseUser user = mAuth.getCurrentUser();
                String userID = Objects.requireNonNull(user).getUid(); // van ennek id-ja alapból?

                String name = mUserPhoneNum.toString();


            }

        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final User myUser = new User(mUserName.toString(), mUserEmail.toString(), mUserPhoneNum.toString());

                final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(myUser.getName())
                        // maaaajd .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();
                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated.");
                                }
                            }
                        });
                currentUser.updateEmail(myUser.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                }
                            }
                        });


            }
        });


        return inflater.inflate(R.layout.fragment_profile_update, container, false);
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

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Uri uri;
            if(data != null){
                uri = data.getData();
                if(uri != null){
                    filePath = PathParser.getPathFromUri(getContext(),uri);
                }
            }
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
}
