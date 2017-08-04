package com.example.a15017523.eventful;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class EditPersonalDetails extends AppCompatActivity {

    EditText editTextAge;
    Button buttonConfirm;
    Spinner spinnerOccupation, spinnerRace;
    CheckBox soccer, programming, ml, singing, photography;

    String gender;
    String race;
    String occupation;

    ProgressDialog mProgress;

    ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_details);

        setTitle("Tell us More!");

        mProgress = new ProgressDialog(this);
        categories = new ArrayList<String>();

        soccer = (CheckBox)findViewById(R.id.cbSoccer);
        programming = (CheckBox)findViewById(R.id.cbProgramming);
        ml = (CheckBox)findViewById(R.id.cbML);
        singing = (CheckBox)findViewById(R.id.cbSinging);
        photography = (CheckBox)findViewById(R.id.cbPhotography);

        editTextAge = (EditText) findViewById(R.id.etAge);

        RadioGroup rg = (RadioGroup) findViewById(R.id.rgGender);
        int selectedButtonId = rg.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(selectedButtonId);
        gender = rb.getText().toString();
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

        buttonConfirm = (Button) findViewById(R.id.btnConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgress.setTitle("Verifying inputs");
                mProgress.setMessage("Checking");
                mProgress.show();

                final String age = editTextAge.getText().toString();

                if(field_verification(age, occupation, race, gender, categories)) {

                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout passPhrase =
                            (LinearLayout) inflater.inflate(R.layout.termsandconditions, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditPersonalDetails.this);
                    builder.setTitle("Terms and Conditions")
                            .setView(passPhrase)
                            .setPositiveButton("I accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent();
                                    intent.putExtra("age", age);
                                    intent.putExtra("occupation", occupation);
                                    intent.putExtra("gender", gender);
                                    intent.putExtra("race", race);
                                    intent.putExtra("interests", categories);

                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

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

    private Boolean field_verification(String age, String occupation, String race, String gender, ArrayList<String> categories) {
        if (!TextUtils.isEmpty(age) &&
                !TextUtils.isEmpty(race) &&
                !TextUtils.isEmpty(gender) &&
                !TextUtils.isEmpty(occupation) &&
                categories != null) {
            return true;
        } else if (TextUtils.isEmpty(age)){
            Log.d("field_verification", "age is empty");
        } else if (TextUtils.isEmpty(occupation)){
            Log.d("field_verification", "occupation is empty");
        } else if (TextUtils.isEmpty(race)){
            Log.d("field_verification", "race is empty");
        } else if (TextUtils.isEmpty(gender)){
            Log.d("field_verification", "gender is empty");
        } else if (categories == null){
            Log.d("field_verification", "cat is empty");
        } else {
            return false;
        }

        return true;
    }
}
