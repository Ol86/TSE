package com.plcoding.wearosstopwatch.presentation

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
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import com.plcoding.wearosstopwatch.presentation.database.UserDataStore
import com.plcoding.wearosstopwatch.presentation.database.entities.AffectData
import androidx.lifecycle.lifecycleScope


class LabelButton (
    val label: String,
    val color: Long,
    val func: ()->Unit
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
                Log.v("success", "This actually worked")
            }
    }

    @Composable
    private fun LabelWatch(
        notificationTimeId: Long,
        navController: NavHostController,
        startDestination: String = "overview",
        modifier: Modifier = Modifier
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
                        func = { navController.navigate("veraergert") }),
                    second = LabelButton(
                        "Traurig",
                        blueButton,
                        func = { navController.navigate("traurig") }),
                    third = LabelButton(
                        "Glücklich",
                        greenButton,
                        func = { navController.navigate("gluecklich") }),
                    fourth = LabelButton(
                        "Entspannt",
                        yellowButton,
                        func = { navController.navigate("entspannt") }),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("veraergert") {
                LabelView(
                    first = LabelButton(
                        "Angespannt",
                        redButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Angespannt") }),
                    second = LabelButton(
                        "Betrübt",
                        redButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Betrübt") }),
                    third = LabelButton(
                        "Frustriert",
                        redButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Frustriert") }),
                    fourth = LabelButton(
                        "Wütend",
                        redButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Wütend") }),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("traurig") {
                LabelView(
                    first = LabelButton(
                        "Miserabel",
                        blueButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Miserabel") }),
                    second = LabelButton(
                        "Deprimiert",
                        blueButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Deprimiert") }),
                    third = LabelButton(
                        "Traurig",
                        blueButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Traurig") }),
                    fourth = LabelButton(
                        "Gelangweilt",
                        blueButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Gelangweilt") }),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("gluecklich") {
                LabelView(
                    first = LabelButton(
                        "Glücklich",
                        greenButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Glücklich") }),
                    second = LabelButton(
                        "Begeistert",
                        greenButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Begeistert") }),
                    third = LabelButton(
                        "Aufgeregt",
                        greenButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Aufgeregt") }),
                    fourth = LabelButton(
                        "Erfreut",
                        greenButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Erfreut") }),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("entspannt") {
                LabelView(
                    first = LabelButton(
                        "Schläfrig",
                        yellowButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Schläfrig") }),
                    second = LabelButton(
                        "Zufrieden",
                        yellowButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Zufrieden") }),
                    third = LabelButton(
                        "Erfüllt",
                        yellowButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Erfüllt") }),
                    fourth = LabelButton(
                        "Ruhig",
                        yellowButton,
                        func = {
                            navController.navigate("kontext")
                            insertAffect(notificationTimeId, "Ruhig") }),
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable("kontext") {
                LabelView(
                    first = LabelButton(
                        "Stand anschauen",
                        contextButton,
                        func = { insertContext(notificationTimeId, "Stand anschauen") }),
                    second = LabelButton(
                        "Unterhaltung",
                        contextButton,
                        func = { insertContext(notificationTimeId, "Unterhaltung") }),
                    third = LabelButton(
                        "Laufen",
                        contextButton,
                        func = { insertContext(notificationTimeId, "Laufen") }),
                    fourth = LabelButton(
                        "Sonstiges",
                        contextButton,
                        func = { insertContext(notificationTimeId, "Sonstiges") }),
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
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Chip(
                    onClick = first.func,
                    label = {
                        Text(
                            text = first.label,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(first.color))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Chip(
                    onClick = second.func,
                    label = {
                        Text(
                            text = second.label,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(second.color))
                )
                Spacer(modifier = Modifier.width(20.dp))
                Chip(
                    onClick = third.func,
                    label = {
                        Text(
                            text = third.label,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(third.color))
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Chip(
                    onClick = fourth.func,
                    label = {
                        Text(
                            text = fourth.label,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = ChipDefaults.primaryChipColors(backgroundColor = Color(fourth.color))
                )
            }
        }
    }
}