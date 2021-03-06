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

public class RamjetNozzlePanel extends Panel {

    RamjetNozzleRightPanel ramjetNozzleRightPanel;
    public RamjetNozzleLeftPanel ramjetNozzleLeftPanel;

    RamjetNozzlePanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        ramjetNozzleLeftPanel = new RamjetNozzleLeftPanel(turbo);
        ramjetNozzleRightPanel = new RamjetNozzleRightPanel(turbo);

        add(ramjetNozzleLeftPanel);
        add(ramjetNozzleRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class RamjetNozzleRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Scrollbar s2;
        Scrollbar s3;
        Scrollbar s4;
        Label lmat;
        Choice nrmat;
        Choice atch;
        Choice aech;

        RamjetNozzleRightPanel(Turbo target) {

            int i2;
            int i3;
            int i4;

            turbo = target;
            setLayout(new GridLayout(7, 1, 10, 5));

            i2 = (int)(((turbo.efficiency[7] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);
            i3 = (int)(((turbo.arthd - turbo.arthmn) / (turbo.arthmx - turbo.arthmn)) * 1000.);
            i4 = (int)(((turbo.arexitd - turbo.arexmn) / (turbo.arexmx - turbo.arexmn)) * 1000.);

            s2 = new Scrollbar(Scrollbar.HORIZONTAL, i2, 10, 0, 1000);
            s3 = new Scrollbar(Scrollbar.HORIZONTAL, i3, 10, 0, 1000);
            s4 = new Scrollbar(Scrollbar.HORIZONTAL, i4, 10, 0, 1000);

            nrmat = new Choice();
            nrmat.setBackground(Color.white);
            nrmat.setForeground(Color.blue);
            nrmat.addItem("<-- My Material");
            nrmat.addItem("Titanium ");
            nrmat.addItem("Stainless Steel");
            nrmat.addItem("Nickel Alloy");
            nrmat.addItem("Ceramic");
            nrmat.addItem("Actively Cooled");
            nrmat.select(5);

            atch = new Choice();
            atch.addItem("Calculate A7/A2");
            atch.addItem("Input A7/A2");
            atch.select(1);

            aech = new Choice();
            aech.addItem("Calculate A8/A7");
            aech.addItem("Input A8/A7");
            aech.select(1);

            lmat = new Label("lbm/ft^3", Label.LEFT);
            lmat.setForeground(Color.blue);

            add(atch);
            add(s3);
            add(s2);
            add(s4);
            add(aech);
            add(nrmat);
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

            turbo.athsched = atch.getSelectedIndex();
            if(turbo.athsched == 0) {
                ramjetNozzleLeftPanel.getF3().setBackground(Color.black);
                ramjetNozzleLeftPanel.getF3().setForeground(Color.yellow);
            }
            if(turbo.athsched == 1) {
                ramjetNozzleLeftPanel.getF3().setBackground(Color.white);
                ramjetNozzleLeftPanel.getF3().setForeground(Color.black);
            }
            turbo.aexsched = aech.getSelectedIndex();
            if(turbo.aexsched == 0) {
                ramjetNozzleLeftPanel.getF4().setBackground(Color.black);
                ramjetNozzleLeftPanel.getF4().setForeground(Color.yellow);
            }
            if(turbo.aexsched == 1) {
                ramjetNozzleLeftPanel.getF4().setBackground(Color.white);
                ramjetNozzleLeftPanel.getF4().setForeground(Color.black);
            }

            // ramjet burner - nozzle
            turbo.mnozr = nrmat.getSelectedIndex();
            if(turbo.mnozr > 0) {
                ramjetNozzleLeftPanel.getTn().setBackground(Color.black);
                ramjetNozzleLeftPanel.getTn().setForeground(Color.yellow);
            }
            if(turbo.mnozr == 0) {
                ramjetNozzleLeftPanel.getTn().setBackground(Color.white);
                ramjetNozzleLeftPanel.getTn().setForeground(Color.black);
            }
            switch (turbo.mnozr) {
                case 0: {
                    V1 = Double.valueOf(ramjetNozzleLeftPanel.getDn().getText());
                    v1 = V1;
                    V2 = Double.valueOf(ramjetNozzleLeftPanel.getTn().getText());
                    v2 = V2;
                    turbo.dnozr = v1 / turbo.dconv;
                    turbo.tnozr = v2 / turbo.tconv;
                    break;
                }
                case 1:
                    turbo.dnozr = 293.02;
                    turbo.tnozr = 1500.;
                    break;
                case 2:
                    turbo.dnozr = 476.56;
                    turbo.tnozr = 2000.;
                    break;
                case 3:
                    turbo.dnozr = 515.2;
                    turbo.tnozr = 2500.;
                    break;
                case 4:
                    turbo.dnozr = 164.2;
                    turbo.tnozr = 3000.;
                    break;
                case 5:
                    turbo.dnozr = 515.2;
                    turbo.tnozr = 4500.;
                    break;
            }
            turbo.solve.compute();
        }

        public void handleBar() { // ramjet burnerPanel -nozzle design
            int i2;
            int i3;
            int i4;
            double v2;
            double v3;
            double v4;
            float fl2;
            float fl3;
            float fl4;

            i2 = s2.getValue();
            i3 = s3.getValue();
            i4 = s4.getValue();

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                turbo.vmn3 = turbo.arthmn;
                turbo.vmx3 = turbo.arthmx;
                turbo.vmn4 = turbo.arexmn;
                turbo.vmx4 = turbo.arexmx;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmx2 = 100.0 - 100.0 * turbo.et7ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
                turbo.vmn3 = -10.0;
                turbo.vmx3 = 10.0;
                turbo.vmn4 = -10.0;
                turbo.vmx4 = 10.0;
            }

            v2 = i2 * (turbo.vmx2 - turbo.vmn2) / 1000. + turbo.vmn2;
            v3 = i3 * (turbo.vmx3 - turbo.vmn3) / 1000. + turbo.vmn3;
            v4 = i4 * (turbo.vmx4 - turbo.vmn4) / 1000. + turbo.vmn4;

            fl2 = (float)v2;
            fl3 = (float)v3;
            fl4 = (float)v4;

            // nozzle design
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.efficiency[7] = v2;
                turbo.arthd = v3;
                turbo.arexitd = v4;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.efficiency[7] = turbo.et7ref + v2 / 100.;
                turbo.arthd = v3 * turbo.a8ref / 100. + turbo.a8ref;
                turbo.arexitd = v4 * turbo.a8ref / 100. + turbo.a8ref;
            }

            ramjetNozzleLeftPanel.getF2().setText(String.valueOf(fl2));
            ramjetNozzleLeftPanel.getF3().setText(String.valueOf(fl3));
            ramjetNozzleLeftPanel.getF4().setText(String.valueOf(fl4));

            turbo.solve.compute();
        }  // end handle
    }  // end rightPanel

    public class RamjetNozzleLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField f4;
        private TextField dn;
        private TextField tn;
        Label l2;
        Label l5;
        Label lmat;
        Label lm2;

        RamjetNozzleLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(7, 2, 5, 5));

            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)turbo.efficiency[7]), 5));
            setF3(new TextField(String.valueOf((float)turbo.arthd), 5));
            getF3().setForeground(Color.black);
            getF3().setBackground(Color.white);
            setF4(new TextField(String.valueOf((float)turbo.arexitd), 5));
            getF4().setForeground(Color.black);
            getF4().setBackground(Color.white);
            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDn(new TextField(String.valueOf((float)turbo.dnozr), 5));
            getDn().setBackground(Color.black);
            getDn().setForeground(Color.yellow);
            setTn(new TextField(String.valueOf((float)turbo.tnozr), 5));
            getTn().setBackground(Color.black);
            getTn().setForeground(Color.yellow);

            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(new Label("A7/A2", Label.CENTER));
            add(getF3());
            add(l2);
            add(getF2());
            add(new Label("A8/A7", Label.CENTER));
            add(getF4());
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
            Double V2;
            Double V3;
            Double V4;
            Double V7;
            Double V8;
            double v2;
            double v3;
            double v4;
            double v7;
            double v8;
            int i2;
            int i3;
            int i4;
            float fl1;

            V2 = Double.valueOf(getF2().getText());
            v2 = V2;
            V3 = Double.valueOf(getF3().getText());
            v3 = V3;
            V4 = Double.valueOf(getF4().getText());
            v4 = V4;
            V7 = Double.valueOf(getDn().getText());
            v7 = V7;
            V8 = Double.valueOf(getTn().getText());
            v8 = V8;

            // Materials
            if(turbo.mnozr == 0) {
                if(v7 <= 1.0 * turbo.dconv) {
                    v7 = 1.0 * turbo.dconv;
                    getDn().setText(String.format("%.0f", v7 * turbo.dconv));
                }
                turbo.dnozr = v7 / turbo.dconv;
                if(v8 <= 500. * turbo.tconv) {
                    v8 = 500. * turbo.tconv;
                    getTn().setText(String.format("%.0f", v8 * turbo.tconv));
                }
                turbo.tnozr = v8 / turbo.tconv;
            }

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
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
                //  throat area ratio
                turbo.arthd = v3;
                turbo.vmn3 = turbo.arthmn;
                turbo.vmx3 = turbo.arthmx;
                if(v3 < turbo.vmn3) {
                    turbo.arthd = v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    turbo.arthd = v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                //  exit area ratio
                turbo.arexitd = v4;
                turbo.vmn4 = turbo.arexmn;
                turbo.vmx4 = turbo.arexmx;
                if(v4 < turbo.vmn4) {
                    turbo.arexitd = v4 = turbo.vmn4;
                    fl1 = (float)v4;
                    getF4().setText(String.valueOf(fl1));
                }
                if(v4 > turbo.vmx4) {
                    turbo.arexitd = v4 = turbo.vmx4;
                    fl1 = (float)v4;
                    getF4().setText(String.valueOf(fl1));
                }
            }

            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                // nozzle efficiency
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
                //  throat area ratio
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
                turbo.arthd = v3 * turbo.a8ref / 100. + turbo.a8ref;
                //  exit area ratio
                turbo.vmn4 = -10.0;
                turbo.vmx4 = 10.0;
                if(v4 < turbo.vmn4) {
                    v4 = turbo.vmn4;
                    fl1 = (float)v4;
                    getF4().setText(String.valueOf(fl1));
                }
                if(v4 > turbo.vmx4) {
                    v4 = turbo.vmx4;
                    fl1 = (float)v4;
                    getF4().setText(String.valueOf(fl1));
                }
                turbo.arexitd = v4 * turbo.a8ref / 100. + turbo.a8ref;
            }

            i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);
            i4 = (int)(((v4 - turbo.vmn4) / (turbo.vmx4 - turbo.vmn4)) * 1000.);

            ramjetNozzleRightPanel.s2.setValue(i2);
            ramjetNozzleRightPanel.s3.setValue(i3);
            ramjetNozzleRightPanel.s4.setValue(i4);

            turbo.solve.compute();
        }  // end handle


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
}  // end ramjetNozzlePanel
 
