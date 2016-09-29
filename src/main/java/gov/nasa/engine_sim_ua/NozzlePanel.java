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

public class NozzlePanel extends Panel {

    NozzleRightPanel nozzleRightPanel;
    public NozzleLeftPanel nozzleLeftPanel;

    NozzlePanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        nozzleLeftPanel = new NozzleLeftPanel(turbo);
        nozzleRightPanel = new NozzleRightPanel(turbo);

        add(nozzleLeftPanel);
        add(nozzleRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class NozzleRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Scrollbar s2;
        Scrollbar s3;
        Label lmat;
        Choice arch;
        Choice nmat;

        NozzleRightPanel(Turbo target) {

            int i1;
            int i2;
            int i3;

            turbo = target;
            setLayout(new GridLayout(7, 1, 10, 5));

            i1 = (int)(((turbo.tt7d - turbo.t7min) / (turbo.t7max - turbo.t7min)) * 1000.);
            i2 = (int)(((turbo.efficiency[7] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);
            i3 = (int)(((turbo.a8rat - turbo.a8min) / (turbo.a8max - turbo.a8min)) * 1000.);

            s1 = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);
            s2 = new Scrollbar(Scrollbar.HORIZONTAL, i2, 10, 0, 1000);
            s3 = new Scrollbar(Scrollbar.HORIZONTAL, i3, 10, 0, 1000);

            nmat = new Choice();
            nmat.setBackground(Color.white);
            nmat.setForeground(Color.blue);
            nmat.addItem("<-- My Material");
            nmat.addItem("Titanium ");
            nmat.addItem("Stainless Steel");
            nmat.addItem("Nickel Alloy");
            nmat.addItem("Ceramic");
            nmat.addItem("Passively Cooled");
            nmat.select(3);

            arch = new Choice();
            arch.addItem("Compute A8/A2");
            arch.addItem("Input A8/A2");
            arch.select(0);

            lmat = new Label("lbm/ft^3 ", Label.LEFT);
            lmat.setForeground(Color.blue);

            add(arch);
            add(s3);
            add(s1);
            add(s2);
            add(new Label(" ", Label.LEFT));
            add(nmat);
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

            turbo.arsched = arch.getSelectedIndex();
            if(turbo.arsched == 0) {
                nozzleLeftPanel.getF3().setBackground(Color.black);
                nozzleLeftPanel.getF3().setForeground(Color.yellow);
            }
            if(turbo.arsched == 1) {
                nozzleLeftPanel.getF3().setBackground(Color.white);
                nozzleLeftPanel.getF3().setForeground(Color.black);
            }
            // nozzle
            turbo.mnozl = nmat.getSelectedIndex();
            if(turbo.mnozl > 0) {
                nozzleLeftPanel.getTn().setBackground(Color.black);
                nozzleLeftPanel.getTn().setForeground(Color.yellow);
                nozzleLeftPanel.getDn().setBackground(Color.black);
                nozzleLeftPanel.getDn().setForeground(Color.yellow);
            }
            if(turbo.mnozl == 0) {
                nozzleLeftPanel.getTn().setBackground(Color.white);
                nozzleLeftPanel.getTn().setForeground(Color.black);
                nozzleLeftPanel.getDn().setBackground(Color.white);
                nozzleLeftPanel.getDn().setForeground(Color.black);
            }
            switch (turbo.mnozl) {
                case 0: {
                    V1 = Double.valueOf(nozzleLeftPanel.getDn().getText());
                    v1 = V1;
                    V2 = Double.valueOf(nozzleLeftPanel.getTn().getText());
                    v2 = V2;
                    turbo.dnozl = v1 / turbo.dconv;
                    turbo.tnozl = v2 / turbo.tconv;
                    break;
                }
                case 1:
                    turbo.dnozl = 293.02;
                    turbo.tnozl = 1500.;
                    break;
                case 2:
                    turbo.dnozl = 476.56;
                    turbo.tnozl = 2000.;
                    break;
                case 3:
                    turbo.dnozl = 515.2;
                    turbo.tnozl = 2500.;
                    break;
                case 4:
                    turbo.dnozl = 164.2;
                    turbo.tnozl = 3000.;
                    break;
                case 5:
                    turbo.dnozl = 400.2;
                    turbo.tnozl = 4100.;
                    break;
            }
            turbo.solve.compute();
        }

        public void handleBar() {     // nozzle design
            int i1;
            int i2;
            int i3;
            double v1;
            double v2;
            double v3;

            i1 = s1.getValue();
            i2 = s2.getValue();
            i3 = s3.getValue();

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.vmn1 = turbo.t7min;
                turbo.vmx1 = turbo.t7max;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                turbo.vmn3 = turbo.a8min;
                turbo.vmx3 = turbo.a8max;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                turbo.vmx2 = 100.0 - 100.0 * turbo.et7ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
                turbo.vmn3 = -10.0;
                turbo.vmx3 = 10.0;
            }

            v1 = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
            v2 = i2 * (turbo.vmx2 - turbo.vmn2) / 1000. + turbo.vmn2;
            v3 = i3 * (turbo.vmx3 - turbo.vmn3) / 1000. + turbo.vmn3;

            // nozzle design
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.tt7d = v1;
                turbo.efficiency[7] = v2;
                turbo.a8rat = v3;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.tt7d = v1 * turbo.t7ref / 100. + turbo.t7ref;
                turbo.efficiency[7] = turbo.et7ref + v2 / 100.;
                turbo.a8rat = v3 * turbo.a8ref / 100. + turbo.a8ref;
            }
            turbo.tt7 = turbo.tt7d / turbo.tconv;

            nozzleLeftPanel.getF1().setText(String.valueOf((float)v1));
            nozzleLeftPanel.getF2().setText(String.valueOf((float)v2));
            nozzleLeftPanel.getF3().setText(String.format("%.3f", v3));

            turbo.solve.compute();
        }  // end handle
    }  // end rightPanel

    public class NozzleLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField dn;
        private TextField tn;
        Label l1;
        Label l2;
        Label l5;
        Label lmat;
        Label lm2;

        NozzleLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(7, 2, 5, 5));

            l1 = new Label("Tmax -R", Label.CENTER);
            setF1(new TextField(String.valueOf((float)turbo.tt7d), 5));

            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)turbo.efficiency[7]), 5));

            setF3(new TextField(String.valueOf((float)turbo.a8rat), 5));
            getF3().setBackground(Color.black);
            getF3().setForeground(Color.yellow);

            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDn(new TextField(String.valueOf((float)turbo.dnozl), 5));
            getDn().setBackground(Color.black);
            getDn().setForeground(Color.yellow);
            setTn(new TextField(String.valueOf((float)turbo.tnozl), 5));
            getTn().setBackground(Color.black);
            getTn().setForeground(Color.yellow);

            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));

            add(new Label("A8/A2 ", Label.CENTER));
            add(getF3());

            add(l1);
            add(getF1());

            add(l2);
            add(getF2());

            add(lm2);
            add(new Label(" ", Label.CENTER));

            add(lmat);
            add(getTn());

            add(l5);
            add(getDn());
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
            Double V7;
            Double V8;
            double v1;
            double v2;
            double v3;
            double v7;
            double v8;
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
            V7 = Double.valueOf(getDn().getText());
            v7 = V7;
            V8 = Double.valueOf(getTn().getText());
            v8 = V8;

            // Materials
            if(turbo.mnozl == 0) {
                if(v7 <= 1.0 * turbo.dconv) {
                    v7 = 1.0 * turbo.dconv;
                    getDn().setText(String.format("%.0f", v7 * turbo.dconv));
                }
                turbo.dnozl = v7 / turbo.dconv;
                if(v8 <= 500. * turbo.tconv) {
                    v8 = 500. * turbo.tconv;
                    getTn().setText(String.format("%.0f", v8 * turbo.tconv));
                }
                turbo.tnozl = v8 / turbo.tconv;
            }

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                // Max afterburner temp
                turbo.tt7d = v1;
                turbo.vmn1 = turbo.t7min;
                turbo.vmx1 = turbo.t7max;
                if(v1 < turbo.vmn1) {
                    turbo.tt7d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.tt7d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                turbo.tt7 = turbo.tt7d / turbo.tconv;
                // nozzle  efficiency
                turbo.efficiency[7] = v2;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                if(v2 < turbo.vmn2) {
                    turbo.efficiency[7] = v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    turbo.efficiency[7] = v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                //  nozzle area ratio
                turbo.a8rat = v3;
                turbo.vmn3 = turbo.a8min;
                turbo.vmx3 = turbo.a8max;
                if(v3 < turbo.vmn3) {
                    turbo.a8rat = v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    turbo.a8rat = v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                // Max afterburner temp
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                if(v1 < turbo.vmn1) {
                    v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                turbo.tt7d = v1 * turbo.t7ref / 100. + turbo.t7ref;
                turbo.tt7 = turbo.tt7d / turbo.tconv;
                // nozzl e  efficiency
                turbo.vmx2 = 100.0 - 100.0 * turbo.et7ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
                if(v2 < turbo.vmn2) {
                    v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                turbo.efficiency[7] = turbo.et7ref + v2 / 100.;
                //  nozzle area ratio
                turbo.vmn3 = -10.0;
                turbo.vmx3 = 10.0;
                if(v3 < turbo.vmn3) {
                    v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                turbo.a8rat = v3 * turbo.a8ref / 100. + turbo.a8ref;
            }

            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

            nozzleRightPanel.s1.setValue(i1);
            nozzleRightPanel.s2.setValue(i2);
            nozzleRightPanel.s3.setValue(i3);

            turbo.solve.compute();
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

        public TextField getDn() {
            return dn;
        }

        public void setDn(TextField dn) {
            this.dn = dn;
        }

        public TextField getTn() {
            return tn;
        }

        public void setTn(TextField tn) {
            this.tn = tn;
        }
    }  //  end  inletLeftPanel
}  // end nozzlePanel
 
