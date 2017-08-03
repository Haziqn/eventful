package com.example.a15017523.eventful;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Home.OnFragmentInteractionListener, All.OnFragmentInteractionListener, MyEvents.OnFragmentInteractionListener {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PARTICIPANT");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        final TextView textViewUserEmail = (TextView) header.findViewById(R.id.tvDisplayEmail);
        final TextView textViewUsername = (TextView) header.findViewById(R.id.tvDisplayUser);
        final CircleImageView imageViewUserDP = (CircleImageView) header.findViewById(R.id.ivUserDP);
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {

            //User is Signed In
            if (user.getPhotoUrl() == null) {
                databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String user_name = dataSnapshot.child("user_name").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();

                        textViewUserEmail.setText(email);
                        textViewUsername.setText(user_name);
                        Picasso.with(getBaseContext()).load(image).into(imageViewUserDP);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                textViewUsername.setText(user.getDisplayName());
                textViewUserEmail.setText(user.getEmail());
                Picasso.with(getBaseContext()).load(user.getPhotoUrl().toString().trim()).into(imageViewUserDP);
            }

            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean age = dataSnapshot.hasChild("age");
                    Boolean gender = dataSnapshot.hasChild("gender");
                    Boolean interests = dataSnapshot.hasChild("interests");
                    Boolean race = dataSnapshot.hasChild("race");
                    Boolean occupation = dataSnapshot.hasChild("occupation");
                    if (!age &&
                            !gender &&
                            !occupation &&
                            !race &&
                            !interests) {
                        Intent intent = new Intent(MainActivity.this, EditProfile.class);
                        startActivity(intent);
                        Snackbar snackbar = Snackbar
                                .make(navigationView, "Please fill in your personal details", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            Intent intent = new Intent(getBaseContext(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        //replace the activity_main with Home(fragment) layout
        Home home = new Home();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(
                R.id.content_main,
                home,
                home.getTag()
        ).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_editprofile) {
            Intent i = new Intent(MainActivity.this, EditProfile.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(MainActivity.this);

            myBuilder.setTitle("Log Out");
            myBuilder.setMessage("Are you sure?");
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent i = new Intent(MainActivity.this, StartActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
            });
            myBuilder.setNegativeButton("Cancel", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
