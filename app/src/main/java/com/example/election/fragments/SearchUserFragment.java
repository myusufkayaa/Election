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
import com.example.election.adapters.UsersAdapter;
import com.example.election.objects.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchUserFragment extends Fragment {
    View view;
    EditText txtSearchEmail;
    Button btnSearchUser;
    RecyclerView lstUsers;
    UsersAdapter adapter;
    String search;
    FirebaseFirestore db;
    ArrayList<User> users;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_search_user, container, false);
            init();
        }
        return view;
    }

    private void init() {
        db=FirebaseFirestore.getInstance();
        users=new ArrayList<>();
        txtSearchEmail=view.findViewById(R.id.txtSearchEmail);
        btnSearchUser=view.findViewById(R.id.btnSearchUser);
        lstUsers=view.findViewById(R.id.lstUsers);
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSearch();
            }
        });
        adapter=new UsersAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        lstUsers.setLayoutManager(layoutManager);
        lstUsers.setAdapter(adapter);
    }

    public void clickSearch(){
        search=txtSearchEmail.getText().toString();
        getUserList();

    }
    public void getUserList(){
        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                users.clear();
                for (QueryDocumentSnapshot document:queryDocumentSnapshots){
                    User user=new User(document);
                    if (user.getEmail().equals(search)){
                        users.add(user);
                    }
                }
                adapter.setUsers(users);
            }
        });
    }
}
