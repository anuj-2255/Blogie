package com.worldtechq.blog.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.worldtechq.blog.Helper.Upload;
import com.worldtechq.blog.R;

public class UploadImages extends AppCompatActivity {


    ImageView iv;
    Button upbtn, slctbtn;
    EditText ed;
    ProgressBar pbar;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    private StorageTask task;

    private Uri uri;
    final static int PReqCode = 1;
    static int REQUESCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        getSupportActionBar().hide();
        iv = findViewById(R.id.imageView);
        iv.setVisibility(View.INVISIBLE);
        upbtn = findViewById(R.id.upbtn);
        slctbtn = findViewById(R.id.slctbtn);

        ed = findViewById(R.id.et);

        pbar = findViewById(R.id.progressBar);
        pbar.setVisibility(View.INVISIBLE);
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //path folder where file is going to be stored.
        storageReference = FirebaseStorage.getInstance().getReference(userid);
        databaseReference = FirebaseDatabase.getInstance().getReference(userid);


        //chosse images
        slctbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22) {
                    checkandpermission();
                } else {
                    opengallery();
                }
            }
        });

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv.VISIBLE == 0) {
                    Toast.makeText(UploadImages.this, "Select image first", Toast.LENGTH_SHORT).show();
                } else if (task != null && task.isInProgress()) {
                    Toast.makeText(UploadImages.this, "upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    pbar.setVisibility(View.VISIBLE);
                    Uploadimage();
                }
            }
        });
    }

    //return extension of file(image)
    private String getfileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    //for upload image into firebase
    private void Uploadimage() {
        final StorageReference imageref = storageReference.child(System.currentTimeMillis() + "." + getfileExtension(uri));
        task = imageref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //to delay the reset progress bar to zero.
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbar.setProgress(0);
                    }
                }, 500);
                Toast.makeText(UploadImages.this, "upload successful", Toast.LENGTH_SHORT).show();
                //to make the UI better
                iv.setVisibility(View.INVISIBLE);
                pbar.setVisibility(View.INVISIBLE);
                upbtn.setVisibility(View.INVISIBLE);

                //to get the download url of image for download.
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri uriiiii = uriTask.getResult();
                final String urllll = String.valueOf(uriiiii);
                Log.d(urllll, "firebase download url" + uriiiii.toString());

                Upload upload = new Upload(ed.getText().toString().trim(), uriiiii.toString());
                String uploadID = databaseReference.push().getKey();
                databaseReference.child(uploadID).setValue(upload);

                ed.getText().clear();
            }
            //failure listener for tell the failure causes
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImages.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            //to see the progress on progress bar
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                pbar.setProgress((int) progress);
            }
        });
    }


    // for opening the gallery (only images)
    private void opengallery() {

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUESCODE);

    }

    //to show the selected image in image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESCODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Glide.with(this).load(uri).into(iv);
            iv.setVisibility(View.VISIBLE);
            upbtn.setVisibility(View.VISIBLE);
        }
    }

    //method for permission to read the data
    private void checkandpermission() {

        if (ContextCompat.checkSelfPermission(UploadImages.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadImages.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(UploadImages.this, "please accept the permission", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(UploadImages.this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            opengallery();
        }
    }
}
