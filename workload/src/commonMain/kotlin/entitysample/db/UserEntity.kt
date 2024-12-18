package entitysample.db

import com.github.yanneckreiss.kconmapper.annotations.KConMapper
import de.yanneckreiss.kconmapper.generated.toUserEntity
import entitysample.dto.CreateUserDTO
import entitysample.dto.UpdateUserDTO
import entitysample.model.Address


@KConMapper(
    fromClasses = [CreateUserDTO::class, UpdateUserDTO::class],
    toClasses = [CreateUserDTO::class, UpdateUserDTO::class]
)
data class UserEntity(
    val uid: String = "SOME_UUID",
    val name: String,
    val address: Address,
)

fun a() {
    val a = CreateUserDTO("name", Address("street", "zip", 1, "city"));
    a.toUserEntity()
}