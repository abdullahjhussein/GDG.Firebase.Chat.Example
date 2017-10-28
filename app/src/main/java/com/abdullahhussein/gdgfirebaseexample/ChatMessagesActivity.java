package com.abdullahhussein.gdgfirebaseexample;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.abdullahhussein.gdgfirebaseexample.model.Chat;
import com.abdullahhussein.gdgfirebaseexample.model.Message;
import com.abdullahhussein.gdgfirebaseexample.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatMessagesActivity extends AppCompatActivity {

    private static final String TAG = "ChatMessagesActivity";

    private RecyclerView recyclerView_messages;
    private AppCompatTextView textView_no_messages;
    private AppCompatEditText editText_message;
    private FloatingActionButton fab_send_message;

    private User user;
    private Chat chat;

    private Query sChatQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        user = (User) getIntent().getSerializableExtra("user");
        chat = (Chat) getIntent().getSerializableExtra("chat");

        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(chat.getTitle());
        getSupportActionBar().setSubtitle(DateUtils.getRelativeTimeSpanString(chat.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

        sChatQuery = FirebaseDatabase.getInstance().getReference().child("messages").child(chat.getChatID()).limitToLast(50);

        recyclerView_messages = findViewById(R.id.recyclerView_messages);
        textView_no_messages = findViewById(R.id.textView_no_messages);
        editText_message = findViewById(R.id.editText_message);
        fab_send_message = findViewById(R.id.fab_send_message);
        fab_send_message.setEnabled(false);

        recyclerView_messages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        editText_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fab_send_message.setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewMessage();
            }
        });

        attachRecyclerViewAdapter();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoMessages(boolean b) {
        textView_no_messages.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView_messages.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        recyclerView_messages.setAdapter(adapter);
    }

    private RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(sChatQuery, Message.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            @Override
            public int getItemViewType(int position) {
                return user.getName().equals(getItem(position).getName()) ? 1 : 2;
            }

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                MessageViewHolder viewHolder;

                if (viewType == 1)
                    viewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sender_message, parent, false));
                else
                    viewHolder = new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receiver_message, parent, false));

                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(MessageViewHolder holder, int position, Message model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                showNoMessages(getItemCount() == 0);
            }
        };
    }


    private void createNewMessage() {

        final Message message = new Message();
        message.setName(user.getName());
        message.setMessage(editText_message.getText().toString().trim());
        message.setTimestamp(System.currentTimeMillis());

        editText_message.setText("");

        FirebaseDatabase.getInstance().getReference()
                .child("messages")
                .child(chat.getChatID())
                .child("message_id_" + message.getTimestamp())
                .setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());
                            updateChat(message);
                        } else {
                            Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                            Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                        }
                    }
                });
    }

    private void updateChat(Message message) {

        ArrayMap<String, Object> map = new ArrayMap<>();

        map.put("lastMessage", message.getMessage());
        map.put("timestamp", message.getTimestamp());

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chat.getChatID())
                .updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());
                        } else {
                            Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                            Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                        }
                    }
                });
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView textView_icon, textView_message, textView_time;

        MessageViewHolder(final View itemView) {
            super(itemView);

            textView_icon = itemView.findViewById(R.id.textView_icon);
            textView_message = itemView.findViewById(R.id.textView_message);
            textView_time = itemView.findViewById(R.id.textView_time);
        }

        void bind(Message model) {
            textView_icon.setText(String.valueOf(model.getName().charAt(0)).toUpperCase());
            textView_message.setText(model.getMessage());
            textView_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        }
    }
}
