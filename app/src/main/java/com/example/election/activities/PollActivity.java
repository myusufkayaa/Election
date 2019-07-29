package com.example.election.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.election.R;
import com.example.election.adapters.OptionAdapter;
import com.example.election.objects.Anket;
import com.example.election.objects.Option;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.HashMap;
import java.util.Map;

public class PollActivity extends AppCompatActivity {
    TextView txtQuestion;
    EditText txtAddOption;
    Button btnAddOption;
    RecyclerView lstOptions;
    Anket anket;
    OptionAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        init();
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        txtQuestion = findViewById(R.id.txtPQuestion);
        txtAddOption = findViewById(R.id.txtPOption);
        btnAddOption = findViewById(R.id.btnAddOption);
        lstOptions = findViewById(R.id.lstPOptions);
        if (getIntent().hasExtra("Anket"))
            anket = (Anket) getIntent().getSerializableExtra("Anket");
        adapter = new OptionAdapter(this, getApplicationContext(), anket);
        txtQuestion.setText(anket.getQuestion());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        lstOptions.setLayoutManager(layoutManager);
        lstOptions.setAdapter(adapter);
    }

    public void clickAdd(View view) {
        if (txtAddOption.getText().toString().equals("")) {
            txtAddOption.setError("Boş Bırakmayın");
            return;
        }
        anket.getOptions().add(new Option(txtAddOption.getText().toString()));
        Map<String, Object> poll = new HashMap<>();
        poll.put("anket", anket);
        db.collection("Anketler").document(anket.getAnketId()).set(poll).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                txtAddOption.setText("");
                InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
