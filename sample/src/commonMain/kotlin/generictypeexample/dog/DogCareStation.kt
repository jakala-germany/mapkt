package generictypeexample.dog

import com.github.yanneckreiss.kconmapper.annotations.KConMapperProperty

class DogCareStation(

    @KConMapperProperty(aliases = ["animalOne"])
    val dogOne: Dog,

    @KConMapperProperty(aliases = ["animalTwo"])
    val dogTwo: Dog,

)