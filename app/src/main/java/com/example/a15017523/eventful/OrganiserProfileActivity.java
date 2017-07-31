package com.example.a15017523.eventful;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class OrganiserProfileActivity extends AppCompatActivity {

    String post_key = null;

    TextView tvOrganiser, tvDesc, tvEmail, tvWeb, tvAddress;
    ImageView img;
    private GoogleMap map;
    RelativeLayout email, web, address;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_profile);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ORGANISER");

        Intent i = getIntent();
        final String post_key = i.getStringExtra("key");

        img = (ImageView) findViewById(R.id.imgOrganiser);
        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvDesc = (TextView)findViewById(R.id.tvDesc);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvWeb = (TextView)findViewById(R.id.tvWeb);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        email = (RelativeLayout)findViewById(R.id.emailLayout);
        web = (RelativeLayout)findViewById(R.id.webLayout);
        address = (RelativeLayout)findViewById(R.id.addressLayout);

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = (String) dataSnapshot.child("user_name").getValue();
                String description = (String) dataSnapshot.child("description").getValue();
                String image = (String) dataSnapshot.child("image").getValue();
                String email = (String) dataSnapshot.child("email").getValue();
                String web = (String) dataSnapshot.child("business_site").getValue();
                String address = (String) dataSnapshot.child("address").getValue();

                tvOrganiser.setText(title);
                tvDesc.setText(description);
                Picasso.with(OrganiserProfileActivity.this).load(image).into(img);
                tvEmail.setText(email);
                tvWeb.setText(web);
                tvAddress.setText(address);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        email.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{tvOrganiser.toString()});
            email.putExtra(Intent.EXTRA_SUBJECT,
                    "Test Email from C347");
            email.putExtra(Intent.EXTRA_TEXT,
                    tvOrganiser.getText());
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email,
                    "Choose an Email client :"));

        }
    });

        web.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = tvEmail.toString();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    });

        address.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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
