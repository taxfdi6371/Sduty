package com.d108.sduty.ui.sign.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d108.sduty.model.Retrofit
import com.d108.sduty.model.dto.InterestHashtag
import com.d108.sduty.model.dto.JobHashtag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG ="TagViewModel"
class TagViewModel: ViewModel() {
    private val _jobList = MutableLiveData<MutableList<JobHashtag>>()
    val jobList: LiveData<MutableList<JobHashtag>>
        get() = _jobList
    fun getJobListValue(){
        viewModelScope.launch(Dispatchers.IO){
            Retrofit.tagApi.getJobList().let {
                if(it.isSuccessful && it.body() != null){
                    _jobList.postValue(it.body() as MutableList<JobHashtag>)
                }else{
                    Log.d(TAG, "getJobListValue: ${it.code()}")
                }
            }
        }
    }

    private val _interestList = MutableLiveData<MutableList<InterestHashtag>>()
    val interestList: LiveData<MutableList<InterestHashtag>>
        get() = _interestList
    fun getInterestListValue(){
        viewModelScope.launch(Dispatchers.IO){
            Retrofit.tagApi.getInterestList().let {
                if(it.isSuccessful && it.body() != null){
                    _interestList.postValue(it.body() as MutableList<InterestHashtag>)
                }else{
                    Log.d(TAG, "getInterestListValue: ${it.code()}")
                }
                    
            }
        }
    }
}