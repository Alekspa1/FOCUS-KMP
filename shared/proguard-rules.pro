# ==============================================================================
# БАЗОВЫЕ НАСТРОЙКИ KOTLIN & COROUTINES
# ==============================================================================
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Защита внутренних механизмов корутин и метаданных Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.sequences.**
-dontwarn kotlinx.coroutines.**

# ==============================================================================
# SELECTION & KTOR (Сетевой слой и сериализация)
# ==============================================================================
# Защита ваших data-классов, помеченных @Serializable
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    *** Companion;
}
-keep class kotlinx.serialization.json.** { *; }

# Ktor: предотвращает вырезание внутренних движков и логгера
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

# ==============================================================================
# KOIN (Внедрение зависимостей)
# ==============================================================================
# Koin использует рефлексию для поиска конструкторов/ViewModel. Не даем их переименовывать.
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepnames class * extends androidx.lifecycle.ViewModel
-dontwarn io.insertkoin.**

# ==============================================================================
# ANDROIDX ROOM & SQLITE (База данных)
# ==============================================================================
# Room использует сгенерированные классы с суффиксом _Impl
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-dontwarn androidx.room.**
-dontwarn androidx.sqlite.**
-keep class * implements androidx.sqlite.db.SupportSQLiteOpenHelper$Factory

# ==============================================================================
# YANDEX MOBILE ADS (Реклама)
# ==============================================================================
# Яндекс требует жесткого сохранения интерфейсов и нативных методов
-dontwarn com.yandex.mobile.ads.**
-keep class com.yandex.mobile.ads.** { *; }
# Для работы WebView внутри рекламы
-keepattributes JavascriptInterface

# ==============================================================================
# RUSTORE SDK (Обновления и платежи)
# ==============================================================================
# RuStore SDK используют рефлексию для IPC (межпроцессного взаимодействия) и биллинга
-dontwarn ru.rustore.sdk.**
-keep class ru.rustore.sdk.** { *; }

# ==============================================================================
# MULTIPLATFORM SETTINGS (Хранилище Key-Value)
# ==============================================================================
-dontwarn com.russhwolf.settings.**

# ==============================================================================
# COIL 3 (Загрузка картинок)
# ==============================================================================
-dontwarn coil3.**

# ==============================================================================
# КОНКРЕТНАЯ ЗАЩИТА ДЛЯ KOIN INJECTION (Activity и Ресиверы)
# ==============================================================================

# Защищаем конструкторы всех классов, которые могут быть созданы системой или Koin
-keepclassmembers class * {
    public <init>(...);
}

# Если вы используете inject() или viewModel() внутри Activity/Fragment/Receiver
-keepclassmembers class * extends android.content.BroadcastReceiver {
    public <init>();
}
-keepclassmembers class * extends android.app.Activity {
    public <init>();
}

# Не даем R8 удалять внутренние интерфейсы Koin, отвечающие за впрыск зависимостей
-keep class io.insertkoin.** { *; }
-keep interface io.insertkoin.** { *; }