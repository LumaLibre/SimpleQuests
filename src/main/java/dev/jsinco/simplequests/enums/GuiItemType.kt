package dev.jsinco.simplequests.enums

enum class GuiItemType {
    PAGE_SWITCHER,
    QUEST,
    CATEGORY,
    RETURN,
    ACHIEVEMENT,
    ACHIEVEMENTS_GUI_OPENER,
    SHOW_PROGRESS_BAR;

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