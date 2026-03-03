import OTPVerificationRequest
import OTPVerificationResponse
import ResendOTPResponse
import SignInRequest
import com.example.myfirstproject.data.model.SignInResponse
import com.example.myfirstproject.data.model.SignUpRequest
import com.example.myfirstproject.data.model.SignUpResponse
import com.example.shoeshop.data.model.ChangePasswordRequest
import com.example.shoeshop.data.model.ChangePasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT


const val API_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndrdmhybHF3YXh6aXRrY2prZmloIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkwNzgxNDQsImV4cCI6MjA4NDY1NDE0NH0.i5AAmY29ujNmXLXZiXXpYFH27SN7441TQct3LzizOn4"
interface UserManagementService {

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @Headers(
        "apikey: $API_KEY",
        "Content-Type: application/json"
    )
    @POST("auth/v1/logout")
    suspend fun logout(): Response<Unit>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Content-Type: application/json",
        "Prefer: return=minimal"
    )
    @POST("auth/v1/verify")
    suspend fun verifyOTP(@Body request: OTPVerificationRequest): Response<OTPVerificationResponse>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Content-Type: application/json",
        "Prefer: return=minimal"
    )
    @POST("rest/v1/rpc/resend_otp")
    suspend fun resendOTP(@Body request: Map<String, String>): Response<ResendOTPResponse>

    @Headers(
        "apikey: ${API_KEY}",
        "Content-Type: application/json"
    )
    @POST("auth/v1/recover")
    suspend fun recoverPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @Headers(
        "apikey: ${API_KEY}",
        "Content-Type: application/json"
    )
    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") token: String, // Bearer токен пользователя
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>


}