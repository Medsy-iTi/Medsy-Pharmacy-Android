package com.medsy.presentation.orderdetails.substitute

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubstituteResultViewModel @Inject constructor(
    val manager: SubstituteResultManager
) : ViewModel()
