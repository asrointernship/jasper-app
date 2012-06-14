/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import be.kuleuven.socialmap.model.InstagramPhoto;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.*;

/**
 * A Dialog that shows InstagramPhotos to the user.
 *
 * @author Jasper Moeys
 */
public class InstagramDialog extends JDialog implements ActionListener{
    
    private JComboBox combobox;
    private JLabel label;
    private static int counter;
    
    public InstagramDialog(List<InstagramPhoto> list){
        super();
        counter++;
        setTitle("Instagram - " + counter);
        setLocation(100, 100);
        setLayout(new BorderLayout());
        label = new JLabel();

        add(label, BorderLayout.CENTER);

        combobox = new JComboBox(createArray(list));
        combobox.addActionListener(this);
        combobox.setPreferredSize(new Dimension(612, 30));
        add(combobox, BorderLayout.NORTH);
        
        UpdatePhoto();
            
        label.setPreferredSize(new Dimension(612,612));
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private Object[] createArray(List<InstagramPhoto> list){
        Object[] array = new Object[list.size()];
        int index = 0;
        for(InstagramPhoto photo : list){
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
        InstagramPhoto photo = ((ListItem)combobox.getSelectedItem()).getPhoto();
        try {
            label.setIcon(new ImageIcon(new URL(photo.getUrl())));
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error loading image", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private class ListItem{
        
        private InstagramPhoto photo;
        
        public ListItem(InstagramPhoto photo){
            this.photo = photo;
        }
        
        public InstagramPhoto getPhoto(){
            return photo;
        }
        
        @Override
        public String toString(){
            return photo.getUsername() + " - " + photo.getCreated_at();
        }
    }
}
