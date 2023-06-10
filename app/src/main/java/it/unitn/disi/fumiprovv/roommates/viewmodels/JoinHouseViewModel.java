package it.unitn.disi.fumiprovv.roommates.viewmodels;

import androidx.lifecycle.ViewModel;

public class JoinHouseViewModel extends ViewModel {
    private String houseId;

    public JoinHouseViewModel(String houseId) {
        this.houseId = houseId;
    }

    public JoinHouseViewModel() {
        this.houseId = "";
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public boolean isHouseIdValid() {
        return houseId.length() > 0;
    }
}
