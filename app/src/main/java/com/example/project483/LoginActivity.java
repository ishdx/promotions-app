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

public class LoginActivity extends AppCompatActivity {

    private EditText editPass, editEmail;
    private int isRetailer = 1;
    private Button login;
    private TextView register;
    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.emailLog);
        editPass = findViewById(R.id.passLog);
        login = findViewById(R.id.log_btn);
        register = findViewById(R.id.reg_btn);

        initObjects();

        initListeners();
    }

    private void initListeners() {
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(switchActivityIntent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verifyFromSQLite();
            }
        });
    }

    private void initObjects(){
        databaseHelper = new DatabaseHelper(this);
        inputValidation = new InputValidation(this);
    }

    private void verifyFromSQLite(){
        if (!inputValidation.isInputEditTextFilled(editEmail)) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(editEmail)) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(editPass)) {
            return;
        }

        if (databaseHelper.checkUser(editEmail.getText().toString().trim()
                ,editPass.getText().toString().trim(), isRetailer)) {

            Toast.makeText(this, "You Logged in Successfully!", Toast.LENGTH_SHORT).show();

            if (isRetailer == 1) {
                Intent switchActivityIntent = new Intent(this, RetailerActivity.class);
                startActivity(switchActivityIntent);
            } else {
                Intent switchActivityIntent = new Intent(this, UserActivity.class);
                startActivity(switchActivityIntent);
            }

            emptyInputEditText();

        } else {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
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
    private void emptyInputEditText(){
        editEmail.setText(null);
        editPass.setText(null);
    }
}