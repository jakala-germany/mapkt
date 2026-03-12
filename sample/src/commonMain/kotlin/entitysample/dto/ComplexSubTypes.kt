package entitysample.dto

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.toComplexSubType

@MapKt(mapTo = [ComplexSubType::class])
data class ComplexSubTypeRemote(
    val name: String,
    val address: AddressRemote?,
    val addresses: List<AddressRemote>?,
)

@MapKt(mapTo = [ComplexSubType::class])
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

@MapKt(mapTo = [Address::class])
data class AddressRemote(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String,
)

@MapKt(mapTo = [Address::class])
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

fun test() {
    val complexSubTypeRemote =
        ComplexSubTypeRemote(
            name = "name",
            address =
                AddressRemote(
                    street = "street",
                    zip = "zip",
                    houseNumber = 1,
                    city = "city",
                ),
            addresses =
                listOf(
                    AddressRemote(
                        street = "street",
                        zip = "zip",
                        houseNumber = 1,
                        city = "city",
                    ),
                ),
        )
    val complexSubTypeLocal =
        ComplexSubTypeLocal(
            name = "name",
            address =
                AddressLocal(
                    street = "street",
                    zip = "zip",
                    houseNumber = 1,
                    city = "city",
                ),
            addresses =
                listOf(
                    AddressLocal(
                        street = "street",
                        zip = "zip",
                        houseNumber = 1,
                        city = "city",
                    ),
                ),
        )
    complexSubTypeRemote.toComplexSubType()
    complexSubTypeLocal.toComplexSubType()
}