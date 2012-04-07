/**
 * @author Stephen Ranshous
 * Date Started: 2/8/2012
 *
 * Calculate the dilutions for lab
 * This uses M1V1 = M2V2
 * User input: M1 M2 V1
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DilutionCalc extends JFrame implements ActionListener {

    /* Properties */
    private double m1, m2;  // molarity of solution
    private double v1, v2;  // volume of solution
    private JPanel main;    // this will be the container panel for the GUI, using BorderLayout

    /* Center Panel */
    private JEditorPane display;    // area the results will be displayed
    private JPanel center;  // container for the display
    private JScrollPane results;    // scrolling pane for the display pane

    /* South Panel */
    private JPanel south;   // south panel that will hold the text fields for user input
    private JPanel m1panel, m2panel, v2panel;   // panels to hold the label and text fields for input
    private JLabel m1l, m2l, v2l;   // labels for the input boxes
    private JTextField m1in, m2in, v2in;    // input boxes for the numbers from the user
    private final int TEXT_BOX_SIZE = 5;    // size for text fields
    private JButton submit, clear; // submit the input, clear the screen
    private JComboBox m1units, m2units, v2units;    // dropbox to select units for volume
    private final Object[] VOLUME_UNIT_OPTIONS = {"L", "mL", "\u00B5L", "nL"};  // type object instead of using java
                                                                                // generics so that it is backwards
                                                                                // compatible with pre Java 7
    private final Object[] MOLAR_UNIT_OPTIONS = {"M", "mM", "\u00B5M", "nM"};



    /* Constructor */

    public DilutionCalc() {
        super("Dilution Calculator w00t!");
        main = new JPanel();
        main.setLayout(new BorderLayout());
        add(main);
        createCenter();
        createSouth();
        main.add(south, BorderLayout.SOUTH);
        main.add(center, BorderLayout.CENTER);
    }


    /* Creation methods */

    public void createCenter() {
        center = new JPanel();
        display = new JEditorPane();
        results = new JScrollPane(display);
        results.setPreferredSize(new Dimension(780, 530));
        display.setEditable(false);
        center.add(results);
    }

    public void createSouth() {
        south = new JPanel();
        south.setPreferredSize(new Dimension(800, 30));

        // Each column will hold its own JPanel which will be used as a container for both the
        // label and text fields. The fourth column will be used for the "Calculate" button
        // and the fifth for the clear screen button
        south.setLayout(new GridLayout(0, 5, 5, 5));

        // create the input panels
        m1panel = new JPanel();     m2panel = new JPanel();     v2panel = new JPanel();

        m1l = new JLabel("M1:");    m2l = new JLabel("M2:");    v2l = new JLabel("V2:");

        m1in = new JTextField(TEXT_BOX_SIZE);
        m2in = new JTextField(TEXT_BOX_SIZE);
        v2in = new JTextField(TEXT_BOX_SIZE);

        m1units = new JComboBox(MOLAR_UNIT_OPTIONS);
        m2units = new JComboBox(MOLAR_UNIT_OPTIONS);
        v2units = new JComboBox(VOLUME_UNIT_OPTIONS);

        m1panel.add(m1l);   m1panel.add(m1in);  m1panel.add(m1units);
        m2panel.add(m2l);   m2panel.add(m2in);  m2panel.add(m2units);
        v2panel.add(v2l);   v2panel.add(v2in);  v2panel.add(v2units);

        submit = new JButton("Calculate");      submit.addActionListener(this);
        clear = new JButton("Clear Screen");    clear.addActionListener(this);

        // add the components
        south.add(m1panel); south.add(m2panel); south.add(v2panel);
        south.add(submit);  south.add(clear);
    }

    /* Override ActionListener */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submit) {
            String m1unit = ((String)(m1units.getSelectedItem())).trim();
            String m2unit = ((String)(m2units.getSelectedItem())).trim();
            String v2unit = ((String)(v2units.getSelectedItem())).trim();
            String m1s = m1in.getText().trim();
            String m2s = m2in.getText().trim();
            String v2s = v2in.getText().trim();

            if(m1s.equals("")) {
                JOptionPane.showMessageDialog(this, "Please input a value for M1", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(m2s.equals("")) {
                JOptionPane.showMessageDialog(this, "Please input a value for M2", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(v2s.equals("")) {
                JOptionPane.showMessageDialog(this, "Please input a value for V2", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                m1 = Double.parseDouble(m1s);
                m2 = Double.parseDouble(m2s);
                v2 = Double.parseDouble(v2s);
            }
            catch(Exception ex) {
                JOptionPane.showMessageDialog(this, "Please input numbers only", "Not a number", JOptionPane.ERROR_MESSAGE);
                return;
            }

            display.setText(display.getText() + "Your dilution input was:" + "   M1: " + m1 + m1unit + "  M2: " + m2 + m2unit + "  V2: " + v2 + v2unit);
            display.setText(display.getText() + "\nV1 = " + "( " + m2 + m2unit + " * " + v2 + v2unit + " ) / " + m1 + m1unit);

            /* Convert based on units */

            m1 = readUnits(m1, m1unit);
            m2 = readUnits(m2, m2unit);
            v2 = readUnits(v2, v2unit);

            /* Calculate the output v1 */
            v1 = ((m2*v2)/m1);
            String v1unit = "";

            if(v1 * 1000 >= 1 && v1 * 1000 <= 100) {
                v1 *= 1000;
                v1unit = "mL";
            }
            else if(v1 * 1000000 >= 1 && v1 * 1000000 <= 100) {
                v1 *= 1000000;
                v1unit = "\u00B5L";
            }
            else if(v1 * 1000000000 >= 1 && v1 * 1000000000 <= 100) {
                v1 *= 1000000000;
                v1unit = "nL";
            }
            else {
                v1unit = "L";
            }

            display.setText(display.getText() + ("\nV1 = " + v1 + v1unit) + "\n\n");
        }
        else if(e.getSource() == clear) {
            display.setText("");
        }
    }

    public double readUnits(double item, String itemUnits) {

            /* Convert based on units */
            if(itemUnits.equals("mM") || itemUnits.equals("mL")) {
                item *= 1E-03;
            }
            else if(itemUnits.equals("\u00B5M") || itemUnits.equals("\u00B5L")) {
                item *= 1E-06;
            }
            else if(itemUnits.equals("nM") || itemUnits.equals("nL")) {
                item *= 1E-09;
            }

            return item;
    }

    /* Main method */
    public static void main(String[] args) {
        DilutionCalc dc = new DilutionCalc();
        dc.setDefaultCloseOperation(EXIT_ON_CLOSE);     // close on exit
        dc.setSize(new Dimension(800, 600));            // set the size to be 800x600 (pixels)
        dc.setLocationRelativeTo(null);                 // center the frame (note: after sizing so it is really centered)
        dc.setVisible(true);                            // show it off to the world
    }

}

