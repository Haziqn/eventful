package com.example.a15017523.eventful;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class MyEvents extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ListView lv;
    private ArrayList<JOIN> alJOIN;
    private ArrayAdapter<JOIN> aaJOIN;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseref;

    public MyEvents() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events,
                container, false);

        lv = (ListView)view.findViewById(R.id.listViewMyEvents);

        alJOIN = new ArrayList<JOIN>();
        aaJOIN = new ArrayAdapter<JOIN>(getContext(), android.R.layout.simple_list_item_1, alJOIN);
        lv.setAdapter(aaJOIN);

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseref = firebaseDatabase.getReference("EVENT_PARTICIPANTS").child(uid);

        databaseref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.i("MainActivity", "onChildAdded()");
                JOIN join = dataSnapshot.getValue(JOIN.class);
                if (join != null) {
                    join.setRef(dataSnapshot.getKey());
                    alJOIN.add(join);
                    aaJOIN.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("MainActivity", "onChildChanged()");

                JOIN join = dataSnapshot.getValue(JOIN.class);
                String selectedId = join.getId();
                if (join != null) {
                    for (int i = 0; i < alJOIN.size(); i++) {
                        if (alJOIN.get(i).getId().equals(selectedId)) {
                            join.setRef(dataSnapshot.getKey());
                            alJOIN.set(i, join);
                        }
                    }
                    aaJOIN.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");

                JOIN join = dataSnapshot.getValue(JOIN.class);
                String selectedId = join.getId();
                for(int i= 0; i < alJOIN.size(); i++) {
                    if (alJOIN.get(i).getId().equals(selectedId)) {
                        alJOIN.remove(i);
                    }
                }
                aaJOIN.notifyDataSetChanged();


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("MainActivity", "onChildMoved()");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Database error occurred", databaseError.toException());

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                JOIN join = alJOIN.get(i);  // Get the selected Student
                String id = join.getId();
                String ref = join.getRef();

                Intent intent = new Intent(getContext(), ViewMYEventDetails.class);
                intent.putExtra("key", id);
                intent.putExtra("ref", ref);
                startActivity(intent);
            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
