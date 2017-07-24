package com.example.a15017523.eventful;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewEventDetails extends AppCompatActivity {

    TextView tvAddress, tvDesc, tvDate, tvTime, tvOrganiser, tvHeadChief, tvTitle;
    ImageView imageView;
    Button btnRegister;
    FirebaseAuth mAuth;
    private GoogleMap map;
    LinearLayout calender, profile;

    String organiser_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_details);

        setTitle("");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvDesc = (TextView)findViewById(R.id.tvDescription);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvHeadChief = (TextView)findViewById(R.id.tvHeadChief);
        imageView = (ImageView)findViewById(R.id.imageView2);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        calender = (LinearLayout)findViewById(R.id.calender);
        profile = (LinearLayout)findViewById(R.id.profile);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mDatabase = databaseReference.child("EVENT");
        DatabaseReference mDatabaseEventP = databaseReference.child("EVENT_PARTICIPANTS");
        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        String itemKey = i.getStringExtra("key");

        final DatabaseReference mDatabaseRef = mDatabase.child(itemKey);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                EVENT event = dataSnapshot.getValue(EVENT.class);
                String title = event.getTitle().toString().trim();
                String description = event.getDescription().toString().trim();
                String image = event.getImage().toString().trim();
                final String address = event.getLocation().toString().trim();
                String head_chief = event.getHead_chief().toString().trim();
                String pax = event.getPax().toString().trim();
                final String organiser = event.getOrganiser().toString().trim();

                DatabaseReference mOrganiser = databaseReference.child("ORGANISER").child(organiser);
                mOrganiser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        organiser_name = dataSnapshot.child("user_name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String date = event.getStartDate().toString().trim();
                String time = event.getStartTime().toString().trim();
                String timestamp = event.getTimeStamp().toString().trim();
                final Double latitude = event.getLat();
                final Double longitude = event.getLng();

                tvTitle.setText(title);
                tvDate.setText(date);
                tvTime.setText(time);
                tvDesc.setText(description);
                tvOrganiser.setText("by: " + organiser_name);
                tvHeadChief.setText("Event-in-charge: " + head_chief);
                tvAddress.setText(address);
                Picasso.with(getBaseContext()).load(image).into(imageView);

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;

                        int permissionCheck = ContextCompat.checkSelfPermission(ViewEventDetails.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION);

                        // Add a marker in Sydney and move the camera
                        LatLng location = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(location).title(address));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        if (ActivityCompat.checkSelfPermission(ViewEventDetails.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewEventDetails.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        map.setMyLocationEnabled(true);

                    }
                });

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                getSupportActionBar().setTitle(title);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference mDatabaseRefEventP = mDatabaseEventP.child(itemKey);
        final String user_id = mAuth.getCurrentUser().getUid();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewEventDetails.this, user_id, Toast.LENGTH_LONG).show();
                if (user_id != "") {
                    final AlertDialog.Builder myBuilder = new AlertDialog.Builder(ViewEventDetails.this);

                    myBuilder.setTitle("Confirm Registration");
                    myBuilder.setMessage("An email will be sent to you upon confirmation of registration.");
                    myBuilder.setCancelable(false);
                    myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabaseRefEventP.child(user_id).setValue("Unassigned");
                            Toast.makeText(ViewEventDetails.this, "Registration success!", Toast.LENGTH_LONG).show();

                            try {
                                GMailSender sender = new GMailSender("username@gmail.com", "password");
                                sender.sendMail("This is Subject",
                                        "This is Body",
                                        "hndeathchair@gmail.com",
                                        "hiiamnew60@gmail.com");
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                            dialog.dismiss();
                        }
                    });

                    myBuilder.setNegativeButton("Cancel", null);

                    AlertDialog myDialog = myBuilder.create();
                    myDialog.show();
                } else {
                    Toast.makeText(ViewEventDetails.this, "You need to be logged in to register for events!", Toast.LENGTH_LONG).show();
                }

            }
        });

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
                i.setComponent(cn);
                startActivity(i);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ViewEventDetails.this, OrganiserProfileActivity.class);
                startActivity(profileIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
