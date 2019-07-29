package com.example.election.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Option implements Serializable {
    private String opName;


    private ArrayList<String> users;

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public Option(String opName) {
        this.opName = opName;
        users=new ArrayList<>();
    }
    public Option(HashMap option){
        opName= (String) option.get("opName");
        users= (ArrayList<String>) option.get("users");
    }
}
