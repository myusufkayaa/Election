package com.example.election.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.adapters.SearchAdapter;
import com.example.election.objects.Anket;
import com.example.election.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    RecyclerView lstHome;
    View view;
    ArrayList<String> anketId;
    ArrayList<Anket> anketler;
    FirebaseFirestore db;
    User currentUser;
    FirebaseAuth auth;
    SearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        lstHome = view.findViewById(R.id.lstHome);
        anketler = new ArrayList<>();
        anketId = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        adapter = new SearchAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), RecyclerView.VERTICAL, false);
        lstHome.setLayoutManager(layoutManager);
        lstHome.setAdapter(adapter);
        getCurrentUser();
    }

    public void getAnketler() {
        anketler.clear();

        db.collection("Anketler")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        anketler.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            for (final String id : anketId) {
                                Anket anket = new Anket(document);
                                anket.setAnketId(document.getId());
                                if (anket.getAnketId().equals(id)) {
                                    anketler.add(anket);
                                    adapter.setAnketler(anketler);
                                }
                            }
                        }

                    }
                });


    }

    public void getCurrentUser() {
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                currentUser = new User(documentSnapshot);
                getAnketIds();
            }
        });
    }

    public void getAnketIds() {
        anketId.clear();
        for (String id : currentUser.getFollowing()) {
            db.collection("Users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    User us = new User(documentSnapshot);
                    for (String id : us.getAnketler()) {
                        anketId.add(id);
                    }
                    getAnketler();
                }
            });
        }
    }
}
