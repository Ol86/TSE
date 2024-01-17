package com.plcoding.wearosstopwatch.presentation

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

                val mainActivityIntent = Intent(this@LabelActivity, MainActivity::class.java)
                startActivity(mainActivityIntent)
                Log.v("success", "This actually worked")
            }
    }

    @Composable
    private fun LabelWatch(
        notificationTimeId: Long,
        navController: NavHostController,
        startDestination: String = "overview",
        modifier: Modifier = Modifier,
        numberOfQuestions: Int = 4
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
                LabelView(
                    first = LabelButton(
                        "Verärgert",
                        redButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("veraergert")
                            }
                            else {
                                insertContext(notificationTimeId, "veraergert")
                            }
                        }),
                    second = LabelButton(
                        "Traurig",
                        blueButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("traurig")
                            }
                            else {
                                insertContext(notificationTimeId, "traurig")
                            }
                        }),
                    third = LabelButton(
                        "Glücklich",
                        greenButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("gluecklich")
                            }
                            else {
                                insertContext(notificationTimeId, "glücklich")
                            }
                        }),
                    fourth = LabelButton(
                        "Entspannt",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 1) {
                                navController.navigate("entspannt")
                            }
                            else {
                                insertContext(notificationTimeId, "entspannt")
                            }
                        }),
                    question = "Wie fühlst du dich gerade?",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("veraergert") {
                LabelView(
                    first = LabelButton(
                        "Angespannt",
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Angespannt")
                            }
                            else {
                                insertContext(notificationTimeId, "angespannt")
                            }
                        }),
                    second = LabelButton(
                        "Betrübt",
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Betrübt")
                            }
                            else {
                                insertContext(notificationTimeId, "betrübt")
                            }
                        }),
                    third = LabelButton(
                        "Frustriert",
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Frustriert")
                            }
                            else {
                                insertContext(notificationTimeId, "frustriert")
                            }
                        }),
                    fourth = LabelButton(
                        "Wütend",
                        redButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Wütend")
                            }
                            else {
                                insertContext(notificationTimeId, "wütend")
                            }
                        }),
                    question = "Wie verärgert?",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("traurig") {
                LabelView(
                    first = LabelButton(
                        "Miserabel",
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Miserabel")
                            }
                            else {
                                insertContext(notificationTimeId, "miserabel")
                            }
                        }),
                    second = LabelButton(
                        "Deprimiert",
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Deprimiert")
                            }
                            else {
                                insertContext(notificationTimeId, "deprimiert")
                            }
                        }),
                    third = LabelButton(
                        "Traurig",
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Traurig")
                            }
                            else {
                                insertContext(notificationTimeId, "traurig")
                            }
                        }),
                    fourth = LabelButton(
                        "Gelangweilt",
                        blueButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Gelangweilt")
                            }
                            else {
                                insertContext(notificationTimeId, "gelangweilt")
                            }
                        }),
                    question = "DDDDDDDDDDDDDDDDDDDDDDDDD",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("gluecklich") {
                LabelView(
                    first = LabelButton(
                        "Glücklich",
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Glücklich")
                            }
                            else {
                                insertContext(notificationTimeId, "glücklich")
                            }
                        }),
                    second = LabelButton(
                        "Begeistert",
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Begeistert")
                            }
                            else {
                                insertContext(notificationTimeId, "begeistert")
                            }
                        }),
                    third = LabelButton(
                        "Aufgeregt",
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Aufgeregt")
                            }
                            else {
                                insertContext(notificationTimeId, "aufgeregt")
                            }
                        }),
                    fourth = LabelButton(
                        "Erfreut",
                        greenButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Erfreut")
                            }
                            else {
                                insertContext(notificationTimeId, "erfreut")
                            }
                        }),
                    question = "Wie glücklich?",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("entspannt") {
                LabelView(
                    first = LabelButton(
                        "Schläfrig",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Schläfrig")
                            }
                            else {
                                insertContext(notificationTimeId, "schläfrig")
                            }
                        }),
                    second = LabelButton(
                        "Zufrieden",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Zufrieden")
                            }
                            else {
                                insertContext(notificationTimeId, "zufrieden")
                            }
                        }),
                    third = LabelButton(
                        "Erfüllt",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Erfüllt")
                            }
                            else {
                                insertContext(notificationTimeId, "erfüllt")
                            }
                        }),
                    fourth = LabelButton(
                        "Ruhig",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 2) {
                                navController.navigate("kontext")
                                insertAffect(notificationTimeId, "Ruhig")
                            }
                            else {
                                insertContext(notificationTimeId, "ruhig")
                            }
                        }),
                    question = "Wie entspannt?",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("kontext") {
                LabelView(
                    first = LabelButton(
                        "Stand anschauen",
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, "Stand anschauen")
                            }
                            else {
                                insertContext(notificationTimeId, "stand_anschauen")
                            }
                        }),
                    second = LabelButton(
                        "Unterhaltung",
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, "Unterhaltung")
                            }
                            else {
                                insertContext(notificationTimeId, "unterhaltung")
                            }
                        }),
                    third = LabelButton(
                        "Laufen",
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, "Laufen")
                            }
                            else {
                                insertContext(notificationTimeId, "laufen")
                            }
                        }),
                    fourth = LabelButton(
                        "Sonstiges",
                        contextButton,
                        func = {
                            if (numberOfQuestions > 3) {
                                navController.navigate("Question_4")
                                insertAffect(notificationTimeId, "Sonstiges")
                            }
                            else {
                                insertContext(notificationTimeId, "sonstiges")
                            }
                        }),
                    question = "Was tust du gerade?",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("Question_4") {
                LabelView(
                    first = LabelButton(
                        "answer4.1",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, "QQQQQ")
                            }
                            else {
                                insertContext(notificationTimeId, "qqqqqq")
                            }
                        }),
                    second = LabelButton(
                        "answer4.2",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, "WWWWW")
                            }
                            else {
                                insertContext(notificationTimeId, "wwwww")
                            }
                        }),
                    third = LabelButton(
                        "answer4.3",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, "EEEEE")
                            }
                            else {
                                insertContext(notificationTimeId, "eeeee")
                            }
                        }),
                    fourth = LabelButton(
                        "answer4.4",
                        yellowButton,
                        func = {
                            if (numberOfQuestions > 4) {
                                navController.navigate("Question_5")
                                insertAffect(notificationTimeId, "RRRRR")
                            }
                            else {
                                insertContext(notificationTimeId, "rrrrr")
                            }
                        }),
                    question = "Question 4",
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("Question_5") {
                LabelView(
                    first = LabelButton(
                        "answer5.1",
                        contextButton,
                        func = { insertContext(notificationTimeId, "mmmmmmmm") }),
                    second = LabelButton(
                        "answer5.2",
                        contextButton,
                        func = { insertContext(notificationTimeId, "xxxxxxxx") }),
                    third = LabelButton(
                        "answer5.3",
                        contextButton,
                        func = { insertContext(notificationTimeId, "ccccccccc") }),
                    fourth = LabelButton(
                        "answer5.4",
                        contextButton,
                        func = { insertContext(notificationTimeId, "vvvvvvvvv")}),
                    question = "Question 5",
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Chip(
                    onClick = first.func,
                    label = {
                        Text(
                            text = first.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(first.color)),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)

                )
                Chip(
                    onClick = second.func,
                    label = {
                        Text(
                            text = second.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(second.color)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(5.dp).width(5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Chip(
                    onClick = third.func,
                    label = {
                        Text(
                            text = third.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(third.color)),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                Chip(
                    onClick = fourth.func,
                    label = {
                        Text(
                            text = fourth.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(fourth.color)),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}