package com.example.a15017523.eventful;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    Button buttonUpdate, buttonDelete, buttonResetPassword, buttonResetEmail;
    EditText editName, editEmail;
    CircleImageView imageButton;

    ProgressDialog mProgress;

    private Uri uri = null;
    public String downloadUrl = "";
    final int GALLERY_REQUEST = 1;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Edit profile");

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        final FirebaseUser user = mAuth.getCurrentUser();

        buttonUpdate = (Button)findViewById(R.id.btnUpdate);
        buttonDelete = (Button)findViewById(R.id.btnDelete);
        buttonResetPassword = (Button)findViewById(R.id.btnResetPw);
//        buttonResetEmail = (Button)findViewById(R.id.btnResetEmail);

        editEmail = (EditText)findViewById(R.id.etEmailLogin);
        editName = (EditText)findViewById(R.id.etUserName);
        imageButton = (CircleImageView)findViewById(R.id.imageButtonUser);

        final String uid = user.getUid();

        DatabaseReference mDatabaseRef = mDatabase.child(uid);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean name = dataSnapshot.hasChild("user_name");
                Boolean image = dataSnapshot.hasChild("image");
                if( name != false && image != false) {
                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String imagee = dataSnapshot.child("image").getValue().toString();

                    editEmail.setText(email);
                    editName.setText(user_name);
                    Picasso.with(getBaseContext()).load(imagee).into(imageButton);
                } else {
                    editName.setText("");
                    editEmail.setText(user.getEmail());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditProfile.this, "Clicked", Toast.LENGTH_SHORT).show();
                updateUserInfo();
            }
        });

//        buttonResetEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resetUserEmail();
//            }
//        });

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
                        String user_id = mAuth.getCurrentUser().getUid();
                        final DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("status").setValue("deactivated").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                userDelete();
                                Intent i = new Intent(EditProfile.this, StartActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
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

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(EditProfile.this);

                myBuilder.setTitle("Reset Password");
                myBuilder.setMessage("A reset password email will be sent to you");
                myBuilder.setCancelable(false);
                myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.sendPasswordResetEmail(user.getEmail());
                    }
                });
                myBuilder.setNegativeButton("Cancel", null);

                AlertDialog myDialog = myBuilder.create();
                myDialog.show();

            }
        });
    }



    public void updateUserInfo() {

        final String email = editEmail.getText().toString();
        final String name = editName.getText().toString();
        final FirebaseUser user = mAuth.getCurrentUser();

        if (uri != null) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();
                        final DatabaseReference current_user_db = mDatabase.child(user_id);
                        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
                        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    PARTICIPANT participant1 = new PARTICIPANT();
                                    participant1.setEmail(email);
                                    participant1.setUser_name(name);
                                    downloadUrl = task.getResult().getDownloadUrl().toString();
                                    participant1.setImage(downloadUrl);
                                    current_user_db.setValue(participant1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                mProgress.dismiss();
                                                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(EditProfile.this, task.getException().toString().trim(), Toast.LENGTH_LONG);
                                            }
                                        }
                                    });
                                }
                            }

                        });
                    } else {
                        String error = "";
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e){
                            error = "Weak password";
                        } catch (FirebaseAuthInvalidCredentialsException e){
                            error = "Invalid email";
                        } catch (FirebaseAuthUserCollisionException e) {
                            error = "Email already exists";
                        } catch (Exception e) {
                            error = "Unknown error";
                        }
                        Toast.makeText(EditProfile.this, error, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    public void userDelete() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(EditProfile.this, StartActivity.class);
                            Log.d("userDelete", "User account deleted.");
                        } else {
                            Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_LONG).show();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
