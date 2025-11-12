package nikita.lusenkov.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConditionDto(
    val text: String,
    val icon: String,
    val code: Int
)