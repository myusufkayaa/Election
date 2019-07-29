package com.example.election.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.election.R;
import com.example.election.objects.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class KayitActivity extends AppCompatActivity {
    private EditText txtMail;
    private EditText txtPass;
    private EditText txtPass2;
    private ImageButton btnKayit;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit);
        init();
    }

    private void init() {
        txtMail=findViewById(R.id.txtKmail);
        txtPass=findViewById(R.id.txtKpass);
        txtPass2=findViewById(R.id.txtKpass2);
        btnKayit=findViewById(R.id.btnKayit);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
    }
    public void clickKayit(View view){
        if (txtMail.getText().toString().equals("")||txtPass.getText().toString().equals("")||txtPass2.getText().toString().equals("")){
            if (txtMail.getText().toString().equals(""))
                txtMail.setError("Boş Bırakmayın");
            if (txtPass.getText().toString().equals(""))
                txtPass.setError("Boş Bırakmayın");
            if (txtPass2.getText().toString().equals(""))
                txtPass2.setError("Boş Bırakmayın");
            return;
        }
        if (!txtPass.getText().toString().equals(txtPass2.getText().toString())){
            txtPass2.setError("Şifreler Uyuşmuyor");
            txtPass.setError("Şifreler Uyuşmuyor");
            return;
        }

        auth.createUserWithEmailAndPassword(txtMail.getText().toString(),txtPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                User user=new User(authResult.getUser().getUid(),authResult.getUser().getEmail());
                Map<String, Object> us = new HashMap<>();
                us.put("user", user);
                db.collection("Users").document(auth.getUid()).set(us);
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
