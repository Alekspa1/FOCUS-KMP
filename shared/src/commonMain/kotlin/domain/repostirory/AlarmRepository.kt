package domain.repostirory

import data.room.model.Item

interface AlarmRepository {
    fun createAlarm(item: Item, repeat: Boolean = false)
    fun deleteAlarm(id: Int)
}
