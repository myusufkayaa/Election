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
import java.util.Currency;

public class MyPollFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    User currentUser;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ArrayList<Anket> myPolls;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_poll, container, false);
        init();
        return view;
    }

    private void init() {
        recyclerView = view.findViewById(R.id.lstMyPoll);
        adapter = new SearchAdapter(getActivity());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        myPolls = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getCurrentUser();

    }

    private void getCurrentUser() {
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                currentUser = new User(documentSnapshot);
                getMyPolls();
            }
        });
    }

    private void getMyPolls() {

        db.collection("Anketler")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        myPolls.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            for (final String id : currentUser.getAnketler()) {
                                Anket anket = new Anket(document);
                                anket.setAnketId(document.getId());
                                if (anket.getAnketId().equals(id)) {
                                    myPolls.add(anket);
                                }
                            }
                        }
                        adapter.setAnketler(myPolls);
                    }
                });
    }
}
