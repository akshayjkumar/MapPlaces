package com.ajdev.aroundme.adapters;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajdev.aroundme.R;
import com.ajdev.aroundme.model.Geometry;
import com.ajdev.aroundme.model.Photo;
import com.ajdev.aroundme.model.Result;
import com.ajdev.aroundme.utils.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for RecyclerView
 *
 * Created by Akshay.Jayakumar on 10/12/2017
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.NearByPlaceViewHolder> {
    private Context context;
    private List<Result> nearByPlacesList;
    private OnItemClickListener mItemClickListener;
    private Location location;
    private int imageWH;

    public SearchResultAdapter(Context context) {
        this.context = context;
        nearByPlacesList = new ArrayList<>();
        imageWH = Utilities.convertDPToPixels(context,80);
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        if (nearByPlacesList.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public NearByPlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = null;
        if (viewType == 0) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.layout_search_item_default, viewGroup, false);
        } else {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.layout_search_item, viewGroup, false);
        }

        return new NearByPlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NearByPlaceViewHolder nearByPlaceViewHolder, int position) {
        if (nearByPlacesList.isEmpty()) {
            return;
        }
        Result result = nearByPlacesList.get(position);
        nearByPlaceViewHolder.pointName.setText(result.getName());
        nearByPlaceViewHolder.pointName.setGravity(Gravity.START | Gravity.LEFT);

        Geometry geometry = result.getGeometry();
        if(getLocation() != null && geometry != null && geometry.getLocation() != null){
            try {
                Location destinationLoc = new Location("destination");
                destinationLoc.setLatitude(geometry.getLocation().getLatitude());
                destinationLoc.setLongitude(geometry.getLocation().getLongitude());
                float distanceInMeters = getLocation().distanceTo(destinationLoc);
                nearByPlaceViewHolder.pointDistance.setText(String.format("%.1f m", distanceInMeters));
            }catch (Exception e){}
        }

        if(result.getOpeningTime() != null && result.getOpeningTime().isOpeningHours())
            nearByPlaceViewHolder.pointOpenHours.setVisibility(View.VISIBLE);
        else
            nearByPlaceViewHolder.pointOpenHours.setVisibility(View.INVISIBLE);


        List<Photo> photos = result.getPhotos();
        if(photos != null && photos.size() > 0) {
            Picasso.with(context)
                .load(Utilities.makePhotoUrl(photos.get(0).getPhotoReference()))
                .error(R.drawable.drawable_image_not_available)
                .resize(imageWH,imageWH).centerCrop()
                .into(nearByPlaceViewHolder.pointImage);
        }else{
            Picasso.with(context)
                .load(R.drawable.drawable_image_not_available)
                .resize(imageWH,imageWH).centerCrop()
                .into(nearByPlaceViewHolder.pointImage);
        }
    }

    @Override
    public int getItemCount() {
        if (nearByPlacesList.isEmpty()) {
            return 1;
        }
        return nearByPlacesList.size();
    }

    public void setDataList(List<Result> resultList) {
        this.nearByPlacesList.clear();
        this.nearByPlacesList.addAll(resultList);
        notifyDataSetChanged();
    }

    public void reset() {
        this.nearByPlacesList.clear();
        notifyDataSetChanged();
    }

    /* viewHolder */
    class NearByPlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView pointImage;
        TextView pointName;
        TextView pointDistance;
        TextView pointOpenHours;

        NearByPlaceViewHolder(View v) {
            super(v);
            if (nearByPlacesList.isEmpty()) {
                return;
            }
            pointImage = (ImageView) v.findViewById(R.id.place_icon_iv);
            pointName = (TextView) v.findViewById(R.id.place_name_tv);
            pointDistance = (TextView) v.findViewById(R.id.place_distance_tv);
            pointOpenHours = (TextView) v.findViewById(R.id.open_now_tv);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, nearByPlacesList.get(getAdapterPosition()));
            }
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /* onClick listener */
    public interface OnItemClickListener {
        void onItemClick(View v, Result result);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }
}