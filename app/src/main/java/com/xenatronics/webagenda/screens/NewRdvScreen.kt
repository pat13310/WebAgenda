package com.xenatronics.webagenda.screens

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.NavController
import com.xenatronics.webagenda.R
import com.xenatronics.webagenda.components.NewTaskBar
import com.xenatronics.webagenda.components.UIComboContact
import com.xenatronics.webagenda.components.UiDatePicker
import com.xenatronics.webagenda.components.UiTimePicker
import com.xenatronics.webagenda.data.Rdv
import com.xenatronics.webagenda.navigation.Screen
import com.xenatronics.webagenda.util.Action
import com.xenatronics.webagenda.util.LockScreenOrientation
import com.xenatronics.webagenda.util.getDateFormatter
import com.xenatronics.webagenda.util.getTimeFormatter
import com.xenatronics.webagenda.viewmodel.ViewModelRdv

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewRdvScreen(
    navController: NavController,
    viewModel: ViewModelRdv,
    rdv: Rdv
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        Scaffold(
            topBar = {
                NewTaskBar(if (rdv.id == 0) "Nouveau rendez-vous" else "Modifier rendez-vous",
                    NavigateToListScreen = {
                        if (rdv.id == 0) {
                            viewModel.updateFields()
                            viewModel.handleRdvAction(Action.ADD)
                            navController.navigate(Screen.ListRdvScreen.route)
                        } else {
                            viewModel.handleRdvAction(Action.UPDATE)
                            navController.navigate(Screen.ListRdvScreen.route)
                        }
                    })
            },
            content = {
                NewRdvContent2(
                    navController = navController,
                    viewModel = viewModel,
                    rdv = rdv
                )
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewRdvContent2(
    navController: NavController,
    viewModel: ViewModelRdv,
    rdv: Rdv,
) {
    BoxWithConstraints {
        val constraint = decoupledConstraints(16.dp)
        ConstraintLayout( constraint) {

            LaunchedEffect(key1 = true) {
                viewModel.loadContact()
            }
            val listContact = viewModel.allContactFlow.collectAsState()
            val timestamp = rdv.date
            Image(painterResource(id = R.drawable.newrdv ), contentDescription = "",
                Modifier
                    .padding(vertical = 8.dp)
                    .scale(1.2f)
                    .layoutId("image"))
            UIComboContact(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .layoutId("textRdv"),
                options = listContact.value.toList().sortedBy { contact -> contact.nom },
                viewModel = viewModel,
                text = rdv.nom,
                onText = {
                    rdv.nom = it
                    viewModel.nom.value = it
                },
                onNavigate = { route ->
                    navController.navigate(route = route)
                }
            )
            UiDatePicker(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .layoutId("textDate"),
                viewModel = viewModel,
                rdv = rdv,
                text = getDateFormatter(timestamp)
            )
            UiTimePicker(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .layoutId("textTime"),
                viewModel = viewModel,
                rdv = rdv,
                text = getTimeFormatter(timestamp)
            )
        }
    }
}

private fun decoupledConstraints(margin: Dp, hMargin: Dp = 16.dp): ConstraintSet {
    return ConstraintSet {
        val image = createRefFor("image")
        val textRdv = createRefFor("textRdv")
        val textDate = createRefFor("textDate")
        val textTime = createRefFor("textTime")

        constrain(image) {
            top.linkTo(parent.top, margin = margin)
            start.linkTo(parent.start, margin = hMargin)
            end.linkTo(parent.end, margin = hMargin)
        }
        constrain(textRdv) {
            top.linkTo(image.bottom, margin = margin)
            start.linkTo(parent.start, margin = hMargin)
            end.linkTo(parent.end, margin = hMargin)
        }
        constrain(textDate) {
            top.linkTo(textRdv.bottom, margin = margin)
            start.linkTo(parent.start, margin = hMargin)
            end.linkTo(parent.end, margin = hMargin)
        }
        constrain(textTime) {
            top.linkTo(textDate.bottom, margin = margin)
            start.linkTo(parent.start, margin = hMargin)
            end.linkTo(parent.end, margin = hMargin)
        }
    }
}