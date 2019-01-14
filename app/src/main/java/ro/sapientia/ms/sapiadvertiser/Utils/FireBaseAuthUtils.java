package ro.sapientia.ms.sapiadvertiser.Utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import ro.sapientia.ms.sapiadvertiser.Interfaces.IAuthCb;


public class FireBaseAuthUtils {

    private static String TAG;
    public static void FireBaseAuth(IAuthCb callback, FirebaseAuth auth,PhoneAuthCredential credential,  String T)
    {
        TAG=T;
        final IAuthCb context=callback;
        auth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) callback, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            context.Succses(task);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "FireBaseAuthUtils.FireBaseAuth:Sign in failed, display a message and update the UI", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Log.w(TAG, "FireBaseAuthUtils.FireBaseAuth:The verification code entered was invalid", task.getException());
                            }
                        }
                    }
                });
    }

}
