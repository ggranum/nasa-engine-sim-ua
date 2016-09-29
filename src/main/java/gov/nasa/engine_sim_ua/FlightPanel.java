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

            i1 = (int)(((Turbo.u0d - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            i2 = (int)(((Turbo.altd - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
            i3 = (int)(((Turbo.throtl - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

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

            if(turbo.lunits <= 1) {
                Turbo.vmn1 = Turbo.u0min;
                Turbo.vmx1 = Turbo.u0max;
                Turbo.vmn2 = Turbo.altmin;
                Turbo.vmx2 = Turbo.altmax;
                Turbo.vmn3 = Turbo.thrmin;
                Turbo.vmx3 = Turbo.thrmax;
            }
            if(turbo.lunits == 2) {
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                Turbo.vmn2 = -10.0;
                Turbo.vmx2 = 10.0;
                Turbo.vmn3 = -10.0;
                Turbo.vmx3 = 10.0;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;

            if(turbo.inptype >= 2) {
                v2 = 0.0;
                i2 = 0;
                s2.setValue(i2);

                V6 = Double.valueOf(flightLeftPanel.o2.getText());
                v6 = V6.doubleValue();
                V7 = Double.valueOf(flightLeftPanel.o3.getText());
                v7 = V7.doubleValue();
                Turbo.ps0 = v6;
                if(v6 <= 0.0) {
                    Turbo.ps0 = v6 = 0.0;
                    fl1 = (float)v6;
                    flightLeftPanel.o2.setText(String.valueOf(fl1));
                }
                if(v6 >= Turbo.pmax) {
                    Turbo.ps0 = v6 = Turbo.pmax;
                    fl1 = (float)v6;
                    flightLeftPanel.o2.setText(String.valueOf(fl1));
                }
                Turbo.ps0 = Turbo.ps0 / Turbo.pconv;
                Turbo.ts0 = v7 + Turbo.tref;
                if(Turbo.ts0 <= Turbo.tmin) {
                    Turbo.ts0 = Turbo.tmin;
                    v7 = Turbo.ts0 - Turbo.tref;
                    fl1 = (float)v7;
                    flightLeftPanel.o3.setText(String.valueOf(fl1));
                }
                if(Turbo.ts0 >= Turbo.tmax) {
                    Turbo.ts0 = Turbo.tmax;
                    v7 = Turbo.ts0 - Turbo.tref;
                    fl1 = (float)v7;
                    flightLeftPanel.o3.setText(String.valueOf(fl1));
                }
                Turbo.ts0 = Turbo.ts0 / Turbo.tconv;
            }

            // flightPanel conditions
            if(turbo.lunits <= 1) {
                Turbo.u0d = v1;
                Turbo.altd = v2;
                Turbo.throtl = v3;
            }
            if(turbo.lunits == 2) {
                Turbo.u0d = v1 * Turbo.u0ref / 100. + Turbo.u0ref;
                Turbo.altd = v2 * Turbo.altref / 100. + Turbo.altref;
                Turbo.throtl = v3 * Turbo.thrref / 100. + Turbo.thrref;
            }

            if(turbo.entype == 1) {
                turbo.abflag = nozch.getSelectedIndex();
            }

            flightLeftPanel.f1.setText(String.format("%.0f", v1));
            flightLeftPanel.f2.setText(String.format("%.0f", v2));
            flightLeftPanel.f3.setText(String.format("%.3f", v3));

            turbo.solve.comPute();
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
            f1 = new TextField(String.valueOf((float)Turbo.u0d), 5);
            f1.setBackground(Color.white);
            f1.setForeground(Color.black);

            l2 = new Label("Altitude-ft", Label.CENTER);
            f2 = new TextField(String.valueOf((float)Turbo.altd), 5);
            f2.setBackground(Color.white);
            f2.setForeground(Color.black);

            l3 = new Label("Throttle", Label.CENTER);
            f3 = new TextField(String.valueOf((float)Turbo.throtl), 5);

            inpch = new Choice();
            inpch.addItem("Gamma");
            inpch.addItem("Gam(T)");
            inpch.select(1);
            f4 = new TextField(String.valueOf((float)Turbo.gama), 5);

            lmach = new Label("Mach", Label.CENTER);
            o1 = new TextField(String.valueOf((float)Turbo.fsmach), 5);
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
            v1 = V1.doubleValue();
            V2 = Double.valueOf(f2.getText());
            v2 = V2.doubleValue();
            V3 = Double.valueOf(f3.getText());
            v3 = V3.doubleValue();
            V4 = Double.valueOf(f4.getText());
            v4 = V4.doubleValue();
            V5 = Double.valueOf(o1.getText());
            v5 = V5.doubleValue();
            V6 = Double.valueOf(o2.getText());
            v6 = V6.doubleValue();
            V7 = Double.valueOf(o3.getText());
            v7 = V7.doubleValue();

            if(turbo.lunits <= 1) {
                // Airspeed
                if(turbo.inptype == 0 || turbo.inptype == 2) {
                    Turbo.u0d = v1;
                    Turbo.vmn1 = Turbo.u0min;
                    Turbo.vmx1 = Turbo.u0max;
                    if(v1 < Turbo.vmn1) {
                        Turbo.u0d = v1 = Turbo.vmn1;
                        fl1 = (float)v1;
                        f1.setText(String.valueOf(fl1));
                    }
                    if(v1 > Turbo.vmx1) {
                        Turbo.u0d = v1 = Turbo.vmx1;
                        fl1 = (float)v1;
                        f1.setText(String.valueOf(fl1));
                    }
                }
                // Mach
                if(turbo.inptype == 1 || turbo.inptype == 3) {
                    Turbo.fsmach = v5;
                    if(Turbo.fsmach < 0.0) {
                        Turbo.fsmach = v5 = 0.0;
                        fl1 = (float)v5;
                        o1.setText(String.valueOf(fl1));
                    }
                    if(Turbo.fsmach > 2.25 && turbo.entype <= 2) {
                        Turbo.fsmach = v5 = 2.25;
                        fl1 = (float)v5;
                        o1.setText(String.valueOf(fl1));
                    }
                    if(Turbo.fsmach > 6.75 && turbo.entype == 3) {
                        Turbo.fsmach = v5 = 6.75;
                        fl1 = (float)v5;
                        o1.setText(String.valueOf(fl1));
                    }
                }
                // Altitude
                if(turbo.inptype <= 1) {
                    Turbo.altd = v2;
                    Turbo.vmn2 = Turbo.altmin;
                    Turbo.vmx2 = Turbo.altmax;
                    if(v2 < Turbo.vmn2) {
                        Turbo.altd = v2 = Turbo.vmn2;
                        fl1 = (float)v2;
                        f2.setText(String.valueOf(fl1));
                    }
                    if(v2 > Turbo.vmx2) {
                        Turbo.altd = v2 = Turbo.vmx2;
                        fl1 = (float)v2;
                        f2.setText(String.valueOf(fl1));
                    }
                }
                // Pres and Temp
                if(turbo.inptype >= 2) {
                    Turbo.altd = v2 = 0.0;
                    fl1 = (float)v2;
                    f2.setText(String.valueOf(fl1));
                    Turbo.ps0 = v6;
                    if(v6 <= 0.0) {
                        Turbo.ps0 = v6 = 0.0;
                        fl1 = (float)v6;
                        o2.setText(String.valueOf(fl1));
                    }
                    if(v6 >= Turbo.pmax) {
                        Turbo.ps0 = v6 = Turbo.pmax;
                        fl1 = (float)v6;
                        o2.setText(String.valueOf(fl1));
                    }
                    Turbo.ps0 = Turbo.ps0 / Turbo.pconv;
                    Turbo.ts0 = v7 + Turbo.tref;
                    if(Turbo.ts0 <= Turbo.tmin) {
                        Turbo.ts0 = Turbo.tmin;
                        v7 = Turbo.ts0 - Turbo.tref;
                        fl1 = (float)v7;
                        o3.setText(String.valueOf(fl1));
                    }
                    if(Turbo.ts0 >= Turbo.tmax) {
                        Turbo.ts0 = Turbo.tmax;
                        v7 = Turbo.ts0 - Turbo.tref;
                        fl1 = (float)v7;
                        o3.setText(String.valueOf(fl1));
                    }
                    Turbo.ts0 = Turbo.ts0 / Turbo.tconv;
                }
                // Throttle
                Turbo.throtl = v3;
                Turbo.vmn3 = Turbo.thrmin;
                Turbo.vmx3 = Turbo.thrmax;
                if(v3 < Turbo.vmn3) {
                    Turbo.throtl = v3 = Turbo.vmn3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
                if(v3 > Turbo.vmx3) {
                    Turbo.throtl = v3 = Turbo.vmx3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
            }
            if(turbo.lunits == 2) {
                // Airspeed
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                if(v1 < Turbo.vmn1) {
                    v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    f1.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    f1.setText(String.valueOf(fl1));
                }
                Turbo.u0d = v1 * Turbo.u0ref / 100. + Turbo.u0ref;
                // Altitude
                Turbo.vmn2 = -10.0;
                Turbo.vmx2 = 10.0;
                if(v2 < Turbo.vmn2) {
                    v2 = Turbo.vmn2;
                    fl1 = (float)v2;
                    f2.setText(String.valueOf(fl1));
                }
                if(v2 > Turbo.vmx2) {
                    v2 = Turbo.vmx2;
                    fl1 = (float)v2;
                    f2.setText(String.valueOf(fl1));
                }
                Turbo.altd = v2 * Turbo.altref / 100. + Turbo.altref;
                // Throttle
                Turbo.vmn3 = -10.0;
                Turbo.vmx3 = 10.0;
                if(v3 < Turbo.vmn3) {
                    v3 = Turbo.vmn3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
                if(v3 > Turbo.vmx3) {
                    v3 = Turbo.vmx3;
                    fl1 = (float)v3;
                    f3.setText(String.valueOf(fl1));
                }
                Turbo.throtl = v3 * Turbo.thrref / 100. + Turbo.thrref;
            }
            // Gamma
            Turbo.gama = v4;
            if(v4 < 1.0) {
                Turbo.gama = v4 = 1.0;
                fl1 = (float)v4;
                f4.setText(String.valueOf(fl1));
            }
            if(v4 > 2.0) {
                Turbo.gama = v4 = 2.0;
                fl1 = (float)v4;
                f4.setText(String.valueOf(fl1));
            }

            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

            flightRightPanel.s1.setValue(i1);
            flightRightPanel.s2.setValue(i2);
            flightRightPanel.s3.setValue(i3);

            turbo.solve.comPute();
        }  // end handle
    }  //  end  inletLeftPanel
}  // end FlightPanel input
 
