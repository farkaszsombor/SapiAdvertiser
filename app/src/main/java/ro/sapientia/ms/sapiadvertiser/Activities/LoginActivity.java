package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ro.sapientia.ms.sapiadvertiser.Interfaces.IAuthCb;
import ro.sapientia.ms.sapiadvertiser.Utils.FireBaseAuthUtils;
import ro.sapientia.ms.sapiadvertiser.Utils.NetworkUtils;
import ro.sapientia.ms.sapiadvertiser.R;

public class LoginActivity extends BasicActivity implements IAuthCb {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private Button mButton;
    private Button mSignUpButton;
    private EditText mEditText;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private boolean mButtonType = false;

    private IAuthCb callback=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();//inicializalom a fuggvenyeket ez mindenhol meg kell irni


        //mindegyik activitybe be kell irni ezt a fuggvenyt
        if (!NetworkUtils.isNetworkAvailable(LoginActivity.this)) {

            toggleViews(false);

        } else {

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClickListener");
                    if (mButtonType) {
                        String verificationCode = mEditText.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        FireBaseAuthUtils.FireBaseAuth(callback,mAuth,credential,TAG);
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
                                        mSignUpButton.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                        FireBaseAuthUtils.FireBaseAuth(callback, mAuth, phoneAuthCredential, TAG);
                                    }

                                    @Override
                                    public void onVerificationFailed(FirebaseException e) {

                                        if(e instanceof FirebaseAuthInvalidCredentialsException)
                                        {
                                            Toast.makeText(LoginActivity.this, "onVerificationFailed: Hiba a szamban", Toast.LENGTH_SHORT).show();
                                        }
                                        else {

                                            // ...
                                            Log.d(TAG, e.getMessage());
                                            Log.d(TAG, e.getStackTrace().toString());
                                            Toast.makeText(LoginActivity.this, "onVerificationFailed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
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

    private void  init()
    {
        mViews= new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        mButton = findViewById(R.id.loginButton);
        mSignUpButton = findViewById(R.id.registerButton);
        mEditText=findViewById(R.id.loginPhoneNumberEditText);

        noInternetTextView=findViewById(R.id.intrnetIsMissing);//fontos h mindig a noInternetTextView -ra rakjuk ra azt a neki megfelelo mezot a toggle biztositja utana a dolgokat

        mViews.add(mButton);
        mViews.add(mSignUpButton);
        mViews.add(mEditText);
    }
    @Override
    public void Succses(Task<AuthResult> task) {
        Log.d(TAG, "signInWithCredential:success");
        FirebaseUser user = task.getResult().getUser();

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
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }



        finish();
    }
}
