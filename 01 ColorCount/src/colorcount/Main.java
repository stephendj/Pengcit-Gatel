package colorcount;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends javax.swing.JFrame {
    public Main() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        showDetailToggleButton.setVisible((false));
        detailPane.setBorder(null);
        detailTextArea.setVisible(false);
        detailTextArea.setText("");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        fileLabel = new javax.swing.JLabel();
        pathTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        countButton = new javax.swing.JButton();
        countLabel = new javax.swing.JLabel();
        showDetailToggleButton = new javax.swing.JToggleButton();
        detailPane = new javax.swing.JScrollPane();
        detailTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Color Count");

        imagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 986, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        controlPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        controlPanel.setToolTipText("");

        fileLabel.setText("File :");

        pathTextField.setEnabled(false);

        browseButton.setText("Browse");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BrowseActionPerformed(evt);
            }
        });

        countButton.setText("Count Color");
        countButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countButtonActionPerformed(evt);
            }
        });

        showDetailToggleButton.setActionCommand("Show Detail");
        showDetailToggleButton.setLabel("Show Detail");
        showDetailToggleButton.setName("Show Detail"); // NOI18N
        showDetailToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDetailToggleButtonActionPerformed(evt);
            }
        });

        detailTextArea.setEditable(false);
        detailTextArea.setColumns(20);
        detailTextArea.setRows(5);
        detailTextArea.setName("Detail Text"); // NOI18N
        detailPane.setViewportView(detailTextArea);
        detailTextArea.getAccessibleContext().setAccessibleName("Detail Text");

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(controlPanelLayout.createSequentialGroup()
                        .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detailPane)
                            .addGroup(controlPanelLayout.createSequentialGroup()
                                .addComponent(fileLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                .addComponent(browseButton)))
                        .addContainerGap())
                    .addGroup(controlPanelLayout.createSequentialGroup()
                        .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(countButton)
                            .addGroup(controlPanelLayout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(countLabel))
                            .addComponent(showDetailToggleButton))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileLabel)
                    .addComponent(pathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addGap(18, 18, 18)
                .addComponent(countButton)
                .addGap(33, 33, 33)
                .addComponent(countLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showDetailToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
        );

        detailPane.getAccessibleContext().setAccessibleName("DetailPane");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(imagePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(imagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BrowseActionPerformed
        chooser = new JFileChooser(new File(System.getProperty("user.home") + "\\Pictures"));
        chooser.setDialogTitle("Select Image");   
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        chooser.setFileFilter(imageFilter);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        { 
            imagePath = chooser.getSelectedFile().getPath();
            pathTextField.setText(imagePath);
            
            this.imagePanel.removeAll();
            ImagePanel image = new ImagePanel(new ImageIcon(imagePath).getImage());
            this.imagePanel.add(image);
            this.imagePanel.revalidate();
            this.imagePanel.repaint();
        }
    }//GEN-LAST:event_BrowseActionPerformed

    private void countButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countButtonActionPerformed
        if(pathTextField.getText().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "Please choose an image first!", "Missing Image",2);
        } else {
            try {
                countLabel.setText("Number of colors : " + ColorCount.getColorCollectionInFile(imagePath).size());
                showDetailToggleButton.setVisible(true);
            } catch(Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Problem occured, please try again");
            }
        }
    }//GEN-LAST:event_countButtonActionPerformed

    private void showDetailToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDetailToggleButtonActionPerformed
        if (countLabel.getText().equalsIgnoreCase("")) {
            
        }
        else {
            if (showDetailToggleButton.isSelected()) {
                detailPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
                detailTextArea.setVisible(true);
                try {
                    detailTextArea.setText(ColorCount.getColorDetailInFile(imagePath));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Problem occured, please try again");
                }
            }
            else {
                detailPane.setBorder(null);
                detailTextArea.setText("");
                detailTextArea.setVisible(false);
            }
        }
    }//GEN-LAST:event_showDetailToggleButtonActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    private JFileChooser chooser;
    private String imagePath;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton countButton;
    private javax.swing.JLabel countLabel;
    private javax.swing.JScrollPane detailPane;
    private javax.swing.JTextArea detailTextArea;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JTextField pathTextField;
    private javax.swing.JToggleButton showDetailToggleButton;
    // End of variables declaration//GEN-END:variables
}
