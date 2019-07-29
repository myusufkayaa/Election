package com.example.election.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.election.R;
import com.example.election.adapters.ResultAdapter;
import com.example.election.objects.Anket;

public class ResultActivity extends AppCompatActivity {
    RecyclerView lstResults;
    Anket anket;
    ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        init();
    }

    private void init() {
        lstResults=findViewById(R.id.lstResults);
        anket= (Anket) getIntent().getSerializableExtra("Anket");
        adapter=new ResultAdapter(this,anket);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        lstResults.setLayoutManager(layoutManager);
        lstResults.setAdapter(adapter);

    }
}
