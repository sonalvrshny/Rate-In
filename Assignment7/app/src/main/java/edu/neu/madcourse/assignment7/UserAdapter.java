package edu.neu.madcourse.assignment7;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context HomeActivity;
    ArrayList<Users> usersList;
    public UserAdapter(HomeActivity homeActivity, ArrayList<Users> usersList) {
    this.HomeActivity=homeActivity;
    this.usersList=usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(HomeActivity).inflate(R.layout.item_user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users=usersList.get(position);
        String[] name=users.username.split("@");
        holder.username.setText(name[0]);
        holder.userStatus.setText(users.status);

        // listener for click on user to send sticker
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity, ChatActivity.class);
            intent.putExtra("name", users.getUsername());
            intent.putExtra("uid", users.getUid());
            HomeActivity.startActivity(intent);
        });

    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userprofile;
        TextView username;
        TextView userStatus;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            userprofile=itemView.findViewById(R.id.user_image);
            username=itemView.findViewById(R.id.username);
            userStatus=itemView.findViewById(R.id.status);


        }
    }
}
