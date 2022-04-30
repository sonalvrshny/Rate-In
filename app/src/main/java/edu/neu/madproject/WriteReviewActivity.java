package edu.neu.madproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WriteReviewActivity extends AppCompatActivity {
    private static final String NUMBER_OF_SELECTED_ITEMS = "NUMBER_OF_SELECTED_ITEMS";
    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    FirebaseDatabase database;
    EditText editTitle, editDesc;
    ArrayList<String> categoriesList;
    ArrayList<String> tagList;
    ArrayList<String> selectTagList;
    Spinner categories;
    AppCompatAutoCompleteTextView tags;
    ChipGroup tagsCG;
    ImageButton productImg;
    ImageView back;
    Uri ImageUri;
    TextView submitData;
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
        tags=findViewById(R.id.tags);
        tagsCG=findViewById(R.id.group_tags);
        database = FirebaseDatabase.getInstance();
        editDesc=findViewById(R.id.reviewDesc);
        editTitle=findViewById(R.id.title);
        categoriesList = new ArrayList<String>();
        tagList = new ArrayList<>();
        selectTagList = new ArrayList<>();
        submitData=findViewById(R.id.submit);
        productImg=(ImageButton)findViewById(R.id.productImage);
        back=findViewById(R.id.img_back);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        // doSomeOperations();
                        Intent data = result.getData();
                        ImageUri = Objects.requireNonNull(data).getData();
                        InputStream imageStream = null;
                        try {
                            imageStream = getContentResolver().openInputStream(ImageUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        BitmapFactory.decodeStream(imageStream);
                        productImg.setImageURI(ImageUri);// To display selected image in image view
                    }
                });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteReviewActivity.this.finish();
            }
        });
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent openGallery=new Intent();
//                openGallery.setAction(Intent.ACTION_GET_CONTENT);
//                openGallery.setType("image/*");
//                startActivityForResult(openGallery,2);
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                someActivityResultLauncher.launch(photoPickerIntent);
            }
        });
        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ImageUri==null){
                    uploadToFirebase(ImageUri, false);
                }else{
//                    Snackbar.make(findViewById(R.id.relativeLayout), "Please select a image",
//                            Snackbar.LENGTH_SHORT)
//                            .show();
                    uploadToFirebase(ImageUri, true);
                }
            }
        });

        DatabaseReference category=database.getReference().child("categories");
        DatabaseReference tag=database.getReference().child("tags");
        category.get().addOnCompleteListener(task1 -> {
            for (DataSnapshot dataSnapshot1 : task1.getResult().getChildren()) {
                String key = dataSnapshot1.getKey().toString();
                categoriesList.add(key);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,categoriesList);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categories.setAdapter(arrayAdapter);
            tag.get().addOnCompleteListener(task2->{
                for (DataSnapshot dataSnapshot1 : task2.getResult().getChildren()) {
                    String key = dataSnapshot1.getValue().toString();
                    tagList.add(key);
                }
                ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, R.layout.chip_item, tagList);
//                tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tags.setAdapter(tagAdapter);
                if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_SELECTED_ITEMS)
                        && (selectTagList == null || selectTagList.size() == 0)) {
                    int size = savedInstanceState.getInt(NUMBER_OF_SELECTED_ITEMS);
                    for (int i = 0; i < size; i++) {
                        String item = savedInstanceState.getString(KEY_OF_INSTANCE + i);
                        selectTagList.add(item);
                        Chip c = new Chip(this);
                        c.setText(item);
                        c.setCloseIconVisible(true);
                        c.setOnCloseIconClickListener(y -> {
                            selectTagList.remove(c.getText());
                            ((ChipGroup)c.getParent()).removeView(c);
                        });
                        tagsCG.addView(c);
                    }
                }
                tags.setOnItemClickListener((adapterView, view, i, l) -> {
                    String item = String.valueOf(adapterView.getItemAtPosition(i));
                    tags.setText("");
                    if(!selectTagList.contains(item)) {
                        selectTagList.add(item);
                        Chip c = new Chip(this);
                        c.setText(item);
                        c.setCloseIconVisible(true);
                        c.setOnCloseIconClickListener(y -> {
                            selectTagList.remove(c.getText());
                            ((ChipGroup)c.getParent()).removeView(c);
                        });
                        tagsCG.addView(c);
                    }
                });
                tags.setOnEditorActionListener((textView, i, keyEvent) -> {
                    boolean handled = false;
                    if (i == EditorInfo.IME_ACTION_NEXT || i == EditorInfo.IME_NULL) {
                        handled = true;
                        String item = String.valueOf(textView.getText());
                        if(item.isEmpty()) return true;
                        textView.setText("");
                        if(!selectTagList.contains(item)) {
                            selectTagList.add(item);
                            Chip c = new Chip(this);
                            c.setText(item);
                            c.setCloseIconVisible(true);
                            c.setOnCloseIconClickListener(y -> {
                                selectTagList.remove(c.getText());
                                ((ChipGroup)c.getParent()).removeView(c);
                            });
                            tagsCG.addView(c);
                        }
                        if(!tagList.contains(item)) {
                            tagList.add(item);
                            Map<String, String> map = new HashMap<>();
                            int itr = 0;
                            for (String t: tagList) {
                                map.put(String.valueOf(itr++), t);
                            }
                            tag.setValue(map).addOnCompleteListener(setTask -> {
                                if (!setTask.isSuccessful()) {
                                    Log.e("Stat Update failed", "There was an error while updating stats");
                                }
                            });
                        }
                    }
                    return handled;
                });
            });
        });

    }

    private void uploadToFirebase(Uri uri, boolean isImage) {
        if(!isImage){
            progressBar.setVisibility(View.INVISIBLE);
            WriteReviewModel model = new WriteReviewModel(
                    editTitle.getText().toString(), simpleRatingBar.getRating(),
                    categories.getSelectedItem().toString(), editDesc.getText().toString(),
                    auth.getCurrentUser().getEmail().split("@")[0], auth.getUid(),
                    WriteReviewActivity.this.selectTagList);
            String modelId = imageUpload.push().getKey();
            imageUpload.child(modelId).setValue(model);
            Snackbar.make(findViewById(R.id.relativeLayout), "Review submitted",
                    Snackbar.LENGTH_SHORT)
                    .show();
            DatabaseReference sender = database.getReference().child("userHistory").child(auth.getUid());
            sender.get().addOnCompleteListener(getTask -> {
                if (!getTask.isSuccessful()) {
                    Log.e("Stat Update failed", "There was an error while updating stats");
                    return;
                }
                DataSnapshot snapshot = getTask.getResult();
                Map<String, Long> history = (Map<String, Long>) snapshot.child("reads").getValue();
                Map<String, Long> wHistory = (Map<String, Long>) snapshot.child("writes").getValue();
                Map<String, Long> wTHistory = (Map<String, Long>) snapshot.child("tagWrites").getValue();
                Map<String, Long> tHistory = (Map<String, Long>) snapshot.child("tagReads").getValue();
                if (wTHistory == null) wTHistory = new HashMap<>();
                if (tHistory == null) tHistory = new HashMap<>();
                if (history == null) history = new HashMap<>();
                if (wHistory == null) wHistory = new HashMap<>();
                wHistory.put(categories.getSelectedItem().toString(),
                        wHistory.getOrDefault(categories.getSelectedItem().toString(),
                                0L) + 1);
                for (String tag : selectTagList) {
                    wTHistory.put(tag, wTHistory.getOrDefault(tag, 0L) + 1);
                }
                UsersStats stats = new UsersStats(history, wHistory, tHistory, wTHistory);
                sender.setValue(stats).addOnCompleteListener(setTask -> {
                    if (!setTask.isSuccessful()) {
                        Log.e("Stat Update failed", "There was an error while updating stats");
                    }
                });
            });
            WriteReviewActivity.this.finish();
        }
        else {
            StorageReference fileRef = imageStorage.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressBar.setVisibility(View.INVISIBLE);
                            WriteReviewModel model = new WriteReviewModel(uri.toString(),
                                    editTitle.getText().toString(), simpleRatingBar.getRating(),
                                    categories.getSelectedItem().toString(), editDesc.getText().toString(),
                                    auth.getCurrentUser().getEmail().split("@")[0], auth.getUid(),
                                    WriteReviewActivity.this.selectTagList);
                            String modelId = imageUpload.push().getKey();
                            imageUpload.child(modelId).setValue(model);
                            Snackbar.make(findViewById(R.id.relativeLayout), "Review submitted",
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                            DatabaseReference sender = database.getReference().child("userHistory").child(auth.getUid());
                            sender.get().addOnCompleteListener(getTask -> {
                                if (!getTask.isSuccessful()) {
                                    Log.e("Stat Update failed", "There was an error while updating stats");
                                    return;
                                }
                                DataSnapshot snapshot = getTask.getResult();
                                Map<String, Long> history = (Map<String, Long>) snapshot.child("reads").getValue();
                                Map<String, Long> wHistory = (Map<String, Long>) snapshot.child("writes").getValue();
                                Map<String, Long> wTHistory = (Map<String, Long>) snapshot.child("tagWrites").getValue();
                                Map<String, Long> tHistory = (Map<String, Long>) snapshot.child("tagReads").getValue();
                                if (wTHistory == null) wTHistory = new HashMap<>();
                                if (tHistory == null) tHistory = new HashMap<>();
                                if (history == null) history = new HashMap<>();
                                if (wHistory == null) wHistory = new HashMap<>();
                                wHistory.put(categories.getSelectedItem().toString(),
                                        wHistory.getOrDefault(categories.getSelectedItem().toString(),
                                                0L) + 1);
                                for (String tag : selectTagList) {
                                    wTHistory.put(tag, wTHistory.getOrDefault(tag, 0L) + 1);
                                }
                                UsersStats stats = new UsersStats(history, wHistory, tHistory, wTHistory);
                                sender.setValue(stats).addOnCompleteListener(setTask -> {
                                    if (!setTask.isSuccessful()) {
                                        Log.e("Stat Update failed", "There was an error while updating stats");
                                    }
                                });
                            });
                            WriteReviewActivity.this.finish();
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
    }

    private String getFileExtension(Uri muri) {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(muri));
    }

    // Handling Orientation Changes on Android
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int selectSize = selectTagList == null ? 0 : selectTagList.size();
//        int tagSize = tagList == null ? 0 : tagList.size();

//        outState.putInt(NUMBER_OF_ITEMS, tagSize);
        outState.putInt(NUMBER_OF_SELECTED_ITEMS, selectSize);

        for (int i = 0; i < selectSize; i++) {
            outState.putString(KEY_OF_INSTANCE + i, selectTagList.get(i));
        }
//        for (int i = 0; i < tagSize; i++) {
//            outState.putString(KEY_OF_INSTANCE + i, tagList.get(i));
//        }
        super.onSaveInstanceState(outState);
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==2 && resultCode==RESULT_OK && data!=null){
//        ImageUri=data.getData();
//        productImg.setImageURI(ImageUri);
//        }
//    }
}