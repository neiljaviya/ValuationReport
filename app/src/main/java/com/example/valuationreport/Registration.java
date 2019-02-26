package com.example.valuationreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private FirebaseFirestore db;

    EditText nameEditText, emailEditText, passwordEditText, mobileEditText, addressEditText;
    EditText companyCodeEditText;
    Spinner dropdownRegistration;
    String[] items;                          //for spinner items
    String name, email, password, address, mobileNo, companyCode;               //for getting and setting input from/to the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        typeCast();  //id's of all the element

        setSpinner();  //setting the value of the spinner and applying condition for company code for client

        getDataFromFirebase();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    public void getDataFromFirebase() {
        try {
            FirebaseApp.initializeApp(this);    //initialize firebase
            db = FirebaseFirestore.getInstance();  //firestore object
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Probleem in Object--------------->" + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("FIRESTORE", e.getMessage());
        }
// get data from firestore

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("1st------>>>>>>>>>", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w("error", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void setSpinner() {

        dropdownRegistration = findViewById(R.id.registrationSpinnerId);
        items = new String[]{"Employee", "Client"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownRegistration.setAdapter(adapter);
        dropdownRegistration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                companyCodeEditText.setVisibility(View.INVISIBLE);
                if (position == 1) {
                    companyCodeEditText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //this is implemented as without this error occurs.
            }
        });
    }

    public void typeCast() {

        nameEditText = findViewById(R.id.registrationNameId);
        emailEditText = findViewById(R.id.registrationEmailId);
        passwordEditText = findViewById(R.id.registrationPasswordId);
        mobileEditText = findViewById(R.id.registrationMobileId);
        addressEditText = findViewById(R.id.registrationAddressId);
        companyCodeEditText = findViewById(R.id.registrationCompanyCodeId);
    }


    public void OnRegisterButtonClick(View view) {

        name = nameEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        address = addressEditText.getText().toString();
        mobileNo = mobileEditText.getText().toString();

        if (dropdownRegistration.getSelectedItem() == "Client") {
            companyCode = companyCodeEditText.getText().toString();
        } else if (dropdownRegistration.getSelectedItem() == "Employee") {
            companyCode = setCompanyCode();
        }
        Toast.makeText(getApplicationContext(), companyCodeEditText.getText().toString() + "", Toast.LENGTH_SHORT).show();   //for checking company code input


        if (!hasValidationError(name, email, password, mobileNo, address)) {


            boolean auth = false;

            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("password", password);
            user.put("mobileNo", mobileNo);
            user.put("address", address);
            user.put("companyCode", companyCode);
            user.put("user", dropdownRegistration.getSelectedItem());    //display whether client or employee
            user.put("Authenticate", auth);          //false until admin approval

            db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getApplicationContext(), documentReference.getId() + "----->", Toast.LENGTH_SHORT).show();   //for checking company code input
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "error found", Toast.LENGTH_SHORT).show();   //for checking company code input
                }
            });
            final Intent iRegisterButton = new Intent(Registration.this, LoginActivity.class);
            startActivity(iRegisterButton);
        } else {
            Toast.makeText(getApplicationContext(), "Inputs are not Valid", Toast.LENGTH_SHORT).show();   //for checking company code input
        }


    }

    public String setCompanyCode() {
        companyCodeEditText.addTextChangedListener(new TextWatcher() {  //necessary to implement all the methoda of TextWatcher
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                companyCodeEditText.setText(String.valueOf(0));
            }
        });
        return companyCode;
    }

    private boolean hasValidationError(String name, String email, String password, String mobile, String address) {


        if (name.length() < 8 && !name.matches("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$")) {
            nameEditText.setError("Name required of atleast 10 Letter");
            nameEditText.requestFocus();
            return true;
        }

        if (email.isEmpty() && !email.matches("^[_A-Za-z0-9-]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$")) {
            emailEditText.setError("format for email : xyz@gmail.com");
            emailEditText.requestFocus();
            return true;
        }

        if (password.isEmpty() && !password.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$") && password.length() < 8) {
            passwordEditText.setError("Password required with alteast 1number ,one capital letter and 1 alphanumeric letter");
            passwordEditText.requestFocus();
            return true;
        }

        if (mobile.isEmpty() && !mobileNo.matches("^[2-9]{2}[0-9]{8}$")) {
            mobileEditText.setError("please enter 10 number");
            mobileEditText.requestFocus();
            return true;
        }

        if (address.isEmpty()) {
            addressEditText.setError("Address required");
            addressEditText.requestFocus();
            return true;
        }
        if (dropdownRegistration.getSelectedItem().equals("Client")) {
            if (companyCode.length() < 0) {
                companyCodeEditText.setError("company code required");
                companyCodeEditText.requestFocus();
                return true;
            }
        }

        return false;
    }

    public class User {
        private String username;
        private HashMap<String, Boolean> privacy;

        public User() {
        }

        public User(String username, HashMap<String, Boolean> privacy) {
            this.username = username;
            this.privacy = privacy;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }


        public void setPrivacy(HashMap<String, Boolean> privacy) {
            this.privacy = privacy;
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
