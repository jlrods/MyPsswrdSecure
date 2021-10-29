package io.github.jlrods.mypsswrdsecure;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class SpinnerArrayAdapter extends ArrayAdapter {
    public SpinnerArrayAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SpinnerArrayAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

}
