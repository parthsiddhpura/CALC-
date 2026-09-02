package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val ACTION_OPEN_MODE = "com.example.widget.ACTION_OPEN_MODE"
        const val EXTRA_MODE = "extra_calculator_mode"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_calculator)

            // Read last result if saved in SharedPreferences
            val prefs = context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)
            val lastExpr = prefs.getString("last_expression", "Ready to calculate") ?: "Ready to calculate"
            val lastResult = prefs.getString("last_result", "0") ?: "0"

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

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun sendUpdateBroadcast(context: Context, lastExpression: String, lastResult: String) {
            val prefs = context.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("last_expression", lastExpression)
                .putString("last_result", lastResult)
                .apply()

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CalculatorAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
