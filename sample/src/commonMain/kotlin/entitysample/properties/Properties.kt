package entitysample.properties

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.MapKtProperty

@MapKt(mapTo = [RemoteSomeProperty::class])
data class SomeProperty(
    @MapKtProperty(aliases = ["fullName"])
    val name: String,
    val value: String,
)

data class RemoteSomeProperty(
    @MapKtProperty(aliases = ["name"])
    val fullName: String,
    val value: String,
)
