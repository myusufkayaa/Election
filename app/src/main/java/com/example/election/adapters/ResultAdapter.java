package com.example.election.adapters;

import android.app.Activity;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.objects.Anket;
import com.example.election.objects.Option;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    Activity activity;
    Anket anket;
    LayoutInflater inflater;
    FirebaseAuth auth;
    FirebaseFirestore db;

    public ResultAdapter(Activity activity, Anket anket) {
        this.activity = activity;
        this.anket = anket;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.result_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        setButtonDelete(holder);
        int toplamOy = 0;
        for (Option option : anket.getOptions()) {
            toplamOy += option.getUsers().size();
        }
        double yuzde = 0;
        if (anket.getOptions().get(position).getUsers().size() != 0)
            yuzde = (100*anket.getOptions().get(position).getUsers().size())/toplamOy;
        holder.txtOption.setText(anket.getOptions().get(position).getOpName() + "  :  %"
                + yuzde + "  (" + anket.getOptions().get(position).getUsers().size() + " oy)");
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOption(position);
            }
        });

    }
    public void setButtonDelete(ViewHolder holder){
        if (anket.getUserId().equals(auth.getUid())){
            holder.btnDelete.setVisibility(View.VISIBLE);
            return;
        }
        holder.btnDelete.setVisibility(View.GONE);
    }
    public void deleteOption(int position){
        anket.getOptions().remove(position);
        Map<String, Object> poll = new HashMap<>();
        poll.put("anket", anket);
        db.collection("Anketler").document(anket.getAnketId()).set(poll).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return anket.getOptions().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOption;
        ConstraintLayout layout;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOption = itemView.findViewById(R.id.txtSonuc);
            layout = itemView.findViewById(R.id.rLayout);
            btnDelete=itemView.findViewById(R.id.btnOptionDelete);
        }
    }
}
