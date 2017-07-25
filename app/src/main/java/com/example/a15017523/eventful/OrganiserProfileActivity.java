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

    TextView tvOrganiser, tvDesc, tvEmail, tvSite, tvAddress;
    ImageView img;
    private GoogleMap map;
    RelativeLayout email, site, address;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

    String organiser_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_profile);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        img = (ImageView) findViewById(R.id.imgOrganiser);
        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvDesc = (TextView)findViewById(R.id.tvDesc);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvSite = (TextView)findViewById(R.id.tvSite);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        email = (RelativeLayout)findViewById(R.id.emailLayout);
        site = (RelativeLayout)findViewById(R.id.siteLayout);
        address = (RelativeLayout)findViewById(R.id.addressLayout);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ORGANISER");

        Intent i = getIntent();
        String itemKey = i.getStringExtra("key");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mDatabase = databaseReference.child("ORGANISER");

        final DatabaseReference mDatabaseRef = mDatabase.child(itemKey);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ORGANISER organiser = dataSnapshot.getValue(ORGANISER.class);
                String title = organiser.getUser_name().toString().trim();
                String description = organiser.getDescription().toString().trim();
                String image = organiser.getImage().toString().trim();
                String email = organiser.getEmail().toString().trim();
                String site = organiser.getSite().toString().trim();
                String address = organiser.getAddress().toString().trim();

                tvOrganiser.setText(title);
                tvDesc.setText(description);
                Picasso.with(getBaseContext()).load(image).into(img);
                tvEmail.setText(email);
                tvSite.setText(site);
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

        site.setOnClickListener(new View.OnClickListener() {
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
