package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Medicine;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.MedViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public abstract class medListFragment extends Fragment {
    private static final String TAG = "medicineListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Medicine, MedViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public medListFragment() { }
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_med, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.messagesListMed);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        Query medQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Medicine>()
                .setQuery(medQuery, Medicine.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Medicine, MedViewHolder>(options) {

            @Override
            public MedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

                return new MedViewHolder(inflater.inflate(R.layout.med_row, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(MedViewHolder viewHolder, int position, final Medicine model) {
                viewHolder.setMedName(model.getMedName());
                viewHolder.setMedDesc(model.getMedDesc());
                viewHolder.setMedRecom(model.getMedRecom());



                final DatabaseReference medRef = getRef(position);
                final String medKey = medRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }


        };
        mRecycler.setAdapter(mAdapter);

        //  fetch();
    }


     @Override
    public void onStart(){
        super.onStart();
        if(mAdapter != null){
            mAdapter.startListening();
        }
     }
      @Override
    public void onStop(){
        super.onStop();
        if (mAdapter != null){
            mAdapter.startListening();
        }
      }


      public String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

  /*  private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts");

        FirebaseRecyclerOptions<Medicine> options =
                new FirebaseRecyclerOptions.Builder<Medicine>()
                        .setQuery(query, new SnapshotParser<Medicine>() {
                            @NonNull
                            @Override
                            public Medicine parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Medicine(snapshot.child("uid").getValue().toString(),
                                        snapshot.child("title").getValue().toString(),
                                        snapshot.child("desc").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Medicine, MedViewHolder_delete>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, Medicine model) {
                holder.setTxtTitle(model.getmTitle());
                holder.setTxtDesc(model.getmDesc());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }*/

}
