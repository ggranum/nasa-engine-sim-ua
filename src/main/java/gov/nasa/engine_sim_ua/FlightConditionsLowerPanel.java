package gov.nasa.engine_sim_ua;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

/**
 *
 */
class FlightConditionsLowerPanel extends Panel {

    TextField o4;
    TextField o5;
    TextField o6;
    TextField o10;
    TextField tfWeight;
    TextField o12;
    TextField o14;
    TextField o15;

    FlightConditionsLowerPanel() {
        setLayout(new GridLayout(4, 4, 1, 5));

        o4 = new TextField();
        o4.setBackground(Color.black);
        o4.setForeground(Color.yellow);
        o5 = new TextField();
        o5.setBackground(Color.black);
        o5.setForeground(Color.yellow);
        o6 = new TextField();
        o6.setBackground(Color.black);
        o6.setForeground(Color.yellow);
        o10 = new TextField();
        o10.setBackground(Color.black);
        o10.setForeground(Color.yellow);
        tfWeight = new TextField();
        tfWeight.setBackground(Color.black);
        tfWeight.setForeground(Color.yellow);
        o12 = new TextField();
        o12.setBackground(Color.black);
        o12.setForeground(Color.yellow);
        o14 = new TextField();
        o14.setBackground(Color.black);
        o14.setForeground(Color.yellow);
        o15 = new TextField();
        o15.setBackground(Color.black);
        o15.setForeground(Color.yellow);

        add(new Label("Net Thrust", Label.CENTER));
        add(o4);
        add(new Label("Fuel Flow", Label.CENTER));
        add(o5);

        add(new Label("Gross Thrust", Label.CENTER));
        add(o14);
        add(new Label("TSFC", Label.CENTER));
        add(o6);

        add(new Label("Ram Drag", Label.CENTER));
        add(o15);
        add(new Label("Core Airflow", Label.CENTER));
        add(o10);

        add(new Label("Fnet / W ", Label.CENTER));
        add(o12);
        add(new Label("Weight", Label.CENTER));
        add(tfWeight);
    }
}  // end FlightConditionsLowerPanel
 
