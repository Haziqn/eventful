package com.example.a15017523.eventful;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class SignUp extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonSignUp, buttonSignIn, buttonSkip, buttonGoogle;
    ImageButton imageButton;

    String TAG = "SignUp.java";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;
    private Uri uri = null;
    public String downloadUrl;
    final int GALLERY_REQUEST = 1;
    ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Register");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        editTextName = (EditText) findViewById(R.id.etName);
        editTextEmail = (EditText) findViewById(R.id.etEmailLogin);
        editTextPassword = (EditText) findViewById(R.id.etPwLogin);
        editTextConfirmPassword = (EditText) findViewById(R.id.etCPw);
        imageButton = (ImageButton) findViewById(R.id.imageButtonUser);
        buttonSignUp = (Button) findViewById(R.id.btnSignUp);
        buttonSignIn = (Button) findViewById(R.id.btnSignIn);
        buttonSkip = (Button) findViewById(R.id.btnDelete);
        buttonGoogle = (Button) findViewById(R.id.btnGoogle);

        buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, GoogleSignIn.class);
                startActivity(intent);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(SignUp.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                } else {
                    Toast.makeText(SignUp.this, "Permission has not been granted has not been granted", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(SignUp.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, GuestMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    private void startRegister() {
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String password2 = editTextConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(password2) &&
                password.equalsIgnoreCase(password2) &&
                uri != null) {

            mProgress.setMessage("Signing up ...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {

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

                         final String password = Base64.encodeToString(encodedBytes, Base64.DEFAULT);

                         String user_id = mAuth.getCurrentUser().getUid();
                         final DatabaseReference current_user_db = mDatabase.child(user_id);
                         StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());


                         filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                             @Override
                             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                 PARTICIPANT participant1 = new PARTICIPANT();
                                 participant1.setEmail(email);
                                 participant1.setPassword(password);
                                 participant1.setStatus("active");
                                 participant1.setUser_name(name);
                                 downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                 participant1.setImage(downloadUrl);
                                 current_user_db.setValue(participant1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()) {
                                             Toast.makeText(SignUp.this, "ok", Toast.LENGTH_SHORT).show();
                                             final FirebaseUser user = mAuth.getCurrentUser();
                                             user.sendEmailVerification();

                                             UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                     .setDisplayName(name + "")
                                                     .setPhotoUri(Uri.parse(downloadUrl))
                                                     .build();

                                             user.updateProfile(profileUpdates)
                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()) {
                                                                 Log.d(TAG, "User profile updated.");
                                                                 Log.d("User name", user.getDisplayName());
                                                                 Log.d("User photo", user.getPhotoUrl().toString());
                                                             } else {
                                                                 Log.e("ERROR", task.getException().toString());
                                                             }
                                                         }
                                                     });

                                             mProgress.dismiss();

                                             Intent intent = new Intent(SignUp.this, MainActivity.class);
                                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                             startActivity(intent);
                                         } else {
                                             Toast.makeText(SignUp.this, task.getException().toString().trim(), Toast.LENGTH_LONG);
                                         }
                                     }
                                 });
                                 finish();

                             }
                         });

                     } else {

                         Log.e("ERROR", task.getException().toString());
                     }
                }
            });
        } else {
                Toast.makeText(SignUp.this, "A field is empty or passwords do not match. Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            imageButton.setImageURI(uri);
        }
//        } else if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()) {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = result.getSignInAccount();
//                firebaseAuthWithGoogle(account);
//            } else {
//                // Google Sign In failed
//                Log.e(TAG, result.getStatus().toString());
//            }
//        }
    }

}
