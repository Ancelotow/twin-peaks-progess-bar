package org.ancelotow.twinpeaksprogressbar

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.plaf.basic.BasicProgressBarUI


class TwinPeaksProgressBarUI : BasicProgressBarUI() {

    private val progressColor = Color(0xCC0000) // Rouge Twin Peaks
    private val backgroundColor = JBColor.BLACK
    private val zigzagColor = Color(0xFFFFFF) // Blanc pour le zigzag

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

        // Fond Noir
        g2.color = backgroundColor
        g2.fillRect(0, 0, width, height)

        // Dessine le zigzag au fond (optionnel, mais très Twin Peaks)
        drawZigzag(g2, width, height)

        // Barre de progression Rouge
        g2.color = progressColor
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)
        g2.fillRect(0, 0, amount, height)

        g2.dispose()
    }

    override fun paintIndeterminate(g: Graphics, c: JComponent) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val width = progressBar.width
        val height = progressBar.height

        // Fond Noir
        g2.color = backgroundColor
        g2.fillRect(0, 0, width, height)

        // Zigzag
        drawZigzag(g2, width, height)

        // Animation "Rideau Rouge" qui bouge
        val frame = (System.currentTimeMillis() / 10 % width).toInt()
        g2.color = progressColor
        g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)
        
        val blockWidth = width / 3
        var startX = frame - blockWidth
        if (startX < 0) {
            g2.fillRect(0, 0, blockWidth + startX, height)
            startX = width + startX
        }
        g2.fillRect(startX, 0, blockWidth, height)

        g2.dispose()
    }

    private fun drawZigzag(g2: Graphics2D, width: Int, height: Int) {
        val oldColor = g2.color
        val oldStroke = g2.stroke
        
        g2.color = Color(0x333333) // Gris foncé pour un zigzag discret en fond
        val size = height / 2
        val stroke = BasicStroke(2f)
        g2.stroke = stroke

        var x = 0
        while (x < width) {
            val poly = Polygon()
            poly.addPoint(x, height)
            poly.addPoint(x + size, 0)
            poly.addPoint(x + size * 2, height)
            g2.drawPolyline(poly.xpoints, poly.ypoints, poly.npoints)
            x += size * 2
        }

        g2.color = oldColor
        g2.stroke = oldStroke
    }

    companion object {
        @JvmStatic
        fun createUI(c: JComponent?): TwinPeaksProgressBarUI {
            return TwinPeaksProgressBarUI()
        }
    }
}