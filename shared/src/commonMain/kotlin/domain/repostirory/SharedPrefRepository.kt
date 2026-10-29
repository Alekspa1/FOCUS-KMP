package domain.repostirory

interface SharedPrefRepository {

    fun saveTextNoteBook(value: String)

    fun loadTextNoteBook() : String

    fun getPremium(): Boolean

    fun savePremium(flag: Boolean)

    fun saveStateService(value: String)

    fun loadStateService() : String
}