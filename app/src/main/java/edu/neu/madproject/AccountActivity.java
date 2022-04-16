package edu.neu.madproject;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import edu.neu.madproject.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView profilePic;
    Uri imageUri;
    ActivityResultLauncher<String> getImage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        //System.out.println(reference);
        databaseReference = database.getReference().child("user").child(Objects.requireNonNull(auth.getUid()));
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        profilePic = findViewById(R.id.profilePic);
        Button logout = findViewById(R.id.logout);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        final String[][] split = new String[1][1];// = username.split("@");
        System.out.println(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getMetadata()).getLastSignInTimestamp());

        getImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                //profilePic.setImageURI(result);
                uploadToFirebase(result);
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage.launch("image/*");

            }
        });
        logout.setOnClickListener(view -> {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            auth.signOut();
            AccountActivity.this.finish();
        });
        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (users.getUid().equals(auth.getUid())) {
                        split[0] = users.getUsername().split("@");
                        toolBarLayout.setTitle(split[0][0]);
                        profilePic.setImageURI(Uri.parse(users.getImageUrl()));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        /*databaseReference.get().addOnCompleteListener(task -> {
            DataSnapshot dataSnapshot = task.getResult();
            split[0] = dataSnapshot.child("username").getValue().toString().split("@");
            toolBarLayout.setTitle(split[0][0]);
            //profilePic.setImageURI();
            Picasso.get().load(Uri.parse(dataSnapshot.child("imageUrl").getValue().toString())).into(profilePic);
        });*/
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                split[0] = snapshot.child("username").getValue().toString().split("@");
                toolBarLayout.setTitle(split[0][0]);
                if(snapshot.hasChild("imageUrl")) {
                    Picasso.get().load(Uri.parse(snapshot.child("imageUrl").getValue().toString())).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void uploadToFirebase(Uri uri) {
        StorageReference local = storageReference.child(System.currentTimeMillis() + "." + fetchFileExtension(uri));
        local.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                local.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        databaseReference.child("imageUrl").setValue(uri.toString());
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private String fetchFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}