package me.seg.fitbites.layouts.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import me.seg.fitbites.R;
import me.seg.fitbites.data.FitClassType;
import me.seg.fitbites.firebase.FirestoreDatabase;
import me.seg.fitbites.firebase.OnTaskComplete;

public class AdminDeleteClassActivity extends AppCompatActivity {
    private Button searchButton, bkBtn;
    private TextView classtext;
    private FirestoreDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_deleteclass);

        searchButton = findViewById(R.id.searchButton);
        classtext = findViewById(R.id.classtext);
        bkBtn = findViewById(R.id.search_class_back_btn);

        //final ChangeClassScreen current = this;
        db.getInstance().viewAllClassTypes(new OnTaskComplete<FitClassType[]>() {
            @Override
            public void onComplete(FitClassType[] result) {
                placeIntoResults(result);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                FitClassType.searchClass(classtext.getText().toString(), new OnTaskComplete<FitClassType[]>() {
                    @Override
                    public void onComplete(FitClassType[] result) {

                        placeIntoResults(result);
                    }
                });

            }

        });

        bkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminDeleteClassActivity.this, AdminClassOptionsActivity.class);
                startActivity(i);
            }
        });


    }

    private void placeIntoResults(FitClassType[] r){
        LinearLayout layout = (LinearLayout)findViewById(R.id.searchresultslayout);
        LinearLayout.LayoutParams layoutP= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layout.removeAllViews();
        boolean addedAtLeastOne = false;

        for (FitClassType c: r){
            addedAtLeastOne = true;
            Button button= new Button(this);
            button.setText(c.getClassName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            if (DialogInterface.BUTTON_POSITIVE == which) {
                                FirestoreDatabase.getInstance().deleteFitClassType(c);
                                Intent intent = new Intent(AdminDeleteClassActivity.this, AdminWelcomeActivity.class);
                                startActivity(intent);
                            }

                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminDeleteClassActivity.this);
                    builder.setMessage("Are you sure you want to remove this class?")
                            .setPositiveButton("Yes",dialogListener)
                                .setNegativeButton("No", dialogListener);
                    builder.show();
                }
            });
            layout.addView(button, layoutP);

        }

        if(!addedAtLeastOne) {
            TextView v = new TextView(this);
            v.setText("No Classes Found");
            layout.addView(v, layoutP);
        }

    }

}
