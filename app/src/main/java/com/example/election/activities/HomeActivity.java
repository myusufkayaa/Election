package com.example.election.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.election.fragments.CreateFragment;
import com.example.election.R;
import com.example.election.fragments.HomeFragment;
import com.example.election.fragments.MyPollFragment;
import com.example.election.fragments.SearchFragment;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.election.objects.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import javax.annotation.Nullable;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    FirebaseFirestore db;
    CreateFragment createFragment;
    SearchFragment searchFragment;
    TextView txtUser;
    TextView txtFollowers;
    TextView txtFollowing;
    HomeFragment homeFragment;
    User currentUser;
    MyPollFragment myPollFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        createFragment = new CreateFragment();
        searchFragment = new SearchFragment();
        homeFragment=new HomeFragment();
        myPollFragment=new MyPollFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        txtUser = headerView.findViewById(R.id.txtUser);
        txtFollowers=headerView.findViewById(R.id.txtFollowers);
        txtFollowing=headerView.findViewById(R.id.txtFollowing);
        db=FirebaseFirestore.getInstance();
        setUserTexts();
        txtUser.setText(auth.getCurrentUser().getEmail());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,homeFragment).commit();
        getSupportActionBar().setTitle("Anasayfa");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void openUsers(){
        final Intent intent=new Intent(getApplicationContext(),UserListActivity.class);
        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("bool",false);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });
        txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("bool",true);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void setUserTexts(){
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    currentUser=new User(documentSnapshot);
                    txtFollowers.setText(String.valueOf(currentUser.getFollowers().size()));
                    txtFollowing.setText(String.valueOf(currentUser.getFollowing().size()));
                    openUsers();

            }
        });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,homeFragment).commit();
                getSupportActionBar().setTitle("Anasayfa");
                break;
            case R.id.nav_create:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, createFragment).commit();
                getSupportActionBar().setTitle("Anket Oluştur");
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
                getSupportActionBar().setTitle("Ara");
                break;
            case R.id.nav_anketler:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,myPollFragment).commit();
                getSupportActionBar().setTitle("Anketlerim");
                break;
            case R.id.nav_signOut:
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
