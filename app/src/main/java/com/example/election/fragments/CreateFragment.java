package com.example.election.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.election.R;
import com.example.election.objects.Anket;
import com.example.election.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateFragment extends Fragment {
    private View view;
    EditText txtAnketId;
    EditText txtQuestion;
    EditText txtAddOption;
    Button btnAddOption;
    Button btnCreate;
    ListView lstOptions;
    ArrayAdapter<String> adapter;
    ArrayList<String> options;
    FirebaseAuth auth;
    FirebaseFirestore db;
    User currentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_create, container, false);
            init();
        }
        return view;
    }

    private void init() {
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        getCurrentUser();
        options = new ArrayList<>();
        txtAnketId = view.findViewById(R.id.txtAnketId);
        txtQuestion = view.findViewById(R.id.txtQuestion);
        txtAddOption = view.findViewById(R.id.txtAddOption);
        btnAddOption = view.findViewById(R.id.btnAddOption);
        btnCreate = view.findViewById(R.id.btnCreate);
        lstOptions = view.findViewById(R.id.lstOptions);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, options);
        lstOptions.setAdapter(adapter);
        btnAddOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOption();
            }
        });
        lstOptions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteOption(i);
                return false;
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPoll();
            }
        });
    }


    private void addOption() {
        if (txtAddOption.getText().toString().equals("")){
            txtAddOption.setError("Boş Bırakmayın");
            return;
        }
        options.add(txtAddOption.getText().toString());
        txtAddOption.setText("");
        adapter.notifyDataSetChanged();
        UIUtil.hideKeyboard(getActivity());

    }


    private void deleteOption(final int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle(options.get(position));
        builder.setMessage("Bu Seçeneği Silmek İstediğinize Emin Misiniz?");
        builder.setNegativeButton("Hayır",null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                options.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }
    private void createPoll(){
        if (txtAnketId.getText().toString().equals("")||txtQuestion.getText().toString().equals("")){
            if (txtAnketId.getText().toString().equals(""))
                txtAnketId.setError("Boş Bırakmayın");
            if (txtQuestion.getText().toString().equals(""))
                txtQuestion.setError("Boş Bırakmayın");
            return;
        }
        final Anket anket=new Anket(txtQuestion.getText().toString(),auth.getUid(),options,txtAnketId.getText().toString());
        Map<String, Object> poll = new HashMap<>();
        poll.put("anket", anket);
        db.collection("Anketler").document(anket.getAnketId()).set(poll).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                txtAddOption.setText("");
                txtAnketId.setText("");
                txtQuestion.setText("");
                options.clear();
                currentUser.getAnketler().add(anket.getAnketId());
                updateUser();
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(),"Başarılı",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void getCurrentUser(){
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                currentUser = new User(documentSnapshot);
            }
        });

    }
    public void updateUser(){
        Map<String, Object> us = new HashMap<>();
        us.put("user", currentUser);
        db.collection("Users").document(currentUser.getuId()).set(us);
    }


}
