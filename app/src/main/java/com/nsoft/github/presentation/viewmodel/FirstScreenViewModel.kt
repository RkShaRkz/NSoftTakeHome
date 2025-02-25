package com.nsoft.github.presentation.viewmodel

import com.nsoft.github.domain.model.FirstScreenErrorState
import com.nsoft.github.domain.navigation.FirstScreenNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FirstScreenViewModel @Inject constructor(

): BaseViewModel<FirstScreenNavigationEvent, FirstScreenErrorState>() {

    override fun initialNavigationStreamValue() = FirstScreenNavigationEvent.NOWHERE
    override fun initialErrorStreamValue() = FirstScreenErrorState.NoError

}