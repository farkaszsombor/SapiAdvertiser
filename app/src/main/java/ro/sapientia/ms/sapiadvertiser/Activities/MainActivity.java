package ro.sapientia.ms.sapiadvertiser.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ro.sapientia.ms.sapiadvertiser.Fragments.CreateAdFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.ListFragment;
import ro.sapientia.ms.sapiadvertiser.Fragments.ProfileUpdateFragment;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.FragmentManager;

public class MainActivity extends BasicActivity {

    private FragmentManager manager;
    private FirebaseAuth mAuth;
    private String name;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment;
            switch (menuItem.getItemId()){
                case R.id.tud:
                    selectedFragment = CreateAdFragment.newInstance();
                    name = "create";
                    break;
                case R.id.home:
                    selectedFragment = ListFragment.newInstance("all");
                    name = "home";
                    break;
                case R.id.profile:
                    selectedFragment = ProfileUpdateFragment.newInstance();
                    name = "profile";
                    break;
                default:
                    selectedFragment = ListFragment.newInstance("all");
                    name = "home";
                    break;
            }
            menuItem.setChecked(true);
            if(!manager.isActive(name)){
                manager.executeTransaction(selectedFragment,R.id.frame_layout, name ,false);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        manager = new FragmentManager(MainActivity.this);
        if(savedInstanceState != null){
            name = savedInstanceState.getString("fragmentName");
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(name);
            manager.executeTransaction(fragment,R.id.frame_layout,name,true);
        }
        else {
            manager.executeTransaction(ListFragment.newInstance("all"), R.id.frame_layout, "home", true);
            bottomNavigationView.getMenu().getItem(1).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Intent authIntent= new Intent(MainActivity.this,SignUpActivity.class);
            startActivity(authIntent);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("fragmentName",name);
        super.onSaveInstanceState(outState);
    }
}
