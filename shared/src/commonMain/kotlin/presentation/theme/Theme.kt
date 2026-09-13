package presentation.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import flashlight.shared.generated.resources.Res
import flashlight.shared.generated.resources.background_drawer_neon
import flashlight.shared.generated.resources.background_drawer_poison
import flashlight.shared.generated.resources.background_groza
import flashlight.shared.generated.resources.background_mramor
import flashlight.shared.generated.resources.background_neon
import flashlight.shared.generated.resources.background_platina
import flashlight.shared.generated.resources.background_poison
import flashlight.shared.generated.resources.background_vulcan
import flashlight.shared.generated.resources.background_zabor
import flashlight.shared.generated.resources.background_drawer_vulcan
import org.jetbrains.compose.resources.DrawableResource


sealed interface Theme{
    val backgroundStart : DrawableResource
    val backgroundDrawer : DrawableResource
    val backgroundCalendar : Color
    val noteBookBackground: Color
    val noteBookBorder: Color
    val textColor: Color
    val iconMicro : ImageVector
    val iconDel : ImageVector
    val tintAlarmOn: Color
    val cardItemBorderAlarm: Color
    val cardItemBorderTrue: Color
    val cardItemBorderFalse: Color
    val cardItemAlarm: Color
    val cardItemTrue: Color
    val cardItemFalse: Color
    val tintAlarmOff: Color
    val textDesc: Color
    val textAlarm: Color
    val chekBoxOff: Color
    val chekBoxOn: Color
    val chekBoxTint: Color
    val iconImage: ImageVector
    val iconTint: Color
    val iconAdd: ImageVector
    val iconAddTint: Color
    val iconDelItem: ImageVector
    val iconDelTint: Color
    val iconDrawerEveryday : ImageVector
    val iconDrawerShare : ImageVector
    val iconDrawerPremium : ImageVector
    val tintPremiumOn : Color
    val tintPremiumOff : Color
    val iconDrawerUpdateOff : ImageVector
    val iconDrawerUpdateOn : ImageVector
    val iconDrawerSettigs : ImageVector
    val backgroundDialog : Color
    val borderCardMenuItem: Color
    val cardMenuItem : Color
    val colorCalendarDaySelect : Color
    val tintCheckBoxSubItemOff : Color
}

data class ThemeNeon (
    override val textColor: Color = Color.White,
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,
    //Блокнот
    override  val noteBookBackground: Color = Color(0x9900BCD4),
    override  val noteBookBorder: Color = Color(0x9900E2FF),
    //Список дел
    override val tintAlarmOn: Color = Color.Yellow,
    override val tintAlarmOff: Color = Color.White,
    override val textDesc: Color = Color(0xFFB6B6B6),


    override val cardItemBorderAlarm: Color = Color(0xB3D6C000),
    override val cardItemBorderTrue: Color = Color(0xB325C800),
    override val cardItemBorderFalse: Color = Color(0xB3FB4141),

    override val cardItemAlarm: Color = Color(0x80006F7E),
    override val cardItemTrue: Color = Color(0x4D23BD00),
    override val cardItemFalse: Color = Color(0x4DF30404),

    override val textAlarm: Color = Color.Yellow,
    override val chekBoxOff: Color = Color.White,
    override val chekBoxOn: Color = Color.White,
    override val chekBoxTint: Color = Color.Black,

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF65D4FF),
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color.Yellow,
    override val tintPremiumOff: Color = Color.White,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFF424242),
    override val backgroundStart: DrawableResource = Res.drawable.background_neon,
    override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_neon,
    override val borderCardMenuItem: Color = Color(0x9900E2FF),
    override val cardMenuItem: Color = Color(0x6500BCD4),
    override val colorCalendarDaySelect: Color = Color.Black,
    override val backgroundCalendar: Color = Color.Transparent,
    override val tintCheckBoxSubItemOff: Color = cardItemBorderFalse


    //Дравер

) : Theme

data class ThemeZabor (
    override val textColor: Color = Color.Black,

    override val noteBookBackground: Color = Color(0x7FFFEB3B),
    override val noteBookBorder: Color = Color(0xFF8D6E63),
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,

    // Список дел
    override val tintAlarmOn: Color = Color.Yellow,   // Насыщенный янтарный (отлично виден на дереве)
    override val tintAlarmOff: Color = Color(0xAA000000),  // Благородный полупрозрачный черный
    override val textDesc: Color = Color.Black,      // Спокойный серый для описания

    // Контуры карточек (тонкие, чистые и понятные)
    override val cardItemBorderAlarm: Color = Color.Yellow, // Аккуратный оранжевый контур
    override val cardItemBorderTrue: Color = Color(0xFF00C853),  // Чистый зеленый контур
    override val cardItemBorderFalse: Color = Color(0x4DF30404), // Четкий красный контур

    // Фоны карточек (Эффект матового стекла — одинаковый мягкий светлый фон для всех!)
   // override val cardItemAlarm: Color = Color(0xE68E8D8D),       // Плотный белый с высокой прозрачностью
    override val cardItemAlarm: Color = Color(0xE69E9282),       // Плотный белый с высокой прозрачностью
    override val cardItemTrue: Color = Color(0xE65AD47D),        // Плотный белый с высокой прозрачностью
   // override val cardItemFalse: Color = Color(0xE6CD5454),
    override val cardItemFalse: Color = Color(0xE6D06767),

    override val textAlarm: Color = Color.Yellow,     // Темно-оранжевый для цифр времени
    override val chekBoxOff: Color =  Color.Black,
    override val chekBoxOn: Color = Color.Black,
    override val chekBoxTint: Color = Color.White,     // Шоколадный цвет для чекбокса

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color.Black,
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.Black,   // Опасный красный для удаления
    override val iconTint: Color = Color.Black,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color.Yellow,
    override val tintPremiumOff: Color = Color.Black,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Update,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,

    // Мягкий светлый фон для диалогов (цвет топленого молока или крем-брюле)
    override val backgroundDialog: Color = Color(0xFFF9F6F0),
    override val backgroundStart: DrawableResource = Res.drawable.background_zabor,
    override val backgroundDrawer: DrawableResource = Res.drawable.background_zabor,
    override val borderCardMenuItem: Color = Color(0xFF5E5F61),
    override val cardMenuItem: Color = Color(0x99B6B6B6),
    override val colorCalendarDaySelect: Color = Color.White,
    override val backgroundCalendar: Color = Color.Transparent,
    override val tintCheckBoxSubItemOff: Color = textColor

) : Theme


data class ThemeStorm(
    override val textColor: Color = Color(0xFFFFFFFF),          // Чистый белый для максимальной читаемости
    override val iconMicro: ImageVector = Icons.Default.Mic,
    override val iconDel: ImageVector = Icons.Default.Delete,

    override val noteBookBackground: Color = Color(0xFF12121E), // Почти чёрный, глубокий индиго
    override val noteBookBorder: Color = Color(0x807B21FF),    // Полупрозрачный яркий фиолетовый

    override val tintAlarmOn: Color = Color(0xFF7B21FF),        // Акцентный фиолетовый (цвет молнии)
    override val tintAlarmOff: Color = Color(0xFF6F6F6F),
    override val textDesc: Color = Color(0xFFB3C8FF),           // Светло‑голубой для описаний (как отсвет молнии)

    override val cardItemBorderAlarm: Color = Color(0xFF7B21FF),
    override val cardItemBorderTrue: Color = Color(0x8000FF87),
    override val cardItemBorderFalse: Color = Color(0x99D50000),

    override val cardItemAlarm: Color = Color(0xEC1A1A28),      // Тёмно‑фиолетовый фон карточки с тревожным оттенком
    override val cardItemTrue: Color = Color(0xE1014E29),
    override val cardItemFalse: Color = Color(0xF23D0101),

    override val textAlarm: Color = Color(0xFFFFD700),          // Золотой для времени — как вспышка перед разрядом (альтернатива: оставить белым)
    override val chekBoxOff: Color = Color.White,
    override val chekBoxOn: Color = Color.White,
    override val chekBoxTint: Color = Color.Black,     // Фиолетовые чекбоксы

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xE67B21FF),         // Золотой плюс — точка фокуса
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.White,
    override val iconTint: Color = Color.White,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFFFFD700),      // Золотой для премиум — выделяется на фиолетовом
    override val tintPremiumOff: Color = Color.White,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFF12121E),   // Тёмный фон для диалогов
    override val backgroundStart: DrawableResource = Res.drawable.background_groza, // Твой фон с молнией
    override val backgroundDrawer: DrawableResource = Res.drawable.background_groza,
    override val borderCardMenuItem: Color = Color(0x807B21FF),
    override val cardMenuItem: Color = Color(0xE61A1A28),        // Тёмно‑фиолетовый фон пунктов меню
    override val colorCalendarDaySelect: Color = Color(0xFF7B21FF), // Фиолетовый для выбранного дня
    override val backgroundCalendar: Color = noteBookBackground,
    override val tintCheckBoxSubItemOff: Color = cardItemBorderFalse,
) : Theme


data class ThemeMarble (
    override val textColor: Color = Color(0xFF1C1D22),           // Глубокий антрацитовый (почти черный) для отличной читаемости на белом

    // Блокнот (Календарь)
    override val noteBookBackground: Color = Color(0xF2F4F5F7), // Мягкий ультра-светлый серый (основа мрамора, 95% плотности)
    override val noteBookBorder: Color = Color(0x99A0A5B0),     // Цвет серых прожилок для аккуратного контура
    override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
    override val iconDel: ImageVector = Icons.Default.Delete,

    // Список дел
    override val tintAlarmOn: Color = Color.Yellow,        // Активный будильник глубокого стального/графитового цвета
    override val tintAlarmOff: Color = Color.Black,       // Выключенный будильник (светло-стальной)
    override val textDesc: Color = Color.Black,           // Сдержанный графитовый для описания задач


    override val cardItemBorderAlarm: Color = Color.Yellow, // Строгий графитовый бордюр для важных задач
    override val cardItemBorderTrue: Color = Color(0xFF2E7D32),  // Спокойный благородный зеленый бордюр (без неона)
    override val cardItemBorderFalse: Color = Color(0xFFC62828), // Сдержанный рубиново-красный бордюр

    override val cardItemAlarm: Color = Color(0xF2B4B5B6),       // Чистый белый фон карточки с высокой плотностью (95%), чтобы отрываться от узора фона
    override val cardItemTrue: Color = Color(0xE65AD47D),        // Плотный белый с высокой прозрачностью
    override val cardItemFalse: Color = Color(0xE6D06767),

    override val textAlarm: Color = Color.Yellow,     // Темно-оранжевый для цифр времени
    override val chekBoxOff: Color =  Color.Black,
    override val chekBoxOn: Color = Color.Black,
    override val chekBoxTint: Color = Color.White,

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color(0xFF1C1D22),         // Черная строгая кнопка добавления задач
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.Black,
    override val iconTint: Color = Color.Black,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color.Yellow,       // Премиальная глянцево-черная корона вместо желтой
    override val tintPremiumOff: Color = Color(0xFFB0B5C0),
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFFFFFFFF),    // Белоснежный фон системных окон и диалогов
    override val backgroundStart: DrawableResource = Res.drawable.background_mramor,
    override val backgroundDrawer: DrawableResource = Res.drawable.background_mramor,
    override val borderCardMenuItem: Color = Color(0xCCCFD2D9),  // Мягкая серебристая рамка пунктов меню
    override val cardMenuItem: Color = Color(0xF2F4F5F7),        // Светлая подложка для пунктов настроек
    override val colorCalendarDaySelect: Color = Color.White,
    override val backgroundCalendar: Color = Color(0x80B4B5B6),
    override val tintCheckBoxSubItemOff: Color = textColor
) : Theme


 data class ThemePoison (

     override val textColor: Color = Color(0xFFE0F7FA),
     // Очень светлый, почти белый мятный оттенок для максимального контраста
     override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
     override val iconDel: ImageVector = Icons.Default.Delete,
     // Блокнот
     override val noteBookBackground: Color = Color(0xCC0B140F),
     override val noteBookBorder: Color = Color(0x8000FF87),

     // Список дел
     override val tintAlarmOn: Color = Color(0xFF00FF87),
     override val tintAlarmOff: Color = Color(0xFF6F6F6F),
     override val textDesc: Color = Color(0xFFA7FFEB),


     override val cardItemBorderAlarm: Color = Color(0xFF00FF87),
     override val cardItemBorderTrue: Color = Color(0x8000FF87),
     override val cardItemBorderFalse: Color = Color(0x99D50000),

     override val cardItemAlarm: Color = Color(0xEC09140E),
     override val cardItemTrue: Color = Color(0xE1014E29),
     override val cardItemFalse: Color = Color(0xF23D0101),

     override val textAlarm: Color = Color(0xFF00FF87),           // Светящийся зеленый текст времени
     override val chekBoxOff: Color = Color.White,
     override val chekBoxOn: Color = Color.White,
     override val chekBoxTint: Color = Color.Black,       // Яркий токсичный чекбокс

     override val iconImage: ImageVector = Icons.Default.Image,
     override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
     override val iconAddTint: Color = Color(0xFFE2FFE9),
     override val iconDelItem: ImageVector = Icons.Default.Delete,
     override val iconDelTint: Color = Color.White,
     override val iconTint: Color = Color.White,
     override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
     override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
     override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
     override val tintPremiumOn: Color = Color(0xFFFFEB3B),       // Золотая корона (как золотые элементы на этикетке Poison)
     override val tintPremiumOff: Color = Color.White,
     override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
     override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
     override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
     override val backgroundDialog: Color = Color(0xFF1A231F),    // Очень темный графитово-зеленый цвет для окон
     override val backgroundStart: DrawableResource = Res.drawable.background_poison, // Оставлено как есть
     override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_poison, // Оставлено как есть
     override val borderCardMenuItem: Color = Color(0x8000FF87),
     override val cardMenuItem: Color = Color(0xE6121D15),         // Фон меню цвета старого темного чугуна
     override val colorCalendarDaySelect: Color = Color.Black,
     override val backgroundCalendar: Color = noteBookBackground,
     override val tintCheckBoxSubItemOff: Color = cardItemBorderFalse,

     ) : Theme

 data class ThemeVolcanic (
     override val textColor: Color = Color(0xFFFFE0B2),           // Мягкий тепло-оранжевый (цвет остывающего пепла)

     // Блокнот (Календарь)
     override val noteBookBackground: Color = Color(0xDC0F0E12), // Плотный базальтово-черный цвет (86% плотности)
     override val noteBookBorder: Color = Color(0xFFFF6D00),     // Насыщенный огненно-оранжевый контур магмы
     override val iconMicro: ImageVector = Icons.Default.Mic, // Тут стоит использовать темную иконку
     override val iconDel: ImageVector = Icons.Default.Delete,

     // Список дел
     override val tintAlarmOff: Color = Color(0xFF90A4AE),       // Активный будильник горит цветом раскаленной лавы
     override val tintAlarmOn: Color = Color(0xFFFF6D00),       // Потухший уголь (выключен)
     override val textDesc: Color = Color(0xFF9E9A9F),           // Дымчато-серый пепельный для описания задач


     override val cardItemBorderAlarm: Color = Color(0xFFFF3D00),
     override val cardItemBorderTrue: Color = Color(0xFF00E676),
     override val cardItemBorderFalse: Color = Color(0xFFFF1744),

     override val cardItemAlarm: Color = Color(0xDA0B0A0D),
     override val cardItemFalse: Color = Color(0xFF3D0101),
     override val cardItemTrue: Color = Color(0xFF014E29),


     override val textAlarm: Color = Color(0xFFFF6D00),           // Огненный текст времени
     override val chekBoxOff: Color = Color.White,
     override val chekBoxOn: Color = Color.White,
     override val chekBoxTint: Color = Color.Black,       // Чекбокс светится оранжевым пламенем

     override val iconImage: ImageVector = Icons.Default.Image,
     override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
     override val iconAddTint: Color = Color(0xFFFFE0B2),         // Кнопка добавления горит цветом магмы
     override val iconDelItem: ImageVector = Icons.Default.Delete,
     override val iconDelTint: Color = Color.White,
     override val iconTint: Color = Color.White,
     override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
     override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
     override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
     override val tintPremiumOn: Color = Color(0xFFFF6D00),       // Огненная корона премиума
     override val tintPremiumOff: Color = Color.White,
     override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
     override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
     override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
     override val backgroundDialog: Color = Color(0xFF0B0A0D),    // Базальтово-черный фон системных окон и диалогов
     override val backgroundStart: DrawableResource = Res.drawable.background_vulcan,
     override val backgroundDrawer: DrawableResource = Res.drawable.background_drawer_vulcan,
     override val borderCardMenuItem: Color = Color(0x80FF6D00),  // Рамка меню цвета лавовой реки
     override val cardMenuItem: Color = Color(0xE60F0E12),        // Плотный фон пунктов настроек
     override val colorCalendarDaySelect: Color = Color.Black,
     override val backgroundCalendar: Color = noteBookBackground      // Черная цифра внутри огненного круга выделения
     ,
     override val tintCheckBoxSubItemOff: Color = cardItemBorderFalse
 ) : Theme


data class ThemePlatinum(
    override val textColor: Color = Color(0xFF1F1F1F),          // Тёмно‑серый текст — мягче чёрного, но читаемо на светлом
    override val iconMicro: ImageVector = Icons.Default.Mic,
    override val iconDel: ImageVector = Icons.Default.Delete,

    override val noteBookBackground: Color = Color(0xFFF5F7FA), // Очень светлый серо‑голубой — «чистый лист»
    override val noteBookBorder: Color = Color(0xFFD1D5DB),     // Светло‑серый контур — нейтральный, не отвлекает

    override val tintAlarmOn: Color = Color.Yellow,        // Активный будильник глубокого стального/графитового цвета
    override val tintAlarmOff: Color = Color.Black,       // Выключенный будильник (светло-стальной)
    override val textDesc: Color = Color.Black,           // Сдержанный графитовый для описания задач


    override val cardItemBorderAlarm: Color = Color.Yellow, // Строгий графитовый бордюр для важных задач
    override val cardItemBorderTrue: Color = Color(0xFF2E7D32),  // Спокойный благородный зеленый бордюр (без неона)
    override val cardItemBorderFalse: Color = Color(0xFFC62828), // Сдержанный рубиново-красный бордюр

    override val cardItemAlarm: Color = Color(0xF2B4B5B6),       // Чистый белый фон карточки с высокой плотностью (95%), чтобы отрываться от узора фона
    override val cardItemTrue: Color = Color(0xE65AD47D),        // Плотный белый с высокой прозрачностью
    override val cardItemFalse: Color = Color(0xE6D06767),

    override val textAlarm: Color = Color.Yellow,     // Темно-оранжевый для цифр времени
    override val chekBoxOff: Color =  Color.Black,
    override val chekBoxOn: Color = Color.Black,
    override val chekBoxTint: Color = Color.White,          // Стальной чекбокс — сдержанно, но заметно

    override val iconImage: ImageVector = Icons.Default.Image,
    override val iconAdd: ImageVector = Icons.Default.AddCircleOutline,
    override val iconAddTint: Color = Color.Black,         // Стальной плюс — точка фокуса без агрессии
    override val iconDelItem: ImageVector = Icons.Default.Delete,
    override val iconDelTint: Color = Color.Black,         // Приглушённый серый для удаления — не кричит
    override val iconTint: Color = Color.Black,
    override val iconDrawerEveryday: ImageVector = Icons.AutoMirrored.Filled.Assignment,
    override val iconDrawerShare: ImageVector = Icons.Default.GroupAdd,
    override val iconDrawerPremium: ImageVector = Icons.Default.WorkspacePremium,
    override val tintPremiumOn: Color = Color(0xFFFFD700),       // Тёплый золотой для премиум — мягкий акцент на холодной палитре
    override val tintPremiumOff: Color = Color.Black,
    override val iconDrawerUpdateOff: ImageVector = Icons.Default.SystemUpdate,
    override val iconDrawerUpdateOn: ImageVector = Icons.Default.Upgrade,
    override val iconDrawerSettigs: ImageVector = Icons.Default.Settings,
    override val backgroundDialog: Color = Color(0xFFFFFFFF),    // Белый фон для диалогов
    override val backgroundStart: DrawableResource = Res.drawable.background_platina, // Твой светлый фон
    override val backgroundDrawer: DrawableResource = Res.drawable.background_platina,
    override val borderCardMenuItem: Color = Color(0xFFE5E7EB),
    override val cardMenuItem: Color = Color(0xFFFFFFFF),
    override val colorCalendarDaySelect: Color = Color(0xFFFFFFFF), // Стальной для выбранного дня
    override val backgroundCalendar: Color = noteBookBackground,
    override val tintCheckBoxSubItemOff: Color = textColor,
) : Theme
