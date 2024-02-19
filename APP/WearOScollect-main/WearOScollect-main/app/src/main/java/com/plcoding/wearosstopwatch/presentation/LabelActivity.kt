package com.plcoding.wearosstopwatch.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.material.*
import androidx.wear.compose.material.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ChipDefaults
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectData
import androidx.lifecycle.lifecycleScope


class LabelButton (
    val label: String,
    val color: Long,
    val func: () -> Unit
)

class LabelActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabelWatch(
                intent.getLongExtra("NotificationTimeId", -1),
                questions = (intent.getSerializableExtra("questions") as Array<TemplateQuestion>).toMutableList(),
                navController = rememberSwipeDismissableNavController(),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    fun insertAffect(id: Long, affect: String) {
        UserDataStore
            .getUserRepository(applicationContext)
            .insertAffect(
                lifecycleScope,
                AffectData(0, id, affect)
            ) {
                //finish()
                Log.v("success", "This actually worked")
            }
    }
    fun insertContext(id: Long, affect: String) {
        UserDataStore
            .getUserRepository(applicationContext)
            .insertAffect(
                lifecycleScope,
                AffectData(0, id, affect)
            ) {
                finish()

                //val mainActivityIntent = Intent(this@LabelActivity, MainActivity::class.java).apply {
                    //putExtra("currentView", 1)
                //}

                //this makes the App return to the Stopwatch View
                //if removed the App returns to the HomeScreen of the Watch
                val mainActivityIntent = Intent(this@LabelActivity, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()
                Log.v("success", "This actually worked")
            }
    }

    @Composable
    private fun LabelWatch(
        notificationTimeId: Long,
        questions: List<TemplateQuestion>,
        navController: NavHostController,
        startDestination: String = "overview",
        modifier: Modifier = Modifier,
        numberOfQuestions: Int = questions.size
    ) {
        NavHost(
            navController = navController,
            startDestination = "overview"
        ) {
            val redButton = 0xFFFF4522
            val greenButton = 0xFFAAAF50
            val yellowButton = 0xFFCCA62B
            val blueButton = 0xFF008CE4
            val contextButton = 0xFF505050

            composable("overview") {
                val buttonVisibility = remember { mutableStateListOf<Boolean>() }
                repeat(4) {
                    buttonVisibility.add(it < numberOfQuestions)
                }
                LabelView(
                    first = LabelButton(
                        questions[0].button1_text,
                        redButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                Log.i("NUMBER", questions.size.toString())
                                Log.i("Questions", questions.toString())
                                navController.navigate("Question_2")
                            }
                            else {
                                Log.i("NUMBER", questions.size.toString())
                                Log.i("Questions", questions.toString())
                                insertContext(notificationTimeId, questions[0].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[0].button2_text,
                        blueButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("Question_2")
                            }
                            else {
                                insertContext(notificationTimeId, questions[0].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[0].button3_text,
                        greenButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("Question_2")
                            }
                            else {
                                insertContext(notificationTimeId, questions[0].button3_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[0].button4_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("Question_2")
                            }
                            else {
                                insertContext(notificationTimeId, questions[0].button4_text)
                            }
                        }),
                    question = questions[0].question,
                    buttonVisibility = listOf(questions[0].button1, questions[0].button2,
                        questions[0].button3, questions[0].button4),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("Question_2") {
                val buttonVisibility = remember { mutableStateListOf<Boolean>() }
                repeat(4) {
                    buttonVisibility.add(it < numberOfQuestions)
                }
                LabelView(
                    first = LabelButton(
                        questions[1].button1_text,
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button1_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[1].button2_text,
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button2_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[1].button3_text,
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button3_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button3_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[1].button4_text,
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button4_text)
                            }
                        }),
                    question = questions[1].question,
                    buttonVisibility = listOf(questions[1].button1, questions[1].button2,
                        questions[1].button3, questions[1].button4),
                    modifier = Modifier.fillMaxSize()
                )
            }

            /*
            composable("traurig") {
                LabelView(
                    first = LabelButton(
                        questions[1].button1_text,
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button1_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[1].button2_text,
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button2_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[1].button3_text,
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button3_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button3_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[1].button4_text,
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button4_text)
                            }
                        }),
                    question = questions[1].question,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("gluecklich") {
                LabelView(
                    first = LabelButton(
                        questions[1].button1_text,
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button1_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[1].button2_text,
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button2_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[1].button3_text,
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button3_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button3_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[1].button4_text,
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button4_text)
                            }
                        }),
                    question = questions[1].question,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("entspannt") {
                LabelView(
                    first = LabelButton(
                        questions[1].button1_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button1_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[1].button2_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button2_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[1].button3_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button3_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button3_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[1].button4_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, questions[1].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[1].button4_text)
                            }
                        }),
                    question = questions[1].question,
                    modifier = Modifier.fillMaxSize()
                )
            }
             */

            composable("kontext") {
                val buttonVisibility = remember { mutableStateListOf<Boolean>() }
                repeat(4) {
                    buttonVisibility.add(it < numberOfQuestions)
                }
                LabelView(
                    first = LabelButton(
                        questions[2].button1_text,
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, questions[2].button1_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[2].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[2].button2_text,
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, questions[2].button2_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[2].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[2].button3_text,
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, questions[2].button3_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[2].button3_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[2].button4_text,
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, questions[2].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[2].button4_text)
                            }
                        }),
                    question = questions[2].question,
                    buttonVisibility = listOf(questions[2].button1, questions[2].button2,
                        questions[2].button3, questions[2].button4),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("Question_4") {
                val buttonVisibility = remember { mutableStateListOf<Boolean>() }
                repeat(4) {
                    buttonVisibility.add(it < numberOfQuestions)
                }
                LabelView(
                    first = LabelButton(
                        questions[3].button1_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, questions[3].button1_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[3].button1_text)
                            }
                        }),
                    second = LabelButton(
                        questions[3].button2_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, questions[3].button2_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[3].button2_text)
                            }
                        }),
                    third = LabelButton(
                        questions[3].button4_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, questions[3].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[3].button4_text)
                            }
                        }),
                    fourth = LabelButton(
                        questions[3].button4_text,
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, questions[3].button4_text)
                            }
                            else {
                                insertContext(notificationTimeId, questions[3].button4_text)
                            }
                        }),
                    question = questions[3].question,
                    buttonVisibility = listOf(questions[3].button1, questions[3].button2,
                        questions[3].button3, questions[3].button4),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("Question_5") {val buttonVisibility = remember { mutableStateListOf<Boolean>() }
                repeat(4) {
                    buttonVisibility.add(it < numberOfQuestions)
                }

                LabelView(
                    first = LabelButton(
                        questions[4].button1_text,
                        contextButton,
                        func = { insertContext(notificationTimeId, questions[4].button1_text) }),
                    second = LabelButton(
                        questions[4].button2_text,
                        contextButton,
                        func = { insertContext(notificationTimeId, questions[4].button2_text) }),
                    third = LabelButton(
                        questions[4].button3_text,
                        contextButton,
                        func = { insertContext(notificationTimeId, questions[4].button3_text) }),
                    fourth = LabelButton(
                        questions[4].button4_text,
                        contextButton,
                        func = { insertContext(notificationTimeId, questions[4].button4_text)}),

                    question = questions[4].question,
                    buttonVisibility = listOf(questions[4].button1, questions[4].button2,
                        questions[4].button3, questions[4].button4),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    @Composable
    fun LabelView(
        first: LabelButton,
        second: LabelButton,
        third: LabelButton,
        fourth: LabelButton,
        question: String,
        buttonVisibility: List<Boolean>,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = question,
                modifier = Modifier.padding(16.dp, 24.dp, 16.dp, 8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (buttonVisibility.getOrNull(0) == true) {
                    Chip(
                        onClick = first.func,
                        label = {
                            Text(
                                text = first.label,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = ChipDefaults.primaryChipColors(backgroundColor = Color(first.color)),
                        modifier = Modifier
                            .width(90.dp)
                            //.weight(1f)
                            .padding(end = 4.dp)
                    )
                }
                if (buttonVisibility.getOrNull(1) == true) {
                    Chip(
                        onClick = second.func,
                        label = {
                            Text(
                                text = second.label,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = ChipDefaults.primaryChipColors(backgroundColor = Color(second.color)),
                        modifier = Modifier
                            .width(90.dp)
                            //.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp).width(5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (buttonVisibility.getOrNull(2) == true) {
                    Chip(
                        onClick = third.func,
                        label = {
                            Text(
                                text = third.label,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = ChipDefaults.primaryChipColors(backgroundColor = Color(third.color)),
                        modifier = Modifier
                            .width(90.dp)
                            //.weight(1f)
                            .padding(end = 4.dp)
                    )
                }
                if (buttonVisibility.getOrNull(3) == true) {
                    Chip(
                        onClick = fourth.func,
                        label = {
                            Text(
                                text = fourth.label,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = ChipDefaults.primaryChipColors(backgroundColor = Color(fourth.color)),
                        modifier = Modifier
                            .width(90.dp)
                            //.weight(1f)
                    )
                }
            }
        }
    }
}