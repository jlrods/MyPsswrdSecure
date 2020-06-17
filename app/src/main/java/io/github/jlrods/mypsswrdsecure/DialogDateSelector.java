package io.github.jlrods.mypsswrdsecure;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

/**
 * Created by rodjose1 on 06/09/2018.
 */


public class DialogDateSelector extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;
    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        Bundle args = this.getArguments();
        if (args != null) {
            long date = args.getLong("date");
            calendar.setTimeInMillis(date);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), listener, year,month,day);
    }
}