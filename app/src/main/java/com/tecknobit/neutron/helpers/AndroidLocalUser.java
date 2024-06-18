package com.tecknobit.neutron.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.tecknobit.neutroncore.helpers.LocalUser;

public class AndroidLocalUser extends LocalUser {

    private final SharedPreferences preferences;

    public AndroidLocalUser(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        initLocalUser();
    }

    @Override
    protected void setPreference(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    @Override
    protected String getPreference(String key) {
        return preferences.getString(key, null);
    }

}
