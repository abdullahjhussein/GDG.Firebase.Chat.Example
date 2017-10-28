package com.abdullahhussein.gdgfirebaseexample.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abdullahhussein.gdgfirebaseexample.R;
import com.abdullahhussein.gdgfirebaseexample.interfaces.OnItemClickListener;
import com.abdullahhussein.gdgfirebaseexample.model.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abdullah Hussein on 25/10/2017.
 * abdullah.hussein109@gmail.com
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Activity activity;
    private OnItemClickListener listener;
    private ArrayList<Chat> items;

    public ChatsAdapter(Activity activity) {
        this.activity = activity;
        this.items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        try {
            viewHolder.textView_icon.setText(String.valueOf(items.get(position).getTitle().charAt(0)).toUpperCase());
            viewHolder.textView_username.setText(items.get(position).getTitle());
            viewHolder.textView_last_message.setText(items.get(position).getLastMessage());
            viewHolder.textView_time.setText(DateUtils.getRelativeTimeSpanString(items.get(position).getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
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

    public void setData(List<Chat> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void appendData(List<Chat> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void add(int location, Chat item) {
        items.add(location, item);
        notifyItemInserted(location);
    }

    public void add(Chat item) {
        items.add(item);
    }

    public void remove(int location) {
        if (location >= items.size())
            return;

        items.remove(location);
        notifyItemRemoved(location);
    }

    public Chat getItem(int position) {
        return items.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView textView_icon, textView_username, textView_last_message, textView_time;

        ViewHolder(final View itemView) {
            super(itemView);

            textView_icon = itemView.findViewById(R.id.textView_icon);
            textView_username = itemView.findViewById(R.id.textView_username);
            textView_last_message = itemView.findViewById(R.id.textView_last_message);
            textView_time = itemView.findViewById(R.id.textView_time);

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