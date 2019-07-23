package com.example.election.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.activities.PollActivity;
import com.example.election.activities.ResultActivity;
import com.example.election.objects.Anket;
import com.example.election.objects.Option;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<Anket> anketler;
    LayoutInflater layoutInflater;
    Activity activity;

    public SearchAdapter(Activity activity) {
        this.activity = activity;
        anketler = new ArrayList<>();
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
        holder.txtItemId.setText("Anket ID: " + anket.getAnketId());
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

    }

    @Override
    public int getItemCount() {
        return anketler.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtItemId, txtQuestion;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemId = itemView.findViewById(R.id.txtItemId);
            txtQuestion = itemView.findViewById(R.id.txtItemQuestion);
            layout = itemView.findViewById(R.id.layout);
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
