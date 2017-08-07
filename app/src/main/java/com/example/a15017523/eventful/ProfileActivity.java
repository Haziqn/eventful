package com.example.a15017523.eventful;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.robertsimoes.shareable.Shareable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername, textViewEmail;
    CircleImageView imageButton;
    Button buttonShare;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

        getSupportActionBar().setTitle("Profile");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        textViewEmail = (TextView) findViewById(R.id.tvEmail);
        textViewUsername = (TextView) findViewById(R.id.tvUsername);
        buttonShare = (Button)findViewById(R.id.btnShare);

        imageButton = (CircleImageView) findViewById(R.id.imageButtonUser);

        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        final DatabaseReference mDatabaseRef = mDatabase.child(uid);

        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shareable shareAction = new Shareable.Builder(ProfileActivity.this)
                        .message("Hi! I am using Eventful!")
                        .url("https://drive.google.com/open?id=0B1mWK9sVsyOoZ2FEWnVON3ZLWEk")
                        .socialChannel(Shareable.Builder.TWITTER)
                        .build();
                shareAction.share();
            }
        });

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean name = dataSnapshot.hasChild("user_name");
                Boolean image = dataSnapshot.hasChild("image");

                if (name && image) {
                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String imagee = dataSnapshot.child("image").getValue().toString();
                    textViewEmail.setText(user.getEmail());
                    textViewUsername.setText(user_name);
                    Picasso.with(getBaseContext()).load(imagee).into(imageButton);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}