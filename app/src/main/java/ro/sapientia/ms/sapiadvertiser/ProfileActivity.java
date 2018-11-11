package ro.sapientia.ms.sapiadvertiser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {

    private EditText mUserName;
    //private CircleImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //pelda iatt konstanst hasznalok
        String nev="Pista";

        mUserName=findViewById(R.id.profileNameEditText);

        mUserName.setText(nev);



    }
}
