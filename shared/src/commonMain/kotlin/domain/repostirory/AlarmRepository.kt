package domain.repostirory

import data.room.model.Item

interface AlarmRepository {
    fun createAlarm(item: Itemr,epeat: Boolean = false)
    fun deleteAlarm(id: Int)
}
