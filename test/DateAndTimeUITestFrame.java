package com.motekew.vse.test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.motekew.vse.ui.*;

/**
 * Initiates <code>TheDateUI</code> and <code>TimeOfDayUI</code> objects
 * in a frame to demonstrate date and time objects in use. 
 */
public class DateAndTimeUITestFrame extends JFrame {
  static final long serialVersionUID = 1L;
  public DateAndTimeUITestFrame() {
    setTitle("DateAndTimeUITestFrame");
    addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                          System.exit(0);
                        }
                      }
    );

    Container contentPane = getContentPane();

    JPanel exitPanel = new JPanel();
    ExitApplicationAction eaa = new ExitApplicationAction();
    JButton exitBtn = new JButton("Exit");
    exitPanel.add(exitBtn);
    exitBtn.addActionListener(eaa);
    
    contentPane.add(exitPanel, "South");

    TheDateUI dateUI = new TheDateUI();
    TimeOfDayUI timeUI = new TimeOfDayUI();

    contentPane.add(dateUI, "West");
    contentPane.add(timeUI, "East");

    pack();

  }

  public static void main(String[] args) {
    DateAndTimeUITestFrame frame = new DateAndTimeUITestFrame();
    frame.setVisible(true);
  }
}
