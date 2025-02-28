package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.model.SecondScreenErrorState
import com.nsoft.github.domain.model.ThirdScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.SecondScreenNavigationEvent
import com.nsoft.github.domain.navigation.ThirdScreenNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ThirdScreenViewModel @Inject constructor(

): BaseViewModel<ThirdScreenNavigationEvent, ThirdScreenErrorState>() {

    override fun initialNavigationStreamValue() = ThirdScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = ThirdScreenErrorState.NoError

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex = _selectedTabIndex.asStateFlow()

    fun onTabSelected(index: Int) {
        _selectedTabIndex.value = index
    }
}
