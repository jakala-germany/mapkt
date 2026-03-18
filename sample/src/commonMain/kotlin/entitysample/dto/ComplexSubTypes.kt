package entitysample.dto

import com.jakala.mapkt.annotations.MapKt

@MapKt(mapTo = ComplexSubType::class)
data class ComplexSubTypeRemote(
    val name: String,
    val address: AddressRemote?,
    val addresses: List<AddressRemote>?,
)

@MapKt(mapTo = ComplexSubType::class)
data class ComplexSubTypeLocal(
    val name: String,
    val address: AddressLocal?,
    val addresses: List<AddressLocal>?,
)

data class ComplexSubType(
    val name: String,
    val address: Address?,
    val addresses: List<Address>?,
)

@MapKt(mapTo = Address::class)
data class AddressRemote(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String,
)

@MapKt(mapTo = Address::class)
data class AddressLocal(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String,
)

data class Address(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String,
)