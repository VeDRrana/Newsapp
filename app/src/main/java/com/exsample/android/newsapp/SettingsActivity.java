package com.exsample.android.newsapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.ListPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minArtNews = findPreference(getString(R.string.settings_min_art_news_key));
            bindPreferenceSummaryToValue(minArtNews);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            // set this fragment and update the summary so that it displays the current value
            bindPreferenceSummaryToValue(orderBy);
        }

        //  method takes care of updating the displayed preference summary after it has been changed
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            //update the summary of a ListPreference
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        //  method takes in a Preference as its parameter
        private void bindPreferenceSummaryToValue(Preference preference) {
            //Set the current ArtPreferenceFragment
            preference.setOnPreferenceChangeListener(this);
            //Read the current value of the preference, and display it
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
