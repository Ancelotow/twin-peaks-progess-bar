package org.ancelotow.twinpeaksprogressbar

import com.intellij.openapi.util.IconLoader
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI
import javax.swing.ImageIcon


class TwinPeaksProgressBarUI : BasicProgressBarUI() {

    private val violetColor = Color(0x8A2BE2)
    private val floorIcon = IconLoader.getIcon("/images/black-lodge-floor.png", TwinPeaksProgressBarUI::class.java)
    private val curtainIcon = IconLoader.getIcon("/images/black-lodge-curtain.png", TwinPeaksProgressBarUI::class.java)
    private val manFromAnotherPlaceIcon = IconLoader.getIcon("/images/man-from-another-place.png", TwinPeaksProgressBarUI::class.java)


    override fun installUI(c: JComponent) {
        super.installUI(c)
        progressBar.isStringPainted = false
        progressBar.border = JBUI.Borders.empty()
    }

    override fun getPreferredSize(c: JComponent?): Dimension {
        return Dimension(super.getPreferredSize(c).width, JBUI.scale(20))
    }

    override fun paintDeterminate(g: Graphics, c: JComponent) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val width = progressBar.width
        val height = progressBar.height
        val amount = (width * progressBar.percentComplete).toInt()
        val arc = JBUI.scale(8)

        // Masque pour les coins arrondis
        val shape = java.awt.geom.RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc.toFloat(), arc.toFloat())
        val oldClip = g2.clip
        g2.clip(shape)

        // 1. Fond zigzag
        drawRepeatingTexture(g2, floorIcon, width, height)

        // 2. Rideau
        if (amount > 0) {
            drawStretchedImage(g2, curtainIcon, amount, height)
        }

        // 3. Bordure
        g2.clip = oldClip
        g2.color = violetColor
        g2.stroke = BasicStroke(JBUI.scale(2).toFloat())
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc)

        g2.dispose()
    }

    private fun drawRepeatingTexture(g2: Graphics2D, icon: Icon, width: Int, height: Int) {
        val w = icon.iconWidth
        val h = icon.iconHeight
        if (w <= 0 || h <= 0) return

        val ratio = w.toDouble() / h.toDouble()
        val scaledWidth = (height * ratio).toInt()

        val bi = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val big = bi.createGraphics()
        icon.paintIcon(null, big, 0, 0)
        big.dispose()

        val tp = TexturePaint(bi, Rectangle2D.Double(0.0, 0.0, scaledWidth.toDouble(), height.toDouble()))
        val oldPaint = g2.paint
        g2.paint = tp
        g2.fillRect(0, 0, width, height)
        g2.paint = oldPaint
    }

    private fun drawStretchedImage(g2: Graphics2D, icon: Icon, width: Int, height: Int) {
        val image = toImage(icon) ?: return
        g2.drawImage(image, 0, 0, width, height, null)
    }

    override fun paintIndeterminate(g: Graphics, c: JComponent) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val width = progressBar.width
        val height = progressBar.height
        val arc = JBUI.scale(8)

        val shape = RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc.toFloat(), arc.toFloat())
        g2.clip(shape)

        drawRepeatingTexture(g2, floorIcon, width, height)

        val frame = (System.currentTimeMillis() / 10 % (width + width / 3)).toInt()
        val curtainWidth = width / 3
        val startX = frame - curtainWidth

        val manImg = toImage(manFromAnotherPlaceIcon)
        if (manImg != null) {
            val srcW = manFromAnotherPlaceIcon.iconWidth
            val srcH = manFromAnotherPlaceIcon.iconHeight

            if (srcW > 0 && srcH > 0 && height > 0) {
                val extraH = JBUI.scale(10)
                val targetH = height + extraH
                val targetW = (targetH.toDouble() * srcW.toDouble() / srcH.toDouble()).toInt().coerceAtLeast(1)

                val frame = (System.currentTimeMillis() / 10 % (width + targetW)).toInt()
                val x = frame - targetW
                val y = -(extraH / 2)                    // centre verticalement malgré la hauteur extra

                g2.drawImage(manImg, x, y, targetW, targetH, null)
            }
        }

        g2.clip = null
        g2.color = violetColor
        g2.stroke = BasicStroke(JBUI.scale(2).toFloat())
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc)

        g2.dispose()
    }

    private fun toImage(icon: Icon): Image? {
        if (icon is ImageIcon) return icon.image

        val w = icon.iconWidth
        val h = icon.iconHeight
        if (w <= 0 || h <= 0) return null

        val bi = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics()
        icon.paintIcon(null, g, 0, 0)
        g.dispose()
        return bi
    }

    companion object {
        @JvmStatic
        fun createUI(c: JComponent?): TwinPeaksProgressBarUI {
            return TwinPeaksProgressBarUI()
        }
    }
}