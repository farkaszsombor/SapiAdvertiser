package ro.sapientia.ms.sapiadvertiser;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends BasicActivity  {

    private static final String TAG = "SignUpActivity";

    private Button mSignUpButton;
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean mButtonType = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        init();


        if (!Utilss.isNetworkAvailable(SignUpActivity.this)) {

            toggleViews();

        } else {
            mSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mButtonType)
                    {
                        String verificationCode= mEmailEditText.getText().toString();
                        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                    else {

                        //helyes irhatott be telefonszamot startbol
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
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
                                        mButtonType=true;

                                        mEmailEditText.setText("");
                                        mEmailEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                        mEmailEditText.setHint("Verification Code");
                                        mSignUpButton.setText("Enter Code");
                                    }

                                    @Override
                                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                        // Sign in with the credential
                                        // ...
                                        signInWithPhoneAuthCredential(phoneAuthCredential);

                                    }

                                    @Override
                                    public void onVerificationFailed(FirebaseException e) {
                                        // ...


                                        Log.d(TAG,e.getMessage());
                                        Log.d(TAG,e.getStackTrace().toString());
                                        Toast.makeText(SignUpActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
                                    }

                                });

                    }

                }
            });
        }


    }




    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            //Regisztralom utolag a profiljat a beleponek

                            //itt akkor is adhat nullt ha a tasktol nem tudom elvenni ebben az esetben
                            //mAuth.getCurrentUser()-el kerem le ezt LE KELL DEBUGGOLNI ZSOMBI
                            if(user!=null)
                            {
                                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(mUserNameEditText.getText().toString()).build());
                                user.updateEmail(mEmailEditText.getText().toString());

                                //ide kell az Intentbe belerakni a belejentkezes utani mezot
                                Intent intent = new Intent(SignUpActivity.this, DebugActivity.class);
                                intent.putExtra("msg","Beregisztralta magat"+ mUserNameEditText.getText().toString());
                                startActivity(intent);

                            }
                            else
                            {
                                Intent intent = new Intent(SignUpActivity.this, DebugActivity.class);
                                intent.putExtra("msg","NULL POINTER EXCEPTION");
                                startActivity(intent);
                            }
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
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

}
