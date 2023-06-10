package it.unitn.disi.fumiprovv.roommates.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;

public class OptionsSurveyAdapter extends ArrayAdapter<String> {
    private final List<String> options;

    public OptionsSurveyAdapter(Context context, List<String> options) {
        super(context, R.layout.survey_option_item, options);
        this.options = options;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.survey_option_item, parent, false);
        }

        TextView textViewOption = convertView.findViewById(R.id.opzioneText);
        ImageView buttonDeleteOption = convertView.findViewById(R.id.buttonDeleteOption);

        String option = getItem(position);

        textViewOption.setText(option);

        buttonDeleteOption.setOnClickListener(v -> {
            options.remove(option);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
