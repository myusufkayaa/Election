package com.example.election.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class ProfileActivity extends AppCompatActivity {
    TextView txtMail;
    Button btnFollow;
    RecyclerView recyclerView;
    User user;
    SearchAdapter adapter;
    FirebaseFirestore db;
    ArrayList<Anket> anketler;
    FirebaseAuth auth;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    private void init() {
        txtMail=findViewById(R.id.txtPMail);
        btnFollow=findViewById(R.id.btnPFollow);
        recyclerView=findViewById(R.id.lstPAnketler);
        user= (User) getIntent().getSerializableExtra("user");
        txtMail.setText(user.getEmail());
        adapter=new SearchAdapter(this);
        anketler=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        getCurrentUser();
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        getAnketler();

    }

    public void getAnketler() {
        anketler.clear();
        for (final String id : user.getAnketler()) {
            db.collection("Anketler")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            anketler.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Anket anket = new Anket(document);
                                if (anket.getAnketId().equals(id)) {
                                    anketler.add(anket);
                                }
                            }
                            adapter.setAnketler(anketler);
                        }
                    });


        }


    }

    public void setButton() {
        if (auth.getUid().equals(user.getuId())){
            btnFollow.setVisibility(View.GONE);
            return;
        }
        btnFollow.setVisibility(View.VISIBLE);
        btnFollow.setText("Takip Et");
        for (String id : currentUser.getFollowing()) {
            if (id.equals(user.getuId())) {
                btnFollow.setText("Takipten Çık");
                return;
            }
        }

    }
    public void getCurrentUser() {
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                currentUser = new User(documentSnapshot);
                setButton();
            }
        });


    }
    public void clickFollow(View view) {
        if (isFollowing()) {
            currentUser.getFollowing().remove(user.getuId());
            user.getFollowers().remove(currentUser.getuId());
        } else {
            currentUser.getFollowing().add(user.getuId());
            user.getFollowers().add(currentUser.getuId());
        }
        updateUser(currentUser);
        updateUser(user);


    }
    public boolean isFollowing() {
        for (String id : currentUser.getFollowing()) {
            if (user.getuId().equals(id)) {
                return true;
            }
        }
        return false;
    }
    public void updateUser(User user) {
        Map<String, Object> us = new HashMap<>();
        us.put("user", user);
        db.collection("Users").document(user.getuId()).set(us);
    }

}
