package ro.sapientia.ms.sapiadvertiser.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ro.sapientia.ms.sapiadvertiser.Model.Advertisement;
import ro.sapientia.ms.sapiadvertiser.R;
import ro.sapientia.ms.sapiadvertiser.Utils.AdAdapter;


public class ListFragment extends Fragment {

    private final static String TAG = ListFragment.class.getSimpleName();
    private String filterOption;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference advertisementRef = FirebaseDatabase.getInstance().getReference("advertismenets");
    private ArrayList<Advertisement> adDataSet = new ArrayList<>();

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(String filter) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("filter",filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterOption = getArguments().getString("filter");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.list_of_ads);
        setUpRecyclerView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setUpRecyclerView(){

        adDataSet.clear();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new AdAdapter(getContext(),adDataSet);
        advertisementRef.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot element : dataSnapshot.getChildren()) {
                    Advertisement advertisement;
                    if(filterOption.equals("UserOnly")){
                        if(!Objects.requireNonNull(element.child("creatorID").getValue()).equals(FirebaseAuth.getInstance().getUid())){
                            continue;
                        }
                        advertisement = element.getValue(Advertisement.class);
                    }
                    else{
                        advertisement = element.getValue(Advertisement.class);
                    }
                    if(!Objects.requireNonNull(advertisement).isIsReported()&& !advertisement.getIsDeleted()){
                        adDataSet.add(advertisement);
                    }
                }
                Collections.reverse(adDataSet);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,databaseError.getMessage());
            }
        });
    }

}
