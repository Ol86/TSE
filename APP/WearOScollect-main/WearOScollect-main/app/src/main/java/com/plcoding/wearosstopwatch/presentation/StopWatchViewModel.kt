@file:OptIn(ExperimentalCoroutinesApi::class)

package com.plcoding.wearosstopwatch.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import kotlinx.coroutines.flow.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationData
import com.plcoding.wearosstopwatch.presentation.database.entities.SessionData
import kotlinx.coroutines.*
import java.time.LocalDateTime

//class StopWatchViewModel: ViewModel() {
class StopWatchViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserDataStore.getUserRepository(getApplication())
    private var _session: SessionData? = null
    private val _elapsedTime = MutableStateFlow(0L)
    private val _timerState = MutableStateFlow(TimerState.RESET)
    val timerState = _timerState.asStateFlow()

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    val stopWatchText = _elapsedTime
        .map { millis ->
            LocalTime.ofNanoOfDay(millis * 1_000_000).format(formatter)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "00:00:00"
        )

    init {
        initialize()
        _timerState
            .flatMapLatest { timerState ->
                getTimerFlow(
                    isRunning = timerState == TimerState.RUNNING
                )
            }
            .onEach { timeDiff ->
                _elapsedTime.update { it + timeDiff }
            }
            .launchIn(viewModelScope)
    }

    private fun initialize() {
        _elapsedTime.update { 5000L }
        viewModelScope.launch {
            userRepository.getActiveSession(CoroutineScope(GlobalScope.coroutineContext),
                {
                    val millis: Long = System.currentTimeMillis() - it.startTimeMillis
                    _session = it
                    if(millis >= 0) {
                        _elapsedTime.update { millis }
                        _timerState.update { TimerState.RUNNING }
                    }
                },
                {
                    Log.v("success","noSession found")
                    _elapsedTime.update { 0L }
                }
            )
        }
    }

    fun start() {
        _timerState.update{ TimerState.RUNNING }
        if(_session==null){
            userRepository.insertSession(CoroutineScope(GlobalScope.coroutineContext),
                SessionData(0,System.currentTimeMillis(),0)) {
                _session = it
                Log.v("success","new Session created $it.id")
            }
        }
    }

    fun resetTimer() {
        _timerState.update { TimerState.RESET }
        _elapsedTime.update { 0L }
        userRepository.insertSession(CoroutineScope(GlobalScope.coroutineContext),
            SessionData(_session!!.id, _session!!.startTimeMillis,System.currentTimeMillis())) {
            _session = null
            Log.v("success","new Session created $it.id")
        }
    }

    private fun getTimerFlow(isRunning: Boolean): Flow<Long> {
        return flow {
            var startMillis = System.currentTimeMillis()
            while(isRunning) {
                val currentMillis = System.currentTimeMillis()
                val timeDiff = if(currentMillis > startMillis) {
                    currentMillis - startMillis
                } else 0L
                emit(timeDiff)
                startMillis = System.currentTimeMillis()
                delay(500L)
            }
        }
    }
}