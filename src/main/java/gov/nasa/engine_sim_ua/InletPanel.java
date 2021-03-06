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
public class InletPanel extends Panel {

    InletRightPanel inletRightPanel;
    public InletLeftPanel inletLeftPanel;

    InletPanel(Turbo turbo) {
        setLayout(new GridLayout(1, 2, 10, 10));
        inletLeftPanel = new InletLeftPanel(turbo);
        inletRightPanel = new InletRightPanel(turbo);
        add(inletLeftPanel);
        add(inletRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class InletRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Choice inltch;
        Choice imat;
        Label lmat;

        InletRightPanel(Turbo target) {

            int i1;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            inltch = new Choice();
            inltch.addItem("Mil Spec Recovery");
            inltch.addItem("Input Recovery");
            inltch.select(0);

            i1 = (int)(((turbo.efficiency[2] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);

            s1 = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);

            imat = new Choice();
            imat.setBackground(Color.white);
            imat.setForeground(Color.blue);
            imat.addItem("<-- My Material");
            imat.addItem("Aluminum");
            imat.addItem("Titanium ");
            imat.addItem("Stainless Steel");
            imat.addItem("Nickel Alloy");
            imat.addItem("Actively Cooled");
            imat.select(1);

            lmat = new Label("lbm/ft^3 ", Label.LEFT);
            lmat.setForeground(Color.blue);

            add(inltch);
            add(s1);
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(imat);
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

        public void handleMat() {  // materials
            Double V1;
            Double V2;
            double v1;
            double v2;

            // inletPanel
            turbo.pt2flag = inltch.getSelectedIndex();
            if(turbo.pt2flag == 0) {
                inletLeftPanel.getF1().setBackground(Color.black);
                inletLeftPanel.getF1().setForeground(Color.yellow);
            }
            if(turbo.pt2flag == 1) {
                inletLeftPanel.getF1().setBackground(Color.white);
                inletLeftPanel.getF1().setForeground(Color.black);
            }
            turbo.minlt = imat.getSelectedIndex();
            if(turbo.minlt > 0) {
                inletLeftPanel.getDi().setBackground(Color.black);
                inletLeftPanel.getDi().setForeground(Color.yellow);
                inletLeftPanel.getTi().setBackground(Color.black);
                inletLeftPanel.getTi().setForeground(Color.yellow);
            }
            if(turbo.minlt == 0) {
                inletLeftPanel.getDi().setBackground(Color.white);
                inletLeftPanel.getDi().setForeground(Color.blue);
                inletLeftPanel.getTi().setBackground(Color.white);
                inletLeftPanel.getTi().setForeground(Color.blue);
            }
            switch (turbo.minlt) {
                case 0: {
                    V1 = Double.valueOf(inletLeftPanel.getDi().getText());
                    v1 = V1;
                    V2 = Double.valueOf(inletLeftPanel.getTi().getText());
                    v2 = V2;
                    turbo.dinlt = v1 / turbo.dconv;
                    turbo.tinlt = v2 / turbo.tconv;
                    break;
                }
                case 1:
                    turbo.dinlt = 170.7;
                    turbo.tinlt = 900.;
                    break;
                case 2:
                    turbo.dinlt = 293.02;
                    turbo.tinlt = 1500.;
                    break;
                case 3:
                    turbo.dinlt = 476.56;
                    turbo.tinlt = 2000.;
                    break;
                case 4:
                    turbo.dinlt = 515.2;
                    turbo.tinlt = 2500.;
                    break;
                case 5:
                    turbo.dinlt = 515.2;
                    turbo.tinlt = 4000.;
                    break;
            }
            turbo.solve.compute();
        }

        public void handleBar() {     // inletPanel recovery
            int i1;
            double v1;

            i1 = s1.getValue();

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.vmn1 = turbo.etmin;
                turbo.vmx1 = turbo.etmax;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmx1 = 100.0 - 100.0 * turbo.et2ref;
                turbo.vmn1 = turbo.vmx1 - 20.0;
            }

            v1 = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;

            // inletPanel design
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.efficiency[2] = v1;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.efficiency[2] = turbo.et2ref + v1 / 100.;
            }

            inletLeftPanel.getF1().setText(String.format("%.3f", v1));

            turbo.solve.compute();
        }  // end handle
    }  // end rightPanel

    public class InletLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField ti;
        private TextField di;
        Label l1;
        Label l5;
        Label lmat;
        Label lm2;

        InletLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            l1 = new Label("Pres Recov.", Label.CENTER);
            setF1(new TextField(String.valueOf((float)turbo.efficiency[2]), 5));
            getF1().setBackground(Color.black);
            getF1().setForeground(Color.yellow);
            lmat = new Label("T lim -R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setTi(new TextField(String.valueOf((float)turbo.tinlt), 5));
            getTi().setBackground(Color.black);
            getTi().setForeground(Color.yellow);
            setDi(new TextField(String.valueOf((float)turbo.dinlt), 5));
            getDi().setBackground(Color.black);
            getDi().setForeground(Color.yellow);

            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(l1);
            add(getF1());
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(lm2);
            add(new Label(" ", Label.CENTER));
            add(lmat);
            add(getTi());
            add(l5);
            add(getDi());
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleText();
            }
        }

        public void handleText() {
            Double V1;
            Double V3;
            Double V5;
            double v1;
            double v3;
            double v5;
            int i1;
            float fl1;

            V1 = Double.valueOf(getF1().getText());
            v1 = V1;
            V3 = Double.valueOf(getDi().getText());
            v3 = V3;
            V5 = Double.valueOf(getTi().getText());
            v5 = V5;

            // materials
            if(turbo.minlt == 0) {
                if(v3 <= 1.0 * turbo.dconv) {
                    v3 = 1.0 * turbo.dconv;
                    getDi().setText(String.format("%.0f", v3 * turbo.dconv));
                }
                turbo.dinlt = v3 / turbo.dconv;
                if(v5 <= 500. * turbo.tconv) {
                    v5 = 500. * turbo.tconv;
                    getTi().setText(String.format("%.0f", v5 * turbo.tconv));
                }
                turbo.tinlt = v5 / turbo.tconv;
            }
            // InletPanel pressure ratio
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.efficiency[2] = v1;
                turbo.vmn1 = turbo.etmin;
                turbo.vmx1 = turbo.etmax;
                if(v1 < turbo.vmn1) {
                    turbo.efficiency[2] = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.efficiency[2] = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmx1 = 100.0 - 100.0 * turbo.et2ref;
                turbo.vmn1 = turbo.vmx1 - 20.0;
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
                turbo.efficiency[2] = turbo.et2ref + v1 / 100.;
            }

            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);

            inletRightPanel.s1.setValue(i1);

            turbo.solve.compute();
        }  // end handle

        public TextField getDi() {
            return di;
        }

        public void setDi(TextField di) {
            this.di = di;
        }

        public TextField getF1() {
            return f1;
        }

        public void setF1(TextField f1) {
            this.f1 = f1;
        }

        public TextField getTi() {
            return ti;
        }

        public void setTi(TextField ti) {
            this.ti = ti;
        }
    }  //  end  inletLeftPanel
}  // end InletPanel panel
 
