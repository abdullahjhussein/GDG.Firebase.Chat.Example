package com.abdullahhussein.gdgfirebaseexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.abdullahhussein.gdgfirebaseexample.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextInputLayout textInputLayout_username;
    private AppCompatEditText editText_username;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInputLayout_username = findViewById(R.id.textInputLayout_username);
        editText_username = findViewById(R.id.editText_username);
        editText_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout_username.setErrorEnabled(s.length() == 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.button_get_started).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_username.getText().toString().trim().isEmpty()) {
                    textInputLayout_username.setErrorEnabled(true);
                    textInputLayout_username.setError("Username is required");
                    editText_username.requestFocus();
                } else {

                    progressDialog.show();

                    loginToFireBase();
                }
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    private void loginToFireBase() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(editText_username.getText().toString().trim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            createNewUser();
                        } else {
                            User user = dataSnapshot.getValue(User.class);

                            Log.e(TAG, "user : " + user.toString());

                            goToChatsActivity(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error while login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewUser() {

        final User user = new User(editText_username.getText().toString().trim());

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(editText_username.getText().toString().trim())
                .setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.e(TAG, "dataSnapshot getKey: " + databaseReference.getKey());

                            Log.e(TAG, "user : " + user.toString());

                            goToChatsActivity(user);
                        } else {
                            Log.e(TAG, "databaseError getCode: " + databaseError.getCode());
                            Log.e(TAG, "databaseError getMessage: " + databaseError.getMessage());
                        }
                    }
                });
    }

    private void goToChatsActivity(User user) {
        progressDialog.dismiss();

        Intent intent = new Intent(getApplicationContext(), ChatsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
