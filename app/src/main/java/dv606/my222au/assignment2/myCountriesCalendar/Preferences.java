package dv606.my222au.assignment2.myCountriesCalendar;

import android.preference.PreferenceActivity;

import java.util.List;

import dv606.my222au.assignment2.R;
import dv606.my222au.assignment2.myCountriesCalendar.CountryPrefrencFragment;

public class Preferences extends PreferenceActivity {


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment (String fragmentName) {
        return (CountryPrefrencFragment.class.getName().equals(fragmentName));
    }


}
