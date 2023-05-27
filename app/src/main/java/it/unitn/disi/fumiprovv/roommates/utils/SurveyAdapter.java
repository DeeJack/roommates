package it.unitn.disi.fumiprovv.roommates.utils;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unitn.disi.fumiprovv.roommates.R;
import it.unitn.disi.fumiprovv.roommates.models.Sondaggio;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {
    private List<Sondaggio> surveyList;

    public SurveyAdapter() {
        this.surveyList = new ArrayList<>();
    }

    public void setData(List<Sondaggio> surveyList) {
        this.surveyList = surveyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item, parent, false);
        return new SurveyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder holder, int position) {
        Sondaggio survey = surveyList.get(position);
        holder.bind(survey);
    }

    @Override
    public int getItemCount() {
        return surveyList.size();
    }

    public static class SurveyViewHolder extends RecyclerView.ViewHolder {
        private TextView questionTextView;
        private RadioGroup optionsRadioGroup;

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.domanda);
            optionsRadioGroup = itemView.findViewById(R.id.optionsRadioGroup);
        }

        public void bind(Sondaggio survey) {
            questionTextView.setText(survey.getDomanda());

            optionsRadioGroup.removeAllViews(); // Clear existing radio buttons

            // Create and add radio buttons for each option
            for (String option : survey.getOpzioni()) {
                RadioButton radioButton = new RadioButton(itemView.getContext());
                radioButton.setText(option);
                optionsRadioGroup.addView(radioButton);
            }
        }
    }
}
