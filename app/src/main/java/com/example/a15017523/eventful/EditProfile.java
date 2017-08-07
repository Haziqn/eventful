package com.example.a15017523.eventful;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    Button buttonUpdate, buttonDelete;
    EditText editTextName, editTextEmail;
    CircleImageView imageButton;

    ProgressDialog mProgress;

    private Uri uri = null;

    final int GALLERY_REQUEST = 1;
    FirebaseAuth mAuth;
    String userid;
    DatabaseReference mDatabase, databaseReference;
    StorageReference Storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextName = (EditText)findViewById(R.id.etUserName);
        editTextEmail = (EditText)findViewById(R.id.etEmailLogin);
        buttonUpdate = (Button)findViewById(R.id.btnUpdate);
        buttonDelete = (Button)findViewById(R.id.btnDelete);
        imageButton = (CircleImageView)findViewById(R.id.imageButtonUser);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Edit profile");

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = databaseReference.child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        DatabaseReference mDatabaseRef = mDatabase.child(uid);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean hasName = dataSnapshot.hasChild("user_name");

                if (hasName) {
                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    editTextName.setText(user_name);
                    Picasso.with(EditProfile.this).load(image).into(imageButton);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(EditProfile.this);

                myBuilder.setTitle("Delete Account");
                myBuilder.setMessage("Are you sure?");
                myBuilder.setCancelable(false);
                myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("EVENT_PARTICIPANTS").child(uid).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if(dataSnapshot.hasChildren()) {
                                    Toast.makeText(EditProfile.this, "You have currently joined events! Please leave all current events.", Toast.LENGTH_LONG).show();
                                } else {
                                    String user_id = mAuth.getCurrentUser().getUid();
                                    final DatabaseReference current_user_db = mDatabase.child(user_id);
                                    current_user_db.child("status").setValue("deactivated").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("EditProfile", "User account deleted.");
                                                            }
                                                        }
                                                    });
                                            Intent i = new Intent(EditProfile.this, StartActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
                myBuilder.setNegativeButton("Cancel", null);

                AlertDialog myDialog = myBuilder.create();
                myDialog.show();

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(EditProfile.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_REQUEST);
                } else {
                    Toast.makeText(EditProfile.this, "Permission has not been granted has not been granted", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(EditProfile.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });
    }

    public void updateUserInfo() {

        mProgress.setTitle("Updating User");
        mProgress.setMessage("Please while we update your account!");
        mProgress.show();

        final String email = editTextEmail.getText().toString();
        final String name = editTextName.getText().toString();
        final DatabaseReference userdb = mDatabase.child(userid);

        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    Map map = new HashMap();
                    map.put("PARTICIPANT/" + userid + "/user_name", name);
                    map.put("PARTICIPANT/" + userid + "/image", task.getResult().getDownloadUrl().toString());
                    databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            mProgress.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();
            imageButton.setImageURI(uri);

            CropImage.activity(uri)
                    .setAspectRatio(1,1)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                uri = result.getUri();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.reset_pass) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(EditProfile.this);

            myBuilder.setTitle("Reset Password");
            myBuilder.setMessage("An email will be sent to you!");
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    mAuth.sendPasswordResetEmail(user.getEmail());
                }
            });
            myBuilder.setNegativeButton("Cancel", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }
        if (id == R.id.verify_email) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(EditProfile.this);

            myBuilder.setTitle("Verify Email");
            myBuilder.setMessage("An email will be sent to you shortly!");
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("EditProfile", "Verification Email successfully sent.");
                                    } else if (!task.isSuccessful()) {
                                        Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            });
            myBuilder.setNegativeButton("Cancel", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
