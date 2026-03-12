package entitysample.dto

import entitysample.model.Address

data class UpdateUserDTO(
    val name: String,
    val address: Address,
)