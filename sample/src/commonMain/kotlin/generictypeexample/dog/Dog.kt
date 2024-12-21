package generictypeexample.dog

import generictypeexample.Animal

abstract class Dog(
    override val name: String,
    override val color: String,
    val barksOften: Boolean
) : Animal
