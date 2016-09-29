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

public class BurnerPanel extends Panel {

    BurnerRightPanel burnerRightPanel;
    BurnerLeftPanel burnerLeftPanel;

    BurnerPanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        burnerLeftPanel = new BurnerLeftPanel(turbo);
        burnerRightPanel = new BurnerRightPanel(turbo);

        add(burnerLeftPanel);
        add(burnerRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class BurnerRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Scrollbar s2;
        Scrollbar s3;
        Label lmat;
        Choice bmat;
        Choice fuelch;

        BurnerRightPanel(Turbo target) {

            int i1;
            int i2;
            int i3;

            turbo = target;
            setLayout(new GridLayout(7, 1, 10, 5));

            i1 = (int)(((Turbo.tt4d - Turbo.t4min) / (Turbo.t4max - Turbo.t4min)) * 1000.);
            i2 = (int)(((Turbo.eta[4] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.);
            i3 = (int)(((Turbo.prat[4] - Turbo.etmin) / (Turbo.pt4max - Turbo.etmin)) * 1000.);

            s1 = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);
            s2 = new Scrollbar(Scrollbar.HORIZONTAL, i2, 10, 0, 1000);
            s3 = new Scrollbar(Scrollbar.HORIZONTAL, i3, 10, 0, 1000);

            bmat = new Choice();
            bmat.setBackground(Color.white);
            bmat.setForeground(Color.blue);
            bmat.addItem("<-- My Material");
            bmat.addItem("Aluminum");
            bmat.addItem("Titanium ");
            bmat.addItem("Stainless Steel");
            bmat.addItem("Nickel Alloy");
            bmat.addItem("Nickel Crystal");
            bmat.addItem("Ceramic");
            bmat.addItem("Actively Cooled");
            bmat.select(4);

            fuelch = new Choice();
            fuelch.addItem("Jet - A");
            fuelch.addItem("Hydrogen");
            fuelch.addItem("<-- Your Fuel");
            fuelch.select(0);

            lmat = new Label("lbm/ft^3 ", Label.LEFT);
            lmat.setForeground(Color.blue);

            add(fuelch);
            add(s1);
            add(s3);
            add(s2);
            add(new Label(" ", Label.LEFT));
            add(bmat);
            add(lmat);
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleMat();
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

        public void handleMat() {
            Double V1;
            Double V2;
            double v1;
            double v2;
            float fl1;

            turbo.fueltype = fuelch.getSelectedIndex();
            if(turbo.fueltype == 0) {
                Turbo.fhv = 18600.;
            }
            if(turbo.fueltype == 1) {
                Turbo.fhv = 49900.;
            }
            burnerLeftPanel.getF4().setBackground(Color.black);
            burnerLeftPanel.getF4().setForeground(Color.yellow);
            Turbo.fhvd = Turbo.fhv * Turbo.flconv;
            fl1 = (float)(Turbo.fhvd);
            burnerLeftPanel.getF4().setText(String.format("%.0f", Turbo.fhvd));

            if(turbo.fueltype == 2) {
                burnerLeftPanel.getF4().setBackground(Color.white);
                burnerLeftPanel.getF4().setForeground(Color.black);
            }

            // burner
            Turbo.mburner = bmat.getSelectedIndex();
            if(Turbo.mburner > 0) {
                burnerLeftPanel.getDb().setBackground(Color.black);
                burnerLeftPanel.getDb().setForeground(Color.yellow);
                burnerLeftPanel.getTb().setBackground(Color.black);
                burnerLeftPanel.getTb().setForeground(Color.yellow);
            }
            if(Turbo.mburner == 0) {
                burnerLeftPanel.getDb().setBackground(Color.white);
                burnerLeftPanel.getDb().setForeground(Color.blue);
                burnerLeftPanel.getTb().setBackground(Color.white);
                burnerLeftPanel.getTb().setForeground(Color.blue);
            }
            switch (Turbo.mburner) {
                case 0: {
                    V1 = Double.valueOf(burnerLeftPanel.getDb().getText());
                    v1 = V1;
                    V2 = Double.valueOf(burnerLeftPanel.getTb().getText());
                    v2 = V2;
                    Turbo.dburner = v1 / Turbo.dconv;
                    Turbo.tburner = v2 / Turbo.tconv;
                    break;
                }
                case 1:
                    Turbo.dburner = 170.7;
                    Turbo.tburner = 900.;
                    break;
                case 2:
                    Turbo.dburner = 293.02;
                    Turbo.tburner = 1500.;
                    break;
                case 3:
                    Turbo.dburner = 476.56;
                    Turbo.tburner = 2000.;
                    break;
                case 4:
                    Turbo.dburner = 515.2;
                    Turbo.tburner = 2500.;
                    break;
                case 5:
                    Turbo.dburner = 515.2;
                    Turbo.tburner = 3000.;
                    break;
                case 6:
                    Turbo.dburner = 164.2;
                    Turbo.tburner = 3000.;
                    break;
                case 7:
                    Turbo.dburner = 515.2;
                    Turbo.tburner = 4500.;
                    break;
            }
            turbo.solve.comPute();
        }

        public void handleBar() {     // burner design
            int i1;
            int i2;
            int i3;
            double v1;
            double v2;
            double v3;
            float fl1;
            float fl2;
            float fl3;

            i1 = s1.getValue();
            i2 = s2.getValue();
            i3 = s3.getValue();

            if(turbo.lunits <= 1) {
                Turbo.vmn1 = Turbo.t4min;
                Turbo.vmx1 = Turbo.t4max;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.etmin;
                Turbo.vmx3 = Turbo.pt4max;
            }
            if(turbo.lunits == 2) {
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et4ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0;
                Turbo.vmx3 = 100.0 - 100.0 * Turbo.p4ref;
                Turbo.vmn3 = Turbo.vmx3 - 20.0;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;

            fl1 = (float)v1;
            fl2 = (float)v2;
            fl3 = (float)v3;
            // burner design
            if(turbo.lunits <= 1) {
                Turbo.tt4d = v1;
                Turbo.eta[4] = v2;
                Turbo.prat[4] = v3;
            }
            if(turbo.lunits == 2) {
                Turbo.tt4d = v1 * Turbo.t4ref / 100. + Turbo.t4ref;
                Turbo.eta[4] = Turbo.et4ref + v2 / 100.;
                Turbo.prat[4] = Turbo.p4ref + v3 / 100.;
            }
            Turbo.tt4 = Turbo.tt4d / Turbo.tconv;

            burnerLeftPanel.getF1().setText(String.valueOf(fl1));
            burnerLeftPanel.getF2().setText(String.valueOf(fl2));
            burnerLeftPanel.getF3().setText(String.valueOf(fl3));

            turbo.solve.comPute();
        }  // end handle
    }  // end rightPanel

    public class BurnerLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField f4;
        Label l1;
        Label l2;
        Label l3;
        Label l4;
        Label l5;
        Label lmat;
        Label lm2;
        private TextField db;
        private TextField tb;

        BurnerLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(7, 2, 5, 5));

            l1 = new Label("Tmax -R", Label.CENTER);
            setF1(new TextField(String.valueOf((float)Turbo.tt4d), 5));
            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)Turbo.eta[4]), 5));
            l3 = new Label("Press. Ratio", Label.CENTER);
            setF3(new TextField(String.valueOf((float)Turbo.prat[4]), 5));
            l4 = new Label("FHV Btu/lb", Label.CENTER);
            setF4(new TextField(String.valueOf((float)Turbo.fhv), 5));
            getF4().setBackground(Color.black);
            getF4().setForeground(Color.yellow);

            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDb(new TextField(String.valueOf((float)Turbo.dburner), 5));
            getDb().setBackground(Color.black);
            getDb().setForeground(Color.yellow);
            setTb(new TextField(String.valueOf((float)Turbo.tburner), 5));
            getTb().setBackground(Color.black);
            getTb().setForeground(Color.yellow);

            add(l4);
            add(getF4());
            add(l1);
            add(getF1());
            add(l3);
            add(getF3());
            add(l2);
            add(getF2());
            add(lm2);
            add(new Label(" ", Label.CENTER));
            add(lmat);
            add(getTb());
            add(l5);
            add(getDb());
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
            double v1;
            double v2;
            double v3;
            double v4;
            double v5;
            double v6;
            int i1;
            int i2;
            int i3;
            float fl1;

            V1 = Double.valueOf(getF1().getText());
            v1 = V1;
            V2 = Double.valueOf(getF2().getText());
            v2 = V2;
            V3 = Double.valueOf(getF3().getText());
            v3 = V3;
            V6 = Double.valueOf(getF4().getText());
            v6 = V6;
            V4 = Double.valueOf(getDb().getText());
            v4 = V4;
            V5 = Double.valueOf(getTb().getText());
            v5 = V5;

            // Materials
            if(Turbo.mburner == 0) {
                if(v4 <= 1.0 * Turbo.dconv) {
                    v4 = 1.0 * Turbo.dconv;
                    getDb().setText(String.format("%.0f", v4 * Turbo.dconv));
                }
                Turbo.dburner = v4 / Turbo.dconv;
                if(v5 <= 500. * Turbo.tconv) {
                    v5 = 500. * Turbo.tconv;
                    getTb().setText(String.format("%.0f", v5 * Turbo.tconv));
                }
                Turbo.tburner = v5 / Turbo.tconv;
            }

            if(turbo.lunits <= 1) {
                // Max burner temp
                Turbo.tt4d = v1;
                Turbo.vmn1 = Turbo.t4min;
                Turbo.vmx1 = Turbo.t4max;
                if(v1 < Turbo.vmn1) {
                    Turbo.tt4d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.tt4d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
                // burner  efficiency
                Turbo.eta[4] = v2;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[4] = v2 = Turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[4] = v2 = Turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                //  burner pressure ratio
                Turbo.prat[4] = v3;
                Turbo.vmn3 = Turbo.etmin;
                Turbo.vmx3 = Turbo.pt4max;
                if(v3 < Turbo.vmn3) {
                    Turbo.prat[4] = v3 = Turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > Turbo.vmx3) {
                    Turbo.prat[4] = v3 = Turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                // fuel heating value
                if(turbo.fueltype == 2) {
                    Turbo.fhvd = v6;
                    Turbo.fhv = Turbo.fhvd / Turbo.flconv;
                }
            }

            if(turbo.lunits == 2) {
                // Max burner temp
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                if(v1 < Turbo.vmn1) {
                    v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                Turbo.tt4d = v1 * Turbo.t4ref / 100. + Turbo.t4ref;
                Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
                // burner  efficiency
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et4ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0;
                if(v2 < Turbo.vmn2) {
                    v2 = Turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > Turbo.vmx2) {
                    v2 = Turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                Turbo.eta[4] = Turbo.et4ref + v2 / 100.;
                //  burner pressure ratio
                Turbo.vmx3 = 100.0 - 100.0 * Turbo.p4ref;
                Turbo.vmn3 = Turbo.vmx3 - 20.0;
                if(v3 < Turbo.vmn3) {
                    v3 = Turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > Turbo.vmx3) {
                    v3 = Turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                Turbo.prat[4] = Turbo.p4ref + v3 / 100.;
            }

            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

            burnerRightPanel.s1.setValue(i1);
            burnerRightPanel.s2.setValue(i2);
            burnerRightPanel.s3.setValue(i3);

            turbo.solve.comPute();
        }  // end handle

        public TextField getF1() {
            return f1;
        }

        public void setF1(TextField f1) {
            this.f1 = f1;
        }

        public TextField getF2() {
            return f2;
        }

        public void setF2(TextField f2) {
            this.f2 = f2;
        }

        public TextField getF3() {
            return f3;
        }

        public void setF3(TextField f3) {
            this.f3 = f3;
        }

        public TextField getF4() {
            return f4;
        }

        public void setF4(TextField f4) {
            this.f4 = f4;
        }

        public TextField getDb() {
            return db;
        }

        public void setDb(TextField db) {
            this.db = db;
        }

        public TextField getTb() {
            return tb;
        }

        public void setTb(TextField tb) {
            this.tb = tb;
        }
    }  //  end  inletLeftPanel
}  // end BurnerPanel
 
