package com.orlove101.android.mvvmstoragetask.ui.cats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.orlove101.android.mvvmstoragetask.persistence.CatsDao
import com.orlove101.android.mvvmstoragetask.persistence.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val taskDao: CatsDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle
): ViewModel() {
}