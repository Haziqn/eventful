package com.example.a15017523.eventful;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewUsername, textViewEmail, textViewAge, textViewOccupation, textViewRace, textViewGender, textViewInterests;
    Button buttonShare;
    CircleImageView imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

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
                String user_name = dataSnapshot.child("user_name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
//                String gender = dataSnapshot.child("gender").getValue().toString();
                String occupation = dataSnapshot.child("occupation").getValue().toString();
                String race = dataSnapshot.child("race").getValue().toString();

                textViewEmail.setText(email);
                textViewUsername.setText(user_name);
//                textViewGender.setText(gender);
                textViewRace.setText(race);
                textViewOccupation.setText(occupation);
                textViewAge.setText(age);
                Picasso.with(getBaseContext()).load(image).into(imageButton);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}