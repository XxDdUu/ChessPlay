package zh.qingzi.portaljob.data.repository


import android.util.Log
import com.sky.chessplay.data.local.datastore.TokenManager
import com.sky.chessplay.data.remote.dto.request.GoogleLoginRequest
import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.data.remote.service.AuthService
import com.sky.chessplay.domain.model.auth.User
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.mapper.toDomain
import com.sky.chessplay.remote.dto.request.RegisterRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<User> {
        return try {
            val res = service.login(request)

            res.token?.let { tokenManager.saveToken(it) }

            Result.success(res.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<User> {
        return try {
            val res = service.register(request)

            res.token?.let { tokenManager.saveToken(it) }

            Result.success(res.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMe(): Result<User> {
        return try {
            val token = tokenManager.getToken()

            if (token == null) {
                return Result.failure(Exception("No token"))
            }

            val res = service.getMe(token)
            val user = res.toDomain()

            Result.success(user)

        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "getMe failed", e)
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val response = service.googleLogin(
                GoogleLoginRequest(idToken)
            )

            response.token?.let {
                tokenManager.saveToken(it)
            }

            Result.success(response.toDomain())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
