package com.example.election.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.election.R;
import com.example.election.adapters.UsersAdapter;
import com.example.election.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class UserListActivity extends AppCompatActivity {
    boolean takipciMi;
    RecyclerView recyclerView;
    ArrayList<User> users;
    FirebaseAuth auth;
    FirebaseFirestore db;
    User currentUser;
    UsersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        init();
    }

    private void init() {
        recyclerView=findViewById(R.id.recycler);
        adapter=new UsersAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        takipciMi=getIntent().getBooleanExtra("bool",false);
        currentUser= (User) getIntent().getSerializableExtra("user");
        users=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        getUsers();
    }

    private void getUsers() {
        if (takipciMi){
            users.clear();
            for (String id:currentUser.getFollowers()){
                db.collection("Users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        User user=new User(documentSnapshot);
                        userControl(user);
                        users.add(user);
                        adapter.setUsers(users);
                    }
                });
            }

            return;
        }
        users.clear();
        for (String id:currentUser.getFollowing()){
            db.collection("Users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    User user=new User(documentSnapshot);
                    userControl(user);
                    users.add(user);
                    adapter.setUsers(users);
                }

            });
        }
    }
    public void userControl(User user){
        for(User us:users){
            if (us.getuId().equals(user.getuId())){
                users.remove(us);
            }

        }

    }
}
