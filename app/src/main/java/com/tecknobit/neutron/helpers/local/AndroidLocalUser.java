package com.tecknobit.neutron.helpers.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.tecknobit.neutroncore.helpers.LocalUser;

/**
 * The {@code AndroidLocalUser} class is useful to represent a user in a mobile application
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LocalUser
 */
public class AndroidLocalUser extends LocalUser {

    /**
     * {@code preferences} the manager of the local preferences
     */
    private final SharedPreferences preferences;

    /**
     * Constructor to init {@link AndroidLocalUser} class
     *
     * @param context: the context where the local user has been instantiated
     */
    public AndroidLocalUser(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        initLocalUser();
    }

    /**
     * Method to store and set a preference
     *
     * @param key: the key of the preference
     * @param value: the value of the preference
     */
    @Override
    protected void setPreference(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    /**
     * Method to get a stored preference
     *
     * @param key: the key of the preference to get
     * @return the preference stored as {@link String}
     */
    @Override
    protected String getPreference(String key) {
        return preferences.getString(key, null);
    }

    /**
     * Method to clear the current local user session <br>
     * No-any params required
     */
    @Override
    public void clear() {
        preferences.edit().clear().apply();
        initLocalUser();
    }

}
