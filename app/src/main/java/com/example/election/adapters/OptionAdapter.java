package com.example.election.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.activities.ResultActivity;
import com.example.election.objects.Anket;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;


public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {
    Activity activity;
    Context context;
    Anket anket;
    LayoutInflater inflater;


    public OptionAdapter(Activity activity,Context context, Anket anket) {
        this.activity = activity;
        this.context = context;
        this.anket = anket;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.option_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtOption.setText(anket.getOptions().get(position).getOpName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOption(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return anket.getOptions().size();
    }

    private void updatePoll() {
        Map<String, Object> poll = new HashMap<>();
        poll.put("anket", anket);
        FirebaseFirestore.getInstance().collection("Anketler")
                .document(anket.getAnketId()).set(poll)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "BAŞARILI", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(context, ResultActivity.class);
                        intent.putExtra("Anket",anket);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickOption(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(anket.getOptions().get(position).getOpName());
        builder.setMessage("Oy Vermek İstediğinize Emin Misiniz?");
        builder.setNegativeButton("Hayır", null);
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                anket.getOptions().get(position).getUsers()
                        .add(FirebaseAuth.getInstance().getUid());
                updatePoll();
            }
        });
        builder.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOption;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOption = itemView.findViewById(R.id.txtListOption);
            layout = itemView.findViewById(R.id.cLayout);
        }
    }
}
