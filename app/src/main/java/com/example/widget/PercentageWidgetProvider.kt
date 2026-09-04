package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class PercentageWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val theme = WidgetDataManager.getThemeFamily(context)
            val views = RemoteViews(context.packageName, R.layout.widget_percentage)

            views.setInt(R.id.widget_root, "setBackgroundResource", theme.bgRes)
            views.setInt(R.id.btn_open_percent_calc, "setBackgroundResource", theme.screenBgRes)
            views.setTextColor(R.id.widget_title, theme.headerTextColorHex)
            views.setTextColor(R.id.pct_equation_text, theme.textColorHex)
            views.setTextColor(R.id.pct_result_text, theme.accentColorHex)

            val rate = WidgetDataManager.getPercentageRate(context)
            val base = WidgetDataManager.getPercentageBase(context)
            val result = WidgetDataManager.getPercentageResult(context)

            views.setTextViewText(R.id.pct_equation_text, "${rate.toInt()}% of ${WidgetDataManager.formatCurrency(base)}")
            views.setTextViewText(R.id.pct_result_text, WidgetDataManager.formatCurrency(result))

            // Tap card -> Open standard calculator with equation
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(CalculatorAppWidgetProvider.EXTRA_MODE, "STANDARD")
                putExtra(WidgetActionReceiver.EXTRA_INITIAL_EXPR, "$base × $rate%")
            }
            val openPendingIntent = PendingIntent.getActivity(
                context, 1001, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_open_percent_calc, openPendingIntent)

            // Rate -5%
            val rSubIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_PERCENT_ADJUST_RATE
                putExtra(WidgetActionReceiver.EXTRA_DELTA, -5.0)
            }
            val rSubPendingIntent = PendingIntent.getBroadcast(
                context, 1002, rSubIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_pct_sub, rSubPendingIntent)

            // Rate +5%
            val rAddIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_PERCENT_ADJUST_RATE
                putExtra(WidgetActionReceiver.EXTRA_DELTA, 5.0)
            }
            val rAddPendingIntent = PendingIntent.getBroadcast(
                context, 1003, rAddIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_pct_add, rAddPendingIntent)

            // Base -1k
            val bSubIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_PERCENT_ADJUST_BASE
                putExtra(WidgetActionReceiver.EXTRA_DELTA, -1000.0)
            }
            val bSubPendingIntent = PendingIntent.getBroadcast(
                context, 1004, bSubIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_base_sub, bSubPendingIntent)

            // Base +1k
            val bAddIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_PERCENT_ADJUST_BASE
                putExtra(WidgetActionReceiver.EXTRA_DELTA, 1000.0)
            }
            val bAddPendingIntent = PendingIntent.getBroadcast(
                context, 1005, bAddIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_base_add, bAddPendingIntent)

            // Theme Cycle
            val themeIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_CYCLE_THEME
            }
            val themePendingIntent = PendingIntent.getBroadcast(
                context, 1006, themeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_theme_cycle, themePendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
