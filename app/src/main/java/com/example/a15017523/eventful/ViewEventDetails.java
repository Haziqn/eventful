package com.example.a15017523.eventful;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ViewEventDetails extends AppCompatActivity {

    TextView tvTitle, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_details);

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvAddress = (TextView)findViewById(R.id.tvAddress);

        Intent i = getIntent();
        String title = i.getStringExtra("title");

        tvTitle.setText(title);
    }
}
