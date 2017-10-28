package com.abdullahhussein.gdgfirebaseexample.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdullahhussein.gdgfirebaseexample.R;
import com.abdullahhussein.gdgfirebaseexample.interfaces.OnItemClickListener;
import com.abdullahhussein.gdgfirebaseexample.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abdullah Hussein on 25/10/2017.
 * abdullah.hussein109@gmail.com
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private Activity activity;
    private OnItemClickListener listener;
    private ArrayList<User> items;

    public UsersAdapter(Activity activity) {
        this.activity = activity;
        this.items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        try {
            viewHolder.textView_icon.setText(String.valueOf(items.get(position).getName().charAt(0)).toUpperCase());
            viewHolder.textView_username.setText(items.get(position).getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<User> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void appendData(List<User> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void add(int location, User item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void add(User item) {
        items.add(item);
    }

    public void remove(int location) {
        if (location >= items.size())
            return;

        items.remove(location);
        notifyItemRemoved(location);
    }

    public User getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textView_icon, textView_username;

        ViewHolder(final View itemView) {
            super(itemView);

            textView_icon = itemView.findViewById(R.id.textView_icon);
            textView_username = itemView.findViewById(R.id.textView_username);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }
}