import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Example FiveGuys: an simulator for a Five Guys ordering system, mainly here
 * as a demonstration of several Java Swing components.
 * 
 * Please note that the GUI design here is certainly not the most appropriate
 * for the task at hand but instead has been chosen to demonstrate as many
 * different types of components as possible in a somewhat reasonable way.
 *
 * Original by Jim Teresco, The College of Saint Rose, CSC 523, Summer 2014
 * 
 * @author Jim Teresco
 * @version Spring 2023
 */

// we have many interfaces implemented here, we separate them by commas
public class FiveGuys implements Runnable, ActionListener, ItemListener, ChangeListener {

    // the maximum number of beef patties allowed on a burger
    private static final int MAX_PATTIES = 4;

    // what can we get on a burger? adding items here will cause the code in run
    // to create a JCheckBox for each option and the values of the checked boxes
    // to be added to the order
    private static final String[] burgerOptions = {
            "cheese",
            "bacon",
            "ketchup",
            "mayo",
            "onion",
            "pickle",
            "lettuce",
            "tomato"
    };

    // fry sizes: again, the code in init will use this to populate appropriate
    // controls, this time, radio buttons
    private static final String[] frySizes = {
            "None",
            "Little",
            "Regular",
            "Large"
    };

    // peanut preferences: same idea, but these will correspond to values
    // in a slider
    private static final String[] peanutAmounts = {
            "None",
            "A little",
            "Some",
            "Quite a bit",
            "Lots"
    };

    // the components we need to remember get instance variables
    private JTextArea orderStatus;
    private JTextField name;
    private JComboBox<String> burgerMenu;
    private JCheckBox[] burgerChecks;
    private JRadioButton[] frySizeButtons;
    private JCheckBox cajunOption;
    private JSpinner drinkSpinner;
    private JSlider peanutSlider;
    private JLabel peanutLabel;
    private JButton addButton, clearButton;

    @Override
    public void run() {
        // set up the GUI "look and feel" which should match
        // the OS on which we are running
        JFrame.setDefaultLookAndFeelDecorated(true);

        // create a JFrame in which we will build our very
        // tiny GUI, and give the window a name
        JFrame frame = new JFrame("FiveGuys");
        frame.setPreferredSize(new Dimension(800, 425));

        // tell the JFrame that when someone closes the
        // window, the application should terminate
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // a BorderLayout JPanel to occupy the frame
        JPanel framePanel = new JPanel(new BorderLayout());
        frame.add(framePanel);

        // we will use the NORTH, CENTER, and SOUTH of the default
        // BorderLayout for panels that contain the order status,
        // next order details, and control buttons, respectively.
        JPanel statusPanel = new JPanel();
        // a BoxLayout with the PAGE_AXIS option gives us a vertical stack
        // within this panel
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.PAGE_AXIS));

        // this attempt to left justify didn't completely work
        JLabel current = new JLabel("Current Order:");
        current.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(current);

        // we would like to display the order results in a large text area, but would
        // like to have scrollbars appear when needed, so we create a JScrollPane,
        // and place an uneditable JTextArea within
        JScrollPane scrollFrame = new JScrollPane();
        orderStatus = new JTextArea(10, 50);
        // we don't want the user editing the order directly - they should
        // add items here using the controls in the window.
        orderStatus.setEditable(false);
        scrollFrame.add(orderStatus);
        scrollFrame.setViewportView(orderStatus);
        statusPanel.add(scrollFrame);

        framePanel.add(statusPanel, BorderLayout.NORTH);

        // next, the main controls area where the order information is selected
        // again with a BoxLayout to get a vertical stack - here there will be a
        // stack of JPanels each of which holds controls for one of the types of
        // items our customers can order
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.PAGE_AXIS));

        // the customer's name (required field)
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel("Order For: "));
        name = new JTextField("", 10);
        namePanel.add(name);
        orderPanel.add(namePanel);

        // burger order
        JPanel burgerPanel = new JPanel();
        burgerPanel.add(new JLabel("Burger:"));
        orderPanel.add(burgerPanel);

        // a JComboBox to select the number of beef patties on our burger
        // and "No burger" if none is ordered
        burgerMenu = new JComboBox<String>();
        burgerMenu.addItem("No Burger");
        burgerMenu.addItem("1 Patty");
        for (int numPatties = 2; numPatties <= MAX_PATTIES; numPatties++) {
            burgerMenu.addItem(numPatties + " Patties");
        }
        burgerMenu.setSelectedItem("No Burger");

        // we want to be notified when someone changes this
        // so the option buttons can be enabled/disabled
        burgerMenu.addActionListener(this);

        burgerPanel.add(burgerMenu);

        // we will create JCheckBox items for each optional topping
        // available on our burgers
        burgerChecks = new JCheckBox[burgerOptions.length];

        for (int option = 0; option < burgerOptions.length; option++) {
            burgerChecks[option] = new JCheckBox(burgerOptions[option]);
            burgerChecks[option].setSelected(false);
            // these options are only available when something other than
            // "No Burger" is selected in the combo box
            burgerChecks[option].setEnabled(false);
            burgerPanel.add(burgerChecks[option]);
        }

        // fries order
        JPanel friesPanel = new JPanel();
        friesPanel.add(new JLabel("Fries:"));
        orderPanel.add(friesPanel);

        // just for something different, we'll create radio buttons for the french fry
        // sizes - by adding the radio buttons to this same button group, we are
        // ensuring
        // that at most one can ever be selected at once
        ButtonGroup fryRadio = new ButtonGroup();
        frySizeButtons = new JRadioButton[frySizes.length];
        for (int size = 0; size < frySizes.length; size++) {
            frySizeButtons[size] = new JRadioButton(frySizes[size]);
            fryRadio.add(frySizeButtons[size]);
            friesPanel.add(frySizeButtons[size]);
        }
        frySizeButtons[0].setSelected(true);
        // add the item listener only to the "None" option, since all we care about
        // for the listener is whether None or something else is selected so we can
        // enable or disable the checkbox for cajun seasoning below
        frySizeButtons[0].addItemListener(this);

        // Cajun style option: a single check box
        cajunOption = new JCheckBox("Cajun Style");
        friesPanel.add(cajunOption);
        cajunOption.setEnabled(false);

        // how many ounces of soft drink, use a JSpinner, just for fun
        JPanel drinkPanel = new JPanel();
        drinkPanel.add(new JLabel("Soft Drink (in ounces):"));
        orderPanel.add(drinkPanel);

        // the "SpinnerNumberModel" here sets the start value, min, max, and
        // increment values for the spinner
        drinkSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 128, 8));
        drinkPanel.add(drinkSpinner);
        // note that we could add appropriate listeners
        // to our spinner but none is needed in this case

        drinkPanel.add(new JLabel("[Choose your own flavors at the machine!]"));

        // to make this seem more like Five Guys, we have peanuts!
        JPanel peanutPanel = new JPanel();
        peanutPanel.add(new JLabel("Peanut request:"));
        orderPanel.add(peanutPanel);

        // a slider that can take on values from 0 to the last entry in
        // the peanut amounts array
        peanutSlider = new JSlider(0, peanutAmounts.length - 1, 0);
        peanutPanel.add(peanutSlider);
        // a label that we'll update with appropriate text as the slider slides
        peanutLabel = new JLabel(peanutAmounts[0]);
        peanutPanel.add(peanutLabel);
        // add the change listener so we are notified when the slider moves
        peanutSlider.addChangeListener(this);

        // this will put a divider line between the main "ordering" controls
        // and the buttons at the bottom
        orderPanel.add(new JSeparator());

        framePanel.add(orderPanel, BorderLayout.CENTER);

        // last, the buttons to submit orders, reset, etc.
        JPanel buttonPanel = new JPanel();

        addButton = new JButton("Add To Order");
        addButton.addActionListener(this);
        buttonPanel.add(addButton);

        clearButton = new JButton("Clear Order");
        clearButton.addActionListener(this);
        buttonPanel.add(clearButton);

        framePanel.add(buttonPanel, BorderLayout.SOUTH);
        // display the window we've created
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method to be called when the state of our "None" radio button changes.
     * 
     * Satisfies the ItemListener interface.
     * 
     * @param e the ItemEvent that trigged the method call
     */
    public void itemStateChanged(ItemEvent e) {

        cajunOption.setEnabled(!frySizeButtons[0].isSelected());
    }

    /**
     * Method to be called when the slider is moved.
     * 
     * Satisfies the ChangeListener interface.
     * 
     * @param e the ChangeEvent that triggered the method call
     */
    public void stateChanged(ChangeEvent e) {

        // we update the peanut label based on the new value of the
        // peanut slider
        peanutLabel.setText(peanutAmounts[peanutSlider.getValue()]);
    }

    /**
     * Method to be called when buttons are pressed or a JComboBox
     * selection changes.
     * 
     * Satisfies the ActionListener interface
     * 
     * @param e the ActionEvent that triggered the method call
     */
    public void actionPerformed(ActionEvent e) {

        // first check if it's the JComboBox selection changing
        if (e.getSource() == burgerMenu) {
            // we want to enable the burger option buttons if "No Burger" is
            boolean buttonStatus = !burgerMenu.getSelectedItem().equals("No Burger");
            for (int option = 0; option < burgerOptions.length; option++) {
                burgerChecks[option].setEnabled(buttonStatus);
            }
            return;
        }

        // an alternate way to determine which object generated the event
        if (e.getActionCommand().equals("Add To Order")) {
            StringBuilder appendOrder = new StringBuilder();
            String thisName = name.getText();
            if (thisName.equals("")) {
                JOptionPane.showMessageDialog(null, "Name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            appendOrder.append(thisName + ": ");

            // a boolean to make sure this person ordered something, will set to true
            // as soon as we find something
            boolean orderedSomething = false;

            // retrieve the current selection from the JComboBox for the burger order
            // the cast to String is necessary here since any Object can be the value
            // returned by getSelectedItem
            String burgerPatties = (String) burgerMenu.getSelectedItem();
            if (!burgerMenu.getSelectedItem().equals("No Burger")) {
                orderedSomething = true;
                appendOrder.append("Burger with " + burgerPatties + ", toppings: [ ");
                // we loop through the check boxes, and add the corresponding string
                // any time we find one that is selected
                for (int option = 0; option < burgerOptions.length; option++) {
                    if (burgerChecks[option].isSelected()) {
                        appendOrder.append(burgerOptions[option] + " ");
                    }
                }
                appendOrder.append("] ");
            }

            // check on the fries
            if (!frySizeButtons[0].isSelected()) {
                orderedSomething = true;
                // here, we search through which of the radio buttons is selected
                for (int size = 1; size < frySizes.length; size++) {
                    if (frySizeButtons[size].isSelected()) {
                        appendOrder.append(frySizes[size] + " fries ");
                    }
                }
                if (cajunOption.isSelected()) {
                    appendOrder.append("[ Cajun Style ] ");
                }
            }

            // how about a drink?
            int drinkSize = (Integer) drinkSpinner.getValue();
            if (drinkSize > 0) {
                orderedSomething = true;
                appendOrder.append(drinkSize + " oz. soft drink ");
            }

            // peanuts? These don't count as an "orderedSomething" item, since they're
            // complementary, but we'll report them in the order
            appendOrder.append("Peanut quantity: " + peanutAmounts[peanutSlider.getValue()]);

            // make sure we have ordered something
            if (!orderedSomething) {
                JOptionPane.showMessageDialog(null, "No items ordered!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // we have a successful order, add to the text area
            appendOrder.append("\n");
            orderStatus.append(appendOrder.toString());

        }

        // if we get here, we either were "Add To Order" and successfully added
        // the order info to the text area, or the "Clear Order" button was
        // pressed - in either case, we need to reset the components for the order
        name.setText("");
        burgerMenu.setSelectedItem("No Burger");
        for (int option = 0; option < burgerOptions.length; option++) {
            burgerChecks[option].setSelected(false);
        }
        frySizeButtons[0].setSelected(true);
        cajunOption.setSelected(false);
        drinkSpinner.setValue(0);
        peanutSlider.setValue(0);
    }

    /**
     * main method to construct our object and launch a thread
     * to run it.
     * 
     * @param args not used
     */
    public static void main(String args[]) {

        // The main method is responsible for creating a thread that
        // will construct and show the graphical user interface.
        javax.swing.SwingUtilities.invokeLater(new FiveGuys());
    }

}
