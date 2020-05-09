package com.yasharth.techgeeks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {
    private Toolbar newPostToolbar;
    private Button newPostBtn;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Uri postImageUri = null;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String desc, current_user_id, randomName;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        randomName = UUID.randomUUID().toString();

        newPostProgress = findViewById(R.id.newpost_progress);
        newPostToolbar = findViewById(R.id.new_postToolbar);


        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.newpost_image);
        newPostDesc = findViewById(R.id.Desc_text);
        newPostBtn = findViewById(R.id.post_button);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);

            }
        });


        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllData();
            }
        });

    }

    private void getAllData() {
        desc = newPostDesc.getText().toString();

        if (TextUtils.isEmpty(desc)){
            Toast.makeText(NewPostActivity.this,"Give Description",Toast.LENGTH_LONG).show();
        }else if (postImageUri == null){
            Toast.makeText(NewPostActivity.this,"Enter Image",Toast.LENGTH_LONG).show();
        }else{
            storeData();

        }
    }

    private void storeData() {

        final StorageReference filepath = storageReference.child("post_images").child(randomName +".jpg");
        final UploadTask uploadTask = filepath.putFile(postImageUri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newPostProgress.setVisibility(View.VISIBLE);

                        HashMap<String,Object> postMap = new HashMap<>();
                        postMap.put("desc", desc);
                        postMap.put("image_url", uri.toString());
                        postMap.put("user_id", current_user_id);
                        postMap.put("timestamp", FieldValue.serverTimestamp());


                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(NewPostActivity.this,"Posted",Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(NewPostActivity.this,MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();

                                    newPostProgress.setVisibility(View.INVISIBLE);
                                }else {

                                    Toast.makeText(NewPostActivity.this,"Not Posted",Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewPostActivity.this, "Error: " +e,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

              postImageUri = result.getUri();
              newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(NewPostActivity.this, "Error: " +error,Toast.LENGTH_SHORT).show();
            }
        }
    }

}


