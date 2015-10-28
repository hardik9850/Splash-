package com.splash.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.splash.MainActivity;
import com.splash.R;
import com.splash.model.Item;

import java.util.ArrayList;

/* Created by hardik on 20/08/15.
*/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private Activity mContext;
    private ArrayList<Item> mItemList;
    public Bitmap bitmapImage;
    private RecyclerView recyclerView;


    public RecyclerViewAdapter() {
    }

    public RecyclerViewAdapter(Activity context, ArrayList<Item> list, Bitmap bitmap,RecyclerView recyclerViewParam) {
        mContext = context;
        mItemList = list;
        bitmapImage = bitmap;
        recyclerView = recyclerViewParam;
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.indexOfChild(v);
        Toast.makeText(mContext, "clicked " + position, Toast.LENGTH_LONG).show();
        addEffect(position);
        ((MainActivity)mContext).changeUI(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.adapter_txt);
            //txtTitle.setOnClickListener(new RecyclerViewAdapter());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_adapter, viewGroup, false);
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.txtTitle.setText(mItemList.get(position).getTitle());

    }


    @Override
    public void onViewRecycled(ViewHolder holder) {
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    private void addEffect(int position){
        System.out.print("9850 "+position);
    }

}
