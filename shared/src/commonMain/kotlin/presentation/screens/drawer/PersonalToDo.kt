package presentation.screens.drawer

import CommonConst.DRAWER_EVERYDAY
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import presentation.theme.Size
import presentation.theme.SizeNormal
import presentation.theme.ThemeNeon

@Composable
fun PersonalToDo(
    theme: ThemeNeon = ThemeNeon(),
    size: Size = SizeNormal(),
    onClick : (String)-> Unit = {},
){

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            3.dp,
                            theme.borderCardMenuItem,
                            RoundedCornerShape(10.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onClick(DRAWER_EVERYDAY)
//                            updateCategory("Повседневные")
//                            scope.launch {
//                                launch { drawerState.close() }
//                                launch {
//                                    pagerState.animateScrollToPage(
//                                        1
//                                    )
//                                }
//                            }
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = theme.cardMenuItem)
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Повседневные",
                        color = theme.textColor,
                        lineHeight = size.lineHeightItem,
                        fontSize = size.textItem
                    )
                }
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = theme.iconDrawerEveryday, // Или ваша иконка ic_menu
                    contentDescription = "Меню",
                    tint = theme.iconTint
                )

            }


}