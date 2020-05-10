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
import com.example.loverecycle.beans.ActivityBean;
import com.example.loverecycle.beans.OrderBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.loverecycle.Constants.IMG_IP;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    List<OrderBean> mOrderList;
    Context context;
    private OrderAdapter.OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OrderAdapter.OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View OrderView;
        ImageView OrderImg;
        TextView OrderCategory;
        TextView OrderState;
        TextView OrderInfo;

        public ViewHolder(View view) {
            super(view);
            OrderView = view;
            OrderImg = view.findViewById(R.id.order_img);
            OrderCategory = view.findViewById(R.id.order_category);
            OrderState = view.findViewById(R.id.order_state);
            OrderInfo = view.findViewById(R.id.order_info);
            OrderInfo.setSelected(true);
            OrderCategory.setSelected(true);
        }
    }


    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        final OrderAdapter.ViewHolder holder = new OrderAdapter.ViewHolder(view);
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
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        OrderBean order= mOrderList.get(position);
        holder.OrderInfo.setText(order.getInfo());
        holder.OrderCategory.setText(order.getCategory());
        holder.OrderState.setText(order.getState());
        String icons = order.getPicture();
        if (!icons.isEmpty()){
            String icon = icons.substring(0, icons.indexOf(";"));
            Glide.with(context).load(IMG_IP+icon).into(holder.OrderImg);
        }



    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public OrderAdapter(Context context,List<OrderBean> OrderList) {
        mOrderList = OrderList;
        this.context = context;
    }

    public void refresh( List<OrderBean> list) {
        mOrderList = list;
        notifyDataSetChanged();
    }

    public static String UTCStringtODefaultString(String UTCString) {
        try
        {

            UTCString = UTCString.replace("Z", " UTC");
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
            SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
