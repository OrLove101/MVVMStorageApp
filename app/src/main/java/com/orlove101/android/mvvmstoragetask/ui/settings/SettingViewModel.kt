package com.orlove101.android.mvvmstoragetask.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orlove101.android.mvvmstoragetask.persistence.Room.CurrentDatabase
import com.orlove101.android.mvvmstoragetask.persistence.Room.PreferencesManager
import com.orlove101.android.mvvmstoragetask.persistence.Room.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
): ViewModel() {
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onChangeDatabaseSelected(database: CurrentDatabase) = viewModelScope.launch {
        preferencesManager.updateCurrentDatabase(database)
    }
}