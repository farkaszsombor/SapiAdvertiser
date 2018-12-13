package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import ro.sapientia.ms.sapiadvertiser.R;

public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Intent myIntent = getIntent();
        String msg=myIntent.getStringExtra("msg");

        TextView e1=findViewById(R.id.debugTexView);

        e1.setText(msg);
    }
}
