package ro.sapientia.ms.sapiadvertiser.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import ro.sapientia.ms.sapiadvertiser.Fragments.DetailsFragment;
import ro.sapientia.ms.sapiadvertiser.Model.Advertisement;
import ro.sapientia.ms.sapiadvertiser.Model.User;
import ro.sapientia.ms.sapiadvertiser.R;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private ArrayList<Advertisement> adList;
    private Context context;

    static class AdHolder extends RecyclerView.ViewHolder{

        TextView titleView,descView,numOfViewsView;
        ImageView advertImage;
        ImageView profilePic;

        AdHolder(@NonNull final View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.ad_title);
            descView = itemView.findViewById(R.id.ad_description);
            numOfViewsView = itemView.findViewById(R.id.num_of_seen);
            advertImage = itemView.findViewById(R.id.ad_picture);
            profilePic = itemView.findViewById(R.id.uploader_picture);
        }
    }

    public AdAdapter(Context context,ArrayList<Advertisement> adList) {
        this.adList = adList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_list_element,viewGroup,false);
        return new AdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdHolder adHolder, int i) {

        adHolder.titleView.setText(adList.get(i).getTitle());
        adHolder.descView.setText(adList.get(i).getShortDescription());
        adHolder.numOfViewsView.setText(String.valueOf(adList.get(i).getNumOfViews()));
        FirebaseDatabase.getInstance().getReference("users").child(adList.get(i).getCreatorID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(Objects.requireNonNull(user).getProfilePic()).apply(RequestOptions.circleCropTransform()).into(adHolder.profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Glide.with(context).load(adList.get(i).getFirstImage()).into(adHolder.advertImage);
        adHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("advertismenets")
                        .child(adList.get(adHolder.getAdapterPosition()).getAdID())
                        .child("numOfViews")
                        .setValue(adList.get(adHolder.getAdapterPosition()).getNumOfViews() + 1);
                DetailsFragment frag = DetailsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Advertisement",adList.get(adHolder.getAdapterPosition()));
                bundle.putString("show","show");
                frag.setArguments(bundle);
                FragmentManager manager = new FragmentManager(context);
                manager.executeTransaction(frag,R.id.frame_layout,"details",false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }


}
