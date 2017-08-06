package com.example.a15017523.eventful;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewEventDetails extends AppCompatActivity {

    TextView tvAddress, tvDesc, tvStartDate, tvStartTime, tvEndDate, tvEndTime, tvOrganiser, tvHeadChief, tvTitle, tvPax, tvTimeStamp, tvType, tvOrgId;
    ImageView imageView;
    Button btnRegister;
    FirebaseAuth mAuth;
    private GoogleMap map;
    LinearLayout calender, profile;

    String organiser_name;

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
        tvStartDate = (TextView)findViewById(R.id.tvStartDate);
        tvStartTime = (TextView)findViewById(R.id.tvStartTime);
        tvEndDate = (TextView)findViewById(R.id.tvEndDate);
        tvEndTime = (TextView)findViewById(R.id.tvEndTime);
        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvHeadChief = (TextView)findViewById(R.id.tvHeadChief);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        tvTimeStamp = (TextView)findViewById(R.id.tvTimeStamp);
        tvType = (TextView)findViewById(R.id.tvEventType);
        tvOrgId = (TextView)findViewById(R.id.tvOrgId);

        imageView = (ImageView)findViewById(R.id.imageView2);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        calender = (LinearLayout)findViewById(R.id.calender);
        profile = (LinearLayout)findViewById(R.id.profile);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mDatabase = databaseReference.child("EVENT");
        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        final String itemKey = i.getStringExtra("key");

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
                String timeStamp = event.getTimeStamp().toString().trim();
                String type = event.getEventType().toString().trim();
                final String organiser = event.getOrganiser().toString().trim();

                DatabaseReference mOrganiser = databaseReference.child("ORGANISER").child(organiser);
                mOrganiser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        organiser_name = dataSnapshot.child("user_name").getValue().toString();
                        tvOrganiser.setText("by: " + organiser_name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String startDate = event.getStartDate().toString().trim();
                String startTime = event.getStartTime().toString().trim();
                String endDate = event.getEndDate().toString().trim();
                String endTime = event.getEndTime().toString().trim();
                final Double latitude = event.getLat();
                final Double longitude = event.getLng();

                tvTitle.setText(title);
                tvStartDate.setText(startDate);
                tvStartTime.setText(startTime);
                tvEndDate.setText(endDate);
                tvEndTime.setText(endTime);
                tvDesc.setText(description);
                tvHeadChief.setText("Event-in-charge: " + head_chief);
                tvAddress.setText(address);
                tvTimeStamp.setText(timeStamp);
                tvType.setText(type);
                tvOrgId.setText(organiser);
                Picasso.with(getBaseContext()).load(image).into(imageView);

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;

                        int permissionCheck = ContextCompat.checkSelfPermission(ViewEventDetails.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION);

                        LatLng location = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(location).title(address));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        if (ActivityCompat.checkSelfPermission(ViewEventDetails.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(ViewEventDetails.this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        final String user_id = mAuth.getCurrentUser().getUid();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user_id != "") {
                    final AlertDialog.Builder myBuilder = new AlertDialog.Builder(ViewEventDetails.this);

                    myBuilder.setTitle("Confirm Registration");
                    myBuilder.setMessage("An email will be sent to you upon confirmation of registration.");
                    myBuilder.setCancelable(false);
                    myBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child("EVENT_PARTICIPANTS").child(itemKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(!dataSnapshot.child(itemKey).hasChild(user_id)) {
                                        String title = tvTitle.getText().toString().trim();
                                        JOIN join1 = new JOIN();
                                        join1.setDatetime(getCurrentTimeStamp());
                                        join1.setId(itemKey);
                                        join1.setStatus("pending");
                                        join1.setName(title);

                                        DatabaseReference push = databaseReference.child("EVENT_PARTICIPANTS").child(itemKey).child(user_id).push();
                                        String push_id = push.getKey();

                                        Map messageMap = new HashMap();
//                                        messageMap.put("EVENT_PARTICIPANTS/" + itemKey+ "/" + user_id + "/" + push_id, "status");
                                        messageMap.put("EVENT_PARTICIPANTS/" + user_id + "/" + push_id, join1);

                                        databaseReference.updateChildren(messageMap);
                                    } else {
                                        Toast.makeText(ViewEventDetails.this, "You have signed up for this", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
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
                String orgKey = tvOrgId.getText().toString();
                Intent i = new Intent(ViewEventDetails.this, OrganiserProfileActivity.class);
                i.putExtra("key", orgKey.toString());
                startActivity(i);
            }
        });
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
