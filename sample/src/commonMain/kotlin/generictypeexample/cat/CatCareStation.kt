package generictypeexample.cat

import com.github.yanneckreiss.kconmapper.annotations.KConMapperProperty

class CatCareStation(

    @KConMapperProperty(aliases = ["animalOne"])
    val catOne: Cat,

    @KConMapperProperty(aliases = ["animalTwo"])
    val catTwo: Cat,

)