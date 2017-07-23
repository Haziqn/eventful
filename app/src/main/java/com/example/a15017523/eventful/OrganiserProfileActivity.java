package com.example.a15017523.eventful;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OrganiserProfileActivity extends AppCompatActivity {

    TextView tvOrganiser, tvDesc;
    Button btnEmail;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

    String organiser_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ORGANISER");

        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvDesc = (TextView)findViewById(R.id.tvDesc);
        btnEmail = (Button)findViewById(R.id.btnEmail);

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
