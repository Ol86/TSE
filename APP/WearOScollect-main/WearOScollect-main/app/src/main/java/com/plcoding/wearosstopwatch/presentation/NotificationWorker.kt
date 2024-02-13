package com.plcoding.wearosstopwatch.presentation

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.NotificationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import java.lang.reflect.Type
import java.time.LocalDateTime

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.i("NotificationWorker", "Called")
        val templateDataListJson = inputData.getString("template_questions")

        templateDataListJson?.let { json ->
            val gson = Gson()
            val listType: Type = object : TypeToken<List<TemplateQuestion>>() {}.type
            val templateQuestions: List<TemplateQuestion> = gson.fromJson(json, listType)
        //TODO try RoutineWorker with flex time for more reliability
        var notificationTimeId: Long
        UserDataStore
            .getUserRepository(applicationContext)
            .insertNotificationTime(CoroutineScope(GlobalScope.coroutineContext),
                NotificationData(0, LocalDateTime.now().toString())) {
                notificationTimeId = it.id
                Log.v("success", "This actually worked with $notificationTimeId")
                NotificationManager(applicationContext).promptNotification(
                    notificationTimeId,
                    templateQuestions
                )
            }
        }
        //TODO start oneTimeWorker to dismiss notification after 3 minutes
        Log.v("NotificationWorker","AH YES, SUCCESS")
        return Result.success()
    }
}