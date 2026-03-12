package entitysample

import com.jakala.mapkt.annotations.MapKt

data class SuperModel(
    val name: String,
)

@MapKt(mapTo = [SuperModel::class])
data class LocalSuperModel(
    val name: String,
)

@MapKt(mapTo = [SuperModel::class])
data class RemoteSuperModel(
    val name: String,
)

sealed class SuperNestedModel {
    data class Inner1(
        val name: String,
    ) : SuperNestedModel()

    data class Inner2(
        val name: String,
    ) : SuperNestedModel()

    enum class InnerEnum {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
    }
}

@MapKt(mapTo = [SuperNestedModel::class])
sealed class LocalNestedModel {
    @MapKt(mapTo = [SuperNestedModel.Inner1::class])
    data class Inner1(
        val name: String,
    ) : LocalNestedModel()

    @MapKt(mapTo = [SuperNestedModel.Inner2::class])
    data class Inner2(
        val name: String,
    ) : LocalNestedModel()

    @MapKt(mapTo = [SuperNestedModel.InnerEnum::class])
    enum class InnerEnum {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
    }
}

sealed class NoppiDoppi

@MapKt(mapTo = [NoppiDoppi::class])
sealed class LocalNoppiDoppi

data class Nopi(
    val name: String,
) : NoppiDoppi()

data class Nopi2(
    val name: String,
) : NoppiDoppi()

@MapKt(mapTo = [Nopi::class])
data class LocalNopi(
    val name: String,
) : LocalNoppiDoppi()

@MapKt(mapTo = [Nopi2::class])
data class LocalNopi2(
    val name: String,
) : LocalNoppiDoppi()