/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import be.kuleuven.socialmap.model.FlickrPhoto;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.*;

/**
 * A Dialog that shows FlickrPhotos to the user.
 *
 * @author Jasper Moeys
 */
public class FlickrDialog extends JDialog implements ActionListener{
    private JComboBox combobox;
    private JLabel label;
    private static int counter;
    
    public FlickrDialog(List<FlickrPhoto> list){
        counter++;
        setTitle("Flickr - " + counter);
        setLocation(150, 100);
        setLayout(new BorderLayout());
        JPanel centerpanel = new JPanel();
        centerpanel.setPreferredSize(new Dimension(504,504));
        
        label = new JLabel();
        label.setAlignmentX(CENTER_ALIGNMENT);
        centerpanel.setLayout(new BoxLayout(centerpanel, BoxLayout.PAGE_AXIS));
        centerpanel.add(Box.createVerticalGlue());
        centerpanel.add(label);
        centerpanel.add(Box.createVerticalGlue());
        add(centerpanel, BorderLayout.CENTER);
        
        
        combobox = new JComboBox(createArray(list));
        combobox.addActionListener(this);
        combobox.setPreferredSize(new Dimension(500, 30));
        add(combobox, BorderLayout.NORTH);
        
        UpdatePhoto();
        
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private Object[] createArray(List<FlickrPhoto> list){
        Object[] array = new Object[list.size()];
        int index = 0;
        for(FlickrPhoto photo : list){
            array[index] = new ListItem(photo);
            index++;
        }
        return array;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        UpdatePhoto();
    }
    
    private void UpdatePhoto(){
        FlickrPhoto photo = ((ListItem)combobox.getSelectedItem()).getPhoto();
        try {
            label.setIcon(new ImageIcon(new URL(photo.getPhotoUrl())));
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error loading image", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class ListItem{
        
        private FlickrPhoto photo;
        
        public ListItem(FlickrPhoto photo){
            this.photo = photo;
        }
        
        public FlickrPhoto getPhoto(){
            return photo;
        }
        
        @Override
        public String toString(){
            return photo.getTitle() + " - " + photo.getDateupload();
        }
    }
}
