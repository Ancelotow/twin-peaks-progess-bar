package org.ancelotow.twinpeaksprogressbar

import com.intellij.openapi.util.IconLoader
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.beans.PropertyChangeListener
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI
import javax.swing.ImageIcon
import javax.swing.Timer


class TwinPeaksProgressBarUI : BasicProgressBarUI(), PropertyChangeListener {
    private val violetColor = Color(0x8A2BE2)
    private val floorIcon = IconLoader.getIcon("/images/black-lodge-floor.png", TwinPeaksProgressBarUI::class.java)
    private val curtainIcon = IconLoader.getIcon("/images/black-lodge-curtain.png", TwinPeaksProgressBarUI::class.java)
    private val manFromAnotherPlaceIcon = IconLoader.getIcon("/images/man-from-another-place.png", TwinPeaksProgressBarUI::class.java)

    override fun installUI(c: JComponent) {
        super.installUI(c)
        progressBar.isStringPainted = false
        progressBar.border = JBUI.Borders.empty()

        if (progressBar.isIndeterminate) {
            animationTimer.start()
        }
        progressBar.addPropertyChangeListener(this)

    }

    override fun propertyChange(evt: java.beans.PropertyChangeEvent) {
        if (evt.propertyName == "indeterminate") {
            previousX = 0
            movingRight = true

            if (progressBar.isIndeterminate) {
                animationTimer.start()
            } else {
                animationTimer.stop()
            }
        }
    }

    override fun uninstallUI(c: JComponent) {
        animationTimer.stop()
        progressBar.removePropertyChangeListener(this)
        super.uninstallUI(c)
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

        val shape = java.awt.geom.RoundRectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat(), arc.toFloat(), arc.toFloat())
        val oldClip = g2.clip
        g2.clip(shape)

        drawRepeatingTexture(g2, floorIcon, width, height)

        if (amount > 0) {
            drawStretchedImage(g2, curtainIcon, amount, height)
        }

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
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val width = progressBar.width
        val height = progressBar.height
        if (width <= 0 || height <= 0) return

        val arc = JBUI.scale(8)

        val shape = RoundRectangle2D.Float(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            arc.toFloat(), arc.toFloat()
        )
        g2.clip = shape

        // Fond
        drawRepeatingTexture(g2, floorIcon, width, height)

        // Image mobile (DESSIN SEULEMENT)
        if (manImg != null) {
            val srcW = manFromAnotherPlaceIcon.iconWidth
            val srcH = manFromAnotherPlaceIcon.iconHeight

            if (srcW > 0 && srcH > 0) {
                val extraH = JBUI.scale(10)
                targetH = height + extraH
                targetW = ((targetH.toDouble() * srcW) / srcH).toInt().coerceAtLeast(1)

                val x = previousX
                val y = -(extraH / 2)

                val transform = g2.transform

                if (!facingRight) {
                    // retourne horizontalement autour du centre de l'image
                    g2.translate(x + targetW / 2.0, 0.0)
                    g2.scale(-1.0, 1.0)
                    g2.translate(-(x + targetW / 2.0), 0.0)
                }

                g2.drawImage(manImg, x, y, targetW, targetH, null)

                g2.transform = transform
            }
        }

        // Bordure
        g2.clip = null
        g2.color = violetColor
        g2.stroke = BasicStroke(JBUI.scale(2).toFloat())
        g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc)
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

    private var previousX = 0
    private var movingRight = true
    private var targetW = 0
    private var targetH = 0
    private var facingRight = true
    private var currentFrameForFacing = 0


    private val manImg = toImage(manFromAnotherPlaceIcon)

    private val animationTimer = Timer(32) { // ~60 FPS
        val step = JBUI.scale(2)
        val width = progressBar.width

        if (targetW == 0 || width <= 0) return@Timer

        if (movingRight) {
            previousX += step
            if (previousX + targetW >= width) {
                previousX = width - targetW
                movingRight = false
            }
        } else {
            previousX -= step
            if (previousX <= 0) {
                previousX = 0
                movingRight = true
            }
        }

        currentFrameForFacing++
        if(currentFrameForFacing >= 10) {
            currentFrameForFacing = 0
            facingRight = !facingRight
        }

        progressBar.repaint()
    }

    companion object {
        @JvmStatic
        fun createUI(c: JComponent?): TwinPeaksProgressBarUI {
            return TwinPeaksProgressBarUI()
        }
    }
}