package gov.nasa.engine_sim_ua;

import java.awt.AWTEvent;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;

/**
 *
 */
public class FlightPanel extends Panel {

    FlightRightPanel flightRightPanel;
    FlightLeftPanel flightLeftPanel;

    FlightPanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 5, 5));

        flightLeftPanel = new FlightLeftPanel(turbo);
        flightRightPanel = new FlightRightPanel(turbo);

        add(flightLeftPanel);
        add(flightRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class FlightRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Scrollbar s2;
        Scrollbar s3;
        Label l2;
        Label l3;
        Choice nozch;
        Choice inptch;

        FlightRightPanel(Turbo target) {

            int i1;
            int i2;
            int i3;

            turbo = target;
            setLayout(new GridLayout(7, 1, 10, 5));

            i1 = (int)(((turbo.u0d - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            i2 = (int)(((turbo.altd - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
            i3 = (int)(((turbo.throtl - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

            s1 = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);
            s2 = new Scrollbar(Scrollbar.HORIZONTAL, i2, 10, 0, 1000);
            s3 = new Scrollbar(Scrollbar.HORIZONTAL, i3, 1, 0, 1000);

            l2 = new Label("lb/sq inputPanel", Label.LEFT);
            l3 = new Label("F", Label.LEFT);

            nozch = new Choice();
            nozch.addItem("Afterburner OFF");
            nozch.addItem("Afterburner ON");
            nozch.select(0);

            inptch = new Choice();
            inptch.addItem("Input Speed + Altitude");
            inptch.addItem("Input Mach + Altitude");
            inptch.addItem("Input Speed+Pres+Temp");
            inptch.addItem("Input Mach+Pres+Temp");
            inptch.select(0);

            add(inptch);
            add(l2);
            add(l3);
            add(s1);
            add(s2);
            add(s3);
            add(nozch);
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_ABSOLUTE) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_LINE_DOWN) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_LINE_UP) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_PAGE_DOWN) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_PAGE_UP) {
                this.handleBar();
            }
        }

        public void handleBar() {     // flightPanel conditions
            int i1;
            int i2;
            int i3;
            Double V6;
            Double V7;
            double v1;
            double v2;
            double v3;
            double v6;
            double v7;
            float fl1;

            i1 = s1.getValue();
            i2 = s2.getValue();
            i3 = s3.getValue();

            turbo.inptype = inptch.getSelectedIndex();
            if(turbo.inptype == 0 || turbo.inptype == 2) {
                flightLeftPanel.f1.setBackground(Color.white);
                flightLeftPanel.f1.setForeground(Color.black);
                flightLeftPanel.o1.setBackground(Color.black);
                flightLeftPanel.o1.setForeground(Color.yellow);
            }
            if(turbo.inptype == 1 || turbo.inptype == 3) {
                flightLeftPanel.f1.setBackground(Color.black);
                flightLeftPanel.f1.setForeground(Color.yellow);
                flightLeftPanel.o1.setBackground(Color.white);
                flightLeftPanel.o1.setForeground(Color.black);
            }
            if(turbo.inptype <= 1) {
                flightLeftPanel.o2.setBackground(Color.black);
                flightLeftPanel.o2.setForeground(Color.yellow);
                flightLeftPanel.o3.setBackground(Color.black);
                flightLeftPanel.o3.setForeground(Color.yellow);
                flightLeftPanel.f2.setBackground(Color.white);
                flightLeftPanel.f2.setForeground(Color.black);
            }
            if(turbo.inptype >= 2) {
                flightLeftPanel.o2.setBackground(Color.white);
                flightLeftPanel.o2.setForeground(Color.black);
                flightLeftPanel.o3.setBackground(Color.white);
                flightLeftPanel.o3.setForeground(Color.black);
                flightLeftPanel.f2.setBackground(Color.black);
                flightLeftPanel.f2.setForeground(Color.yellow);
            }

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.vmn1 = turbo.u0min;
                turbo.vmx1 = turbo.u0max;
                turbo.vmn2 = turbo.altmin;
                turbo.vmx2 = turbo.altmax;
                turbo.vmn3 = turbo.thrmin;
                turbo.vmx3 = turbo.thrmax;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                turbo.vmn2 = -10.0;
                turbo.vmx2 = 10.0;
                turbo.vmn3 = -10.0;
                turbo.vmx3 = 10.0;
            }

            v1 = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
            v2 = i2 * (turbo.vmx2 - turbo.vmn2) / 1000. + turbo.vmn2;
            v3 = i3 * (turbo.vmx3 - turbo.vmn3) / 1000. + turbo.vmn3;

            if(turbo.inptype >= 2) {
                v2 = 0.0;
                i2 = 0;
                s2.setValue(i2);

                V6 = Double.valueOf(flightLeftPanel.o2.getText());
                v6 = V6;
                V7 = Double.valueOf(flightLeftPanel.o3.getText());
                v7 = V7;
                turbo.ps0 = v6;
                if(v6 <= 0.0) {
                    turbo.ps0 = v6 = 0.0;
                    fl1 = (float)v6;
                    flightLeftPanel.o2.setText(String.valueOf(fl1));
                }
                if(v6 >= turbo.pmax) {
                    turbo.ps0 = v6 = turbo.pmax;
                    fl1 = (float)v6;
                    flightLeftPanel.o2.setText(String.valueOf(fl1));
                }
                turbo.ps0 = turbo.ps0 / turbo.pconv;
                turbo.ts0 = v7 + turbo.tref;
                if(turbo.ts0 <= turbo.tmin) {
                    turbo.ts0 = turbo.tmin;
                    v7 = turbo.ts0 - turbo.tref;
                    fl1 = (float)v7;
                    flightLeftPanel.o3.setText(String.valueOf(fl1));
                }
                if(turbo.ts0 >= turbo.tmax) {
                    turbo.ts0 = turbo.tmax;
                    v7 = turbo.ts0 - turbo.tref;
                    fl1 = (float)v7;
                    flightLeftPanel.o3.setText(String.valueOf(fl1));
                }
                turbo.ts0 = turbo.ts0 / turbo.tconv;
            }

            // flightPanel conditions
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.u0d = v1;
                turbo.altd = v2;
                turbo.throtl = v3;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.u0d = v1 * turbo.u0ref / 100. + turbo.u0ref;
                turbo.altd = v2 * turbo.altref / 100. + turbo.altref;
                turbo.throtl = v3 * turbo.thrref / 100. + turbo.thrref;
            }

            if(turbo.entype == 1) {
                turbo.abflag = nozch.getSelectedIndex();
            }

            flightLeftPanel.f1.setText(String.format("%.0f", v1));
            flightLeftPanel.f2.setText(String.format("%.0f", v2));
            flightLeftPanel.f3.setText(String.format("%.3f", v3));

            turbo.solve.compute();
        }  // end handle
    }  // end rightPanel

    public class FlightLeftPanel extends Panel {

        Turbo turbo;
        TextField f1;
        TextField f2;
        TextField f3;
        TextField f4;
        TextField o1;
        TextField o2;
        TextField o3;
        Label l1;
        Label l2;
        Label l3;
        Label lmach;
        Choice inpch;

        FlightLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(7, 2, 5, 5));

            l1 = new Label("Speed-mph", Label.CENTER);
            f1 = new TextField(String.valueOf((float)turbo.u0d), 5);
            f1.setBackground(Color.white);
            f1.setForeground(Color.black);

            l2 = new Label("Altitude-ft", Label.CENTER);
            f2 = new TextField(String.valueOf((float)turbo.altd), 5);
            f2.setBackground(Color.white);
            f2.setForeground(Color.black);

            l3 = new Label("Throttle", Label.CENTER);
            f3 = new TextField(String.valueOf((float)turbo.throtl), 5);

            inpch = new Choice();
            inpch.addItem("Gamma");
            inpch.addItem("Gam(T)");
            inpch.select(1);
            f4 = new TextField(String.valueOf((float)turbo.gama), 5);

            lmach = new Label("Mach", Label.CENTER);
            o1 = new TextField(String.valueOf((float)turbo.fsmach), 5);
            o1.setBackground(Color.black);
            o1.setForeground(Color.yellow);

            o2 = new TextField();
            o2.setBackground(Color.black);
            o2.setForeground(Color.yellow);

            o3 = new TextField();
            o3.setBackground(Color.black);
            o3.setForeground(Color.yellow);

            add(lmach);
            add(o1);

            add(new Label(" Press ", Label.CENTER));
            add(o2);

            add(new Label(" Temp  ", Label.CENTER));
            add(o3);

            add(l1);
            add(f1);

            add(l2);
            add(f2);

            add(l3);
            add(f3);

            add(inpch);
            add(f4);
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleText();
            }
        }

        public void handleText() {
            Double V1;
            Double V2;
            Double V3;
            Double V4;
            Double V5;
            Double V6;
            Double V7;
            double v1;
            double v2;
            double v3;
            double v4;
            double v5;
            double v6;
            double v7;
            int i1;
            int i2;
            int i3;
            float fl1;

            turbo.gamopt = inpch.getSelectedIndex();

            V1 = Double.valueOf(f1.getText());
            v1 = V1;
            V2 = Double.valueOf(f2.getText());
            v2 = V2;
            V3 = Double.valueOf(f3.getText());
            v3 = V3;
            V4 = Double.valueOf(f4.getText());
            v4 = V4;
            V5 = Double.valueOf(o1.getText());
            v5 = V5;
            V6 = Double.valueOf(o2.getText());
            v6 = V6;
            V7 = Double.valueOf(o3.getText());
            v7 = V7;

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                // Airspeed
                if(turbo.inptype == 0 || turbo.inptype == 2) {
                    turbo.u0d = v1;
                    turbo.vmn1 = turbo.u0min;
                    turbo.vmx1 = turbo.u0max;
                    if(v1 < turbo.vmn1) {
                        turbo.u0d = v1 = turbo.vmn1;
                        fl1 = (float)v1;
                        f1.setText(String.valueOf(fl1));
                    }
                    if(v1 > turbo.vmx1) {
                        turbo.u0d = v1 = turbo.vmx1;
                        fl1 = (float)v1;
                        f1.setText(String.valueOf(fl1));
                    }
                }
                // Mach
                if(turbo.inptype == 1 || turbo.inptype == 3) {
                    turbo.fsmach = v5;
                    if(turbo.fsmach < 0.0) {
                        turbo.fsmach = v5 = 0.0;
                        fl1 = (float)v5;
                        o1.setText(String.valueOf(fl1));
                    }
                    if(turbo.fsmach > 2.25 && turbo.entype <= 2) {
                        turbo.fsmach = v5 = 2.25;
                        fl1 = (float)v5;
                        o1.setText(String.valueOf(fl1));
                    }
                    if(turbo.fsmach > 6.75 && turbo.entype == 3) {
                        turbo.fsmach = v5 = 6.75;
                        fl1 = (float)v5;
                        o1.setText(String.valueOf(fl1));
                    }
                }
                // Altitude
                if(turbo.inptype <= 1) {
                    turbo.altd = v2;
                    turbo.vmn2 = turbo.altmin;
                    turbo.vmx2 = turbo.altmax;
                    if(v2 < turbo.vmn2) {
                        turbo.altd = v2 = turbo.vmn2;
                        fl1 = (float)v2;
                        f2.setText(String.valueOf(fl1));
                    }
                    if(v2 > turbo.vmx2) {
                        turbo.altd = v2 = turbo.vmx2;
                        fl1 = (float)v2;
                        f2.setText(String.valueOf(fl1));
                    }
                }
                // Pres and Temp
                if(turbo.inptype >= 2) {
                    turbo.altd = v2 = 0.0;
                    fl1 = (float)v2;
                    f2.setText(String.valueOf(fl1));
                    turbo.ps0 = v6;
                    if(v6 <= 0.0) {
                        turbo.ps0 = v6 = 0.0;
                        fl1 = (float)v6;
                        o2.setText(String.valueOf(fl1));
                    }
                    if(v6 >= turbo.pmax) {
                        turbo.ps0 = v6 = turbo.pmax;
                        fl1 = (float)v6;
                        o2.setText(String.valueOf(fl1));
                    }
                    turbo.ps0 = turbo.ps0 / turbo.pconv;
                    turbo.ts0 = v7 + turbo.tref;
                    if(turbo.ts0 <= turbo.tmin) {
                        turbo.ts0 = turbo.tmin;
                        v7 = turbo.ts0 - turbo.tref;
                        fl1 = (float)v7;
                        o3.setText(String.valueOf(fl1));
                    }
                    if(turbo.ts0 >= turbo.tmax) {
                        turbo.ts0 = turbo.tmax;
                        v7 = turbo.ts0 - turbo.tref;
                        fl1 = (float)v7;
                        o3.setText(String.valueOf(fl1));
                    }
                    turbo.ts0 = turbo.ts0 / turbo.tconv;
                }
                // Throttle
                turbo.throtl = v3;
                turbo.vmn3 = turbo.thrmin;
                turbo.vmx3 = turbo.thrmax;
                if(v3 < turbo.vmn3) {
                    turbo.throtl = v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    turbo.throtl = v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                // Airspeed
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                if(v1 < turbo.vmn1) {
                    v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    f1.setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    f1.setText(String.valueOf(fl1));
                }
                turbo.u0d = v1 * turbo.u0ref / 100. + turbo.u0ref;
                // Altitude
                turbo.vmn2 = -10.0;
                turbo.vmx2 = 10.0;
                if(v2 < turbo.vmn2) {
                    v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    f2.setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    f2.setText(String.valueOf(fl1));
                }
                turbo.altd = v2 * turbo.altref / 100. + turbo.altref;
                // Throttle
                turbo.vmn3 = -10.0;
                turbo.vmx3 = 10.0;
                if(v3 < turbo.vmn3) {
                    v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
                turbo.throtl = v3 * turbo.thrref / 100. + turbo.thrref;
            }
            // Gamma
            turbo.gama = v4;
            if(v4 < 1.0) {
                turbo.gama = v4 = 1.0;
                fl1 = (float)v4;
                f4.setText(String.valueOf(fl1));
            }
            if(v4 > 2.0) {
                turbo.gama = v4 = 2.0;
                fl1 = (float)v4;
                f4.setText(String.valueOf(fl1));
            }

            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

            flightRightPanel.s1.setValue(i1);
            flightRightPanel.s2.setValue(i2);
            flightRightPanel.s3.setValue(i3);

            turbo.solve.compute();
        }  // end handle
    }  //  end  inletLeftPanel
}  // end FlightPanel input
 
