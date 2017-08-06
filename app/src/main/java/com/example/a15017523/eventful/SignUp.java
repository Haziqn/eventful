package com.example.a15017523.eventful;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonSignUp;
    CircleImageView imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;
    final PARTICIPANT participant1 = new PARTICIPANT();

    private Uri uri = null;
    public String downloadUrl;
    final int GALLERY_REQUEST = 1;
    ProgressDialog mProgress;
    String TAG = "SignUp.java";

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    String name = "";
    String email = "";
    String password = "";
    String password2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Enter Your Credentials");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        editTextName = (EditText) findViewById(R.id.etUserName);
        editTextEmail = (EditText) findViewById(R.id.etEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.etPwLogin);
        editTextConfirmPassword = (EditText) findViewById(R.id.etCpw);
        imageButton = (CircleImageView) findViewById(R.id.imageButtonUser);

        buttonSignUp = (Button)findViewById(R.id.btnSignUp);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(SignUp.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_REQUEST);
                } else {
                    ActivityCompat.requestPermissions(SignUp.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setTitle("Verifying credentials");
                mProgress.setMessage("Checking");
                mProgress.show();
                name = editTextName.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                password2 = editTextConfirmPassword.getText().toString().trim();

                if (field_verification(name, email, password, password2, uri)) {
                    mProgress.hide();
                    startRegister();
                } else if (uri == null) {
                    Toast.makeText(SignUp.this, "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, "One or more text fields is empty. Please, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startRegister() {

            mProgress.setTitle("Registering User");
            mProgress.setMessage("Please while we create your account!");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        final String encodePassword = encrypt_password(password);

                        String user_id = mAuth.getCurrentUser().getUid();
                        final DatabaseReference current_user_db = mDatabase.child(user_id);
                        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
                        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {

                                    participant1.setEmail(mAuth.getCurrentUser().getEmail());
                                    participant1.setPassword(encodePassword);
                                    participant1.setStatus("active");
                                    participant1.setUser_name(name);
                                    downloadUrl = task.getResult().getDownloadUrl().toString();
                                    participant1.setImage(downloadUrl);
                                    current_user_db.setValue(participant1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                editor.putString("password", password);
                                                editor.commit();
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "Email sent.");
                                                                }
                                                            }
                                                        });

                                                mProgress.dismiss();
                                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(SignUp.this, task.getException().toString().trim(), Toast.LENGTH_LONG);
                                            }
                                        }
                                    });
                                } else {
                                    String error = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        error = "Requires at least 1 capital letter, 1 special character, 1 number and at least 6 characters!";
                                    } catch (FirebaseAuthInvalidCredentialsException e){
                                        error = "Invalid email";
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        error = "Email already exists";
                                    } catch (Exception e) {
                                        error = "Unknown error";
                                    }
                                    Toast.makeText(SignUp.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                    }
                }

            });
    }

    private String encrypt_password(String password) {
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e(TAG, "AES secret key spec error");
        }

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(password.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "AES encryption error");
        }
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);

    }

    private Boolean field_verification(String name, String email, String password, String password2, Uri uri) {
        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(password2) &&
                password.equalsIgnoreCase(password2) && uri != null) {
            return true;
        } return false;
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
        // else if (resultCode == RESULT_OK && requestCode == 9){
//            age = data.getStringExtra("age").toString();
//            race = data.getStringExtra("race").toString();
//            occupation = data.getStringExtra("occupation").toString();
//            gender = data.getStringExtra("gender").toString();
//            interests = data.getStringArrayListExtra("interests");
//            participant1.setAge(age);
//            participant1.setGender(gender);
//            participant1.setRace(race);
//            participant1.setOccupation(occupation);
//            participant1.setInterests(interests);
//            startRegister();
//        }
    }
}
