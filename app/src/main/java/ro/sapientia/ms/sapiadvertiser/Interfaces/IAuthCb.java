package ro.sapientia.ms.sapiadvertiser.Interfaces;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public interface IAuthCb {

    void Succses(Task<AuthResult> task);

}
