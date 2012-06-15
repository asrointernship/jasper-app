/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import be.kuleuven.socialmap.io.StaticIO;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This class takes care of the initialization. This way the application can run
 * as OS independent as possible. Invoke the {@link #run()} method to start.
 *
 * @author Jasper Moeys
 */
public class Initializer extends JDialog {

    private CardLayout layout;
    private final String START = "start";
    private final String FILECHOOSER = "filechooser";
    private final String FINISHED = "finished";
    private final String OSCHOOSER = "oschooser";
    private String currentDirectory;
    private String os;
    private boolean finished;

    /**
     * Starts the Initialization Dialog. This will only collect the user's
     * input, but not actually initialize the application settings or save any
     * preferences. Invoke the static method {@link #run()} instead to take care
     * of that.
     */
    public Initializer() {
        super((JDialog) null, "Initialize", true);
        this.setLocation(300, 100);
        this.setSize(new Dimension(500, 500));
        this.setResizable(false);
        layout = new CardLayout();
        this.setLayout(layout);


        JPanel start = new JPanel(null);
        JLabel startText = new JLabel("<html><p>For the application to work properly you will have to show me the directory where the application is located, containing the jar and configuration files.</p><br><p>Click \"Next\" to continue and select the appropriate directory.</p></html>");
        startText.setSize(new Dimension(460, 400));
        startText.setLocation(20, 20);
        startText.setVerticalAlignment(JLabel.TOP);
        startText.setHorizontalAlignment(JLabel.LEFT);
        start.add(startText);
        JButton startButton = new JButton(new AbstractAction("Next") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                layout.next(Initializer.this.getContentPane());
            }
        });
        startButton.setSize(new Dimension(100, 30));
        startButton.setLocation(380, 450);
        start.add(startButton);

        this.add(START, start);

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.add(FILECHOOSER, fileChooser);
        fileChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!ae.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                    currentDirectory = fileChooser.getSelectedFile().toString();
                    File file = new File(currentDirectory);
                    File props = new File(file, "socialmap.properties");
                    if (!props.exists()) {
                        JOptionPane.showMessageDialog(Initializer.this, "This directory doesn't contain the socialmap.properties file.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        layout.next(Initializer.this.getContentPane());
//                    Initializer.this.dispose();
                    }
                } else {
                    Initializer.this.dispose();
                }
            }
        });

        /*
         * start
         */
        JPanel osProps = new JPanel(null);
        JLabel osText = new JLabel("<html><p>Please select your Operating System.</p><br><p>Click \"Next\" to continue.</p></html>");
        osText.setSize(new Dimension(450, 75));
        osText.setLocation(20, 20);
        osText.setVerticalAlignment(JLabel.TOP);
        osText.setHorizontalAlignment(JLabel.LEFT);
        osProps.add(osText);
        final JButton nextButton = new JButton(new AbstractAction("Next") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                layout.next(Initializer.this.getContentPane());
            }
        });
        nextButton.setSize(new Dimension(100, 30));
        nextButton.setLocation(380, 450);
        nextButton.setEnabled(false);
        osProps.add(nextButton);
        final JList list = new JList(new Object[]{new ListItem("Linux 32 bit", "linux32"),
                    new ListItem("Linux 64 bit", "linux64"),
                    new ListItem("Windows 32 bit", "windows32"),
                    new ListItem("Windows 64 bit", "windows64"),
                    new ListItem("Mac OS X", "macosx"),});
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setFixedCellHeight(30);
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (lse.getValueIsAdjusting() == false) {
                    Object selected = list.getSelectedValue();
                    if (selected == null) {
                        nextButton.setEnabled(false);
                    } else {
                        nextButton.setEnabled(true);
                        ListItem listitem = (ListItem) selected;
                        Initializer.this.os = listitem.getValue();
                    }
                }
            }
        });
        list.setSize(200, 150);
        list.setLocation(20, 100);
        osProps.add(list);
        this.add(OSCHOOSER, osProps);
        /*
         * stop
         */


        JPanel finished = new JPanel(null);
        JLabel finishText = new JLabel("<html><p>The initialization appears to have finished successfully.</p><br><p>Click \"Finish\" to continue.</p></html>");
        finishText.setSize(new Dimension(450, 450));
        finishText.setLocation(20, 20);
        finishText.setVerticalAlignment(JLabel.TOP);
        finishText.setHorizontalAlignment(JLabel.LEFT);
        finished.add(finishText);
        JButton finishButton = new JButton(new AbstractAction("Finish") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Initializer.this.dispose();
                Initializer.this.finished = true;
            }
        });
        finishButton.setSize(new Dimension(100, 30));
        finishButton.setLocation(380, 450);
        finished.add(finishButton);

        this.add(FINISHED, finished);


        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * When the user has finished, will return the directory where the
     * application is located.
     *
     * @return the current directory
     */
    public String getCurrentDirectory() {
        return this.currentDirectory;
    }

    /**
     * When the user has finished, will return the OS string.
     *
     * @return the OS
     */
    public String getOs() {
        return this.os;
    }

    /**
     * Returns true when the user has succesfully entered all required
     * parameters and has clicked "Finish".
     *
     * @return true when the user has finished the initialization process, false
     * if he's closed or canceled the Dialog.
     */
    public boolean isFinished() {
        return this.finished;
    }

    private class ListItem extends AbstractMap.SimpleEntry<String, String> {

        public ListItem(String key, String value) {
            super(key, value);
        }

        @Override
        public String toString() {
            return this.getKey();
        }
    }

    /**
     * Invoke this method to run the initialization process.
     *
     * @return true if initialization ended succesfully, false if something went
     * wrong or the user canceled it.
     */
    public static boolean run() {
        boolean res = false;
        boolean doInit = false;
        String currentDirectory = null;
        String os = null;
        if (StaticIO.fileExists(".socialMapInit", false)) {
            try {
                String[] initProps = StaticIO.getFileContents(".socialMapInit", false).split("\n");
                currentDirectory = initProps[0];
                os = initProps[1];
                if (os.isEmpty() || currentDirectory.isEmpty()) {
                    doInit = true;
                }
            } catch (IOException ex) {
                doInit = true;
            } catch (IndexOutOfBoundsException ex) {
                doInit = true;
            }
            if (doInit) {
                int answer = JOptionPane.showConfirmDialog(null, "Something went wrong during auto initialization. \nDo you want to initialize manually?", "Initialization", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer != JOptionPane.OK_OPTION) {
                    doInit = false;
                }
            }
        } else {
            doInit = true;
        }

        if (doInit) {
            Initializer init = new Initializer();
            if (init.isFinished()) {
                currentDirectory = init.getCurrentDirectory();
                os = init.getOs();
            }
        }

        if (currentDirectory != null && os != null) {
            StaticIO.setCurrentDirectory(currentDirectory);
            System.setProperty("java.library.path", StaticIO.getPath("data/"+os));

            try {
                Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);

                res = true;

                StaticIO.writeToFile(".socialMapInit", currentDirectory + "\n" + os, false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Something went wrong:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalAccessException ex) {
                JOptionPane.showMessageDialog(null, "Something went wrong:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NoSuchFieldException ex) {
                JOptionPane.showMessageDialog(null, "Something went wrong:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(null, "Something went wrong:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Your preferences couldn't be saved:\n" + ex, "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
        return res;
    }
}
