package it.unitn.disi.fumiprovv.roommates.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

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
        private Button voteButton;
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        public SurveyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.domanda);
            optionsRadioGroup = itemView.findViewById(R.id.optionsRadioGroup);
            voteButton = itemView.findViewById(R.id.buttonVota);
        }

        public void bind(Sondaggio survey) {
            questionTextView.setText(survey.getDomanda());

            optionsRadioGroup.removeAllViews(); // Clear existing radio buttons

            String currentUserId = mAuth.getUid();

            // Create and add radio buttons for each option
            if (survey.getSceltaMultipla()==Boolean.TRUE) {
                // Survey allows multiple choices, use checkboxes
                for (String option : survey.getOpzioni()) {
                    CheckBox checkBox = new CheckBox(itemView.getContext());
                    checkBox.setText(option);
                    if(survey.getVotanti().contains(currentUserId)) {
                        checkBox.setEnabled(false);
                    }
                    optionsRadioGroup.addView(checkBox);
                }
            } else {
                // Survey allows single choice, use radio buttons
                for (String option : survey.getOpzioni()) {
                    RadioButton radioButton = new RadioButton(itemView.getContext());
                    radioButton.setText(option);
                    if(survey.getVotanti().contains(currentUserId)) {
                        radioButton.setEnabled(false);
                    }
                    optionsRadioGroup.addView(radioButton);
                }
            }

            if (survey.getVotanti().contains(currentUserId)) {
                // User has already voted, disable the vote button
                voteButton.setEnabled(false);
            } else {
                // User has not voted, enable the vote button
                voteButton.setEnabled(true);
            }

            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    survey.addVotante(currentUserId);
                    Log.d("boh", currentUserId);
                    //aggiungi voto all'opzione selezionata
                }
            });
        }
    }
}
