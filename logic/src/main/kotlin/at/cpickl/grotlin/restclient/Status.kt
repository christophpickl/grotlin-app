package at.cpickl.grotlin.restclient

public enum class Status(public val code: Int, public val label: String) {
    class object {
        fun byCode(searchCode: Int): Status {
            val found = Status.values().firstOrNull { it.code == searchCode }
            if (found == null) {
                throw IllegalArgumentException("Unsupported status code ${searchCode}!")
            }
            return found
        }
    }
    _200_OK: Status(200, "Ok")
    _204_NO_CONTENT: Status(204, "No Content")
    _401_UNAUTHORIZED: Status(401, "Unauthorized") // who are you?
    _403_FORBIDDEN: Status(403, "Forbidden") // i know you, but you are not allowed!
    _404_NOT_FOUND: Status(404, "Not Found")
    _500_INTERNAL_SERVER_ERROR: Status(500, "Internal Server Error")
}

