package br.com.robsonldo.myutils.model;

import br.com.robsonldo.library.preference.PreferenceObject;
import br.com.robsonldo.library.preference.annotations.ObjectSharedPreference;

@ObjectSharedPreference("my_setting")
public class Setting extends PreferenceObject {

    private Boolean acceptNotification;

    public Boolean getAcceptNotification() {
        return acceptNotification;
    }

    public void setAcceptNotification(Boolean acceptNotification) {
        this.acceptNotification = acceptNotification;
    }
}