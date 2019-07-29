package com.example.election.objects;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Anket implements Serializable {
    private String anketId;
    private String question;
    private String userId;
    ArrayList<Option> options;

    public ArrayList<Option> getOptions() {
        return options;
    }

    public String getAnketId() {
        return anketId;
    }

    public void setAnketId(String anketId) {
        this.anketId = anketId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Anket(String question, String userId, ArrayList<String> optionList) {

        this.question = question;
        this.userId = userId;
        options = new ArrayList<>();
        for (String op : optionList)
            options.add(new Option(op));
    }
    public Anket(DocumentSnapshot document){
        question=(String) ((HashMap)document.getData().get("anket")).get("question");
        userId=(String) ((HashMap)document.getData().get("anket")).get("userId");
        options=new ArrayList<>();
        for (HashMap option:((ArrayList<HashMap>) ((HashMap)document.getData().get("anket")).get("options"))){
            options.add(new Option(option));
        }
    }
}
