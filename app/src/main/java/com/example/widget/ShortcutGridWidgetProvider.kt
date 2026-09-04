package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class ShortcutGridWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val theme = WidgetDataManager.getThemeFamily(context)
            val views = RemoteViews(context.packageName, R.layout.widget_shortcut_grid)

            views.setInt(R.id.widget_root, "setBackgroundResource", theme.bgRes)
            views.setTextColor(R.id.widget_title, theme.headerTextColorHex)

            // Setup the 6 shortcuts
            val shortcuts = WidgetDataManager.getShortcuts()
            val buttonIds = listOf(
                R.id.btn_shortcut_1,
                R.id.btn_shortcut_2,
                R.id.btn_shortcut_3,
                R.id.btn_shortcut_4,
                R.id.btn_shortcut_5,
                R.id.btn_shortcut_6
            )

            for (i in buttonIds.indices) {
                if (i in shortcuts.indices) {
                    val s = shortcuts[i]
                    views.setTextViewText(buttonIds[i], "${s.symbol} ${s.title}")

                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(CalculatorAppWidgetProvider.EXTRA_MODE, s.modeKey)
                    }
                    val pi = PendingIntent.getActivity(
                        context, 700 + i, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(buttonIds[i], pi)
                }
            }

            // Theme Cycle
            val themeIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_CYCLE_THEME
            }
            val themePendingIntent = PendingIntent.getBroadcast(
                context, 710, themeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_theme_cycle, themePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
