package edu.neu.madcourse.assignment7;

import android.content.Context;
import android.net.Uri;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatsViewAdapter extends RecyclerView.Adapter<StatsViewAdapter.MyViewHolder> {
    private List<Pair<String, Long>> list;
    private Context context;
    private HashMap<String, String> stickers;
    public StatsViewAdapter(Context context, List<Pair<String, Long>> statsList, HashMap<String, String> stickersUrl){
        this.context=context;
        this.list=statsList;
        this.stickers=stickersUrl;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate( R.layout.statsview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String s = list.get(position).first;
        String url="";
        if(stickers.containsKey(s)){
            url=stickers.get(s);
        }
        String c= String.valueOf(list.get(position).second);

//        if(s.equals("angry")){
//            url="https://firebasestorage.googleapis.com/v0/b/assignment7-10bc7.appspot.com/o/stickers%2Fangry.png?alt=media&token=c1589746-c60f-421f-9039-edf74c71cb02";
//        }else if(s.equals("cry")) {
//            url = "https://firebasestorage.googleapis.com/v0/b/assignment7-10bc7.appspot.com/o/stickers%2Fcry.png?alt=media&token=e82099ed-0103-4d5c-9b39-563b51c6abf0";
//        }else if(s.equals("happy")){
//            url="https://firebasestorage.googleapis.com/v0/b/assignment7-10bc7.appspot.com/o/stickers%2Fhappy.png?alt=media&token=f4ff9319-3d3f-483f-bae0-a287139cfbf9";
//        }else{
//            url="https://firebasestorage.googleapis.com/v0/b/assignment7-10bc7.appspot.com/o/stickers%2Fwink.png?alt=media&token=9a9641c7-fce1-465d-b24a-a4a1431d7ff6";
//        }
        Uri uri = Uri.parse(url);
        holder.countTextView.setText("Count: "+ c);
        Picasso.get().load(uri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView countTextView;
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
             countTextView=itemView.findViewById(R.id.count);
             imageView=itemView.findViewById(R.id.emoImage);
        }
    }
}
