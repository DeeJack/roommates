package it.unitn.disi.fumiprovv.roommates.viewmodels;

import androidx.lifecycle.ViewModel;

public class HouseViewModel extends ViewModel {
    private String houseId;

    public HouseViewModel(String houseId) {
        this.houseId = houseId;
    }

    public HouseViewModel() {
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
