package com.example.a15017523.eventful;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        FirebaseRecyclerAdapter<EVENT, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EVENT, BlogViewHolder>(

                EVENT.class,
                R.layout.row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, EVENT model, int position) {

                viewHolder.setTitle("title");
                viewHolder.setDesc("description");
                viewHolder.setAddress(model.getAddress());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setOrganiser(model.getOrganiser());
                viewHolder.setHeadChief(model.getHead_chief());
                viewHolder.setPax(model.getPax());
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
            TextView postTitle = (TextView)mView.findViewById(R.id.title_);
            postTitle.setText(title);
        }

        public void setDesc(String desc) {
            TextView postDesc = (TextView)mView.findViewById(R.id.desc_);
            postDesc.setText(desc);
        }

        public void setAddress(String address) {
            TextView postAddress = (TextView)mView.findViewById(R.id.address_);
            postAddress.setText(address);
        }

        public void setDate(String date) {
            TextView postDate = (TextView)mView.findViewById(R.id.date_);
            postDate.setText(date);
        }

        public void setTime(String time) {
            TextView postTime = (TextView)mView.findViewById(R.id.time_);
            postTime.setText(time);
        }

        public void setOrganiser(String organiser) {
            TextView postOrganiser = (TextView)mView.findViewById(R.id.organiser_);
            postOrganiser.setText(organiser);
        }

        public void setHeadChief(String headChief) {
            TextView postHeadChief = (TextView)mView.findViewById(R.id.head_chief);
            postHeadChief.setText(headChief);
        }

        public void setPax(String Pax) {
            TextView postPax = (TextView)mView.findViewById(R.id.pax_);
            postPax.setText(Pax);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_all,
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
