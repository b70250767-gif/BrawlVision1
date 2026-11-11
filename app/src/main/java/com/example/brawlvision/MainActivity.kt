package com.example.brawlvision

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.view.View
import android.view.MotionEvent
import kotlin.math.hypot

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ArenaView(this))
    }
}

class ArenaView(context: android.content.Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var esp = true
    private var aim = true
    private var sensitivity = 55

    private val targets = listOf(
        Pair(0.25f, 0.30f),
        Pair(0.70f, 0.25f),
        Pair(0.75f, 0.60f),
        Pair(0.35f, 0.65f)
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.drawColor(Color.rgb(11, 16, 32))

        paint.color = Color.WHITE
        paint.textSize = 42f
        canvas.drawText("BRAWL VISION", 35f, 75f, paint)

        paint.textSize = 23f
        paint.color = Color.LTGRAY
        canvas.drawText("AIM TRAINER", 35f, 115f, paint)

        paint.color = Color.rgb(23, 36, 58)
        canvas.drawRoundRect(
            20f, 150f, w - 20f, h * 0.67f,
            25f, 25f, paint
        )

        val arenaH = h * 0.67f - 150f
        val playerX = w * 0.5f
        val playerY = 150f + arenaH * 0.85f

        val positions = targets.map {
            Pair(
                20f + it.first * (w - 40f),
                150f + it.second * arenaH
            )
        }

        if (aim) {
            val target = positions.minByOrNull {
                hypot(it.first - playerX, it.second - playerY)
            }!!

            paint.color = Color.rgb(83, 224, 181)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f

            canvas.drawLine(
                playerX, playerY,
                target.first, target.second, paint
            )

            canvas.drawCircle(
                playerX, playerY,
                65f + sensitivity, paint
            )

            paint.style = Paint.Style.FILL
        }

        positions.forEachIndexed { index, p ->
            paint.color = Color.rgb(228, 91, 104)
            canvas.drawCircle(p.first, p.second, 22f, paint)

            if (esp) {
                paint.color = Color.CYAN
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f

                canvas.drawRect(
                    p.first - 35f, p.second - 42f,
                    p.first + 35f, p.second + 42f,
                    paint
                )

                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                paint.textSize = 18f

                canvas.drawText(
                    "BOT ${index + 1}",
                    p.first - 30f,
                    p.second - 50f,
                    paint
                )
            }
        }

        paint.color = Color.GREEN
        canvas.drawCircle(playerX, playerY, 25f, paint)

        paint.color = Color.WHITE
        paint.textSize = 27f

        canvas.drawText(
            "ESP: ${if (esp) "ON" else "OFF"}",
            35f, h * 0.76f, paint
        )

        canvas.drawText(
            "AIM: ${if (aim) "ON" else "OFF"}",
            35f, h * 0.83f, paint
        )

        canvas.drawText(
            "SENSITIVITY: $sensitivity%",
            35f, h * 0.90f, paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when {
                event.y > height * 0.72f &&
                    event.y < height * 0.80f ->
                    esp = !esp

                event.y >= height * 0.80f &&
                    event.y < height * 0.87f ->
                    aim = !aim

                event.y >= height * 0.87f ->
                    sensitivity = (sensitivity + 10) % 110
            }

            invalidate()
        }

        return true
    }
}
