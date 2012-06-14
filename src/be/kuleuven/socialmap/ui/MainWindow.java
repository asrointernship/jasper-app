/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * The Main Window of the application. This window will embed the Applet that takes care of the visualization.
 *
 * @author Jasper Moeys
 */
public class MainWindow extends JFrame {
    
    private MapApplet applet;
    private JPanel panel;
    private JButton zoomInButton, zoomOutButton;
    private JMenuBar menubar;
    private JMenu menuFile;
    private JMenuItem saveScreen, exit;
    private JCheckBox flickrCheck, twitterCheck, instaCheck, fsCheck;
    private JRadioButton texturesOnButton, texturesOffButton, mapOnButton, mapOffButton, legendOnButton, legendOffButton;
    private ButtonGroup texturesGroup, mapGroup, legendGroup;
    
    public MainWindow(){
        super("Social Map");
        this.setLocation(300, 100);
        this.setResizable(false);
        this.setLayout(new BorderLayout(2, 0));
        applet = new MapApplet(this);
        this.add(applet, BorderLayout.CENTER);
        
        
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(150, 0));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        Box zoomBox = Box.createHorizontalBox();
        zoomBox.add(Box.createHorizontalGlue());
        zoomInButton = new JButton(new AbstractAction("+") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                applet.zoomIn();
            }
        });
        zoomInButton.setMaximumSize(new Dimension(40, 25));
        zoomInButton.setMinimumSize(new Dimension(40, 25));
        zoomInButton.setPreferredSize(new Dimension(40, 25));
        zoomInButton.setFont(zoomInButton.getFont().deriveFont(Font.PLAIN, 30));
//        zoomInButton.setAlignmentX(CENTER_ALIGNMENT);
        zoomBox.add(zoomInButton);
        zoomBox.add(Box.createRigidArea(new Dimension(5, 0)));
        
        zoomOutButton = new JButton(new AbstractAction("-") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                applet.zoomOut();
            }
        });
        zoomOutButton.setMaximumSize(new Dimension(40, 25));
        zoomOutButton.setMinimumSize(new Dimension(40, 25));
        zoomOutButton.setPreferredSize(new Dimension(40, 25));
        zoomOutButton.setFont(zoomOutButton.getFont().deriveFont(Font.PLAIN, 30));
//        zoomOutButton.setAlignmentX(CENTER_ALIGNMENT);
        zoomBox.add(zoomOutButton);
        zoomBox.add(Box.createHorizontalGlue());
//        zoomBox.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(zoomBox);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        Box layersOuterBox = Box.createHorizontalBox();
        Box layersInnerBox = Box.createVerticalBox();
        layersOuterBox.add(layersInnerBox);
        layersOuterBox.add(Box.createHorizontalGlue());
        
        ItemListener layersListener = new MapLayersItemListener();
        flickrCheck =  new JCheckBox("Flickr");
        twitterCheck =  new JCheckBox("Twitter");
        instaCheck =  new JCheckBox("Instagram");
        fsCheck =  new JCheckBox("Foursquare");
        fsCheck.setSelected(true);
        flickrCheck.addItemListener(layersListener);
        twitterCheck.addItemListener(layersListener);
        instaCheck.addItemListener(layersListener);
        fsCheck.addItemListener(layersListener);
//        flickrCheck.setAlignmentX(LEFT_ALIGNMENT);
//        twitterCheck.setAlignmentX(LEFT_ALIGNMENT);
//        instaCheck.setAlignmentX(LEFT_ALIGNMENT);
//        fsCheck.setAlignmentX(LEFT_ALIGNMENT);
//        layersInnerBox.setAlignmentX(CENTER_ALIGNMENT);
        layersInnerBox.add(new JLabel("<html><b>Switch layers on/off</b></html>"));
        layersInnerBox.add(flickrCheck);
        layersInnerBox.add(twitterCheck);
        layersInnerBox.add(instaCheck);
        layersInnerBox.add(fsCheck);
        panel.add(layersOuterBox);
        
        Box texturesHBox = Box.createHorizontalBox();
        Box texturesVBox = Box.createVerticalBox();
        Box texturesInnerHBox = Box.createHorizontalBox();
        texturesGroup = new ButtonGroup();
        texturesOnButton = new JRadioButton("on");
        texturesOffButton = new JRadioButton("off");
        texturesGroup.add(texturesOffButton);
        texturesGroup.add(texturesOnButton);
        texturesOffButton.setSelected(true);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        texturesVBox.add(texturesHBox);
        texturesHBox.add(new JLabel("<html><b>Textures</b></html>"));
        texturesHBox.add(Box.createHorizontalGlue());
        texturesVBox.add(texturesInnerHBox);
        texturesInnerHBox.add(texturesOnButton);
        texturesInnerHBox.add(texturesOffButton);
        texturesInnerHBox.add(Box.createHorizontalGlue());
        ActionListener texturesListener = new TexturesActionListener();
        texturesOnButton.addActionListener(texturesListener);
        texturesOffButton.addActionListener(texturesListener);
        panel.add(texturesVBox);
        
        
        Box mapHBox = Box.createHorizontalBox();
        Box mapVBox = Box.createVerticalBox();
        Box mapInnerHBox = Box.createHorizontalBox();
        mapGroup = new ButtonGroup();
        mapOnButton = new JRadioButton("on");
        mapOffButton = new JRadioButton("off");
        mapGroup.add(mapOffButton);
        mapGroup.add(mapOnButton);
        mapOffButton.setSelected(true);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        mapVBox.add(mapHBox);
        mapHBox.add(new JLabel("<html><b>Background map</b></html>"));
        mapHBox.add(Box.createHorizontalGlue());
        mapVBox.add(mapInnerHBox);
        mapInnerHBox.add(mapOnButton);
        mapInnerHBox.add(mapOffButton);
        mapInnerHBox.add(Box.createHorizontalGlue());
        ActionListener mapListener = new MapActionListener();
        mapOnButton.addActionListener(mapListener);
        mapOffButton.addActionListener(mapListener);
        panel.add(mapVBox);
        
        Box legendHBox = Box.createHorizontalBox();
        Box legendVBox = Box.createVerticalBox();
        Box legendInnerHBox = Box.createHorizontalBox();
        legendGroup = new ButtonGroup();
        legendOnButton = new JRadioButton("on");
        legendOffButton = new JRadioButton("off");
        legendGroup.add(legendOffButton);
        legendGroup.add(legendOnButton);
        legendOffButton.setSelected(true);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        legendVBox.add(legendHBox);
        legendHBox.add(new JLabel("<html><b>Legend</b></html>"));
        legendHBox.add(Box.createHorizontalGlue());
        legendVBox.add(legendInnerHBox);
        legendInnerHBox.add(legendOnButton);
        legendInnerHBox.add(legendOffButton);
        legendInnerHBox.add(Box.createHorizontalGlue());
        ActionListener legendListener = new LegendActionListener();
        legendOnButton.addActionListener(legendListener);
        legendOffButton.addActionListener(legendListener);
        panel.add(legendVBox);
        
        this.add(panel, BorderLayout.EAST);
        
        menubar = new JMenuBar();
//        menubar.setCursor(Cursor.getDefaultCursor());
        menuFile = new JMenu("File");
//        menuFile.setCursor(Cursor.getDefaultCursor());
        menuFile.setMnemonic(KeyEvent.VK_F);
        saveScreen = new JMenuItem(new AbstractAction("Save screen") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fileChooser = new JFileChooser() {

                    @Override
                    public void approveSelection() {
                        File f = getSelectedFile();
                        if (f.exists() && getDialogType() == SAVE_DIALOG) {
                            int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                            switch (result) {
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION:
                                    return;
                                case JOptionPane.CANCEL_OPTION:
                                    cancelSelection();
                                    return;
                                case JOptionPane.CLOSED_OPTION:
                                    return;
                            }
                        }
                        super.approveSelection();
                    }
                };
                fileChooser.setFileFilter(new FileFilter() {

                    @Override
                    public boolean accept(File file) {
                        boolean res = false;
                        if (file.isDirectory()) {
                            res = true;
                        }
                        String name = file.getName();
                        if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".tiff") || name.endsWith(".tif") || name.endsWith(".JPG") || name.endsWith(".PNG") || name.endsWith(".JPEG") || name.endsWith(".TIFF") || name.endsWith(".TIF")) {
                            res = true;
                        }
                        return res;
                    }

                    @Override
                    public String getDescription() {
                        return "Images (jpeg, png, tiff)";
                    }
                });
                int option = fileChooser.showSaveDialog(MainWindow.this);
                if(option == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    applet.takeScreenshot(selectedFile.getAbsolutePath());
                }
            }
        });
//        saveScreen.setCursor(Cursor.getDefaultCursor());
        exit = new JMenuItem(new AbstractAction("Exit") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });
//        exit.setCursor(Cursor.getDefaultCursor());
        
        menuFile.add(saveScreen);
        menuFile.add(exit);
        menubar.add(menuFile);
        this.setJMenuBar(menubar);
        
        applet.init();
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    private class MapLayersItemListener implements ItemListener{

        @Override
        public void itemStateChanged(ItemEvent ie) {
            int state = ie.getStateChange();
            ItemSelectable selected = ie.getItemSelectable();
            if (selected == flickrCheck) {
                if (state == ItemEvent.SELECTED) {
                    applet.enableFlickr();
                } else if (state == ItemEvent.DESELECTED) {
                    applet.disableFlickr();
                }
            } else if (selected == twitterCheck) {
                if (state == ItemEvent.SELECTED) {
                    applet.enableTwitter();
                } else if (state == ItemEvent.DESELECTED) {
                    applet.disableTwitter();
                }
            } else if (selected == instaCheck) {
                if (state == ItemEvent.SELECTED) {
                    applet.enableInstagram();
                } else if (state == ItemEvent.DESELECTED) {
                    applet.disableInstagram();
                }
            } else if (selected == fsCheck) {
                if (state == ItemEvent.SELECTED) {
                    applet.enableFoursquare();
                } else if (state == ItemEvent.DESELECTED) {
                    applet.disableFoursquare();
                }
            }
        }
        
    }
    
    private class TexturesActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            ButtonModel selected = texturesGroup.getSelection();
            if(texturesOnButton.getModel() == selected){
                applet.enableTextures();
            } else if(texturesOffButton.getModel() == selected){
                applet.disableTextures();
            }
        }
    }
    
    private class MapActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            ButtonModel selected = mapGroup.getSelection();
            if(mapOnButton.getModel() == selected){
                applet.enableMap();
            } else if(mapOffButton.getModel() == selected){
                applet.disableMap();
            }
        }
    }
    
    private class LegendActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            ButtonModel selected = legendGroup.getSelection();
            if(legendOnButton.getModel() == selected){
                applet.enableLegend();
            } else if(legendOffButton.getModel() == selected){
                applet.disableLegend();
            }
        }
    }
    
}
