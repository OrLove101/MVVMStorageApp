package com.orlove101.android.mvvmstoragetask.ui.addeditcat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.persistence.Room.CatsDao
import com.orlove101.android.mvvmstoragetask.persistence.Room.CurrentDatabase
import com.orlove101.android.mvvmstoragetask.persistence.SQLite.CatsDatabaseHelper
import com.orlove101.android.mvvmstoragetask.ui.ADD_TASK_RESULT_OK
import com.orlove101.android.mvvmstoragetask.ui.EDIT_TASK_RESULT_OK
import com.orlove101.android.mvvmstoragetask.ui.cats.CatsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCatViewModel @Inject constructor(
    private val catsDao: CatsDao,
    private val state: SavedStateHandle,
    private val catsDbHelper: CatsDatabaseHelper
) : ViewModel() {
    val cat = state.get<Cat>("cat")
    var catName = state.get<String>("catName") ?: cat?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }
    var catAge = state.get<String>("catAge") ?: cat?.age?.toString() ?: ""
        set(value) {
            field = value
            state.set("catAge", value)
        }
    var catBreed = state.get<String>("catBreed") ?: cat?.breed ?: ""
        set(value) {
            field = value
            state.set("catBreed", value)
        }

    private val addEditCatEventChannel = Channel<AddEditCatEvent>()
    val addEditCatEvent = addEditCatEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if ( catName.isBlank() ) {
            showInvalidInputMessage("Name cannot be empty")
        }
        if ( catAge.isBlank() ) {
            showInvalidInputMessage("Age cannot be empty")
        }
        if ( catBreed.isBlank() ) {
            showInvalidInputMessage("Breed cannot be empty")
        }
        if ( cat != null ) {
            val updateCat = cat.copy(name = catName, age = catAge.toInt(), breed = catBreed)
            updateCat(updateCat)
        } else {
            val newCat = Cat(name = catName, age = catAge.toInt(), breed = catBreed)
            createCat(newCat)
        }
    }

    private fun createCat(cat: Cat) {
        viewModelScope.launch {
            if (CatsViewModel.currentDatabase == CurrentDatabase.ROOM) {
                catsDao.insert(cat)
            } else {
                catsDbHelper.insert(cat)
            }
            addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
        }
    }

    private fun updateCat(cat: Cat) {
        viewModelScope.launch {
            if (CatsViewModel.currentDatabase == CurrentDatabase.ROOM) {
                catsDao.update(cat)
            } else {
                catsDbHelper.update(cat)
            }
            addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }
    }

    private fun showInvalidInputMessage(text: String) {
        viewModelScope.launch {
            addEditCatEventChannel.send(AddEditCatEvent.ShowInvalidInputMessage(text))
        }
    }

    sealed class AddEditCatEvent {
        data class ShowInvalidInputMessage(val msg: String): AddEditCatEvent()
        data class NavigateBackWithResult(val result: Int): AddEditCatEvent()
    }
}