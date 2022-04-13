package edu.neu.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {
    FirebaseDatabase database;
    ArrayList<String> categoriesList;
    Spinner categories;
    ImageButton productImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        categories=findViewById(R.id.categorySpinner);
        database = FirebaseDatabase.getInstance();
        categoriesList = new ArrayList<String>();
        productImg=(ImageButton)findViewById(R.id.productImage);
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });
        DatabaseReference category=database.getReference().child("categories");
        category.get().addOnCompleteListener(task1->{
            for (DataSnapshot dataSnapshot1 : task1.getResult().getChildren()) {
                String key = dataSnapshot1.getKey().toString();
                Log.d("Category",key);
                categoriesList.add(key);

            }
            Log.d("Category",categoriesList.toString());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,categoriesList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categories.setAdapter(arrayAdapter);
    });

}
}