package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ro.sapientia.ms.sapiadvertiser.Interfaces.IAuthCb;
import ro.sapientia.ms.sapiadvertiser.Model.User;
import ro.sapientia.ms.sapiadvertiser.Utils.FireBaseAuthUtils;
import ro.sapientia.ms.sapiadvertiser.Utils.NetworkUtils;
import ro.sapientia.ms.sapiadvertiser.R;

public class SignUpActivity extends BasicActivity implements IAuthCb {

    private static final String TAG = "SignUpActivity";

    private Button mSignUpButton;
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private String mVerificationId;
    private String mEmail;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mButtonType = false;
    private FirebaseAuth mAuth;

    private IAuthCb callback=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        init();


        if (!NetworkUtils.isNetworkAvailable(SignUpActivity.this)) {

            toggleViews(false);

        } else {
            mSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mButtonType)
                    {
                        String verificationCode= mEmailEditText.getText().toString();
                        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                        FireBaseAuthUtils.FireBaseAuth(callback,mAuth,credential,TAG);
                    }
                    else PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mPhoneEditText.getText().toString(), 30L /*timeout*/, TimeUnit.SECONDS,
                            SignUpActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                @Override
                                public void onCodeSent(String verificationId,
                                                       PhoneAuthProvider.ForceResendingToken token) {
                                    // The SMS verification code has been sent to the provided phone number, we
                                    // now need to ask the user to enter the code and then construct a credential
                                    // by combining the code with a verification ID.
                                    Log.d(TAG, "onCodeSent:" + verificationId);

                                    // Save verification ID and resending token so we can use them later
                                    mVerificationId = verificationId;
                                    mResendToken = token;

                                    mUserNameEditText.setVisibility(View.INVISIBLE);
                                    mPhoneEditText.setVisibility(View.INVISIBLE);
                                    mButtonType = true;
                                    mEmail=mEmailEditText.getText().toString();
                                    mEmailEditText.setText("");
                                    mEmailEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    mEmailEditText.setHint("Verification Code");
                                    mSignUpButton.setText("Enter Code");
                                }

                                @Override
                                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                    FireBaseAuthUtils.FireBaseAuth(callback, mAuth, phoneAuthCredential, TAG);
                                }

                                @Override
                                public void onVerificationFailed(FirebaseException e) {
                                    // ...


                                    Log.d(TAG, e.getMessage());
                                    Log.d(TAG, e.getStackTrace().toString());
                                    Toast.makeText(SignUpActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
                                }

                            });

                }
            });
        }


    }


    private void init()
    {
        mViews= new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        mSignUpButton=findViewById(R.id.signUpButton);
        mUserNameEditText=findViewById(R.id.userNameEditText);
        mEmailEditText=findViewById(R.id.emailEditText);
        mPhoneEditText=findViewById(R.id.phoneNumberEditText);
        noInternetTextView=findViewById(R.id.noInternetTextView);

        mViews.add(mSignUpButton);
        mViews.add(mUserNameEditText);
        mViews.add(mEmailEditText);
        mViews.add(mPhoneEditText);
    }


    @Override
    public void Succses(Task<AuthResult> task) {
            Log.d(TAG, "signInWithCredential:success");

            FirebaseUser user = task.getResult().getUser();
            if(user!=null)
            { User me= new User(mUserNameEditText.getText().toString(),mEmail,user.getPhoneNumber());
                try
                {
                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(me.getName()).build());
                user.updateEmail(me.getEmail());
                FirebaseDatabase database =FirebaseDatabase.getInstance();

                database.getReference("users").child(user.getUid()).setValue(me);
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                }
                catch (Exception e)
                {
                    Log.d(TAG, " database.getReference(\"users\").child(user.getUid()).setValue(me);");
                    Log.d(TAG, e.getMessage());
                    Log.d(TAG, e.getStackTrace().toString());


                    Intent intent = new Intent(SignUpActivity.this, DebugActivity.class);
                    intent.putExtra("msg",me.getName()+"--"+me.getEmail()+"::"+me.getPhoneNum());
                    startActivity(intent);
                }

            }
            else
            {
                Intent intent = new Intent(SignUpActivity.this, DebugActivity.class);
                intent.putExtra("msg","NULL POINTER EXCEPTION");
                startActivity(intent);
            }
            finish();
    }

}
