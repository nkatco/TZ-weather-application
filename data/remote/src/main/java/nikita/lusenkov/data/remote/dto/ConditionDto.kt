package nikita.lusenkov.data.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ConditionDto(
    val text: String,
    val icon: String,
    val code: Int
)