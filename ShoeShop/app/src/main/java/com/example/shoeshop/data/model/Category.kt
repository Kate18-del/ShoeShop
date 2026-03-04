import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val title: String,
    var isSelected: Boolean = false
)