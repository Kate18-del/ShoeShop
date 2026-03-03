

import com.google.gson.annotations.SerializedName

data class ForgotPasswordResponse(
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("message")
    val message: String? = null
)
