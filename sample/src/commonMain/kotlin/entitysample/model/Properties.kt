package entitysample.model

import com.jakala.mapkt.annotations.MapKt
import com.jakala.mapkt.annotations.PropertyMapping

@MapKt(
    mapTo = RemoteSomeProperty::class,
    aliases = [
        PropertyMapping(source = "name", target = "fullName"),
    ],
    ignores = ["newValue"],
)
@MapKt(
    mapTo = LocalSomeProperty::class,
    ignores = ["dbId", "newValue"],
)
data class SomeProperty(
    val name: String,
    val value: String,
    val newValue: String,
)

data class RemoteSomeProperty(
    val fullName: String,
    val value: String,
)

data class LocalSomeProperty(
    val dbId: Long,
    val name: String,
    val value: String,
)