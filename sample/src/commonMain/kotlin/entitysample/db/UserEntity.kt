package entitysample.db

import com.jakala.mapkt.annotations.MapKt
import entitysample.dto.CreateUserDTO
import entitysample.dto.UpdateUserDTO
import entitysample.model.Address

@MapKt(mapTo = CreateUserDTO::class)
@MapKt(mapTo = UpdateUserDTO::class)
data class UserEntity(
    val name: String,
    val address: Address,
)