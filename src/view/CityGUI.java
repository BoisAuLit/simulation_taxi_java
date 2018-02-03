package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import model.Actor;
import model.City;
import model.DrawableItem;
import model.Item;
import model.Location;
import model.Passenger;
import model.PassengerSource;
import model.Taxi;
import model.Vehicle;

/**
 * Provide a view of the vehicles and passengers in the city.
 * 
 * @author David J. Barnes and Michael Kolling. Modified A. Morelle. Modified
 *         Bohao LI.
 * @version 2013.12.30
 */
public class CityGUI extends JFrame implements Actor {
    private static final long serialVersionUID = 20131230;

    private City city;
    private CityView cityView;

    private JPanel cityPanel;
    private JPanel rightPanel;
    private JPanel bottomPanel;

    private ChartPanel chartPanel;
    private final DefaultCategoryDataset barDataset;
    private JFreeChart chart;

    // private IHM ihm;
    private static final String ROW_KEY = "Vehicles";

    @SuppressWarnings("unused")
    private JLabel dimension;
    @SuppressWarnings("unused")
    private JLabel nbTaxis;
    private JLabel nbAvailableTaxis;
    private JLabel nbWaitingPeople;
    @SuppressWarnings("unused")
    private JLabel nbShuttles;
    private JLabel nbFreeShuttles;
    private JLabel missedPickup;

    private Map<Vehicle, JLabel> map;
    private PassengerSource passengerSource;

    /**
     * Constructor for objects of class CityGUI
     * 
     * @param city
     *            : the city whose state is to be displayed.
     * @param passengerSource
     *            The source of passengers
     */
    public CityGUI(City city, PassengerSource passengerSource) {
        // Create and set up the window
        super("Simulation of taxis operating on a city grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.city = city;
        this.passengerSource = passengerSource;
        cityView = new CityView(city.getWidth(), city.getHeight());

        cityPanel = new JPanel(new GridBagLayout());

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        bottomPanel = new JPanel(new GridLayout(0, 3));

        map = new HashMap<>();

        // Prepare the data set
        barDataset = new DefaultCategoryDataset();
        for (Item i : city.getItems())
            if (i instanceof Vehicle) {
                map.put((Vehicle) i, new JLabel("idle count [id = " + ((Vehicle) i).getID() + "] : "));
                barDataset.setValue(1, ROW_KEY, ((Vehicle) i).getID());
            }

        LineBorder lineBorder2 = new LineBorder(Color.blue, 2, true);
        TitledBorder titledBorder2 = new TitledBorder(lineBorder2, "Simulation information");
        titledBorder2.setTitleColor(Color.red);
        bottomPanel.setBorder(titledBorder2);

        bottomPanel.add(
                dimension = new JLabel("Dimesion: width = " + city.getWidth() + " , height = " + city.getHeight()));
        bottomPanel.add(nbTaxis = new JLabel("Number of taxis: " + city.getNbTaxis()));
        bottomPanel.add(nbAvailableTaxis = new JLabel("Available taxis: "));
        bottomPanel.add(nbWaitingPeople = new JLabel("Waiting people: "));
        bottomPanel.add(nbShuttles = new JLabel("Number of shuttles: " + city.getNbShuttles()));
        bottomPanel.add(nbFreeShuttles = new JLabel("Available shuttles: " + city.getNbShuttles()));
        bottomPanel.add(missedPickup = new JLabel("Missed pickups: "));

        // Create the chart
        chart = ChartFactory.createBarChart("Number of successful transport", "Vehicles", "Share", barDataset,
                PlotOrientation.HORIZONTAL, false, true, false);
        chartPanel = new ChartPanel(chart);

        for (JLabel j : map.values())
            bottomPanel.add(j);

        createContentPane();
        displayGUI();
    }

    /**
     * Create and set up the content pane
     */
    private void createContentPane() {
        cityPanel.add(cityView);
        rightPanel.add(chartPanel);
        rightPanel.add(new IHM());
        add(cityPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Size and display this frame
     */
    private void displayGUI() {
        int left, right, top, bottom, width, height;

        GraphicsConfiguration config = getGraphicsConfiguration();

        left = Toolkit.getDefaultToolkit().getScreenInsets(config).left;
        right = Toolkit.getDefaultToolkit().getScreenInsets(config).right;
        top = Toolkit.getDefaultToolkit().getScreenInsets(config).top;
        bottom = Toolkit.getDefaultToolkit().getScreenInsets(config).bottom;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        width = screenSize.width - left - right;
        height = screenSize.height - top - bottom;

        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(screenSize.width * 3 / 4, screenSize.height * 3 / 4));

        pack();
        setVisible(true);
    }

    /**
     * Display the current state of the city.
     */
    public void act() {
        cityView.preparePaint();

        int nb_free_taxis = 0;
        int nb_psg = 0;
        int nb_free_shuttles = 0;
        int missed_pickup = 0;

        for (Item item : city.getItems()) {
            if (item instanceof DrawableItem) {
                if (item instanceof Vehicle) {
                    barDataset.setValue(((Vehicle) item).getNbSuccess(), ROW_KEY, ((Vehicle) item).getID());
                    Vehicle v = (Vehicle) item;
                    map.get(v).setText("idle count [id = " + v.getID() + "] : " + v.getIdleCount());
                    if (((Vehicle) item).isFree())
                        if (item instanceof Taxi)
                            nb_free_taxis++;
                        else
                            nb_free_shuttles++;
                }

                if (item instanceof Passenger)
                    nb_psg++;

                DrawableItem it = (DrawableItem) item;
                Location location = it.getLocation();
                cityView.drawImage(location.getX(), location.getY(), it.getImage());
            }
        }

        missed_pickup = passengerSource.getMissedPickups();

        nbAvailableTaxis.setText("Available taxis: " + nb_free_taxis);
        nbWaitingPeople.setText("Waiting people: " + nb_psg);
        nbFreeShuttles.setText("Available shuttles: " + nb_free_shuttles);
        missedPickup.setText("Missed pickups: " + missed_pickup);

        repaint();
    }

    /**
     * IHM keyboard for dialing
     */
    private class IHM extends JPanel implements ActionListener {
        private static final long serialVersionUID = 1L;

        private JPanel numPad;
        private JPanel lowPanel;
        private JPanel topPanel;
        private JPanel bottomPanel;

        private JLabel dialLabel;
        private JLabel xLabel;
        private JLabel yLabel;
        private JTextField dialField;
        private JTextField xField;
        private JTextField yField;

        private JButton dialButton;
        private JButton clearButton;
        private JButton backspace;

        public IHM() {

            numPad = new JPanel(new GridLayout(5, 3));
            for (int i = 0; i < 9; i++)
                ((JButton) numPad.add(new JButton("" + (i + 1)))).addActionListener(this);
            ((JButton) numPad.add(new JButton("*"))).addActionListener(this);
            ((JButton) numPad.add(new JButton("0"))).addActionListener(this);
            ((JButton) numPad.add(new JButton("#"))).addActionListener(this);
            ((JButton) numPad.add(new JButton("T"))).addActionListener(this);
            ((JButton) numPad.add(new JButton("-"))).addActionListener(this);
            ((JButton) numPad.add(new JButton("S"))).addActionListener(this);

            lowPanel = new JPanel(new BorderLayout());
            topPanel = new JPanel();
            bottomPanel = new JPanel();

            dialLabel = new JLabel("Number to dial: ");
            xLabel = new JLabel("x: ");
            yLabel = new JLabel("y: ");
            dialField = new JTextField(15);
            xField = new JTextField(5);
            yField = new JTextField(5);
            dialButton = new JButton("Dial");
            clearButton = new JButton("Clear");
            backspace = new JButton("Backspace");

            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            bottomPanel.add(dialButton);
            bottomPanel.add(clearButton);
            bottomPanel.add(backspace);

            GroupLayout layout = new GroupLayout(topPanel);
            topPanel.setLayout(layout);
            layout.setAutoCreateGaps(true);

            // Horizontally, we want to align the labels and the text fields
            // along the left (LEADING) edge
            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(dialLabel)
                            .addComponent(xLabel).addComponent(yLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(dialField)
                            .addComponent(xField).addComponent(yField)));

            // Vertically, we want to align each label with his textfield
            // on the baseline of the components
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(dialLabel)
                            .addComponent(dialField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(xLabel)
                            .addComponent(xField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(yLabel)
                            .addComponent(yField)));
            dialButton.addActionListener(this);
            clearButton.addActionListener(this);
            backspace.addActionListener(this);

            add(numPad);
            lowPanel.add(topPanel, BorderLayout.CENTER);
            lowPanel.add(bottomPanel, BorderLayout.SOUTH);
            add(lowPanel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            if (source.getClass() == JButton.class) {
                if (source == dialButton) {
                    try {
                        int x = Integer.parseInt(xField.getText());
                        int y = Integer.parseInt(yField.getText());

                        int width = city.getWidth();
                        int height = city.getHeight();
                        if (x < 0 || x >= width || y < 0 || y >= height) {
                            JOptionPane.showMessageDialog(null, "Please input a right position in the city!",
                                    "Inane warning", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        passengerSource.handleDial(dialField.getText(), x, y);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please input just numbers!", "Inane warning",
                                JOptionPane.WARNING_MESSAGE);
                    }

                } else if (source == clearButton) {
                    dialField.setText("");
                } else if (source == backspace) {
                    if (dialField.getText().isEmpty())
                        return;
                    String newText = dialField.getText().substring(0, dialField.getText().length() - 1);
                    dialField.setText(newText);
                } else {
                    String original = dialField.getText();
                    String append = ((JButton) source).getText();
                    if (original.length() + append.length() > dialField.getColumns())
                        return;
                    dialField.setText(original + append);
                }
            }

        }
    }

    /**
     * Provide a graphical view of a rectangular city. This is a nested class (a
     * class defined inside a class) which defines a custom component for the
     * user interface. This component displays the city. This is rather advanced
     * GUI stuff - you can ignore this for your project if you like.
     */
    private class CityView extends JPanel {
        static final long serialVersionUID = 20131230;

        private final int VIEW_SCALING_FACTOR = 10;

        private int cityWidth;
        private int cityHeight;
        private int xScale;
        private int yScale;

        private Dimension size;
        private Graphics g;
        private Image cityImage;

        public CityView(int cityWidth, int cityHeight) {
            this.cityWidth = cityWidth;
            this.cityHeight = cityHeight;
            setBackground(Color.white);
            size = new Dimension(0, 0);
        }

        public void preparePaint() {
            // Draw the grid
            g.setColor(Color.white);
            g.fillRect(0, 0, size.width - 1, size.height - 1);

            g.setColor(Color.gray);

            for (int i = 0, x = 0; x < size.width; i++, x = i * xScale)
                g.drawLine(x, 0, x, size.height - 1);

            for (int i = 0, y = 0; y < size.height; i++, y = i * yScale)
                g.drawLine(0, y, size.width - 1, y);
        }

        public void drawImage(int x, int y, Image image) {
            g.drawImage(image, x * xScale + 1, y * yScale + 1, xScale - 1, yScale - 1, this);
        }

        /**
         * Draw the image for a particular item.
         */
        @Override
        public void paintComponent(Graphics g) {
            if (cityImage != null)
                g.drawImage(cityImage, 0, 0, null);
        }

        // to keep the aspect ratio of the city view
        // every time the size of the frame changes
        @Override
        public Dimension getPreferredSize() {
            Dimension pS = getParent().getSize(); // parent panel size, ps -->
                                                  // parentSize
            float cVW = 0;
            float cVH = 0; // cVW --> cityViewWidth, cVH --> cityViewHeight
            float rP = (pS.width) / ((float) pS.height); // ratio of parent
                                                         // panet, rP -->
                                                         // ratioParent
            float rC = (cityWidth) / ((float) cityHeight); // ratio of the city,
                                                           // rC --> ratioCity
            cVH = (cVW = rC >= rP ? pS.width : pS.height * rC) / rC;
            int cVW_ = 0;
            int cVH_ = 0;

            xScale = (cVW_ = ((int) cVW) - ((int) cVW) % cityWidth) / cityWidth;
            yScale = (cVH_ = ((int) cVH) - ((int) cVH) % cityHeight) / cityHeight;
            cityImage = cityView.createImage(cVW_ + 1, cVH_ + 1);
            g = cityImage.getGraphics();

            if (xScale < 1)
                xScale = VIEW_SCALING_FACTOR;
            if (yScale < 1)
                yScale = VIEW_SCALING_FACTOR;

            return (size = new Dimension(cVW_ + 1, cVH_ + 1));
        }

    }
}
