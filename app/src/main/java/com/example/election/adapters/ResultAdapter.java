package com.example.election.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.objects.Anket;
import com.example.election.objects.Option;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    Activity activity;
    Anket anket;
    LayoutInflater inflater;

    public ResultAdapter(Activity activity, Anket anket) {
        this.activity = activity;
        this.anket = anket;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.result_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int toplamOy = 0;
        for (Option option : anket.getOptions()) {
            toplamOy += option.getUsers().size();
        }
        double yuzde = 0;
        if (anket.getOptions().get(position).getUsers().size() != 0)
            yuzde = (100*anket.getOptions().get(position).getUsers().size())/toplamOy;
        holder.txtOption.setText(anket.getOptions().get(position).getOpName() + "  :  %"
                + yuzde + "  (" + anket.getOptions().get(position).getUsers().size() + " oy)");

    }

    @Override
    public int getItemCount() {
        return anket.getOptions().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtOption;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOption = itemView.findViewById(R.id.txtSonuc);
            layout = itemView.findViewById(R.id.rLayout);
        }
    }
}
