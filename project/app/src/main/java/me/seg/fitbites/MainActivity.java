package me.seg.fitbites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Member;

import me.seg.fitbites.firebase.AuthManager;
import me.seg.fitbites.firebase.FirestoreDatabase;
import me.seg.fitbites.firebase.OnTaskComplete;

public class MainActivity extends AppCompatActivity {
    private Button login, signup;
    private TextView user, pass;
    private MainActivity current;
    private AlertDialog.Builder alert;
    private AlertDialog dialog;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = findViewById(R.id.editTextTextPersonName);
        pass = findViewById(R.id.editTextTextPassword);
        login = findViewById(R.id.loginButton);
        signup = findViewById(R.id.button_signUp);


        current = this;
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user.getText().length() == 0 || pass.getText().length() == 0) {
                    alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Error");
                    alert.setMessage("You must fill in all the text fields!");
                    dialog = alert.create();
                    dialog.show();
                    return;
                }

                AuthManager.getInstance().validateUser(user.getText().toString(), pass.getText().toString(), new OnTaskComplete<AuthManager.LoginResult>() {
                    @Override
                    public void onComplete(AuthManager.LoginResult result) {
                        if (result == null || !result.isSuccessful()) {
                            reEnter();
                        } else {
                            UserData u= AuthManager.getInstance().getCurrentUserData();

                            if (u instanceof Instructor){
                                Intent intent = new Intent(current, InstructorWelcome.class);
                                startActivity(intent);

                            }
                            else if (u instanceof GymMember){
                                Intent intent = new Intent(current, GymMemberWelcome.class);
                                startActivity(intent);

                            } else if(u instanceof Admin) {
                                Intent intent = new Intent(current, AdminLogin.class);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                                alert.setTitle("Error");
                                alert.setMessage("Something went wrong! Info: Invalid usertype!");
                                AlertDialog dialog = alert.create();
                                dialog.show();
                            }

                        }
                    }
                });


            }
        });


        signup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                Intent intent = new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(intent);
            }
            });

        }


    @SuppressLint("SetTextI18n")
    public void reEnter() {
        user.setText("Invalid");
        pass.setText("");
        alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Error");
        alert.setMessage("Email and password is invalid!");
        dialog = alert.create();
        dialog.show();
    }





}