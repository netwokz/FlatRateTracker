package com.netwokz.flatratetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements View.OnClickListener, AddTicketDialog.ButtonListener {

    Button btnTicket, btnData;
    DBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.activity_main);

        btnTicket = (Button) findViewById(android.R.id.btn_add_ticket);
        btnData = (Button) findViewById(android.R.id.btn_view_database);
        btnTicket.setOnClickListener(this);
        btnData.setOnClickListener(this);

        try {
            mHelper = new DBHelper(this);
            if (!mHelper.dbExists())
                mHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    public void updateUI() {
//        Ticket mTicket = new Ticket(0, 12345, 6.5);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(android.R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.btn_add_ticket:
                AddTicketDialog mNewTicket = AddTicketDialog.newInstance();
                mNewTicket.show(getSupportFragmentManager(), "new-ticket-dialog");
                break;
            case android.R.id.btn_view_database:
                Intent intent = new Intent("com.netwokz.flatratetracker.CALENDAR_VIEW");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void dismissDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        AddTicketDialog mTicket = (AddTicketDialog) fm.findFragmentByTag("new-ticket-dialog");
//        ft.remove(mTicket);
        ft.commit();
    }
}
