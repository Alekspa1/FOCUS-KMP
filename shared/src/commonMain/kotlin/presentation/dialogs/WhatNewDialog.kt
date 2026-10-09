package presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.theme.Theme
import presentation.theme.ThemeNeon

@Composable
fun WhatNewDialog(onClose: () -> Unit = {}, theme: Theme = ThemeNeon()) {

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text("Что нового", color = theme.textColor) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    //.padding(horizontal = 10.dp)
            ) {

                Text(text = "Уменьшил вес приложения", color = theme.textColor)
                HorizontalDivider(
                    thickness = 1.dp,
                    color = theme.textColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(text = "*Если после этого будут какие то глюки то пожалуйста воспользуйтесь обратной связью", color = theme.textColor)
                HorizontalDivider(
                    thickness = 1.dp,
                    color = theme.textColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 6.dp)
                )


            }


        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClose()
                },
                content = { Text("Понятно") })
        }
    )
}


@Preview(showBackground = true)
@Composable
fun Prev() {
    WhatNewDialog()
}