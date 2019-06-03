package com.worldtechq.blog.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.worldtechq.blog.Adaptors.ImageAdaptor;
import com.worldtechq.blog.Helper.Upload;
import com.worldtechq.blog.R;

import java.util.ArrayList;
import java.util.List;

import static com.worldtechq.blog.R.menu.opt;

public class ShowImages extends AppCompatActivity implements ImageAdaptor.OnItemClickListeners {
    EditText edsearch;
    RecyclerView recyclerView;
    ProgressBar pcircle;
    FirebaseAuth mauth1;
    private ImageAdaptor imageAdaptor;
    private ValueEventListener mevent;
    private FirebaseStorage mstorage;
    private DatabaseReference databaseReference;
    private List<Upload> muplod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);
        getSupportActionBar().getElevation();
        getSupportActionBar().getThemedContext();
        edsearch = findViewById(R.id.et);
        pcircle = findViewById(R.id.pbarcircle);
        recyclerView = findViewById(R.id.recycler_view);
        //to fix the length and width of the recycler view
        recyclerView.setHasFixedSize(false);
        //to set the layout of recycler view(how it is going to show the data either vertical or horizontal)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        muplod = new ArrayList<>();
        imageAdaptor = new ImageAdaptor(ShowImages.this, muplod);

        recyclerView.setAdapter(imageAdaptor);
        imageAdaptor.setonItemClickListener(ShowImages.this);

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mstorage = FirebaseStorage.getInstance();
        //set the path from where teh images will be going to fetch.
        databaseReference = FirebaseDatabase.getInstance().getReference(userid);

        mevent = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                muplod.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setkey(postSnapshot.getKey());
                    muplod.add(upload);
                    pcircle.setVisibility(View.INVISIBLE);
                }
                imageAdaptor.notifyDataSetChanged();
                pcircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShowImages.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //adding option with the help of this method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(opt, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //assigning the functionality on menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //for logout from firebase account.
        if (id == R.id.menu) {
            mauth1.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        } else if (id == R.id.upload) {
            startActivity(new Intent(this, UploadImages.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "position is : " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatIWant(int position) {
        Toast.makeText(this, "available soon ", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDeleteClick(int position) {
        delete(position);
    }

    //method for delete the file
    public void delete(int position){
        Upload sitem = muplod.get(position);
        final String skey = sitem.getkey();

        StorageReference sref = mstorage.getReferenceFromUrl(sitem.getMurl());
        sref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                databaseReference.child(skey).removeValue();
                Toast.makeText(ShowImages.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowImages.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}