package com.vauthenticator.server.password

import kotlin.random.Random

data class PasswordCriteria(
    val charactersSize: Int,
    val specialCharactersSize: Int,
    val numberCharactersSize: Int
) {
    fun size() = charactersSize + specialCharactersSize + numberCharactersSize
}

enum class AlphabetFamily(val family: Int) {
    SPECIAL_CHARACTER_ALPHABET_FAMILY(0),
    NUMBER_ALPHABET_FAMILY(1),
    ALPHABET_FAMILY(2),
}

enum class Alphabet(
    val character: CharArray,
    val family: AlphabetFamily
) {
    SPECIAL_CHARACTER_ALPHABET(specialCharactersAlphabet, AlphabetFamily.SPECIAL_CHARACTER_ALPHABET_FAMILY),
    NUMBER_ALPHABET(numberAlphabet, AlphabetFamily.NUMBER_ALPHABET_FAMILY),
    ALPHABET(alphabet, AlphabetFamily.ALPHABET_FAMILY),
}

@JvmInline
value class Password(val content: String)

class PasswordGenerator(private val passwordCriteria: PasswordCriteria) {

    private fun nextCharacterFamily(): Alphabet {
        val nextInt = Random.nextInt(0, 2)

        return when (nextInt) {
            0 -> Alphabet.SPECIAL_CHARACTER_ALPHABET
            1 -> Alphabet.ALPHABET
            2 -> Alphabet.ALPHABET
            else -> {
                TODO()
            }
        }
    }

    fun generate(): String {
        var charactersSize = passwordCriteria.charactersSize
        var specialCharactersSize = passwordCriteria.specialCharactersSize
        var numberCharactersSize = passwordCriteria.numberCharactersSize

        val random = Random
        val builder = StringBuilder()
        for (index in 1..passwordCriteria.size()) {
            val nextCharacterFamily = nextCharacterFamily()

            builder.append(nextCharacterFamily.character[random.nextInt(nextCharacterFamily.character.size - 1)])
        }

        return builder.toString()
    }
}

val specialCharactersAlphabet = charArrayOf(
    '!',
    '@',
    '#',
    '$',
    '%',
    '^',
    '&',
    '*',
    '(',
    ')',
    '-',
    '_',
    '=',
    '+',
    '[',
    ']',
    '{',
    '}',
    ';',
    ':',
    '"',
    '\'',
    '\\',
    '|',
    '~',
    '`',
    ',',
    '<',
    '.',
    '>',
    '/',
    '?',
)

val numberAlphabet = charArrayOf(
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
)

val alphabet = charArrayOf(
    'A',
    'B',
    'C',
    'D',
    'E',
    'F',
    'G',
    'H',
    'I',
    'J',
    'K',
    'L',
    'M',
    'N',
    'O',
    'P',
    'Q',
    'R',
    'S',
    'T',
    'U',
    'V',
    'W',
    'X',
    'Y',
    'Z',

    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'j',
    'k',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'u',
    'v',
    'w',
    'x',
    'y',
    'z'
)
