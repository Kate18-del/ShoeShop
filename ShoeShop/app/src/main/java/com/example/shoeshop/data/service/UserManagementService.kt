import ResendOTPResponse
import SignInRequest
import com.example.myfirstproject.data.model.SignInResponse
import com.example.myfirstproject.data.model.SignUpRequest
import com.example.myfirstproject.data.model.SignUpResponse
import com.example.shoeshop.data.model.AddToCartRequest
import com.example.shoeshop.data.model.Cart
import com.example.shoeshop.data.model.ChangePasswordRequest
import com.example.shoeshop.data.model.ChangePasswordResponse
import com.example.shoeshop.data.model.CreateOrderItemRequest
import com.example.shoeshop.data.model.CreateOrderRequest
import com.example.shoeshop.data.model.Favorite
import com.example.shoeshop.data.model.Order
import com.example.shoeshop.data.model.OrderItem
import com.example.shoeshop.data.model.OrderStatus
import com.example.shoeshop.data.model.Payment
import com.example.shoeshop.data.model.UpdateCartRequest
import com.example.shoeshop.data.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.*

const val API_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZneWhydXlsYmlmdmx4bHl3YWJ4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzI1NDc3NTgsImV4cCI6MjA4ODEyMzc1OH0.JoinvvEiL1IgvuM3-5nGJLDttWKUikEmrCb3biweAQw"

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
        "apikey: ${API_KEY}",
        "Content-Type: application/json"
    )
    @POST("auth/v1/verify")
    suspend fun verifyOtp(@Body verifyOtpRequest: VerifyOtpRequest): Response<VerifyOtpResponse>

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
    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshToken(
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>

    @Headers(
        "apikey: ${API_KEY}",
        "Content-Type: application/json"
    )
    @PUT("auth/v1/user")
    suspend fun changePassword(
        @Header("Authorization") authorization: String, // Bearer токен
        @Body changePasswordRequest: ChangePasswordRequest
    ): Response<ChangePasswordResponse>


    // ===== ПРОФИЛЬ =====

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Query("user_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Profile>>

    @POST("rest/v1/profiles")
    suspend fun createProfile(
        @Body profile: Profile,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Profile>>


    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("user_id") filter: String,
        @Body updates: UpdateProfileRequest,  // ← Используем специальный data class
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Profile>>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfileById(
        @Query("id") filter: String,
        @Body updates: UpdateProfileRequest,  // ← Используем специальный data class
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Profile>>

    // ===== CATALOG METHODS =====

    @GET("rest/v1/categories")
    suspend fun getCategories(
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Category>>

    @GET("rest/v1/products")
    suspend fun getProducts(
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Product>>

    @GET("rest/v1/products")
    suspend fun getProductsByCategory(
        @Query("category_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Product>>


    // ===== FAVORITE METHODS =====

    @GET("rest/v1/favourite")
    suspend fun getFavorites(
        @Query("user_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Favorite>>

    @POST("rest/v1/favourite")
    suspend fun addToFavorite(
        @Body favorite: Map<String, String>,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Favorite>>

    @DELETE("rest/v1/favourite")
    suspend fun removeFromFavorite(
        @Query("id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<Unit>


    @GET("rest/v1/products")
    suspend fun getProductById(
        @Query("id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Product>>

    // ===== CART METHODS =====

    @GET("rest/v1/cart")
    suspend fun getCart(
        @Query("user_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Cart>>

    @POST("rest/v1/cart")
    suspend fun addToCart(
        @Body cartItem: AddToCartRequest,  // ← Используем импортированный класс
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Cart>>

    @PATCH("rest/v1/cart")
    suspend fun updateCartItem(
        @Query("id") filter: String,
        @Body updates: UpdateCartRequest,  // ← Используем импортированный класс
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Cart>>

    @DELETE("rest/v1/cart")
    suspend fun removeFromCart(
        @Query("id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<Unit>

    // ===== ORDER METHODS =====

    @GET("rest/v1/orders")
    suspend fun getOrders(
        @Query("user_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Order>>

    @POST("rest/v1/orders")
    suspend fun createOrder(
        @Body order: CreateOrderRequest,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<Order>>

    @POST("rest/v1/orders_items")
    suspend fun createOrderItems(
        @Body orderItems: List<CreateOrderItemRequest>,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<OrderItem>>

// ===== PAYMENT METHODS =====

    @GET("rest/v1/payments")
    suspend fun getPayments(
        @Query("user_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Payment>>

// ===== ORDER METHODS (дополнительные) =====

    @GET("rest/v1/orders")
    suspend fun getUserOrders(
        @Query("user_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY,
        @Header("Prefer") prefer: String = "order=created_at.desc"
    ): Response<List<Order>>

    @GET("rest/v1/orders_items")
    suspend fun getOrderItems(
        @Query("order_id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<OrderItem>>

    @GET("rest/v1/order_status")
    suspend fun getOrderStatuses(
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<OrderStatus>>

    @PATCH("rest/v1/orders")
    suspend fun updateOrderStatus(
        @Query("id") filter: String,
        @Body updates: Map<String, Any>,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Order>>

    // Для получения одного заказа с фильтром по id
    @GET("rest/v1/orders")
    suspend fun getOrderById(
        @Query("id") filter: String,
        @Header("Authorization") authorization: String,
        @Header("apikey") apiKey: String = API_KEY
    ): Response<List<Order>>

}


