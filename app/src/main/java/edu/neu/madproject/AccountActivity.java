package edu.neu.madproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import edu.neu.madproject.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView profilePic;
    ActivityResultLauncher<String> getImage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Button uploadImage;
    Button myReviewsButton;
    TextView lastLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        myReviewsButton = findViewById(R.id.myReviewsButton);
        lastLogin = findViewById(R.id.lastLogin);
        uploadImage = findViewById(R.id.upload);
        profilePic = findViewById(R.id.profilePic);
        Button logout = findViewById(R.id.logout);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        final String[][] split = new String[1][1];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss");
        lastLogin.append(formatter.format(new Date(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getMetadata()).getLastSignInTimestamp())));
        uploadImage.setOnClickListener(view -> getImage.launch("image/*"));
        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), this::uploadToFirebase);
        logout.setOnClickListener(view -> {
            SharedPrefUtils.savePassword("", this);
            SharedPrefUtils.saveEmail("", this);
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            Log.d("SharedPref", intent.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            AccountActivity.this.finish();
            auth.signOut();
        });

        myReviewsButton.setOnClickListener(view -> {
            Intent intent = new Intent(AccountActivity.this,MyReviewsActivity.class);
            intent.putExtra("username",toolBarLayout.getTitle());
            startActivity(intent);
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                split[0] = Objects.requireNonNull(snapshot.child("username").getValue()).toString().split("@");
                toolBarLayout.setTitle(split[0][0]);
                if(snapshot.hasChild("imageUrl")) {
                    Picasso.get().load(Uri.parse(Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString())).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void uploadToFirebase(Uri uri) {

        if(uri!=null) {
            separateThread sep = new separateThread(uri);
            new Thread(sep).start();
        }
    }

    private String fetchFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    class separateThread implements Runnable{

        Uri uri;
        public separateThread(Uri uri) {
            this.uri = uri;
        }

        @Override
        public void run() {
            StorageReference local = storageReference.child(System.currentTimeMillis() + "." + fetchFileExtension(uri));
            local.putFile(uri).addOnSuccessListener(taskSnapshot -> local.getDownloadUrl().addOnSuccessListener(uri1 -> databaseReference.child("imageUrl").setValue(uri1.toString()))).addOnProgressListener(snapshot -> {

            }).addOnFailureListener(e -> {

            });

        }
    }

}