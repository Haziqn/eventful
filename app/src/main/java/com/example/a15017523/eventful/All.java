package com.example.a15017523.eventful;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import static com.example.a15017523.eventful.R.layout.fragment_all;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link All.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link All#newInstance} factory method to
 * create an instance of this fragment.
 */
public class All extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    public All() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment All.
     */
    // TODO: Rename and change types and number of parameters
    public static All newInstance(String param1, String param2) {
        All fragment = new All();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EVENT, BlogViewHolder>(

                EVENT.class,
                R.layout.row,
                BlogViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, EVENT model, final int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setTimeStamp(model.getTimeStamp());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String[] itemArray = {""};
                        final Intent i = new Intent(getContext(), ViewEventDetails.class);
                        String itemKey = String.valueOf(firebaseRecyclerAdapter.getRef(position).getKey());
                        i.putExtra("key", itemKey);
                        DatabaseReference itemTitle = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("title");
                        DatabaseReference itemAddress = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("address");
                        DatabaseReference itemDate = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("date");
                        DatabaseReference itemDesc = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("description");
                        DatabaseReference itemHead = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("head_chief");
                        DatabaseReference itemImage = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("image");
                        DatabaseReference itemOrganiser = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("organiser");
                        DatabaseReference itemPax = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("pax");
                        DatabaseReference itemStatus = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("status");
                        DatabaseReference itemTime = FirebaseDatabase.getInstance().getReference().child("EVENT").child(itemKey).child("time");

                        itemTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                 itemArray[0] = dataSnapshot.getValue(String.class);
                                 i.putExtra("title", itemArray[0]);
                                 Toast.makeText(getContext(), itemArray[0], Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        startActivity(i);


                    }
                });
            }

        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setTitle(String title) {
            TextView postTitle = (TextView)mView.findViewById(R.id.post_Title);
            postTitle.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView)mView.findViewById(R.id.post_Desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image) {
            ImageView post_Image = (ImageView)mView.findViewById(R.id.post_Image);
            Picasso.with(ctx).load(image).into(post_Image);
        }

        public void setTimeStamp(String timeStamp) {
            TextView post_ts = (TextView)mView.findViewById(R.id.post_TS);
            post_ts.setText(timeStamp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all,
                container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mBlogList =(RecyclerView)view.findViewById(R.id.all_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
