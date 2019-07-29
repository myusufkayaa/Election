package com.example.election.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.activities.PollActivity;
import com.example.election.activities.ResultActivity;
import com.example.election.objects.Anket;
import com.example.election.objects.Option;
import com.example.election.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<Anket> anketler;
    LayoutInflater layoutInflater;
    Activity activity;
    User currentUser;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseFirestore db=FirebaseFirestore.getInstance();


    public SearchAdapter(Activity activity) {
        this.activity = activity;
        anketler = new ArrayList<>();
        getCurrentUser();
    }

    public void setAnketler(ArrayList<Anket> anketler) {
        this.anketler = anketler;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Anket anket = anketler.get(position);
        if (anket == null) return;
        holder.txtQuestion.setText(anket.getQuestion());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userControl(anket)){
                    Intent intent=new Intent(activity, ResultActivity.class);
                    intent.putExtra("Anket", (Serializable) anketler.get(position));
                    activity.startActivity(intent);
                    return;
                }
                Intent intent = new Intent(activity, PollActivity.class);
                intent.putExtra("Anket", (Serializable) anketler.get(position));
                activity.startActivity(intent);
            }
        });
        setBtnDelete(holder,position);
        holder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Anket");
                intent.putExtra(Intent.EXTRA_TEXT, anketler.get(position).getAnketId());
                activity.startActivity(Intent.createChooser(intent,"Şununla Paylaş"));
            }
        });

    }
    private void getCurrentUser(){
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                currentUser=new User(documentSnapshot);
            }
        });
    }
    private void clickDelete(ViewHolder holder, final int position){
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser.getAnketler().remove(anketler.get(position).getAnketId());
                Map<String, Object> us = new HashMap<>();
                us.put("user", currentUser);
                db.collection("Users").document(currentUser.getuId()).set(us);
                db.collection("Anketler").document(anketler.get(position).getAnketId()).delete();
                anketler.remove(position);
                notifyDataSetChanged();
            }
        });

    }
    private void setBtnDelete(ViewHolder holder,int position){
        holder.btnDelete.setVisibility(View.GONE);
        for (String id:currentUser.getAnketler()){
            if(id.equals(anketler.get(position).getAnketId())){
                holder.btnDelete.setVisibility(View.VISIBLE);
                clickDelete(holder,position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return anketler.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestion;
        ConstraintLayout layout;
        ImageButton btnDelete;
        ImageButton btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtQuestion = itemView.findViewById(R.id.txtItemQuestion);
            layout = itemView.findViewById(R.id.layout);
            btnDelete=itemView.findViewById(R.id.btnDelete);
            btnShare=itemView.findViewById(R.id.btnShare);
        }
    }
    private  boolean userControl(Anket anket){
        for (Option option:anket.getOptions()){
            for (String id:option.getUsers()){
                if (FirebaseAuth.getInstance().getUid().equals(id))
                    return true;
            }
        }
        return false;
    }
}
