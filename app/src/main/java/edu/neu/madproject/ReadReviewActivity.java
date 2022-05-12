package edu.neu.madproject;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class ReadReviewActivity extends AppCompatActivity {
ImageView imageView;
TextView category,username,title,rating,descText, reviewItemTitle;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_review);
        imageView=findViewById(R.id.imageView);
        category=findViewById(R.id.category);
        title=findViewById(R.id.title);
        username=findViewById(R.id.username);
        rating=findViewById(R.id.rating);
        descText=findViewById(R.id.descText);
        reviewItemTitle=findViewById(R.id.reviewTitle);
        back=findViewById(R.id.img_back);
        Uri uri = Uri.parse(getIntent().getStringExtra("image"));
        Picasso.get().load(uri).into(imageView);
        Float ratingval=getIntent().getFloatExtra("rating",0);
        DecimalFormat df = new DecimalFormat("#.#");
        category.setText(getIntent().getStringExtra("category"));
        title.setText(getIntent().getStringExtra("title"));
        reviewItemTitle.setText(getIntent().getStringExtra("title")+" review");
        rating.setText(String.valueOf(df.format(ratingval)));
        username.setText(getIntent().getStringExtra("username"));
        descText.setText(getIntent().getStringExtra("content"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadReviewActivity.this.finish();
            }
        });
    }
}