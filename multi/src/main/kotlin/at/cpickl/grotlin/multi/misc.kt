package at.cpickl.grotlin.multi

fun String.toIntOrThrow(toThrow: Exception): Int {
    try {
        return toInt()
    } catch(e: NumberFormatException) {
        throw toThrow
    }
}
