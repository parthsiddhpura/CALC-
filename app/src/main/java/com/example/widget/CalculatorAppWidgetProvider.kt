package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R

class CalculatorAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId, options)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId, newOptions)
    }

    companion object {
        const val ACTION_OPEN_MODE = "com.example.widget.ACTION_OPEN_MODE"
        const val EXTRA_MODE = "extra_calculator_mode"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            options: Bundle? = null
        ) {
            val theme = WidgetDataManager.getThemeFamily(context)
            val minHeight = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) ?: 140

            // Responsive Layout Selection:
            // 2x1 -> minHeight < 110dp
            // 4x4 -> minHeight >= 220dp
            // 4x2 -> default
            val views = when {
                minHeight < 110 -> buildCompact2x1Views(context, theme)
                minHeight >= 220 -> buildLarge4x4Views(context, theme)
                else -> buildStandard4x2Views(context, theme)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // --- 2x1 Compact Layout (e.g. ₹25,000 + 18% = ₹29,500) ---
        private fun buildCompact2x1Views(context: Context, theme: WidgetThemeFamily): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_adaptive_2x1)
            views.setInt(R.id.widget_root, "setBackgroundResource", theme.bgRes)

            val expr = WidgetDataManager.getLastExpression(context)
            val res = WidgetDataManager.getLastResult(context)

            views.setTextViewText(R.id.widget_compact_expr, expr)
            views.setTextViewText(R.id.widget_compact_result, "= $res")
            views.setTextColor(R.id.widget_compact_expr, theme.subtextColorHex)
            views.setTextColor(R.id.widget_compact_result, theme.accentColorHex)

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MODE, "STANDARD")
            }
            val pi = PendingIntent.getActivity(
                context, 101, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_open_calc, pi)
            views.setOnClickPendingIntent(R.id.btn_quick_launch, pi)
            return views
        }

        // --- 4x2 Standard Card (Full mini calculator & shortcuts) ---
        private fun buildStandard4x2Views(context: Context, theme: WidgetThemeFamily): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_calculator)
            views.setInt(R.id.widget_root, "setBackgroundResource", theme.bgRes)
            views.setInt(R.id.btn_launch_calc, "setBackgroundResource", theme.btnBgRes)
            views.setTextColor(R.id.widget_title, theme.headerTextColorHex)
            views.setTextColor(R.id.widget_result, theme.textColorHex)
            views.setTextColor(R.id.widget_expression, theme.subtextColorHex)

            val lastExpr = WidgetDataManager.getLastExpression(context)
            val lastResult = WidgetDataManager.getLastResult(context)

            views.setTextViewText(R.id.widget_expression, lastExpr)
            views.setTextViewText(R.id.widget_result, lastResult)

            // Click listener for launching Standard Calculator
            val calcIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MODE, "STANDARD")
            }
            val calcPendingIntent = PendingIntent.getActivity(
                context, 101, calcIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_launch_calc, calcPendingIntent)
            views.setOnClickPendingIntent(R.id.widget_root, calcPendingIntent)

            // Click listener for launching GST Calculator
            val gstIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MODE, "GST_CALCULATOR")
            }
            val gstPendingIntent = PendingIntent.getActivity(
                context, 102, gstIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_launch_gst, gstPendingIntent)

            // Click listener for launching Calculation Chains
            val chainsIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MODE, "CALCULATION_CHAINS")
            }
            val chainsPendingIntent = PendingIntent.getActivity(
                context, 103, chainsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_launch_chains, chainsPendingIntent)

            // Click listener for launching AI Math Copilot
            val aiIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MODE, "AI_COPILOT")
            }
            val aiPendingIntent = PendingIntent.getActivity(
                context, 104, aiIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_launch_ai, aiPendingIntent)

            return views
        }

        // --- 4x4 Command Center (Calculator + History + Shortcuts) ---
        private fun buildLarge4x4Views(context: Context, theme: WidgetThemeFamily): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_adaptive_4x4)
            views.setInt(R.id.widget_root, "setBackgroundResource", theme.bgRes)
            views.setTextColor(R.id.widget_title, theme.headerTextColorHex)
            views.setTextColor(R.id.widget_result, theme.textColorHex)
            views.setTextColor(R.id.widget_expression, theme.subtextColorHex)

            val expr = WidgetDataManager.getLastExpression(context)
            val res = WidgetDataManager.getLastResult(context)
            views.setTextViewText(R.id.widget_expression, expr)
            views.setTextViewText(R.id.widget_result, res)

            // History rows
            val history = WidgetDataManager.getHistoryList(context)
            if (history.isNotEmpty()) views.setTextViewText(R.id.hist_row_1, "• " + history[0])
            if (history.size > 1) views.setTextViewText(R.id.hist_row_2, "• " + history[1])
            if (history.size > 2) views.setTextViewText(R.id.hist_row_3, "• " + history[2])

            // Mode launches
            bindMode(context, views, R.id.btn_hub_calc, "STANDARD", 151)
            bindMode(context, views, R.id.btn_hub_gst, "GST_CALCULATOR", 152)
            bindMode(context, views, R.id.btn_hub_emi, "EMI_LOAN", 153)
            bindMode(context, views, R.id.btn_hub_bmi, "BMI_CALCULATOR", 154)
            bindMode(context, views, R.id.btn_hub_age, "AGE_CALCULATOR", 155)
            bindMode(context, views, R.id.btn_hub_forex, "CURRENCY_CONVERTER", 156)
            bindMode(context, views, R.id.btn_open_calc, "STANDARD", 157)

            // Theme cycle button
            val themeIntent = Intent(context, WidgetActionReceiver::class.java).apply {
                action = WidgetActionReceiver.ACTION_CYCLE_THEME
            }
            val themePendingIntent = PendingIntent.getBroadcast(
                context, 160, themeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_theme_cycle, themePendingIntent)

            return views
        }

        private fun bindMode(context: Context, views: RemoteViews, viewId: Int, mode: String, reqCode: Int) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_MODE, mode)
            }
            val pi = PendingIntent.getActivity(
                context, reqCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(viewId, pi)
        }

        fun sendUpdateBroadcast(context: Context, lastExpression: String, lastResult: String) {
            WidgetDataManager.updateCalculation(context, lastExpression, lastResult)
        }
    }
}
