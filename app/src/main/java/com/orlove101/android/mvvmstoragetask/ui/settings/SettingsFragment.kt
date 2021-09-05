package com.orlove101.android.mvvmstoragetask.ui.settings

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.orlove101.android.mvvmstoragetask.R
import com.orlove101.android.mvvmstoragetask.persistence.Room.CurrentDatabase
import com.orlove101.android.mvvmstoragetask.persistence.Room.SortOrder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: PreferenceFragmentCompat() {
    val viewModel: SettingViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preferences, rootKey)
        val sortByName: Preference? = findPreference("name")
        sortByName?.setOnPreferenceClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_NAME)
            Toast.makeText(requireContext(), "Sort by name!", Toast.LENGTH_LONG).show()
            true
        }

        val sortByBreed: Preference? = findPreference("breed")
        sortByBreed?.setOnPreferenceClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_BREED)
            Toast.makeText(requireContext(), "Sort by breed!", Toast.LENGTH_LONG).show()
            true
        }

        val sortByAge: Preference? = findPreference("age")
        sortByAge?.setOnPreferenceClickListener {
            viewModel.onSortOrderSelected(SortOrder.BY_AGE)
            Toast.makeText(requireContext(), "Sort by age!", Toast.LENGTH_LONG).show()
            true
        }

        val roomDatabase: Preference? = findPreference("room")
        roomDatabase?.setOnPreferenceClickListener {
            viewModel.onChangeDatabaseSelected(CurrentDatabase.ROOM)
            Toast.makeText(requireContext(), "Database switched to Room!", Toast.LENGTH_LONG).show()
            true
        }

        val sqlDatabase: Preference? = findPreference("sql")
        sqlDatabase?.setOnPreferenceClickListener {
            viewModel.onChangeDatabaseSelected(CurrentDatabase.SQLITE)
            Toast.makeText(requireContext(), "Database switched to SQLite!", Toast.LENGTH_LONG).show()
            true
        }
    }
}