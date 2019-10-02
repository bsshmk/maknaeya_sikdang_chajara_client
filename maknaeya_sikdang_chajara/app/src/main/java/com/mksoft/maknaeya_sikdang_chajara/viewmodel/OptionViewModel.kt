package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mksoft.maknaeya_sikdang_chajara.R
import com.mksoft.maknaeya_sikdang_chajara.base.BaseViewModel
import com.mksoft.maknaeya_sikdang_chajara.model.AppDataBase
import com.mksoft.maknaeya_sikdang_chajara.model.OptionState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class OptionViewModel : BaseViewModel(){
    @Inject
    lateinit var appDataBase: AppDataBase

    val rangeButtonsState:MutableList<MutableLiveData<Int>> = mutableListOf(MutableLiveData(), MutableLiveData(), MutableLiveData(),MutableLiveData())
    val rangeValueList:List<Double> = listOf(2.0, 1.0, 0.5, 0.3)
    var rangeStateIDX = 0
    //초기화 이전에 미리 선언 필요 바인딩이 컴파일 시간에 수행되기 때문인 듯
    private lateinit var subscription: Disposable

    init {
        initOptionState()

    }
    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    fun insertButtonState(buttonNum:Int){
        subscription = Observable.fromCallable {
            val newOptionState = OptionState(0, buttonNum)
            var saveEmptyList:MutableList<OptionState> = mutableListOf()
            saveEmptyList.add(newOptionState)
            appDataBase.optionStateDao().insertOptionState(*saveEmptyList.toTypedArray())
            Observable.just(saveEmptyList)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            }

    }
    fun getRangeValue():Double{
        return rangeValueList[rangeStateIDX]
    }
    fun clickRangeButton(buttonNum:Int){
        insertButtonState(buttonNum)
        for(idx in 0..3){
            if(idx == buttonNum){
                rangeButtonsState[idx].value = R.color.selectedButton
                rangeStateIDX = idx
            }else{
                rangeButtonsState[idx].value = R.color.defaultColor
            }
        }
    }
    private fun initOptionState(){
        subscription = Observable.fromCallable {
            appDataBase.optionStateDao().getOptionState()
        }.concatMap {
            optionState->
            if(optionState.isEmpty()){
                Observable.just(0)
            }else{
                Observable.just(optionState[0].rangeButtonState)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {optionState ->
                    clickRangeButton(optionState!!)}
                ,{}
            )
    }

}