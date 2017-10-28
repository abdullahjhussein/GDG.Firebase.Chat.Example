package com.abdullahhussein.gdgfirebaseexample;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.abdullahhussein.gdgfirebaseexample.adapters.UsersAdapter;
import com.abdullahhussein.gdgfirebaseexample.interfaces.OnItemClickListener;
import com.abdullahhussein.gdgfirebaseexample.model.Chat;
import com.abdullahhussein.gdgfirebaseexample.model.Message;
import com.abdullahhussein.gdgfirebaseexample.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    private static final String TAG = "UsersActivity";

    private SwipeRefreshLayout swipeRefreshLayout_users;
    private RecyclerView recyclerView_users;
    private AppCompatTextView textView_no_users;
    private ProgressBar progressBar;

    private User user;
    private ArrayList<User> users = new ArrayList<>();

    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Users");

        user = (User) getIntent().getSerializableExtra("user");

        swipeRefreshLayout_users = findViewById(R.id.swipeRefreshLayout_users);
        recyclerView_users = findViewById(R.id.recyclerView_users);
        textView_no_users = findViewById(R.id.textView_no_users);
        progressBar = findViewById(R.id.progressBar);

        swipeRefreshLayout_users.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout_users.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
            }
        });

        usersAdapter = new UsersAdapter(this);
        usersAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                createNewChat(users.get(position));
            }
        });

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView_users.getContext(), DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getApplicationContext(), R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView_users.addItemDecoration(horizontalDecoration);
        recyclerView_users.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView_users.setAdapter(usersAdapter);

        showProgressBar();

        getUsers();
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

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout_users.setVisibility(View.GONE);
    }

    private void showNoUsers() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout_users.setVisibility(View.VISIBLE);

        recyclerView_users.setVisibility(View.GONE);
        textView_no_users.setVisibility(View.VISIBLE);
    }

    private void showUsers() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout_users.setVisibility(View.VISIBLE);

        recyclerView_users.setVisibility(View.VISIBLE);
        textView_no_users.setVisibility(View.GONE);
    }

    private void getUsers() {

        FirebaseDatabase.getInstance().getReference()
                .child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                swipeRefreshLayout_users.setRefreshing(false);

                users.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    users.add(dataSnapshot1.getValue(User.class));
                }

                usersAdapter.setData(users);

                if (users.isEmpty()) {
                    showNoUsers();
                } else {
                    showUsers();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
            }
        });
    }

    private void createNewChat(final User chatUser) {
        if (user.getContacts().containsKey(chatUser.getName())) {
            getChatData(user.getContacts().get(chatUser.getName()));
        } else {

            final String chatID = "chat_id_" + System.currentTimeMillis();

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(chatID)
                    .setValue(new Chat(user.getName() + " and " + chatUser.getName()), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());
                                createChatMember(chatID, chatUser.getName());
                            } else {
                                Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                                Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                            }
                        }
                    });
        }
    }

    private void getChatData(String chatID) {
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chatID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            goToChatMessagesActivity(chat.setChatID(dataSnapshot.getKey()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void goToChatMessagesActivity(Chat chat) {
        Intent intent = new Intent(getApplicationContext(), ChatMessagesActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("chat", chat);
        startActivity(intent);
    }

    private void createChatMember(final String chatID, final String username) {

        ArrayMap<String, Boolean> members = new ArrayMap<>();

        members.put(user.getName(), true);
        members.put(username, true);

        FirebaseDatabase.getInstance().getReference()
                .child("members")
                .child(chatID)
                .setValue(members, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());
                            createUserContact(user.getName(), username, chatID);
                            createUserContact(username, user.getName(), chatID);
                            createFirstMessage(chatID);
                        } else {
                            Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                            Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                        }
                    }
                });
    }

    private void createUserContact(String username, String contactName, final String chatID) {

        ArrayMap<String, Object> members = new ArrayMap<>();

        members.put(contactName, chatID);

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(username)
                .child("contacts")
                .updateChildren(members, new DatabaseReference.CompletionListener() {
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

    private void createFirstMessage(final String chatID) {

        final Message message = new Message();
        message.setName(user.getName());
        message.setMessage("Hi");
        message.setTimestamp(System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference()
                .child("messages")
                .child(chatID)
                .child("message_id_" + message.getTimestamp())
                .setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());
                            updateChat(chatID, message);
                        } else {
                            Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                            Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                        }
                    }
                });
    }

    private void updateChat(final String chatID, Message message) {

        ArrayMap<String, Object> map = new ArrayMap<>();

        map.put("lastMessage", message.getMessage());
        map.put("timestamp", message.getTimestamp());

        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(chatID)
                .updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());
                            getChatData(chatID);
                        } else {
                            Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                            Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                        }
                    }
                });
    }
}
