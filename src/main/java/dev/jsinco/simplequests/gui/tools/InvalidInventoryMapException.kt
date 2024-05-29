package dev.jsinco.simplequests.gui.tools

class InvalidInventoryMapException : Exception() {
    override val message: String
        get() = "Invalid inventory map. List size must be equal to total inventory size."
}