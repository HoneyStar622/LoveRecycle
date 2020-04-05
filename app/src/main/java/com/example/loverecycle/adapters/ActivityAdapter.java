package com.example.loverecycle.adapters;

import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loverecycle.R;
import com.example.loverecycle.beans.ActivityBean;

import static com.example.loverecycle.Constants.IMG_IP;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    List<ActivityBean> mActivityList;
    Context context;
    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View activityView;
        ImageView activityImg;
        TextView activityName;
        TextView activityAssociation;
        TextView activityDate;
        TextView activityInfo;

        public ViewHolder(View view) {
            super(view);
            activityView = view;
            activityImg = view.findViewById(R.id.activity_img);
            activityName = view.findViewById(R.id.activity_name);
            activityAssociation = view.findViewById(R.id.activity_association);
            activityInfo = view.findViewById(R.id.activity_info);
            activityDate = view.findViewById(R.id.activity_Date);
            activityInfo.setSelected(true);
            activityName.setSelected(true);
            activityAssociation.setSelected(true);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });

        }
        return holder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ActivityBean activity = mActivityList.get(position);
        //HttpHelp.getInstance().getURLimage(activity.getImg());
        //holder.activityImg.setImageResource(activity.getImg());
        holder.activityName.setText(activity.getName());
        holder.activityInfo.setText(activity.getInfo());
        //holder.activityAssociation.setText(activity.getAssociationId());
        Glide.with(context).load(IMG_IP+activity.getIcon()).into(holder.activityImg);
        holder.activityDate.setText(UTCStringtODefaultString(activity.getStartDate())+" 到 "+ UTCStringtODefaultString(activity.getEndDate()));

    }

    @Override
    public int getItemCount() {
        return mActivityList.size();
    }

    public ActivityAdapter(Context context,List<ActivityBean> activityList) {
        mActivityList = activityList;
        this.context = context;
    }

    public void refresh( List<ActivityBean> list) {
        mActivityList = list;
        notifyDataSetChanged();
    }

    public static String UTCStringtODefaultString(String UTCString) {
        try
        {

                UTCString = UTCString.replace("Z", " UTC");
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
                SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = utcFormat.parse(UTCString);
                return defaultFormat.format(date);

        } catch(ParseException pe)
        {
            pe.printStackTrace();
            return null;
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);

    }


}