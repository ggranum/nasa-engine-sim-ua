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

            i1 = (int)(((Turbo.p3p2d - Turbo.cprmin) / (Turbo.cprmax - Turbo.cprmin)) * 1000.);
            i2 = (int)(((Turbo.eta[3] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.);

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
            Turbo.ncflag = stgch.getSelectedIndex();
            if(Turbo.ncflag == 0) {
                compressorLeftPanel.getF3().setBackground(Color.black);
                compressorLeftPanel.getF3().setForeground(Color.yellow);
            }
            if(Turbo.ncflag == 1) {
                compressorLeftPanel.getF3().setBackground(Color.white);
                compressorLeftPanel.getF3().setForeground(Color.black);
            }

            Turbo.mcomp = cmat.getSelectedIndex();
            if(Turbo.mcomp > 0) {
                compressorLeftPanel.getDc().setBackground(Color.black);
                compressorLeftPanel.getDc().setForeground(Color.yellow);
                compressorLeftPanel.getTc().setBackground(Color.black);
                compressorLeftPanel.getTc().setForeground(Color.yellow);
            }
            if(Turbo.mcomp == 0) {
                compressorLeftPanel.getDc().setBackground(Color.white);
                compressorLeftPanel.getDc().setForeground(Color.blue);
                compressorLeftPanel.getTc().setBackground(Color.white);
                compressorLeftPanel.getTc().setForeground(Color.blue);
            }
            switch (Turbo.mcomp) {
                case 0: {
                    V1 = Double.valueOf(compressorLeftPanel.getDc().getText());
                    v1 = V1;
                    V2 = Double.valueOf(compressorLeftPanel.getTc().getText());
                    v2 = V2;
                    Turbo.dcomp = v1 / Turbo.dconv;
                    Turbo.tcomp = v2 / Turbo.tconv;
                    break;
                }
                case 1:
                    Turbo.dcomp = 170.7;
                    Turbo.tcomp = 900.;
                    break;
                case 2:
                    Turbo.dcomp = 293.02;
                    Turbo.tcomp = 1500.;
                    break;
                case 3:
                    Turbo.dcomp = 476.56;
                    Turbo.tcomp = 2000.;
                    break;
                case 4:
                    Turbo.dcomp = 515.2;
                    Turbo.tcomp = 2500.;
                    break;
                case 5:
                    Turbo.dcomp = 515.2;
                    Turbo.tcomp = 3000.;
                    break;
                case 6:
                    Turbo.dcomp = 164.2;
                    Turbo.tcomp = 3000.;
                    break;
            }
            turbo.solve.comPute();
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

            if(turbo.lunits <= 1) {
                Turbo.vmn1 = Turbo.cprmin;
                Turbo.vmx1 = Turbo.cprmax;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
            }
            if(turbo.lunits == 2) {
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et3ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;

            fl1 = (float)v1;
            fl2 = (float)v2;

            //  compressor design
            if(turbo.lunits <= 1) {
                Turbo.prat[3] = Turbo.p3p2d = v1;
                Turbo.eta[3] = v2;
            }
            if(turbo.lunits == 2) {
                Turbo.prat[3] = Turbo.p3p2d = v1 * Turbo.cpref / 100. + Turbo.cpref;
                Turbo.eta[3] = Turbo.et3ref + v2 / 100.;
            }

            compressorLeftPanel.getF1().setText(String.valueOf(fl1));
            compressorLeftPanel.getF2().setText(String.valueOf(fl2));

            turbo.solve.comPute();
        }  // end handle
    }  // end rightPanel

    public class CompressorLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField dc;
        private TextField tc;
        Label l1;
        Label l2;
        Label l5;
        Label lmat;
        Label lm2;

        CompressorLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            l1 = new Label("Press. Ratio", Label.CENTER);
            setF1(new TextField(String.valueOf((float)Turbo.p3p2d), 5));
            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)Turbo.eta[13]), 5));
            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setF3(new TextField(String.valueOf((int)Turbo.ncomp), 5));
            getF3().setBackground(Color.black);
            getF3().setForeground(Color.yellow);

            setDc(new TextField(String.valueOf((float)Turbo.dcomp), 5));
            getDc().setBackground(Color.black);
            getDc().setForeground(Color.yellow);
            setTc(new TextField(String.valueOf((float)Turbo.tcomp), 5));
            getTc().setBackground(Color.black);
            getTc().setForeground(Color.yellow);

            add(new Label("Stages ", Label.CENTER));
            add(getF3());
            add(l1);
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
            if(Turbo.mcomp == 0) {
                if(v4 <= 1.0 * Turbo.dconv) {
                    v4 = 1.0 * Turbo.dconv;
                    getDc().setText(String.format("%.0f", v4 * Turbo.dconv));
                }
                Turbo.dcomp = v4 / Turbo.dconv;
                if(v6 <= 500. * Turbo.tconv) {
                    v6 = 500. * Turbo.tconv;
                    getTc().setText(String.format("%.0f", v6 * Turbo.tconv));
                }
                Turbo.tcomp = v6 / Turbo.tconv;
            }
            // number of stages
            if(Turbo.ncflag == 1) {
                Turbo.ncomp = i3;
                if(Turbo.ncomp <= 0) {
                    Turbo.ncomp = 1;
                    getF3().setText(String.valueOf(Turbo.ncomp));
                }
            }

            if(turbo.lunits <= 1) {
                // Compressor pressure ratio
                Turbo.prat[3] = Turbo.p3p2d = v1;
                Turbo.vmn1 = Turbo.cprmin;
                Turbo.vmx1 = Turbo.cprmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                // Compressor efficiency
                Turbo.eta[3] = v2;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[3] = v2 = Turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[3] = v2 = Turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
            }
            if(turbo.lunits == 2) {
                // Compressor pressure ratio
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
                Turbo.prat[3] = Turbo.p3p2d = v1 * Turbo.cpref / 100. + Turbo.cpref;
                // Compressor efficiency
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et3ref;
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
                Turbo.eta[3] = Turbo.et3ref + v2 / 100.;
            }

            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);

            compressorRightPanel.s1.setValue(i1);
            compressorRightPanel.s2.setValue(i2);

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
 
