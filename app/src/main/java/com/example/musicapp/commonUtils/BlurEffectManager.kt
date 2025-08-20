package com.example.musicapp.commonUtils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi

/**
 * 高斯模糊效果管理器
 * 增强版：支持API 31+使用RenderEffect，低版本使用RenderScript fallback
 * 修改版：只对特定组件应用模糊效果
 */
object BlurEffectManager {

    private const val TAG = "BlurEffectManager"
    private const val BLUR_RADIUS = 15f // 优化模糊半径，降低过度模糊
    private const val FALLBACK_BLUR_RADIUS = 10f // 低版本模糊半径

    /**
     * 为特定组件应用高斯模糊效果
     * @param blurView 需要应用模糊效果的视图
     */
    fun applyBlurToComponent(blurView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applyBlurToComponentInternal(blurView)
        } else {
            // 低版本设备使用RenderScript实现模糊
            applyFallbackBlurToComponent(blurView)
        }
    }

    /**
     * API 31+ 使用RenderEffect实现模糊
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyBlurToComponentInternal(blurView: View) {
        try {
            Log.d(TAG, "开始应用RenderEffect高斯模糊效果到组件: ${blurView.id}")
            val blurEffect = RenderEffect.createBlurEffect(
                BLUR_RADIUS,
                BLUR_RADIUS,
                Shader.TileMode.CLAMP
            )
            blurView.setRenderEffect(blurEffect)
            Log.d(TAG, "RenderEffect模糊应用成功到组件: ${blurView.id}")
        } catch (e: Exception) {
            Log.e(TAG, "RenderEffect模糊应用失败到组件: ${blurView.id}", e)
            // 失败时降级使用低版本方案
            applyFallbackBlurToComponent(blurView)
        }
    }

    /**
     * 低版本设备使用RenderScript实现模糊（API < 31）
     */
    private fun applyFallbackBlurToComponent(blurView: View) {
        try {
            Log.d(TAG, "使用RenderScript fallback模糊方案到组件: ${blurView.id}")
            // 1. 绘制视图到Bitmap
            blurView.isDrawingCacheEnabled = true
            blurView.buildDrawingCache()
            val bitmap = blurView.drawingCache ?: return
            val blurredBitmap = blurBitmap(blurView.context, bitmap)

            // 2. 设置模糊后的Bitmap为背景
            blurView.background = android.graphics.drawable.BitmapDrawable(
                blurView.resources,
                blurredBitmap
            )
            blurView.isDrawingCacheEnabled = false
            Log.d(TAG, "RenderScript模糊应用成功到组件: ${blurView.id}")
        } catch (e: Exception) {
            Log.e(TAG, "低版本模糊方案应用失败到组件: ${blurView.id}", e)
        }
    }

    /**
     * 为导航栏应用高斯模糊效果（自动适配版本）
     * @param navContainer 导航栏容器视图
     * @deprecated 使用 applyBlurToComponent 替代
     */
    @Deprecated("使用 applyBlurToComponent 替代")
    fun applyBlurToNavBar(navContainer: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applyBlurToNavBarInternal(navContainer)
        } else {
            // 低版本设备使用RenderScript实现模糊
            applyFallbackBlur(navContainer)
        }
    }

    /**
     * API 31+ 使用RenderEffect实现模糊
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyBlurToNavBarInternal(navContainer: View) {
        try {
            Log.d(TAG, "开始应用RenderEffect高斯模糊效果")
            val blurEffect = RenderEffect.createBlurEffect(
                BLUR_RADIUS,
                BLUR_RADIUS,
                Shader.TileMode.CLAMP
            )
            navContainer.setRenderEffect(blurEffect)
            Log.d(TAG, "RenderEffect模糊应用成功")
        } catch (e: Exception) {
            Log.e(TAG, "RenderEffect模糊应用失败", e)
            // 失败时降级使用低版本方案
            applyFallbackBlur(navContainer)
        }
    }

    /**
     * 低版本设备使用RenderScript实现模糊（API < 31）
     */
    private fun applyFallbackBlur(navContainer: View) {
        try {
            Log.d(TAG, "使用RenderScript fallback模糊方案")
            // 1. 绘制视图到Bitmap
            navContainer.isDrawingCacheEnabled = true
            navContainer.buildDrawingCache()
            val bitmap = navContainer.drawingCache ?: return
            val blurredBitmap = blurBitmap(navContainer.context, bitmap)

            // 2. 设置模糊后的Bitmap为背景
            if (navContainer is FrameLayout) {
                navContainer.background = android.graphics.drawable.BitmapDrawable(
                    navContainer.resources,
                    blurredBitmap
                )
            }
            navContainer.isDrawingCacheEnabled = false
            Log.d(TAG, "RenderScript模糊应用成功")
        } catch (e: Exception) {
            Log.e(TAG, "低版本模糊方案应用失败", e)
        }
    }

    /**
     * 使用RenderScript模糊Bitmap
     */
    private fun blurBitmap(context: Context, source: Bitmap): Bitmap {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, source)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        script.setRadius(FALLBACK_BLUR_RADIUS.coerceAtMost(25f)) // 最大模糊半径为25
        script.setInput(input)
        script.forEach(output)
        output.copyTo(source)

        rs.destroy()
        return source
    }

    /**
     * 移除特定组件的模糊效果
     */
    fun removeBlurFromComponent(blurView: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setRenderEffect(null)
        } else {
            // 恢复原始背景
            blurView.background = null
        }
        Log.d(TAG, "模糊效果已从组件移除: ${blurView.id}")
    }

    /**
     * 移除导航栏的模糊效果
     * @deprecated 使用 removeBlurFromComponent 替代
     */
    @Deprecated("使用 removeBlurFromComponent 替代")
    fun removeBlurFromNavBar(navContainer: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            navContainer.setRenderEffect(null)
        } else {
            // 恢复原始背景
            navContainer.background = null
        }
        Log.d(TAG, "模糊效果已移除")
    }

    /**
     * 检查是否支持硬件加速模糊
     */
    fun isHardwareBlurSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}