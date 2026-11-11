package com.example.brawlvision

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot
import kotlin.math.min

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TrainingView(this))
    }
}

private data class Target(val x: Float, val y: Float, val label: String)

private class TrainingView(context: android.content.Context) : View(context) {
    private val bg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 34f
        typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD)
    }
    private val small = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFB7C3D0.toInt()
        textSize = 23f
    }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3.5f
    }
    private val targets = listOf(
        Target(.25f, .30f, "BOT 01"),
        Target(.72f, .24f, "BOT 02"),
        Target(.78f, .56f, "BOT 03"),
        Target(.34f, .67f, "BOT 04")
    )
    private var espEnabled = true
    private var aimEnabled = true
    private var sensitivity = 55
    private var selected = 0
    private var playerX = .50f
    private var playerY = .84f
    private var sliderDragging = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawColor(0xFF0B1020.toInt())

        // Header
        bg.color = 0xFF151E33.toInt()
        canvas.drawRoundRect(RectF(22f, 22f, w - 22f, 142f), 26f, 26f, bg)
        text.textSize = 39f
        text.color = Color.WHITE
        canvas.drawText("BRAWL VISION", 48f, 75f, text)
        small.textSize = 21f
        canvas.drawText("AIM & VISION TRAINER", 50f, 111f, small)

        // Arena panel
        val top = 170f
        val bottom = h * .67f
        bg.color = 0xFF17243A.toInt()
        canvas.drawRoundRect(RectF(22f, top, w - 22f, bottom), 24f, 24f, bg)
        line.color = 0xFF344967.toInt()
        for (i in 1..3) {
            val y = top + (bottom - top) * i / 4f
            canvas.drawLine(42f, y, w - 42f, y, line)
        }
        for (i in 1..4) {
            val x = 22f + (w - 44f) * i / 5f
            canvas.drawLine(x, top + 18f, x, bottom - 18f, line)
        }

        val arenaH = bottom - top
        val arenaW = w - 44f
        val px = 22f + playerX * arenaW
        val py = top + playerY * arenaH * .72f
        val nearest = targets.indices.minByOrNull { i ->
            val tx = 22f + targets[i].x * arenaW
            val ty = top + targets[i].y * arenaH
            hypot(tx - px, ty - py)
        } ?: 0

        if (aimEnabled) {
            val t = targets[nearest]
            val tx = 22f + t.x * arenaW
            val ty = top + t.y * arenaH
            line.color = 0xFF53E0B5.toInt()
            line.strokeWidth = 4f
            canvas.drawLine(px, py, tx, ty, line)
            line.color = 0x6653E0B5
            line.strokeWidth = 2f
            canvas.drawCircle(px, py, 72f + sensitivity, line)
        }

        targets.forEachIndexed { index, target ->
            val tx = 22f + target.x * arenaW
            val ty = top + target.y * arenaH
            bg.color = if (index == selected) 0xFFFFB84D.toInt() else 0xFFE45B68.toInt()
            canvas.drawCircle(tx, ty, 23f, bg)
            bg.color = 0xFF101827.toInt()
            canvas.drawCircle(tx, ty, 9f, bg)
            if (espEnabled) {
                line.color = if (index == selected) 0xFFFFD27A.toInt() else 0xFF63D8FF.toInt()
                line.strokeWidth = 3f
                canvas.drawRoundRect(RectF(tx - 34f, ty - 43f, tx + 34f, ty + 43f), 8f, 8f, line)
                small.textSize = 19f
                small.color = Color.WHITE
                canvas.drawText(target.label, tx - 38f, ty - 53f, small)
                val dist = hypot(tx - px, ty - py).toInt()
                small.textSize = 17f
                canvas.drawText("${dist}u", tx - 17f, ty + 66f, small)
            }
        }

        // Player marker
        bg.color = 0xFF55D6A5.toInt()
        canvas.drawCircle(px, py, 27f, bg)
        bg.color = 0xFF0D2630.toInt()
        canvas.drawCircle(px, py, 11f, bg)
        small.color = 0xFFB7C3D0.toInt()
        small.textSize = 20f
        canvas.drawText("YOU", px - 20f, py + 52f, small)

        // Controls
        val panelTop = bottom + 24f
        drawToggle(canvas, 22f, panelTop, w * .48f - 30f, 94f, "ESP", espEnabled)
        drawToggle(canvas, w * .52f, panelTop, w - 22f, 94f, "AIM ASSIST", aimEnabled)

        bg.color = 0xFF151E33.toInt()
        canvas.drawRoundRect(RectF(22f, panelTop + 112f, w - 22f, h - 28f), 22f, 22f, bg)
        text.textSize = 26f
        canvas.drawText("SENSITIVITY", 46f, panelTop + 158f, text)
        small.textSize = 24f
        canvas.drawText("$sensitivity%", w - 115f, panelTop + 158f, small)
        line.color = 0xFF40516B.toInt()
        line.strokeWidth = 10f
        canvas.drawLine(54f, panelTop + 205f, w - 54f, panelTop + 205f, line)
        line.color = 0xFF53E0B5.toInt()
        canvas.drawLine(54f, panelTop + 205f, 54f + (w - 108f) * sensitivity / 100f, panelTop + 205f, line)
        bg.color = 0xFF53E0B5.toInt()
        canvas.drawCircle(54f + (w - 108f) * sensitivity / 100f, panelTop + 205f, 14f, bg)
        small.textSize = 18f
        small.color = 0xFF8FA2BC.toInt()
        canvas.drawText("DEMO MODE • TRAINING FIELD ONLY", 46f, h - 48f, small)
    }

    private fun drawToggle(c: Canvas, left: Float, top: Float, right: Float, height: Float, title: String, enabled: Boolean) {
        bg.color = if (enabled) 0xFF1D4A48.toInt() else 0xFF252F43.toInt()
        c.drawRoundRect(RectF(left, top, right, top + height), 18f, 18f, bg)
        text.textSize = 25f
        text.color = Color.WHITE
        c.drawText(title, left + 20f, top + 57f, text)
        bg.color = if (enabled) 0xFF53E0B5.toInt() else 0xFF65738A.toInt()
        c.drawCircle(right - 31f, top + height / 2f, 10f, bg)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val w = width.toFloat()
        val h = height.toFloat()
        val panelTop = h * .67f + 24f
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (event.y in (panelTop + 165f)..(panelTop + 245f)) {
                    sensitivity = (((event.x - 54f) / (w - 108f)) * 100f).toInt().coerceIn(0, 100)
                    invalidate()
                    return true
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (event.y in panelTop..(panelTop + 100f)) {
                        if (event.x < w / 2f) espEnabled = !espEnabled else aimEnabled = !aimEnabled
                    } else if (event.y < h * .67f) {
                        selected = (selected + 1) % targets.size
                    }
                    invalidate()
                    return true
                }
            }
        }
        return true
    }
}
