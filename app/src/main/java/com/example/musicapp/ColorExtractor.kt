package com.example.musicapp

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.abs

/**
 * 颜色提取器
 * 用于从图片中提取主色调并应用到界面背景
 */
object ColorExtractor {
    
    private const val TAG = "ColorExtractor"
    
    /**
     * 从Bitmap中提取主色调
     * @param bitmap 要分析的图片
     * @return 主色调的RGB值
     */
    fun extractDominantColor(bitmap: Bitmap): Int {
        try {
            // 缩小图片以提高性能
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
            
            // 收集所有像素的颜色
            val pixels = IntArray(100 * 100)
            scaledBitmap.getPixels(pixels, 0, 100, 0, 0, 100, 100)
            
            // 统计颜色频率，同时考虑饱和度和亮度
            val colorCount = mutableMapOf<Int, Float>()
            for (pixel in pixels) {
                // 忽略透明像素
                if (Color.alpha(pixel) > 128) {
                    // 将颜色量化到较少的级别以提高准确性
                    val quantizedColor = quantizeColor(pixel)
                    
                    // 计算颜色的权重（考虑饱和度和亮度）
                    val weight = calculateColorWeight(pixel)
                    colorCount[quantizedColor] = colorCount.getOrDefault(quantizedColor, 0f) + weight
                }
            }
            
            // 找到权重最高的颜色
            val dominantColor = colorCount.maxByOrNull { it.value }?.key ?: Color.GRAY
            
            // 调整颜色亮度和饱和度
            val adjustedColor = adjustColorForBackground(dominantColor)
            
            Log.d(TAG, "提取到主色调: ${String.format("#%06X", adjustedColor)}")
            
            // 清理资源
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }
            
            return adjustedColor
            
        } catch (e: Exception) {
            Log.e(TAG, "提取主色调时发生错误", e)
            return Color.GRAY
        }
    }
    
    /**
     * 计算颜色的权重，优先选择浅色系
     */
    private fun calculateColorWeight(color: Int): Float {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        
        // 计算饱和度
        val max = maxOf(red, green, blue)
        val min = minOf(red, green, blue)
        val saturation = if (max == 0) 0f else (max - min).toFloat() / max
        
        // 计算亮度
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000f / 255f
        
        // 权重计算：优先选择浅色系，同时保持一定的饱和度
        val saturationWeight = saturation * 1.5f // 适中的饱和度权重
        val brightnessWeight = if (brightness > 0.6f) 2.0f else if (brightness > 0.4f) 1.5f else 1.0f // 浅色权重更高
        
        return saturationWeight * brightnessWeight
    }
    
    /**
     * 量化颜色，将相似的颜色归为一类
     */
    private fun quantizeColor(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        
        // 将RGB值量化到32个级别
        val quantizedRed = (red / 8) * 8
        val quantizedGreen = (green / 8) * 8
        val quantizedBlue = (blue / 8) * 8
        
        return Color.rgb(quantizedRed, quantizedGreen, quantizedBlue)
    }
    
    /**
     * 调整颜色使其适合作为背景色（浅色系）
     */
    private fun adjustColorForBackground(color: Int): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        
        // 计算亮度
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000
        
        // 强制转换为浅色系，确保背景不会影响文字可读性
        val lightColor = if (brightness < 180) {
            // 如果颜色较暗，调亮到浅色
            val factor = 0.8f // 保持一定的饱和度
            val lightRed = minOf(255, (red + (255 - red) * factor).toInt())
            val lightGreen = minOf(255, (green + (255 - green) * factor).toInt())
            val lightBlue = minOf(255, (blue + (255 - blue) * factor).toInt())
            Color.rgb(lightRed, lightGreen, lightBlue)
        } else {
            // 如果颜色已经较亮，进一步调亮并降低饱和度
            val factor = 0.9f // 降低饱和度
            val lightRed = minOf(255, (red + (255 - red) * 0.3f).toInt())
            val lightGreen = minOf(255, (green + (255 - green) * 0.3f).toInt())
            val lightBlue = minOf(255, (blue + (255 - blue) * 0.3f).toInt())
            Color.rgb(lightRed, lightGreen, lightBlue)
        }
        
        return lightColor
    }
    
    /**
     * 生成浅色系渐变背景色
     * @param baseColor 基础颜色
     * @return 渐变颜色数组 [开始颜色, 结束颜色]
     */
    fun generateGradientColors(baseColor: Int): Array<Int> {
        val red = Color.red(baseColor)
        val green = Color.green(baseColor)
        val blue = Color.blue(baseColor)
        
        // 生成更浅的渐变开始色（接近白色）
        val startColor = Color.argb(
            255,
            minOf(255, (red + (255 - red) * 0.7f).toInt()),
            minOf(255, (green + (255 - green) * 0.7f).toInt()),
            minOf(255, (blue + (255 - blue) * 0.7f).toInt())
        )
        
        // 生成稍深的渐变结束色（保持浅色）
        val endColor = Color.argb(
            255,
            minOf(255, (red + (255 - red) * 0.3f).toInt()),
            minOf(255, (green + (255 - green) * 0.3f).toInt()),
            minOf(255, (blue + (255 - blue) * 0.3f).toInt())
        )
        
        return arrayOf(startColor, endColor)
    }
    
    /**
     * 检查颜色是否适合作为背景（对比度检查）
     */
    fun isColorSuitableForBackground(color: Int): Boolean {
        val brightness = (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) / 1000
        return brightness in 50..200 // 适中的亮度范围
    }
} 