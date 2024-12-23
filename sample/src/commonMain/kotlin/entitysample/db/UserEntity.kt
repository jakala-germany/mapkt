package entitysample.db

import com.github.yanneckreiss.kconmapper.annotations.KConMap
import entitysample.dto.CreateUserDTO
import entitysample.dto.UpdateUserDTO
import entitysample.model.Address


@KConMap(mapTo = [CreateUserDTO::class, UpdateUserDTO::class])
data class UserEntity(
    val name: String,
    val address: Address,
)

fun a() {
    val a = UserEntity(name = "a", address = Address("a", "a", 1, "a"))
    a.toUpdateUserDTO()
}