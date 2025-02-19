package com.memorygame.main
import androidx.lifecycle.ViewModel
import kotlin.random.Random


class MemoryGameViewModel: ViewModel() {


    // =================================================================================================

    private fun getASCIICapitalLetter(characterInt :Int): Char {

        // Ensure the range is within the valid ASCII capital letter range (65-90)
        if (characterInt < 65 || characterInt > 90) {
            throw IllegalArgumentException("Range must be between 65 (A) and 90 (Z)")
        }

        return (characterInt.toChar())
    }


    // =================================================================================================


    fun getCharacterArray(rows :Int, columns :Int): Array<Char> {

        var totalPositions :Int = rows * columns / 2

        if(totalPositions > 25) {
            totalPositions = 25
        }

        var characterInt = 65

        val characterList   : Array<Char> = Array(rows * columns) { '*' }
        val usedPositions   : MutableList<Int>          = mutableListOf()

        while(totalPositions > 0 && characterInt <= 90) {

            val character   :Char = getASCIICapitalLetter(characterInt)
            var position1   :Int
            var position2   :Int

            // Ensure that position1 and position2 are unique and not used
            do {
                position1 = Random.nextInt(0, rows * columns)
            } while (usedPositions.contains(position1))

            usedPositions.add(position1) // Mark position1 as used

            do {
                position2 = Random.nextInt(0, rows * columns)
            } while (usedPositions.contains(position2) || position2 == position1)

            usedPositions.add(position2) // Mark position2 as used

            characterList[position1] = character
            characterList[position2] = character

            characterInt ++
            totalPositions --
        }

        return characterList
    }

    // =================================================================================================


}