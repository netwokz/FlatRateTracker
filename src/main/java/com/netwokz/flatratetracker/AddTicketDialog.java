package com.netwokz.flatratetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Steve on 10/16/13.
 */
public class AddTicketDialog extends DialogFragment implements View.OnClickListener {

    private int ticketID, mYear;
    private String mMake, mModel;
    private double hours;

    EditText etID;
    Spinner spYear, spMake, spModel, spHour, spMinute;

    ButtonListener mListener;

    DBHelper mHelper;

    public interface ButtonListener {
        public void dismissDialog();
    }

    public static AddTicketDialog newInstance() {
        AddTicketDialog fragment = new AddTicketDialog();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onViewSelected");
        }

        mHelper = new DBHelper(getActivity().getApplicationContext());

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.add_ticket_dialog, null);
        etID = (EditText) view.findViewById(R.id.ticket_id_value);
        spYear = (Spinner) view.findViewById(R.id.sp_year);
        spMake = (Spinner) view.findViewById(R.id.sp_make);
        spModel = (Spinner) view.findViewById(R.id.sp_model);
        spHour = (Spinner) view.findViewById(R.id.sp_hour);
        spMinute = (Spinner) view.findViewById(R.id.sp_minute);

        Button OK = (Button) view.findViewById(R.id.btn_ok);
        Button Cancel = (Button) view.findViewById(R.id.btn_cancel);

        OK.setOnClickListener(this);
        Cancel.setOnClickListener(this);

        ArrayAdapter<CharSequence> mHourSpinner = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.hours_first, android.R.layout.simple_spinner_item);
        mHourSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHour.setAdapter(mHourSpinner);

        ArrayAdapter<CharSequence> mMinuteSpinner = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.hours_second, android.R.layout.simple_spinner_item);
        mMinuteSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMinute.setAdapter(mMinuteSpinner);

        spYear.setEnabled(false);
        spMake.setEnabled(false);
        spModel.setEnabled(false);

        populateYearSpinner();

        mDialogBuilder.setView(view);
        return mDialogBuilder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                // Save data
                getSpinnerData();
                saveData();
                break;
            case R.id.btn_cancel:
                // Dismiss Dialog
                mListener.dismissDialog();
                break;
        }
    }

    private void saveData() {
//        Ticket mTicket = new Ticket(ticketID, System.currentTimeMillis(), make, model, hours);
        Toast.makeText(getActivity().getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void populateYearSpinner() {
        spYear.setEnabled(true);
        mHelper.openDB();
        ArrayList<String> mYearList = mHelper.getYears();
        mHelper.close();
        ArrayAdapter<String> mYearAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, mYearList);
        spYear.setAdapter(new NothingSelectedSpinnerAdapter(mYearAdapter, R.layout.year_row_nothing_selected, getActivity().getApplicationContext()));
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mYear = Integer.parseInt((String) parent.getSelectedItem());
                    populateMakeSpinner(mYear);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void populateMakeSpinner(int year) {
        spMake.setEnabled(true);
        mHelper.openDB();
        ArrayList<String> mMakeList = mHelper.getMakes(year);
        mHelper.close();
        ArrayAdapter<String> mMakeAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, mMakeList);
        spMake.setAdapter(new NothingSelectedSpinnerAdapter(mMakeAdapter, R.layout.make_row_nothing_selected, getActivity().getApplicationContext()));
        spMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mMake = (String) parent.getSelectedItem();
                    populateModelSpinner(mYear, mMake);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void populateModelSpinner(int year, String make) {
        spModel.setEnabled(true);
        mHelper.openDB();
        ArrayList<String> mModelList = mHelper.getModels(year, make);
        mHelper.close();
        ArrayAdapter<String> mModelAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, mModelList);
        spModel.setAdapter(new NothingSelectedSpinnerAdapter(mModelAdapter, R.layout.model_row_nothing_selected, getActivity().getApplicationContext()));
        spModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mModel = (String) parent.getSelectedItem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSpinnerData() {
        if (isEmpty(etID)) {
            prompt(0);
        } else {
            try {
                ticketID = Integer.parseInt(etID.getText().toString().trim());
            } catch (NumberFormatException e) {
                Log.d(getClass().toString(), "Invalid Ticket ID");
            }
        }
    }

    private int prompt(int i) {
        String text = null;
        switch (i) {
            case 0:
                text = "Please enter a Ticket ID.";
                break;
            case 1:
                text = "Please enter a Make.";
                break;
            case 2:
                text = "Please enter a Model.";
                break;
            case 3:
                text = "Please enter an Hour amount.";
                break;
            default:
        }
        if (text == null)
            Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_LONG).show();
        return 0;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void copyDataToSD(ArrayList<String> array, String str) {
        File file = new File("database-" + str + ".txt");
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out != null) {
            for (String s : array) {
                out.println(s);
            }
            out.close();
        }
    }
}
