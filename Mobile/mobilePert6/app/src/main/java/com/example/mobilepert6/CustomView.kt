package com.example.mobilepert6

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class CustomView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val normalTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bioTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linkTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkModeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkModeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkModeNormalTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var vectorDrawableBadge: Drawable
    private lateinit var vectorDrawableLocation: Drawable
    private lateinit var vectorDrawableCalendar: Drawable
    private lateinit var vectorDrawableLink: Drawable
    private lateinit var vectorDrawableBack: Drawable
    private lateinit var vectorDrawableMore: Drawable

    private val radius = 140f
    private val buttonRect = RectF()
    private val followingBounds = Rect()
    private val jmlFollowingBounds = Rect()
    private val darkModeToggleRect = RectF()

    private var name: String = ""
    private var username: String = ""
    private var bio: String = ""
    private var location: String = ""
    private var website: String = ""
    private var joinedDate: String = ""
    private var followingCount: String = "0"
    private var followerCount: String = "0"

    // Dark Mode
    private var isDarkMode = false
    private lateinit var sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        isDarkMode = sharedPref.getBoolean("dark_mode", false)

        initPaints()

        initIcons()
    }

    private fun initPaints() {
        // Light Mode Paints
        textPaint.apply {
            color = if (isDarkMode) Color.WHITE else Color.BLACK
            textSize = 50f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        normalTextPaint.apply {
            color = if (isDarkMode) Color.LTGRAY else Color.DKGRAY
            textSize = 40f
            textAlign = Paint.Align.LEFT
        }
        bioTextPaint.apply {
            color = if (isDarkMode) Color.WHITE else Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        linkTextPaint.apply {
            color = Color.parseColor("#87CEFA")
            textSize = 40f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Dark Mode Specific Paints
        darkModeBgPaint.color = Color.parseColor("#121212")
        darkModeTextPaint.apply {
            color = Color.WHITE
            textSize = 50f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        darkModeNormalTextPaint.apply {
            color = Color.LTGRAY
            textSize = 40f
            textAlign = Paint.Align.LEFT
        }
    }

    private fun initIcons() {
        vectorDrawableBadge = ContextCompat.getDrawable(context, R.drawable.baseline_badge_24)!!
        vectorDrawableLocation = ContextCompat.getDrawable(context, R.drawable.baseline_add_location_24)!!
        vectorDrawableCalendar = ContextCompat.getDrawable(context, R.drawable.baseline_calendar_month_24)!!
        vectorDrawableLink = ContextCompat.getDrawable(context, R.drawable.baseline_link_24)!!
        vectorDrawableBack = ContextCompat.getDrawable(context, R.drawable.baseline_arrow_back_24)!!
        vectorDrawableMore = ContextCompat.getDrawable(context, R.drawable.baseline_more_vert_24)!!

        val iconColor = if (isDarkMode) Color.WHITE else Color.BLACK
        vectorDrawableBack.setTint(iconColor)
        vectorDrawableMore.setTint(iconColor)
        vectorDrawableBadge.setTint(iconColor)
        vectorDrawableLocation.setTint(iconColor)
        vectorDrawableCalendar.setTint(iconColor)
    }

    fun setUserProfile(user: DatabaseHelper.UserProfile) {
        name = user.name
        username = user.username
        bio = user.bio ?: ""
        location = user.location ?: ""
        website = user.website ?: ""
        joinedDate = user.joinDate ?: ""
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (isDarkMode) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), darkModeBgPaint)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        paint.color = if (isDarkMode) Color.parseColor("#1E1E1E") else Color.BLUE
        canvas.drawRect(0f, 0f, width.toFloat(), height - 1800f, paint)

        vectorDrawableBack.setBounds(45, 40, 115, 110)
        vectorDrawableBack.draw(canvas)
        vectorDrawableMore.setBounds(width - 85, 40, width - 15, 110)
        vectorDrawableMore.draw(canvas)

        paintCircle.color = if (isDarkMode) Color.DKGRAY else Color.GRAY
        val circleX = radius + 30f
        val circleY = height - radius - 1700f
        canvas.drawCircle(circleX, circleY, radius, paintCircle)

        val textY = circleY + radius + 60f
        val textX = circleX - radius

        val currentTextPaint = if (isDarkMode) darkModeTextPaint else textPaint
        val currentNormalTextPaint = if (isDarkMode) darkModeNormalTextPaint else normalTextPaint
        canvas.drawText(name, textX, textY, currentTextPaint)
        canvas.drawText(username, textX, textY + 55f, currentNormalTextPaint)

        canvas.drawText(bio, textX, textY + 125f, bioTextPaint)

        vectorDrawableBadge.setBounds(textX.toInt(), textY.toInt() + 150, textX.toInt() + 50, textY.toInt() + 200)
        vectorDrawableBadge.draw(canvas)
        canvas.drawText("Advertising & Marketing Agency", textX + 70f, textY + 190f, currentNormalTextPaint)

        vectorDrawableLocation.setBounds(textX.toInt(), textY.toInt() + 210, textX.toInt() + 50, textY.toInt() + 260)
        vectorDrawableLocation.draw(canvas)
        canvas.drawText(location, textX + 70f, textY + 250f, currentNormalTextPaint)

        vectorDrawableLink.setBounds(textX.toInt() + 390, textY.toInt() + 210, textX.toInt() + 440, textY.toInt() + 260)
        vectorDrawableLink.draw(canvas)
        canvas.drawText(website, textX + 470f, textY + 250f, linkTextPaint)

        vectorDrawableCalendar.setBounds(textX.toInt(), textY.toInt() + 280, textX.toInt() + 50, textY.toInt() + 330)
        vectorDrawableCalendar.draw(canvas)
        canvas.drawText("Joined $joinedDate", textX + 70f, textY + 320f, currentNormalTextPaint)

        canvas.drawText(followingCount, textX, textY + 390f, currentTextPaint)
        canvas.drawText("Following", textX + 100f, textY + 390f, currentNormalTextPaint)
        normalTextPaint.getTextBounds("Following", 0, "Following".length, followingBounds)
        followingBounds.offset((textX + 100f).toInt(), (textY + 390f).toInt())
        canvas.drawText(followerCount, textX + 300f, textY + 390f, currentTextPaint)
        canvas.drawText("Followers", textX + 400f, textY + 390f, currentNormalTextPaint)

        val btnEditX = width / 4f + 400f
        val btnEditY = height - radius - 1620f
        buttonRect.set(btnEditX, btnEditY, btnEditX + 350f, btnEditY + 100f)
        paint.color = if (isDarkMode) Color.DKGRAY else Color.LTGRAY
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawRoundRect(buttonRect, 50f, 50f, paint)
        paint.style = Paint.Style.FILL
        paint.color = if (isDarkMode) Color.WHITE else Color.BLACK
        paint.textSize = 45f
        val buttonText = "Edit Profile"
        val textWidth = paint.measureText(buttonText)
        canvas.drawText(buttonText, buttonRect.centerX() - textWidth / 2, buttonRect.centerY() + 15f, paint)

        darkModeToggleRect.set(width - 150f, 50f, width - 50f, 100f)
        paint.color = if (isDarkMode) Color.YELLOW else Color.DKGRAY
        canvas.drawRoundRect(darkModeToggleRect, 25f, 25f, paint)
        paint.color = if (isDarkMode) Color.BLACK else Color.WHITE
        canvas.drawText(
            if (isDarkMode) "☀️" else "🌙",
            darkModeToggleRect.centerX() - 15f,
            darkModeToggleRect.centerY() + 15f,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Dark Mode Toggle
                if (darkModeToggleRect.contains(event.x, event.y)) {
                    toggleDarkMode()
                    return true
                }

                if (followingBounds.contains(event.x.toInt(), event.y.toInt()) ||
                    jmlFollowingBounds.contains(event.x.toInt(), event.y.toInt())) {
                    val intent = Intent(context, FollowingActivity::class.java)
                    context.startActivity(intent)
                    return true
                }

                if (jmlFollowingBounds.contains(event.x.toInt(), event.y.toInt())) {
                    val intent = Intent(context, FollowingActivity::class.java)
                    context.startActivity(intent)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        sharedPref.edit().putBoolean("dark_mode", isDarkMode).apply()

        val iconColor = if (isDarkMode) Color.WHITE else Color.BLACK
        vectorDrawableBack.setTint(iconColor)
        vectorDrawableMore.setTint(iconColor)
        vectorDrawableBadge.setTint(iconColor)
        vectorDrawableLocation.setTint(iconColor)
        vectorDrawableCalendar.setTint(iconColor)

        invalidate()
    }
}