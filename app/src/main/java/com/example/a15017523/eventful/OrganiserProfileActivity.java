package com.example.a15017523.eventful;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class OrganiserProfileActivity extends AppCompatActivity {

    TextView tvOrganiser, tvDesc;
    Button btnEmail;
    ImageView imageView;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

    String organiser_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        Intent i = getIntent();
        final String organiserKey = i.getStringExtra("organiserKey");
        final String title = i.getStringExtra("title");

        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvDesc = (TextView)findViewById(R.id.tvDesc);
        btnEmail = (Button)findViewById(R.id.btnEmail);
        imageView = (ImageView)findViewById(R.id.imageOrganiser);

        final DatabaseReference mDatabaseRef = mDatabase.child(organiserKey);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ORGANISER organiser = dataSnapshot.getValue(ORGANISER.class);
                String organiserName = organiser.getUser_name().toString().trim();
                String desc = organiser.getDescription().toString().trim();
                String image = organiser.getImage().toString().trim();

                tvOrganiser.setText(organiserName);
                tvDesc.setText(desc);
                Picasso.with(getBaseContext()).load(image).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ORGANISER organiser = dataSnapshot.getValue(ORGANISER.class);

                        String emailOrganiser = organiser.getEmail().toString().trim();

                        Intent email = new Intent(Intent.ACTION_SEND);
                        // Put essentials like email address, subject & body text
                        email.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{emailOrganiser});
                        email.putExtra(Intent.EXTRA_SUBJECT,
                                "Enquiries for " + title);
                        email.putExtra(Intent.EXTRA_TEXT,
                                "Testing Email from Eventful");
                        // This MIME type indicates email
                        email.setType("message/rfc822");
                        // createChooser shows user a list of app that can handle
                        // this MIME type, which is, email
                        startActivity(Intent.createChooser(email,
                                "Choose an Email client :"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
