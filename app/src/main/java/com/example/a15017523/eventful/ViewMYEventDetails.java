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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class ViewMYEventDetails extends AppCompatActivity {

    TextView tvAddress, tvDesc, tvStartDate, tvStartTime, tvEndDate, tvEndTime, tvOrganiser, tvHeadChief, tvTitle, tvPax, tvTimeStamp, tvType, tvOrgId;
    ImageView imageView;
    Button btnRegister;
    FirebaseAuth mAuth;
    private GoogleMap map;
    LinearLayout calender, profile;
    String ref;
    String organiser_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_myevent_details);

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
        ref = i.getStringExtra("ref");

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

                        int permissionCheck = ContextCompat.checkSelfPermission(ViewMYEventDetails.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION);

                        LatLng location = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(location).title(address));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        if (ActivityCompat.checkSelfPermission(ViewMYEventDetails.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(ViewMYEventDetails.this,
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
                    final AlertDialog.Builder myBuilder = new AlertDialog.Builder(ViewMYEventDetails.this);

                    myBuilder.setTitle("Cancel Registration");
                    myBuilder.setMessage("Are you sure?");
                    myBuilder.setCancelable(false);
                    myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child("EVENT_PARTICIPANTS").child("EVENT_PARTICIPANTS").child(user_id).child(ref).removeValue();
                            databaseReference.child("EVENT_PARTICIPANTS").child("EVENT_PARTICIPANTS").child(itemKey).child(user_id).child(ref).setValue("left");
                        }
                    });

                    myBuilder.setNegativeButton("No", null);

                    AlertDialog myDialog = myBuilder.create();
                    myDialog.show();
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
                Intent i = new Intent(ViewMYEventDetails.this, OrganiserProfileActivity.class);
                i.putExtra("key", orgKey.toString());
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.viewmyevent, menu);
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
        if (id == R.id.action_showKey) {
            final AlertDialog.Builder myBuilder = new AlertDialog.Builder(ViewMYEventDetails.this);

            myBuilder.setTitle("Your registration Key");
            myBuilder.setMessage(ref);

            myBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
