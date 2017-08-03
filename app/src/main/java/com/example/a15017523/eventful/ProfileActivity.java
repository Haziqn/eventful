package com.example.a15017523.eventful;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername, textViewEmail, textViewAge, textViewOccupation, textViewRace, textViewGender, textViewInterests;
    Button buttonShare;
    CircleImageView imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

    String interestss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        textViewEmail = (TextView) findViewById(R.id.tvEmail);
        textViewUsername = (TextView) findViewById(R.id.tvUsername);
        textViewAge = (TextView) findViewById(R.id.tvAge);
        textViewGender = (TextView) findViewById(R.id.tvGender);
        textViewRace = (TextView) findViewById(R.id.tvRace);
        textViewInterests = (TextView) findViewById(R.id.tvInterests);
        textViewOccupation = (TextView) findViewById(R.id.tvOccupation);

        imageButton = (CircleImageView) findViewById(R.id.imageButtonUser);
        buttonShare = (Button) findViewById(R.id.btnShare);

        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        DatabaseReference mDatabaseRef = mDatabase.child(uid);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean name = dataSnapshot.hasChild("user_name");
                Boolean image = dataSnapshot.hasChild("image");
                Boolean age = dataSnapshot.hasChild("age");
                Boolean interests = dataSnapshot.child("interests").hasChildren();
                Boolean race = dataSnapshot.hasChild("race");
                Boolean occupation = dataSnapshot.hasChild("occupation");
                Boolean gender = dataSnapshot.hasChild("gender");

                if (name && image) {

                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String imagee = dataSnapshot.child("image").getValue().toString();
                    textViewEmail.setText(user.getEmail());
                    textViewUsername.setText(user_name);
                    Picasso.with(getBaseContext()).load(imagee).into(imageButton);

                } else if (age && interests && race && occupation && gender) {

                    String race2 = dataSnapshot.child("race").getValue().toString();
                    String age2 = dataSnapshot.child("age").getValue().toString();
                    ArrayList<String> interests2 = (ArrayList<String>) dataSnapshot.child("interests").getValue();
                    String occupation2 = dataSnapshot.child("occupation").getValue().toString();
                    String gender2 = dataSnapshot.child("gender").getValue().toString();
                    textViewAge.setText(age2);

                    for (int i = 0; i < interests2.size(); i++) {
                        interestss += interests2.get(i).toString() + "\n";
                    }

                    textViewInterests.setText(interestss);

                    if (gender2 == "Male") {
                        textViewGender.setText("Male");
                    } else if (gender2 == "Female") {
                        textViewGender.setText("Female");
                    }

                    textViewRace.setText(race2);
                    textViewOccupation.setText(occupation2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}