package domain.repostirory

interface VoiceIntentRepository {
   suspend fun openVoice() : Result<String>
}