package com.example.loverecycle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loverecycle.R;
import com.example.loverecycle.beans.OrderBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.loverecycle.Constants.IMG_IP;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        List<String> mMyList;
        Context context;
private MyAdapter.OnItemClickListener onItemClickListener = null;

public void setOnItemClickListener(MyAdapter.OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
        }

static class ViewHolder extends RecyclerView.ViewHolder {
    View myView;
    ImageView myImg;
    TextView myText;

    public ViewHolder(View view) {
        super(view);
        myView = view;
        myImg = view.findViewById(R.id.img_my_item);
        myText = view.findViewById(R.id.tv_my_item);
    }
}


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_item, parent, false);
        final MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(view);
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
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        String myItem= mMyList.get(position);

        switch (myItem) {
            case "修改信息":
                holder.myImg.setImageResource(R.drawable.my);
                break;
            case "修改密码":
                holder.myImg.setImageResource(R.drawable.my_password);
                break;
            case "我的社团":
                holder.myImg.setImageResource(R.drawable.my_club);
                break;
            case "应用设置":
                holder.myImg.setImageResource(R.drawable.my_setting);
                break;
            case "意见反馈":
                holder.myImg.setImageResource(R.drawable.my_advise);
                break;
            case "关于我们":
                holder.myImg.setImageResource(R.drawable.my_info);
                break;
        }
        holder.myText.setText(myItem);




    }

    @Override
    public int getItemCount() {
        return mMyList.size();
    }

    public MyAdapter(Context context, List<String> MyList) {
        mMyList = MyList;
        this.context = context;
    }

    public void refresh( List<String> list) {
        mMyList = list;
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
