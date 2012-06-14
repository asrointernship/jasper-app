/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import be.kuleuven.socialmap.model.Tweet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.*;

/**
 * A Dialog that shows Tweets to the user.
 *
 * @author Jasper Moeys
 */
public class TwitterDialog extends JDialog {
    
    private static int counter;
    
    public TwitterDialog(List<Tweet> list){
        super();
        counter++;
        setTitle("Twitter - " + counter);
        setLocation(150, 50);
        setLayout(new BorderLayout());
        
        JList jlist = new JList(list.toArray());
        jlist.setCellRenderer(new TweetRenderer());
        JScrollPane scrollpane = new JScrollPane(jlist);
        scrollpane.setPreferredSize(new Dimension(500, 400));
        add(scrollpane, BorderLayout.CENTER);
        
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private class TweetRenderer implements ListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList jlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Tweet tweet = (Tweet)value;
            JPanel listcell = new JPanel(new BorderLayout());
            JPanel infoPanel = new JPanel();
            listcell.setPreferredSize(new Dimension(450, 80));
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
            infoPanel.add(new JLabel(tweet.getFrom_user()));
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(new JLabel(tweet.getCreated_at().toString()));
            listcell.add(infoPanel, BorderLayout.NORTH);
            listcell.add(new JLabel("<html><p>" + tweet.getText() + "</p></html>"), BorderLayout.CENTER);
            listcell.setBorder(BorderFactory.createEtchedBorder());
            return listcell;
        }
        
    }
}