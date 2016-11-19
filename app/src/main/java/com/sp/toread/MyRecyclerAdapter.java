package com.sp.toread;

/**
 * Created by my on 2016/11/12.
 */
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by my on 2016/10/23.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>  {
    private List<String> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private Cursor mmcursor;
    private DBHelper mmDbhelper;
    int k=0;
    private OnItemClickListener mOnItemClickListener;
    public MyRecyclerAdapter(Context context,Cursor cursor,DBHelper dbHelper){
        this.mContext=context;
        this.mmcursor=cursor;
        this.mmDbhelper=dbHelper;
        inflater= LayoutInflater. from(mContext);
    }
    @Override
    public int getItemCount() {
        return mmcursor.getCount();
    }

    //填充onCreateViewHolder方法返回的holder中的控件
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        mmcursor.moveToPosition(mmcursor.getCount()-position-1);
        holder.tv_title.setText(mmcursor.getString(1));
        holder.tv_writter.setText(mmcursor.getString(2));
        //实现接口
        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    //重写onCreateViewHolder方法，返回一个自定义的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bookview,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_writter;
        public MyViewHolder(View view) {
            super(view);
            tv_title=(TextView)view.findViewById(R.id.bookview_title);
            tv_writter=(TextView) view.findViewById(R.id.bookview_writter);
        }
    }
    public interface OnItemClickListener{
        void onClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }
}