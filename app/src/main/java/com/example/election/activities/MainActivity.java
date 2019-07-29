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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText txtMail;
    private EditText txtPass;
    private ImageButton btnGiris;
    private ImageButton btnKayit;
    private Button btnCikis;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        txtMail=findViewById(R.id.txtMail);
        txtPass=findViewById(R.id.txtPass);
        btnGiris=findViewById(R.id.btnGiris);
        btnKayit=findViewById(R.id.btnKayit);
        btnCikis=findViewById(R.id.btncikis);
        auth=FirebaseAuth.getInstance();
        authControl();
    }

    public void giris(View view){
        if (auth.getCurrentUser()!=null){
           Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
           startActivity(intent);
            finish();
            return;
        }
        if (txtMail.getText().toString().equals("")||txtPass.getText().toString().equals("")){
            if (txtMail.getText().toString().equals(""))
                txtMail.setError("Boş Bırakmayın");
            if (txtPass.getText().toString().equals(""))
                txtPass.setError("Boş Bırakmayın");
            return;
        }
        if (auth.getCurrentUser()==null){
            auth.signInWithEmailAndPassword(txtMail.getText().toString(),txtPass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

    }
    public void kayit(View view){
        Intent intent=new Intent(this,KayitActivity.class);
        startActivity(intent);
    }
    public void cikis(View view){
        auth.signOut();
        txtMail.setVisibility(View.VISIBLE);
        txtPass.setVisibility(View.VISIBLE);
        btnKayit.setVisibility(View.VISIBLE);
        btnCikis.setVisibility(View.GONE);
    }
    private void authControl(){
        if (auth.getCurrentUser()==null) return;
        txtMail.setVisibility(View.GONE);
        txtPass.setVisibility(View.GONE);
        btnKayit.setVisibility(View.GONE);
        btnCikis.setVisibility(View.VISIBLE);
        btnCikis.setText(auth.getCurrentUser().getEmail()+"\n Hesabından Çıkış Yap");

    }
}
