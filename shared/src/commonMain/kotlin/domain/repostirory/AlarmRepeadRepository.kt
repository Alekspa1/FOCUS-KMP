package domain.repostirory


interface AlarmRepeadRepository {
    suspend fun alarmRepead(id: Int, repeat : Boolean = false,sendMessage : (String) -> Unit = {})

}
