package com.nsoft.github.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.model.SecondScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import com.nsoft.github.domain.navigation.SecondScreenNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SecondScreenViewModel @Inject constructor(

): BaseViewModel<SecondScreenNavigationEvent, SecondScreenErrorState>() {

    override fun initialNavigationStreamValue() = SecondScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = SecondScreenErrorState.NoError

}