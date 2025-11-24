package store.view

class CommonOutput {

    fun printMessage(message: String) {
        println(message)
    }

    fun printInline(message: String) {
        print(message)
    }

    fun showError(message: String) {
        println("[ERROR] $message")
    }

    fun printSeparator() {
        println("======================================")
    }

    fun askYesOrNo(message: String) {
        println("$message (Y/N)")
        print("입력: ")
    }
}