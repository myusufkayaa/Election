package com.example.election.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.election.R;
import com.example.election.activities.ProfileActivity;
import com.example.election.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    ArrayList<User> users;
    Activity activity;
    LayoutInflater inflater;
    FirebaseFirestore db;
    User currentUser;
    FirebaseAuth auth;

    public UsersAdapter(Activity activity) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.users = new ArrayList<>();
        this.activity = activity;
        getCurrentUser();
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        setButton(position, holder);
        holder.txtMail.setText(users.get(position).getEmail());
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFollow(position);
                setButton(position,holder);
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, ProfileActivity.class);
                intent.putExtra("user",users.get(position));
                activity.startActivity(intent);
            }
        });

    }

    public void clickFollow(int position) {
        if (isFollowing(position)) {
            currentUser.getFollowing().remove(users.get(position).getuId());
            users.get(position).getFollowers().remove(currentUser.getuId());
        } else {
            currentUser.getFollowing().add(users.get(position).getuId());
            users.get(position).getFollowers().add(currentUser.getuId());
        }
        updateUser(currentUser);
        updateUser(users.get(position));


    }

    public boolean isFollowing(int position) {
        for (String id : currentUser.getFollowing()) {
            if (users.get(position).getuId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void updateUser(User user) {
        Map<String, Object> us = new HashMap<>();
        us.put("user", user);
        db.collection("Users").document(user.getuId()).set(us);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void getCurrentUser() {
        db.collection("Users").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                currentUser = new User(documentSnapshot);
            }
        });


    }

    public void setButton(int position, ViewHolder holder) {
        if (currentUser.getuId().equals(users.get(position).getuId())){
            holder.btnFollow.setVisibility(View.GONE);
            return;
        }
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.btnFollow.setText("Takip Et");
        for (String id : currentUser.getFollowing()) {
            if (id.equals(users.get(position).getuId())) {
                holder.btnFollow.setText("Takipten Çık");
                return;
            }
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMail;
        Button btnFollow;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMail = itemView.findViewById(R.id.txtListMail);
            layout = itemView.findViewById(R.id.uLayout);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}
