package it.unitn.disi.fumiprovv.roommates.viewmodels;

import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private String name;
    private String imageUrl;
    private boolean isModerator;

    public UserViewModel(String name, String imageUrl, boolean isModerator) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.isModerator = isModerator;
    }

    public UserViewModel(String name) {
        this.name = name;
        this.imageUrl = "";
        this.isModerator = false;
    }

    public UserViewModel() {
        this.name = "";
        this.imageUrl = "";
        this.isModerator = false;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIsModerator(boolean moderator) {
        isModerator = moderator;
    }
}
