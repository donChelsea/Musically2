package com.example.musically2.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musically2.data.TrackRepository
import com.example.musically2.domain.models.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TracksViewModel @Inject constructor(
    private val repository: TrackRepository
): ViewModel() {

    private val _trackList = MutableLiveData<List<Track>>(emptyList())
    val trackList: LiveData<List<Track>> get() = _trackList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTracks().let { tracks ->
                _trackList.postValue(tracks.sortedBy { it.index })
            }
        }
    }
}