package colorcount;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.JPanel;

class ImagePanel extends JPanel {

    private final Image img;

    public ImagePanel(Image img) {
        this.img = img.getScaledInstance(1000, 750, Image.SCALE_DEFAULT);
        Dimension size = new Dimension(1000,750);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
        this.setMaximumSize(size);
        this.setSize(size);
        this.setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(img, 0, 0, this);
    }
}
