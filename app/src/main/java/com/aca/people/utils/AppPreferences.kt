package com.aca.people.utils

import com.aca.people.presentation.util.resource.theme.AppTheme
import io.paperdb.Paper

object AppPreferences {

    const val APP_THEME = "AppTheme"

    fun setTheme(theme: AppTheme) {
        Paper.book().write(APP_THEME, theme)
    }
    fun getTheme(): AppTheme {
        return Paper.book().read(APP_THEME, AppTheme.Default)!!
    }

    //endregion
}