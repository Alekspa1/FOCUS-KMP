package com.dragon.shared.data.repostitory

import domain.repostirory.VoiceIntentRepository

class DesktopVoiceIntentImpl : VoiceIntentRepository {
    override suspend fun openVoice(): Result<String> {
        return Result.failure(UnsupportedOperationException("Голосовой ввод пока не поддерживается на Десктопе"))
    }
}