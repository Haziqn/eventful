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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    Button buttonUpdate, buttonDelete;
    EditText editTextName, editTextEmail, editTextAge, editTextPassword;
    CircleImageView imageButton;

    Spinner spinnerOccupation, spinnerRace;
    CheckBox soccer, programming, ml, singing, photography;

    RadioGroup rg;
    RadioButton rb, radioButtonMale, radioButtonFemale;

    String gender;
    String race;
    String occupation;
    ArrayList<String> categories;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

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

        categories = new ArrayList<String>();
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();

        final FirebaseUser user = mAuth.getCurrentUser();

        buttonUpdate = (Button)findViewById(R.id.btnUpdate);
        buttonDelete = (Button)findViewById(R.id.btnDelete);

        editTextEmail = (EditText)findViewById(R.id.etEmailLogin);
        editTextEmail.setClickable(false);
        editTextName = (EditText)findViewById(R.id.etUserName);
        editTextAge = (EditText)findViewById(R.id.etAge);
        editTextPassword = (EditText)findViewById(R.id.etPasswordLogin);
        editTextPassword.setClickable(false);

        soccer = (CheckBox)findViewById(R.id.cbSoccer);
        programming = (CheckBox)findViewById(R.id.cbProgramming);
        ml = (CheckBox)findViewById(R.id.cbML);
        singing = (CheckBox)findViewById(R.id.cbSinging);
        photography = (CheckBox)findViewById(R.id.cbPhotography);

        rg = (RadioGroup) findViewById(R.id.rgGender);
        int selectedButtonId = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(selectedButtonId);
        radioButtonMale = (RadioButton)findViewById(R.id.rbMale);
        radioButtonFemale = (RadioButton)findViewById(R.id.rbFemale);

        gender = rb.getText().toString();

        imageButton = (CircleImageView)findViewById(R.id.imageButtonUser);

        spinnerOccupation = (Spinner) findViewById(R.id.spinnerOccupation);
        ArrayAdapter<CharSequence> adapterOccupation = ArrayAdapter.createFromResource(this,
                R.array.occupation_array, android.R.layout.simple_spinner_item);
        adapterOccupation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOccupation.setAdapter(adapterOccupation);
        spinnerOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                occupation = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                occupation = "";
            }
        });
        spinnerRace = (Spinner) findViewById(R.id.spinnerRace);
        ArrayAdapter<CharSequence> adapterRace = ArrayAdapter.createFromResource(this,
                R.array.race_array, android.R.layout.simple_spinner_item);
        adapterRace.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRace.setAdapter(adapterRace);
        spinnerRace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                race = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                race = "";
            }
        });

        final String uid = user.getUid();

        DatabaseReference mDatabaseRef = mDatabase.child(uid);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean name = dataSnapshot.hasChild("user_name");
                Boolean image = dataSnapshot.hasChild("image");
                Boolean age = dataSnapshot.hasChild("age");
                Boolean interests = dataSnapshot.child("interests").hasChildren();
                Boolean race = dataSnapshot.hasChild("race");
                Boolean occupation = dataSnapshot.hasChild("occupation");
                Boolean gender = dataSnapshot.hasChild("gender");

                if (name && image) {

                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String imagee = dataSnapshot.child("image").getValue().toString();

                    editTextEmail.setText(user.getEmail());
                    editTextName.setText(user_name);
                    Picasso.with(getBaseContext()).load(imagee).into(imageButton);

                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                    String password = prefs.getString("password", null);
                    if (password != null) {
                        editTextPassword.setText(password);
                    }
                } else if (age && interests && race && occupation && gender) {

                    String race2 = dataSnapshot.child("race").getValue().toString();
                    String age2 = dataSnapshot.child("age").getValue().toString();
                    ArrayList<String> interests2 = (ArrayList<String>) dataSnapshot.child("interests").getValue();
                    String occupation2 = dataSnapshot.child("occupation").getValue().toString();
                    String gender2 = dataSnapshot.child("gender").getValue().toString();
                    editTextAge.setText(age2);

                    for (int i = 0; i < interests2.size(); i++) {
                        Log.d("interests", interests2.get(i).toString());
                        if (interests2.get(i).toString() == "Soccer") {
                            programming.setChecked(false);
                            ml.setChecked(false);
                            singing.setChecked(false);
                            photography.setChecked(false);
                        } else if (interests2.get(i).toString() == "Programming") {
                            soccer.setChecked(false);
                            ml.setChecked(false);
                            singing.setChecked(false);
                            photography.setChecked(false);
                        } else if (interests2.get(i).toString() == "Mobile Legends") {
                            soccer.setChecked(false);
                            programming.setChecked(false);
                            singing.setChecked(false);
                            photography.setChecked(false);
                        } else if (interests2.get(i).toString() == "Singing") {
                            soccer.setChecked(false);
                            programming.setChecked(false);
                            ml.setChecked(false);
                            photography.setChecked(false);
                        } else if (interests2.get(i).toString() == "Photography") {
                            soccer.setChecked(false);
                            programming.setChecked(false);
                            ml.setChecked(false);
                            singing.setChecked(false);
                        }
                    }

                    if (gender2 == "Male") {
                        radioButtonMale.setChecked(true);
                        radioButtonFemale.setChecked(false);
                    } else if (gender2 == "Female") {
                        radioButtonFemale.setChecked(true);
                        radioButtonMale.setChecked(false);
                    }

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

        mProgress.setTitle("Registering User");
        mProgress.setMessage("Please while we create your account!");
        mProgress.show();

        String email = editTextEmail.getText().toString();
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        final String password = editTextPassword.getText().toString();
        String encodePassword = encrypt_password(password);

        final PARTICIPANT participant = new PARTICIPANT();
        participant.setAge(age);
        participant.setInterests(categories);
        participant.setRace(race);
        participant.setOccupation(occupation);
        participant.setGender(gender);
        participant.setUser_name(name);
        participant.setEmail(email);
        participant.setPassword(encodePassword);

        String uid = mAuth.getCurrentUser().getUid();

        final DatabaseReference userdb = mDatabase.child(uid);

        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    userdb.setValue(participant).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                            editor.putString("password", password);
                            editor.commit();
                            mProgress.dismiss();
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(EditProfile.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
            Log.e("Password", "AES secret key spec error");
        }

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(password.getBytes());
        } catch (Exception e) {
            Log.e("Password", "AES encryption error");
        }
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.cbSoccer:
                if (!checked) {
                    categories.remove(soccer.getText().toString());
                } else {
                    categories.add(soccer.getText().toString());
                    break;
                }

            case R.id.cbProgramming:
                if (!checked) {
                    categories.remove(programming.getText().toString());
                } else {
                    categories.add(programming.getText().toString());
                    break;
                }

            case R.id.cbML:
                if (!checked) {
                    categories.remove(ml.getText().toString());
                } else {
                    categories.add(ml.getText().toString());
                    break;
                }

            case R.id.cbSinging:
                if (!checked) {
                    categories.remove(singing.getText().toString());
                } else {
                    categories.add(singing.getText().toString());
                    break;
                }

            case R.id.cbPhotography:
                if (!checked) {
                    categories.remove(photography.getText().toString());
                } else {
                    categories.add(photography.getText().toString());
                    break;
                }
        }
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

        if (id == R.id.reset_email) {
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(EditProfile.this);

            myBuilder.setTitle("Reset Email");
            myBuilder.setMessage("An email will be sent to you shortly!");
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.updateEmail(user.getEmail())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("EditProfile", "User email address updated.");
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
                                        Log.d("EditProfile", "Email sent.");
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
