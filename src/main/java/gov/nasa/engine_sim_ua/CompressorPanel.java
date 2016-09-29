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
public class CompressorPanel extends Panel {

    public CompressorRightPanel compressorRightPanel;
    public CompressorLeftPanel compressorLeftPanel;

    CompressorPanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        compressorLeftPanel = new CompressorLeftPanel(turbo);
        compressorRightPanel = new CompressorRightPanel(turbo);

        add(compressorLeftPanel);
        add(compressorRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class CompressorRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Scrollbar s2;
        Choice stgch;
        Choice cmat;
        Label lmat;

        CompressorRightPanel(Turbo target) {

            int i1;
            int i2;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            i1 = (int)(((turbo.p3p2d - turbo.cprmin) / (turbo.cprmax - turbo.cprmin)) * 1000.);
            i2 = (int)(((turbo.efficiency[3] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);

            s1 = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);
            s2 = new Scrollbar(Scrollbar.HORIZONTAL, i2, 10, 0, 1000);

            cmat = new Choice();
            cmat.setBackground(Color.white);
            cmat.setForeground(Color.blue);
            cmat.addItem("<-- My Material");
            cmat.addItem("Aluminum");
            cmat.addItem("Titanium ");
            cmat.addItem("Stainless Steel");
            cmat.addItem("Nickel Alloy");
            cmat.addItem("Nickel Crystal");
            cmat.addItem("Ceramic");
            cmat.select(2);

            lmat = new Label("lbm/ft^3", Label.LEFT);
            lmat.setForeground(Color.blue);

            stgch = new Choice();
            stgch.addItem("Compute # Stages");
            stgch.addItem("Input # Stages");
            stgch.select(0);

            add(stgch);
            add(s1);
            add(s2);
            add(new Label(" ", Label.LEFT));
            add(cmat);
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

            // compressor
            turbo.ncflag = stgch.getSelectedIndex();
            if(turbo.ncflag == 0) {
                compressorLeftPanel.getF3().setBackground(Color.black);
                compressorLeftPanel.getF3().setForeground(Color.yellow);
            }
            if(turbo.ncflag == 1) {
                compressorLeftPanel.getF3().setBackground(Color.white);
                compressorLeftPanel.getF3().setForeground(Color.black);
            }

            turbo.mcomp = cmat.getSelectedIndex();
            if(turbo.mcomp > 0) {
                compressorLeftPanel.getDc().setBackground(Color.black);
                compressorLeftPanel.getDc().setForeground(Color.yellow);
                compressorLeftPanel.getTc().setBackground(Color.black);
                compressorLeftPanel.getTc().setForeground(Color.yellow);
            }
            if(turbo.mcomp == 0) {
                compressorLeftPanel.getDc().setBackground(Color.white);
                compressorLeftPanel.getDc().setForeground(Color.blue);
                compressorLeftPanel.getTc().setBackground(Color.white);
                compressorLeftPanel.getTc().setForeground(Color.blue);
            }
            switch (turbo.mcomp) {
                case 0: {
                    V1 = Double.valueOf(compressorLeftPanel.getDc().getText());
                    v1 = V1;
                    V2 = Double.valueOf(compressorLeftPanel.getTc().getText());
                    v2 = V2;
                    turbo.dcomp = v1 / turbo.dconv;
                    turbo.tcomp = v2 / turbo.tconv;
                    break;
                }
                case 1:
                    turbo.dcomp = 170.7;
                    turbo.tcomp = 900.;
                    break;
                case 2:
                    turbo.dcomp = 293.02;
                    turbo.tcomp = 1500.;
                    break;
                case 3:
                    turbo.dcomp = 476.56;
                    turbo.tcomp = 2000.;
                    break;
                case 4:
                    turbo.dcomp = 515.2;
                    turbo.tcomp = 2500.;
                    break;
                case 5:
                    turbo.dcomp = 515.2;
                    turbo.tcomp = 3000.;
                    break;
                case 6:
                    turbo.dcomp = 164.2;
                    turbo.tcomp = 3000.;
                    break;
            }
            turbo.solve.compute();
        }

        public void handleBar() {  // compressor design
            int i1;
            int i2;
            double v1;
            double v2;
            float fl1;
            float fl2;

            i1 = s1.getValue();
            i2 = s2.getValue();

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.vmn1 = turbo.cprmin;
                turbo.vmx1 = turbo.cprmax;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                turbo.vmx2 = 100.0 - 100.0 * turbo.et3ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
            }

            v1 = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
            v2 = i2 * (turbo.vmx2 - turbo.vmn2) / 1000. + turbo.vmn2;

            fl1 = (float)v1;
            fl2 = (float)v2;

            //  compressor design
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.pressureRatio[3] = turbo.p3p2d = v1;
                turbo.efficiency[3] = v2;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.pressureRatio[3] = turbo.p3p2d = v1 * turbo.cpref / 100. + turbo.cpref;
                turbo.efficiency[3] = turbo.et3ref + v2 / 100.;
            }

            compressorLeftPanel.getF1().setText(String.valueOf(fl1));
            compressorLeftPanel.getF2().setText(String.valueOf(fl2));

            turbo.solve.compute();
        }  // end handle
    }  // end rightPanel

    public class CompressorLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField dc;
        private TextField tc;
        Label lblPressureRatio3_2;
        Label l2;
        Label l5;
        Label lmat;
        Label lm2;

        CompressorLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            lblPressureRatio3_2 = new Label("Press. Ratio", Label.CENTER);
            setF1(new TextField(String.valueOf(turbo.p3p2d), 5));
            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)turbo.efficiency[13]), 5));
            lmat = new Label("T lim ºR", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setF3(new TextField(String.valueOf(turbo.ncomp), 5));
            getF3().setBackground(Color.black);
            getF3().setForeground(Color.yellow);

            setDc(new TextField(String.valueOf(turbo.dcomp), 5));
            getDc().setBackground(Color.black);
            getDc().setForeground(Color.yellow);
            setTc(new TextField(String.valueOf(turbo.tcomp), 5));
            getTc().setBackground(Color.black);
            getTc().setForeground(Color.yellow);

            add(new Label("Stages ", Label.CENTER));
            add(getF3());
            add(lblPressureRatio3_2);
            add(getF1());
            add(l2);
            add(getF2());
            add(lm2);
            add(new Label(" ", Label.CENTER));
            add(lmat);
            add(getTc());
            add(l5);
            add(getDc());
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleText();
            }
        }

        public void handleText() {
            Double V1;
            Double V2;
            Double V4;
            Double V6;
            double v1;
            double v2;
            double v4;
            double v6;
            Integer I3;
            int i1;
            int i2;
            int i3;
            float fl1;

            V1 = Double.valueOf(getF1().getText());
            v1 = V1;
            V2 = Double.valueOf(getF2().getText());
            v2 = V2;
            V4 = Double.valueOf(getDc().getText());
            v4 = V4;
            V6 = Double.valueOf(getTc().getText());
            v6 = V6;

            I3 = Integer.valueOf(getF3().getText());
            i3 = I3;

            // materials
            if(turbo.mcomp == 0) {
                if(v4 <= 1.0 * turbo.dconv) {
                    v4 = 1.0 * turbo.dconv;
                    getDc().setText(String.format("%.0f", v4 * turbo.dconv));
                }
                turbo.dcomp = v4 / turbo.dconv;
                if(v6 <= 500. * turbo.tconv) {
                    v6 = 500. * turbo.tconv;
                    getTc().setText(String.format("%.0f", v6 * turbo.tconv));
                }
                turbo.tcomp = v6 / turbo.tconv;
            }
            // number of stages
            if(turbo.ncflag == 1) {
                turbo.ncomp = i3;
                if(turbo.ncomp <= 0) {
                    turbo.ncomp = 1;
                    getF3().setText(String.valueOf(turbo.ncomp));
                }
            }

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                // Compressor pressure ratio
                turbo.pressureRatio[3] = turbo.p3p2d = v1;
                turbo.vmn1 = turbo.cprmin;
                turbo.vmx1 = turbo.cprmax;
                if(v1 < turbo.vmn1) {
                    turbo.pressureRatio[3] = turbo.p3p2d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.pressureRatio[3] = turbo.p3p2d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                // Compressor efficiency
                turbo.efficiency[3] = v2;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                if(v2 < turbo.vmn2) {
                    turbo.efficiency[3] = v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    turbo.efficiency[3] = v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                // Compressor pressure ratio
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
                turbo.pressureRatio[3] = turbo.p3p2d = v1 * turbo.cpref / 100. + turbo.cpref;
                // Compressor efficiency
                turbo.vmx2 = 100.0 - 100.0 * turbo.et3ref;
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
                turbo.efficiency[3] = turbo.et3ref + v2 / 100.;
            }

            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);

            compressorRightPanel.s1.setValue(i1);
            compressorRightPanel.s2.setValue(i2);

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

        public TextField getDc() {
            return dc;
        }

        public void setDc(TextField dc) {
            this.dc = dc;
        }

        public TextField getTc() {
            return tc;
        }

        public void setTc(TextField tc) {
            this.tc = tc;
        }
    }  //  end  inletLeftPanel
}  // end CompressorPanel panel
 
