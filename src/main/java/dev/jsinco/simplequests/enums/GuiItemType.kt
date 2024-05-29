package dev.jsinco.simplequests.enums

enum class GuiItemType {
    PAGE_SWITCHER,
    QUEST,
    CATEGORY;

    companion object{
        fun getItemType(s: String): GuiItemType? {
            return try {
                valueOf(s)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}