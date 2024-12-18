package generictypeexample.cat

import generictypeexample.Animal


abstract class Cat(
    override val name: String,
    override val color: String,
    val meowsOften: Boolean
) : Animal
