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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ro.sapientia.ms.sapiadvertiser.Fragments.DetailsFragment;
import ro.sapientia.ms.sapiadvertiser.Model.Advertisement;
import ro.sapientia.ms.sapiadvertiser.R;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdHolder> {

    private ArrayList<Advertisement> adList;
    private Context context;

    static class AdHolder extends RecyclerView.ViewHolder{

        TextView titleView,descView,numOfViewsView;
        ImageView advertImage;
        CircleImageView profilePic;

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
        Glide.with(context).load(adList.get(i).getFirstImage()).into(adHolder.advertImage);
        Glide.with(context).load(adList.get(i).getFirstImage()).into(adHolder.profilePic);
        adHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("advertismenets")
                        .child(adList.get(adHolder.getAdapterPosition()).getAdID())
                        .child("numOfViews")
                        .setValue(adList.get(adHolder.getAdapterPosition()).getNumOfViews() + 1);
                DetailsFragment frag = new DetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("Advertisement",adList.get(adHolder.getAdapterPosition()));
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
