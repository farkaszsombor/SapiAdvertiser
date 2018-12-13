package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ro.sapientia.ms.sapiadvertiser.R;

public class LoginActivity extends BasicActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private Button mButton;
    private Button mSignUpButton;
    private EditText mEditText;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private boolean mButtonType = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ro.sapientia.ms.sapiadvertiser.R.layout.activity_login);

        init();//inicializalom a fuggvenyeket ez mindenhol meg kell irni


        //mindegyik activitybe be kell irni ezt a fuggvenyt
        if (!Utilss.isNetworkAvailable(LoginActivity.this)) {

            toggleViews();

        } else {

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClickListener");
                    if (mButtonType) {
                        String verificationCode = mEditText.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                mEditText.getText().toString(), 30L /*timeout*/, TimeUnit.SECONDS,
                                LoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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
                                        mButtonType = true;
                                        mButton.setText("Enter Code");
                                        mEditText.setHint("Veryfy Code");
                                        mEditText.setText("");
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
                                        Log.d(TAG, e.getMessage());
                                        Log.d(TAG, e.getStackTrace().toString());
                                        Toast.makeText(LoginActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
                                    }

                                });
                    }
                }
            });

            mSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
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

                            //itt akkor is adhat nullt ha a tasktol nem tudom elvenni ebben az esetben
                            //mAuth.getCurrentUser()-el kerem le ezt LE KELL DEBUGGOLNI ZSOMBI
                            if(user.getDisplayName()==null)
                            {
                                mAuth.getInstance().signOut();
                                Intent intent = new Intent(LoginActivity.this, DebugActivity.class);
                                intent.putExtra("msg","Sikertelenul jelentkezett be mert nem volt profilja"+ user.getPhoneNumber());
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                //ide kell az Intentbe belerakni a belejentkezes utani mezot
                                Intent intent = new Intent(LoginActivity.this, DebugActivity.class);
                                intent.putExtra("msg","Bejelentkezett"+ user.getDisplayName());
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

    private void  init()
    {
        mViews= new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        mButton = findViewById(ro.sapientia.ms.sapiadvertiser.R.id.loginButton);
        mSignUpButton = findViewById(ro.sapientia.ms.sapiadvertiser.R.id.signUpButton);
        mEditText=findViewById(ro.sapientia.ms.sapiadvertiser.R.id.loginPhoneNumberEditText);

        noInternetTextView=findViewById(R.id.intrnetIsMissing);//fontos h mindig a noInternetTextView -ra rakjuk ra azt a neki megfelelo mezot a toggle biztositja utana a dolgokat

        mViews.add(mButton);
        mViews.add(mSignUpButton);
        mViews.add(mEditText);
    }

}
