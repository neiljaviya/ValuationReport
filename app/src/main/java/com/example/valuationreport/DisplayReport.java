package com.example.valuationreport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DisplayReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_report);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void onEditClick(View view) {
        final Intent i_edit = new Intent(this, NewInspection.class);
        startActivity(i_edit);
    }

    public void onSubmitClick(View v) {
        final Intent i_submit = new Intent(this, Dashboard.class);
        startActivity(i_submit);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
