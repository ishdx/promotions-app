package com.example.project483;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project483.modals.UserModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPass, editConfirm;
    private int isRetailer = 1;
    private Button register;
    private TextView login;
    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initiate views
        editName = findViewById(R.id.nameReg);
        editEmail = findViewById(R.id.emailReg);
        editPass = findViewById(R.id.passReg);
        editConfirm = findViewById(R.id.confPassReg);
        register = findViewById(R.id.register_btn);
        login = findViewById(R.id.login_btn);


        // initiate objects
        initObjects();

        // initiate listeners
        initListeners();

    }

    private void initListeners() {
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                postDataToSQLite();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(switchActivityIntent);
            }
        });
    }

    private void initObjects() {
        inputValidation = new InputValidation(this);
        databaseHelper = new DatabaseHelper(this);
        user = new UserModel();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.retailer:
                if (checked)
                    isRetailer = 1;
                    break;
            case R.id.user:
                if (checked)
                    isRetailer = 0;
                break;
        }
    }

    private void postDataToSQLite() {
        if (!inputValidation.isInputEditTextFilled(editName)) {
            Toast.makeText(this, "Please Enter Full Name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!inputValidation.isInputEditTextEmail(editEmail)) {
            Toast.makeText(this, "Please Enter Email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!inputValidation.isInputEditTextFilled(editPass)) {
            Toast.makeText(this, "Please Enter Password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!inputValidation.isInputEditTextFilled(editConfirm)) {
            Toast.makeText(this, "Please Confirm The Password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!inputValidation.isInputEditTextMatches(editPass, editConfirm)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!databaseHelper.checkUser(editEmail.getText().toString().trim())) {

            user.setName(editName.getText().toString().trim());
            user.setEmail(editEmail.getText().toString().trim());
            user.setPassword(editPass.getText().toString().trim());
            user.setRetailer(isRetailer);

            databaseHelper.addUser(user);

            // show success message that record saved successfully
            Toast.makeText(this, "You Registered Successfully!", Toast.LENGTH_SHORT).show();
            if (isRetailer == 1) {
                Intent switchActivityIntent = new Intent(this, RetailerActivity.class);
                startActivity(switchActivityIntent);
            } else {
                Intent switchActivityIntent = new Intent(this, UserActivity.class);
                startActivity(switchActivityIntent);
            }

            emptyInputEditText();

        } else {
            // show error message that record already exists
            Toast.makeText(this, "Email Already Exists!", Toast.LENGTH_SHORT).show();
        }
    }

    private void emptyInputEditText(){
        editName.setText(null);
        editEmail.setText(null);
        editPass.setText(null);
        editConfirm.setText(null);
    }
}