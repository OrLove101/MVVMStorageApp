package com.orlove101.android.mvvmstoragetask.ui.cats

import android.content.Context
import androidx.lifecycle.*
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.persistence.Room.CatsDao
import com.orlove101.android.mvvmstoragetask.persistence.Room.CurrentDatabase
import com.orlove101.android.mvvmstoragetask.persistence.Room.PreferencesManager
import com.orlove101.android.mvvmstoragetask.persistence.Room.SortOrder
import com.orlove101.android.mvvmstoragetask.persistence.SQLite.CatsDatabaseHelper
import com.orlove101.android.mvvmstoragetask.ui.ADD_TASK_RESULT_OK
import com.orlove101.android.mvvmstoragetask.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatsViewModel @Inject constructor(
    private val catsDao: CatsDao,
    private val preferencesManager: PreferencesManager,
    private val state: SavedStateHandle,
    private val catsDbHelper: CatsDatabaseHelper
): ViewModel() {
    val searchQuery = state.getLiveData("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow

    private val catsEventChannel = Channel<CatsEvent>()
    val catsEvent = catsEventChannel.receiveAsFlow()

    private val catsFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow,
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        currentDatabase = filterPreferences.currentDatabase
        if (filterPreferences.currentDatabase == CurrentDatabase.ROOM) {
            catsDao.getCats(query, filterPreferences.sortOrder)
        } else {
            catsDbHelper.getCats(query, filterPreferences.sortOrder)
        }
    }

    val cats = catsFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onCatSelected(cat: Cat) {
        viewModelScope.launch {
            catsEventChannel.send(CatsEvent.NavigateToEditCatScreen(cat))
        }
    }

    fun onCatSwiped(cat: Cat) {
        viewModelScope.launch {
            if (currentDatabase == CurrentDatabase.ROOM) {
                catsDao.delete(cat)
            } else {
                catsDbHelper.delete(cat)
                // TODO fix behaviour: after deleting remains empty row in recycler view but after app reload all correctly
            }
            catsEventChannel.send(CatsEvent.ShowUndoDeleteCatMessage(cat))
        }
    }

    fun onUndoDeleteClick(cat: Cat) {
        viewModelScope.launch {
            if (currentDatabase == CurrentDatabase.ROOM) {
                catsDao.insert(cat)
            } else {
                catsDbHelper.insert(cat)
            }
        }
    }

    fun onAddNewCatClick() {
        viewModelScope.launch {
            catsEventChannel.send(CatsEvent.NavigateToAddCatScreen)
        }
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showCatSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showCatSavedConfirmationMessage("Task updated")
        }
    }

    private fun showCatSavedConfirmationMessage(text: String) = viewModelScope.launch {
        catsEventChannel.send(CatsEvent.ShowCatSavedConfirmationMessage(text))
    }

    fun onSettingsClick(): Job {
        return viewModelScope.launch {
            catsEventChannel.send(CatsEvent.NavigateToSettingsScreen)
        }
    }

    sealed class CatsEvent {
        object NavigateToAddCatScreen: CatsEvent()
        data class NavigateToEditCatScreen(val cat: Cat): CatsEvent()
        data class ShowUndoDeleteCatMessage(val cat: Cat): CatsEvent()
        data class ShowCatSavedConfirmationMessage(val msg: String): CatsEvent()
        object NavigateToSettingsScreen: CatsEvent()
    }

    companion object {
        var currentDatabase: CurrentDatabase = CurrentDatabase.ROOM
    }
}