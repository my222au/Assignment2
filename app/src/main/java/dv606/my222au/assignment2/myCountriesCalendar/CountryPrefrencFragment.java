package dv606.my222au.assignment2.myCountriesCalendar;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import dv606.my222au.assignment2.R;

public class CountryPrefrencFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.country_prefs);
    }

}
