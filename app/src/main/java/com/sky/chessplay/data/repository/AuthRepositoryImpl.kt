package zh.qingzi.portaljob.data.repository


import com.sky.chessplay.data.remote.dto.request.LoginRequest
import com.sky.chessplay.data.remote.service.AuthService
import com.sky.chessplay.domain.model.auth.User
import com.sky.chessplay.domain.repository.AuthRepository
import com.sky.chessplay.mapper.toDomain
import com.sky.chessplay.remote.dto.request.RegisterRequest
import javax.inject.Inject
class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): User {
        return service.register(request).toDomain()
    }

    override suspend fun login(request: LoginRequest): User {
        return service.login(request).toDomain()
    }

    override suspend fun getMe(): User {
        return service.getMe().toDomain()
    }
}