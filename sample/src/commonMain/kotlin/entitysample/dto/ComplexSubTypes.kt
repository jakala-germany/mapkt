package entitysample.dto

import com.github.yanneckreiss.kconmapper.annotations.KConMap

@KConMap(mapTo = [ComplexSubType::class])
data class ComplexSubTypeRemote(
    val name: String,
    val address: AddressRemote
)

@KConMap(mapTo = [ComplexSubType::class])
data class ComplexSubTypeLocal(
    val name: String,
    val address: AddressLocal
)

data class ComplexSubType(
    val name: String,
    val address: Address
)

@KConMap(mapTo = [Address::class])
data class AddressRemote(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String
)

@KConMap(mapTo = [Address::class])
data class AddressLocal(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String
)

data class Address(
    val street: String,
    val zip: String,
    val houseNumber: Int,
    val city: String
)

fun test() {
    val complexSubTypeRemote = ComplexSubTypeRemote(
        name = "name",
        address = AddressRemote(
            street = "street",
            zip = "zip",
            houseNumber = 1,
            city = "city"
        )
    )
    val complexSubTypeLocal = ComplexSubTypeLocal(
        name = "name",
        address = AddressLocal(
            street = "street",
            zip = "zip",
            houseNumber = 1,
            city = "city"
        )
    )
    complexSubTypeRemote.toComplexSubType()
    complexSubTypeLocal.toComplexSubType()
}