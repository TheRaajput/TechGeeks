package com.yasharth.techgeeks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageUri = null;
    private Button saveBtn;
    private EditText setupName;
    private String name;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private ProgressBar setupProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        Toolbar setUpToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setUpToolbar);
        getSupportActionBar().setTitle("Account Settings");

        setupName = findViewById(R.id.Setup_Name);
        setupImage = findViewById(R.id.setup_image);
        saveBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);


        setupProgress.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("Name");
                        String image = task.getResult().getString("Image");

                        mainImageUri = Uri.parse(image);

                        setupName.setText(name);


                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);

                    }

                }else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Error:" +message,Toast.LENGTH_LONG).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
                saveBtn.setEnabled(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllData();
            }
        });



        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    }else {

                        bringImagepicker();
                    }

                }else {
                    bringImagepicker();
                }

            }
        });

    }

    private void bringImagepicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);

    }

    private void getAllData() {
            name = setupName.getText().toString();
            if (mainImageUri == null) {

                Toast.makeText(SetupActivity.this, "Select Image", Toast.LENGTH_LONG).show();

            } else if (TextUtils.isEmpty(name)) {
                Toast.makeText(SetupActivity.this, "Input Name", Toast.LENGTH_LONG).show();
            } else {
                StoreInformation();
            }
    }

    private void StoreInformation() {


            final StorageReference filepath = storageReference.child("profile_images").child(user_id + ".jpg");
            final UploadTask uploadTask = filepath.putFile(mainImageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("Name", name);
                            userMap.put("Image", uri.toString());
                            firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(SetupActivity.this, "Settings updated", Toast.LENGTH_LONG).show();
                                        Intent IntoMain = new Intent(SetupActivity.this, MainActivity.class);
                                        startActivity(IntoMain);
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(SetupActivity.this, "FIRESTORE Error:" + message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
