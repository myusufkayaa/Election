package com.example.election.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.adapters.SearchAdapter;
import com.example.election.objects.Anket;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    View view;
    SearchAdapter adapter;
    EditText txtSearch;
    Button btnSearch;
    RecyclerView recyclerView;
    ArrayList<Anket> anketler;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view=inflater.inflate(R.layout.fragment_search,container,false);
            init();
        }
        return view;
    }

    private void init() {
        adapter=new SearchAdapter(getActivity());
        txtSearch=view.findViewById(R.id.txtSearchId);
        btnSearch=view.findViewById(R.id.btnSearch);
        anketler=new ArrayList<>();
        recyclerView=view.findViewById(R.id.lstSearch);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        db=FirebaseFirestore.getInstance();
        adapter.setAnketler(anketler);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSearch();

            }
        });

    }

    private void clickSearch(){
        if (txtSearch.getText().toString().equals("")){
            txtSearch.setError("Boş Bırakmayın");
            return;
        }
        getAnketler(txtSearch.getText().toString());
    }

    private void getAnketler(final String id) {
        db.collection("Anketler")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        anketler.clear();
                        for (QueryDocumentSnapshot document:queryDocumentSnapshots){
                            Anket anket=new Anket(document);
                            if (anket.getAnketId().equals(id)){
                                anketler.add(anket);
                            }
                        }
                        adapter.setAnketler(anketler);
                    }
                });

    }
}
