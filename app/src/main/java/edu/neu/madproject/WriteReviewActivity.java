package edu.neu.madproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class WriteReviewActivity extends AppCompatActivity {
    FirebaseDatabase database;
    EditText editTitle, editDesc;
    ArrayList<String> categoriesList;
    Spinner categories;
    ImageButton productImg;
    Uri ImageUri;
    Button submitData;
    ProgressBar progressBar;
    FirebaseAuth auth;
    RatingBar simpleRatingBar ;
    StorageReference imageStorage= FirebaseStorage.getInstance().getReference();
    DatabaseReference imageUpload=FirebaseDatabase.getInstance().getReference("reviews");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write_review);
        auth = FirebaseAuth.getInstance();
        simpleRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        categories=findViewById(R.id.categorySpinner);
        database = FirebaseDatabase.getInstance();
        editDesc=findViewById(R.id.reviewDesc);
        editTitle=findViewById(R.id.title);
        categoriesList = new ArrayList<String>();
        submitData=findViewById(R.id.submit);
        productImg=(ImageButton)findViewById(R.id.productImage);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery=new Intent();
                openGallery.setAction(Intent.ACTION_GET_CONTENT);
                openGallery.setType("image/*");
                startActivityForResult(openGallery,2);
            }
        });
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ImageUri!=null){
                        uploadToFirebase(ImageUri);
                }else{
                    Snackbar.make(findViewById(R.id.relativeLayout), "Please select a image",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
        Log.d("username", String.valueOf(auth.getCurrentUser()));
//        DatabaseReference username = database.getReference().child("user").child(auth.getUid());
//        username.get().addOnCompleteListener(task -> {
//            for (DataSnapshot dataSnapshot1 : task.getResult().getChildren()) {
//                String key = dataSnapshot1.getKey().toString();
//                Log.d("username",key);
//            }
//        });
        DatabaseReference category=database.getReference().child("categories");
        category.get().addOnCompleteListener(task1->{
            for (DataSnapshot dataSnapshot1 : task1.getResult().getChildren()) {
                String key = dataSnapshot1.getKey().toString();
                categoriesList.add(key);

            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,categoriesList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categories.setAdapter(arrayAdapter);
    });

}

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef=imageStorage.child(System.currentTimeMillis()+"."+getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    progressBar.setVisibility(View.INVISIBLE);

                    WriteReviewModel model =new WriteReviewModel(uri.toString(),editTitle.getText().toString(),simpleRatingBar.getRating(),categories.getSelectedItem().toString(),editDesc.getText().toString());
                    String modelId=imageUpload.push().getKey();
                    imageUpload.child(modelId).setValue(model);
                    Snackbar.make(findViewById(R.id.relativeLayout), "Image Upload Successfully",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.relativeLayout), "Image Upload failed",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private String getFileExtension(Uri muri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2 && resultCode==RESULT_OK && data!=null){
        ImageUri=data.getData();
        productImg.setImageURI(ImageUri);
        }
    }
}