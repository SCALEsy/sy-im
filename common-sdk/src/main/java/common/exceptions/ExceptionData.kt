package common.exceptions

data class ExceptionData(val code: Int, val msg: String) {

    fun toException(): ImException {
        return ImException(code, msg)
    }
}