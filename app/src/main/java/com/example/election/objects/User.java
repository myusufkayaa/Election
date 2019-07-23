package com.example.election.objects;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable {
    private String uId;
    private String email;
    private ArrayList<String> anketler;
    private ArrayList<String> followers;
    private ArrayList<String> following;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getAnketler() {
        return anketler;
    }

    public void setAnketler(ArrayList<String> anketler) {
        this.anketler = anketler;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public User(String uId, String email) {
        this.uId = uId;
        this.email = email;
        anketler=new ArrayList<>();
        followers=new ArrayList<>();
        following=new ArrayList<>();
    }

    public User(DocumentSnapshot document) {
        uId=(String) ((HashMap)document.getData().get("user")).get("uId");
        email=(String) ((HashMap)document.getData().get("user")).get("email");
        anketler=(ArrayList<String>) ((HashMap)document.getData().get("user")).get("anketler");
        followers=(ArrayList<String>) ((HashMap)document.getData().get("user")).get("followers");
        following=(ArrayList<String>) ((HashMap)document.getData().get("user")).get("following");
    }

    public User() {
    }
}
