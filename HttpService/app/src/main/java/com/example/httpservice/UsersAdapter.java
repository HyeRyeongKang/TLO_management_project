package com.example.httpservice;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CustomViewHolder> {
    private ArrayList<ProblemData> mList = null;
    private Activity context = null;


    public UsersAdapter(Activity context, ArrayList<ProblemData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView id;
        protected TextView name;
        protected TextView level;


        public CustomViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.textView_list_id);
            this.name = (TextView) view.findViewById(R.id.textView_list_name);
            this.level = (TextView) view.findViewById(R.id.textView_list_level);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.id.setText(mList.get(position).get_id());
        viewholder.name.setText(mList.get(position).get_name());
        viewholder.level.setText(mList.get(position).get_level());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}
