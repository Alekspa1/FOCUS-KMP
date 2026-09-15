package presentation.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.whatNewImageShare
import org.jetbrains.compose.resources.painterResource
import presentation.theme.Theme
import presentation.theme.ThemeNeon

@Composable
fun WhatNewDialog(onClose : () -> Unit = {},theme: Theme = ThemeNeon()){

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text("Что нового", color = theme.textColor) },
        text = {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 10.dp)){
            Text(text = "Добавлены новые темы", color = theme.textColor)
                HorizontalDivider(
                    thickness = 1.dp,
                    color = theme.textColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            Text(text = "Возможность создание подзадач", color = theme.textColor)

                HorizontalDivider(
                    thickness = 1.dp,
                    color = theme.textColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 6.dp)
                )

            Text(text = "Видно что входит в PREMIUM версию на странице оплаты", color = theme.textColor)
                HorizontalDivider(
                    thickness = 1.dp,
                    color = theme.textColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 6.dp)
                )

                Text(text = "Можно поделиться картинкой или текстом прямо в приложение", color = theme.textColor)
                Image(
                    painter = painterResource(Res.drawable.whatNewImageShare),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }


        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClose() },
                content = { Text("Понятно") })
        }
    )
}


@Preview
@Composable
fun Prev(){
    WhatNewDialog()
}