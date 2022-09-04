package cn.ommiao.library.colorpicker

enum class ColorPickerStateValue {
    PREVIEW, PICK;

    fun reverse(): ColorPickerStateValue {
        return when (this) {
            PICK -> PREVIEW
            PREVIEW -> PICK
        }
    }
}
