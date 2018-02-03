package controller;

import java.awt.event.*;
import java.awt.BorderLayout;
import java.util.concurrent.CountDownLatch;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Configuration reader for the simulation.
 * 
 * @author Bohao LI
 * @version 2017.03.23
 */
public class ConfigReader extends Thread implements ActionListener {

    private JFrame frame;
    // If user hasn't finished input, simulation will not start.
    private CountDownLatch latch;
    // User input will be stocked in the following variables.
    private int nb_taxis;
    private int nb_navettes;
    private int size_width;
    private int size_height;

    // Create the components we will put in the form.
    private JButton confirmButton;
    private JLabel nbTaxisLabel;
    private JLabel nbNavettesLabel;
    private JLabel largeurLabel;
    private JLabel hauteurLabel;
    private JTextField nbTaxisTextField;
    private JTextField nbNavettesTextField;
    private JTextField largeurTextField;
    private JTextField hauteurTextField;

    public ConfigReader(CountDownLatch latch) {
        this.latch = latch;
    }

    public void build() {
        // Create the components we will put in the form.
        nbTaxisLabel = new JLabel("nombre de taxis de la compagnie : ");
        nbNavettesLabel = new JLabel("nombre de navettes de la compagnie : ");
        largeurLabel = new JLabel("largeur de la grille de la ville : ");
        hauteurLabel = new JLabel("hauteur de la grille de la ville : ");
        nbTaxisTextField = new JTextField(5);
        nbNavettesTextField = new JTextField(5);
        largeurTextField = new JTextField(5);
        hauteurTextField = new JTextField(5);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildContentPane();
        frame.pack();
        // put the window int the midlle of the screen.
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void buildContentPane() {
        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel();
        confirmButton = new JButton("Confirm");
        bottomPanel.add(confirmButton);

        // Create the layout.
        GroupLayout layout = new GroupLayout(topPanel);
        topPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);

        // Horizontally, we want to align the labels and the text fields
        // along the left (LEADING) edge.
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(nbTaxisLabel)
                        .addComponent(nbNavettesLabel).addComponent(largeurLabel).addComponent(hauteurLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(nbTaxisTextField)
                        .addComponent(nbNavettesTextField).addComponent(largeurTextField)
                        .addComponent(hauteurTextField)));

        // Vertically, we want to align each label with his textfield
        // on the baseline of the components.
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(nbTaxisLabel)
                        .addComponent(nbTaxisTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(nbNavettesLabel)
                        .addComponent(nbNavettesTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(largeurLabel)
                        .addComponent(largeurTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(hauteurLabel)
                        .addComponent(hauteurTextField)));

        confirmButton.addActionListener(this);
        frame.getRootPane().setDefaultButton(confirmButton);
        frame.getContentPane().add(topPanel, BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * @return The number of taxis to be put in the city.
     */
    public int getNb_taxis() {
        return nb_taxis;
    }

    /**
     * @return The number of shuttles to be put in the city.
     */
    public int getNb_navettes() {
        return nb_navettes;
    }

    /**
     * @return The width of the city grid.
     */
    public int getSize_width() {
        return size_width;
    }

    /**
     * @return The height of the city grid.
     */
    public int getSize_height() {
        return size_height;
    }

    /**
     * Open the configuration window.
     */
    public void run() {
        frame = new JFrame("Configuration");
        build();
    }

    /**
     * Defien actions associated with the button on the configuration window.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // No text field should be empty.
        if (nbTaxisTextField.getText().isEmpty() || nbNavettesTextField.getText().isEmpty()
                || largeurTextField.getText().isEmpty() || hauteurTextField.getText().isEmpty()) {
            // Warn the user to input correctly.
            JOptionPane.showMessageDialog(null, "Please complete the form!", "Inane warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            nb_taxis = Integer.parseInt(nbTaxisTextField.getText());
            nb_navettes = Integer.parseInt(nbNavettesTextField.getText());
            size_width = Integer.parseInt(largeurTextField.getText());
            size_height = Integer.parseInt(hauteurTextField.getText());
            // User input end, we release the latch.
            latch.countDown();
            // Close the configuration window.
            frame.dispose();
        } catch (NumberFormatException ex) {
            // Inuput in all text fields should all be numbers.
            JOptionPane.showMessageDialog(null, "Please input just numbers!", "Inane warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
