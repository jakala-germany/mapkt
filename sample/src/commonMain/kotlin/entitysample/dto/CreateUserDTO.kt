package entitysample.dto

import entitysample.model.Address

data class CreateUserDTO(
    val name: String,
    val address: Address,
)