package com.lena.timetracker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance and return it
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (getTag().equals(getString(R.string.record_start))) {
            ((CreateOrEditRecordActivity) getActivity()).onStartTimeSet(view, hourOfDay, minute);
        }
        else if (getTag().equals(getString(R.string.record_end))) {
            ((CreateOrEditRecordActivity) getActivity()).onEndTimeSet(view, hourOfDay, minute);
        }
    }
}