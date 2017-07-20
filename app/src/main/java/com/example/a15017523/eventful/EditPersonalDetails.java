package com.example.a15017523.eventful;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class EditPersonalDetails extends AppCompatActivity {

    EditText editTextAge, editTextOccupation;
    Button buttonConfirm;
    RadioGroup rg;
    RadioButton rb;
    Spinner spinner;
    CheckBox soccer, programming, ml, singing, photography;

    String gender;
    String race;
    String age;
    String occupation;
    ProgressDialog mProgress;

    ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_details);

        setTitle("Tell us More!");

        final Intent intent = new Intent(this, SignUp.class);
        mProgress = new ProgressDialog(this);
        categories = new ArrayList<String>();

        soccer = (CheckBox)findViewById(R.id.cbSoccer);
        programming = (CheckBox)findViewById(R.id.cbProgramming);
        ml = (CheckBox)findViewById(R.id.cbML);
        singing = (CheckBox)findViewById(R.id.cbSinging);
        photography = (CheckBox)findViewById(R.id.cbPhotography);

        editTextAge = (EditText) findViewById(R.id.etAge);
        editTextOccupation = (EditText) findViewById(R.id.etOccupation);
        rg = (RadioGroup) findViewById(R.id.rgGender);
        int selectedButtonId = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(selectedButtonId);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.race_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                setResult(RESULT_OK);
                    intent.putExtra("age", age);
                    intent.putExtra("occupation", occupation);
                    intent.putExtra("gender", gender);
                    intent.putExtra("race", race);
                    intent.putExtra("interests", categories);
//                mProgress.setTitle("Verifying inputs");
//                mProgress.setMessage("Checking");
//                mProgress.show();
//
//                gender = rb.getText().toString();
//                final String age = editTextAge.getText().toString();
//                final String occupation = editTextOccupation.getText().toString();
//
//                if(field_verification(age, occupation, race, gender, categories)) {
//                    intent.putExtra("age", age);
//                    intent.putExtra("occupation", occupation);
//                    intent.putExtra("gender", gender);
//                    intent.putExtra("race", race);
//                    intent.putExtra("interests", categories);
//                    setResult(RESULT_OK);
//                    mProgress.dismiss();
//                } else {
//                    mProgress.dismiss();
//                    Toast.makeText(EditPersonalDetails.this, "One or more textfields is empty. Please, try again", Toast.LENGTH_SHORT).show();
//                }
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
                !TextUtils.isEmpty(occupation) &&
                !TextUtils.isEmpty(race) &&
                !TextUtils.isEmpty(gender) &&
                categories != null) {
            return true;
        } return false;
    }
}
