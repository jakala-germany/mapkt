package entitysample.db

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.toUpdateUserDTO
import entitysample.dto.CreateUserDTO
import entitysample.dto.UpdateUserDTO
import entitysample.model.Address

@MapKt(mapTo = [CreateUserDTO::class, UpdateUserDTO::class])
data class UserEntity(
    val name: String,
    val address: Address,
)

fun a() {
    val a = UserEntity(name = "a", address = Address("a", "a", 1, "a"))
    a.toUpdateUserDTO()
}