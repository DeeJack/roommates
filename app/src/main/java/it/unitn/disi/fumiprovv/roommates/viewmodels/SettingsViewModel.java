package it.unitn.disi.fumiprovv.roommates.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private boolean isDarkMode;
    private boolean isWhosHomeEnabled;

    public SettingsViewModel(boolean isDarkMode, boolean isWhosHomeEnabled) {
        this.isDarkMode = isDarkMode;
        this.isWhosHomeEnabled = isWhosHomeEnabled;
    }

    public SettingsViewModel() {
        this.isDarkMode = false;
        this.isWhosHomeEnabled = false;
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public boolean isWhosHomeEnabled() {
        return isWhosHomeEnabled;
    }

    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }

    public void setWhosHomeEnabled(boolean whosHomeEnabled) {
        isWhosHomeEnabled = whosHomeEnabled;
    }
}
