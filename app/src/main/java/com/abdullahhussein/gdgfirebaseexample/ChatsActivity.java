package com.abdullahhussein.gdgfirebaseexample;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.abdullahhussein.gdgfirebaseexample.adapters.ChatsAdapter;
import com.abdullahhussein.gdgfirebaseexample.interfaces.OnItemClickListener;
import com.abdullahhussein.gdgfirebaseexample.model.Chat;
import com.abdullahhussein.gdgfirebaseexample.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {

    private static final String TAG = "ChatsActivity";

    private SwipeRefreshLayout swipeRefreshLayout_chats;
    private RecyclerView recyclerView_chats;
    private AppCompatTextView textView_no_chats;
    private ProgressBar progressBar;

    private User user;

    private ArrayList<Chat> chats = new ArrayList<>();
    private ChatsAdapter chatsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Chats");

        user = (User) getIntent().getSerializableExtra("user");

        swipeRefreshLayout_chats = findViewById(R.id.swipeRefreshLayout_chats);
        recyclerView_chats = findViewById(R.id.recyclerView_chats);
        textView_no_chats = findViewById(R.id.textView_no_chats);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.fab_new_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUsersActivity();
            }
        });

        swipeRefreshLayout_chats.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout_chats.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
            }
        });

        chatsAdapter = new ChatsAdapter(this);
        chatsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                goToChatMessagesActivity(position);
            }
        });

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView_chats.getContext(), DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getApplicationContext(), R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView_chats.addItemDecoration(horizontalDecoration);
        recyclerView_chats.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView_chats.setAdapter(chatsAdapter);

        if (user.getContacts().keySet().isEmpty()) {
            showNoChats();
        } else {

            showProgressBar();

            getChats();
        }
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
        swipeRefreshLayout_chats.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }


    private void showChats() {
        swipeRefreshLayout_chats.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        recyclerView_chats.setVisibility(View.VISIBLE);
        textView_no_chats.setVisibility(View.GONE);
    }

    private void showNoChats() {
        swipeRefreshLayout_chats.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        recyclerView_chats.setVisibility(View.GONE);
        textView_no_chats.setVisibility(View.VISIBLE);
    }

    private void goToChatMessagesActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), ChatMessagesActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("chat", chatsAdapter.getItem(position));
        startActivity(intent);
    }

    private void goToUsersActivity() {
        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void getContacts() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(user.getName())
                .child("contacts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 0) {
                            showNoChats();
                        } else {
                            user.getContacts().clear();

                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                user.getContacts().put(dataSnapshot1.getKey(), dataSnapshot1.getValue(String.class));
                            }

                            getChats();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error while login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getChats() {

        chats.clear();

        for (String key : user.getContacts().keySet()) {
            getChatData(key);
        }
    }

    private void getChatData(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(user.getContacts().get(key))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null)
                            chats.add(dataSnapshot.getValue(Chat.class).setChatID(dataSnapshot.getKey()));

                        if (chats.size() == user.getContacts().keySet().size()) {
                            chatsAdapter.setData(chats);

                            swipeRefreshLayout_chats.setRefreshing(false);

                            showChats();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                        Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                    }
                });
    }
}