/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import be.kuleuven.socialmap.model.FoursquareVenue;
import be.kuleuven.socialmap.model.Tweet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;

/**
 * A Dialog that shows FoursquareVenues to the user.
 *
 * @author Jasper Moeys
 */
public class FoursquareDialog extends JDialog {
    
    private static int counter;
    
    public FoursquareDialog(List<FoursquareVenue> list){
        super();
        counter++;
        setTitle("Foursquare - " + counter);
        setLocation(150, 50);
        setLayout(new BorderLayout());
        
        JList jlist = new JList(list.toArray());
        jlist.setCellRenderer(new FsRenderer());
        JScrollPane scrollpane = new JScrollPane(jlist);
        scrollpane.setPreferredSize(new Dimension(500, 400));
        add(scrollpane, BorderLayout.CENTER);
        
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private class FsRenderer implements ListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            FoursquareVenue venue = (FoursquareVenue)value;
            JPanel listcell = new JPanel(new GridLayout(4, 1));
            listcell.setPreferredSize(new Dimension(450, 80));
            
            listcell.add(new JLabel("Name: " + venue.getName()));
            String categories = "";
            for(String cat : venue.getCategories()){
                categories += ", " + cat;
            }
            if(categories.isEmpty()) {
                categories = "/";
            } else {
                categories = categories.substring(2, categories.length());
            }
            listcell.add(new JLabel("Categorie(s): " + categories));
            listcell.add(new JLabel("Total Checkins: " + venue.getCheckinsCount()));
            listcell.add(new JLabel("Unique Checkins: " + venue.getUsersCount()));
            
            listcell.setBorder(BorderFactory.createEtchedBorder());
            return listcell;
        }
        
    }
}