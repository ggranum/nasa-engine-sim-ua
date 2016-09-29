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
public class FanPanel extends Panel {

    RightPanel rightPanel;
    public LeftPanel leftPanel;

    FanPanel(Turbo turbo) {
        setLayout(new GridLayout(1, 2, 10, 10));

        leftPanel = new LeftPanel(turbo);
        rightPanel = new RightPanel(turbo);

        add(leftPanel);
        add(rightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class RightPanel extends Panel {

        Turbo turbo;
        private Scrollbar s1;
        private Scrollbar s2;
        private Scrollbar s3;
        Label lmat;
        Choice fmat;

        RightPanel(Turbo target) {

            int i1;
            int i2;
            int i3;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            i1 = (int)(((turbo.p3fp2d - turbo.fprmin) / (turbo.fprmax - turbo.fprmin)) * 1000.);
            i2 = (int)(((turbo.eta[13] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);
            i3 = (int)(((turbo.byprat - turbo.bypmin) / (turbo.bypmax - turbo.bypmin)) * 1000.);

            setS1(new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000));
            setS2(new Scrollbar(Scrollbar.HORIZONTAL, i2, 10, 0, 1000));
            setS3(new Scrollbar(Scrollbar.HORIZONTAL, i3, 10, 0, 1000));

            fmat = new Choice();
            fmat.setBackground(Color.white);
            fmat.setForeground(Color.blue);
            fmat.addItem("<-- My Material");
            fmat.addItem("Aluminum");
            fmat.addItem("Titanium ");
            fmat.addItem("Stainless Steel");
            fmat.addItem("Nickel Alloy");
            fmat.addItem("Nickel Crystal");
            fmat.addItem("Ceramic");
            fmat.select(2);

            lmat = new Label("lbm/ft^3 ", Label.LEFT);
            lmat.setForeground(Color.blue);

            add(getS3());
            add(getS1());
            add(getS2());
            add(new Label(" ", Label.LEFT));
            add(fmat);
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

            // fanPanel
            turbo.mfan = fmat.getSelectedIndex();
            if(turbo.mfan > 0) {
                leftPanel.getDf().setBackground(Color.black);
                leftPanel.getDf().setForeground(Color.yellow);
                leftPanel.getTf().setBackground(Color.black);
                leftPanel.getTf().setForeground(Color.yellow);
            }
            if(turbo.mfan == 0) {
                leftPanel.getDf().setBackground(Color.white);
                leftPanel.getDf().setForeground(Color.blue);
                leftPanel.getTf().setBackground(Color.white);
                leftPanel.getTf().setForeground(Color.blue);
            }
            switch (turbo.mfan) {
                case 0: {
                    V1 = Double.valueOf(leftPanel.getDf().getText());
                    v1 = V1;
                    V2 = Double.valueOf(leftPanel.getTf().getText());
                    v2 = V2;
                    turbo.dfan = v1 / turbo.dconv;
                    turbo.tfan = v2 / turbo.tconv;
                    break;
                }
                case 1:
                    turbo.dfan = 170.7;
                    turbo.tfan = 900.;
                    break;
                case 2:
                    turbo.dfan = 293.02;
                    turbo.tfan = 1500.;
                    break;
                case 3:
                    turbo.dfan = 476.56;
                    turbo.tfan = 2000.;
                    break;
                case 4:
                    turbo.dfan = 515.2;
                    turbo.tfan = 2500.;
                    break;
                case 5:
                    turbo.dfan = 515.2;
                    turbo.tfan = 3000.;
                    break;
                case 6:
                    turbo.dfan = 164.2;
                    turbo.tfan = 3000.;
                    break;
            }
            turbo.solve.compute();
        }

        public void handleBar() {     // fanPanel design
            int i1;
            int i2;
            int i3;
            double v1;
            double v2;
            double v3;
            float fl1;
            float fl2;
            float fl3;

            i1 = getS1().getValue();
            i2 = getS2().getValue();
            i3 = getS3().getValue();

            if(turbo.lunits <= 1) {
                turbo.vmn1 = turbo.fprmin;
                turbo.vmx1 = turbo.fprmax;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                turbo.vmn3 = turbo.bypmin;
                turbo.vmx3 = turbo.bypmax;
            }
            if(turbo.lunits == 2) {
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                turbo.vmx2 = 100.0 - 100.0 * turbo.et13ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
                turbo.vmn3 = -10.0;
                turbo.vmx3 = 10.0;
            }

            v1 = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
            v2 = i2 * (turbo.vmx2 - turbo.vmn2) / 1000. + turbo.vmn2;
            v3 = i3 * (turbo.vmx3 - turbo.vmn3) / 1000. + turbo.vmn3;

            fl1 = (float)v1;
            fl2 = (float)v2;
            fl3 = (float)v3;

            // fanPanel design
            if(turbo.lunits <= 1) {
                turbo.prat[13] = turbo.p3fp2d = v1;
                turbo.eta[13] = v2;
                turbo.byprat = v3;
            }
            if(turbo.lunits == 2) {
                turbo.prat[13] = turbo.p3fp2d = v1 * turbo.fpref / 100. + turbo.fpref;
                turbo.eta[13] = turbo.et13ref + v2 / 100.;
                turbo.byprat = v3 * turbo.bpref / 100. + turbo.bpref;
            }
            if(turbo.entype == 2) {
                turbo.a2 = turbo.afan = turbo.acore * (1.0 + turbo.byprat);
                turbo.a2d = turbo.a2 * turbo.aconv;
            }
            turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);

            leftPanel.getF1().setText(String.valueOf(fl1));
            leftPanel.getF2().setText(String.valueOf(fl2));
            leftPanel.getF3().setText(String.valueOf(fl3));

            turbo.solve.compute();
        }  // end handle

        public Scrollbar getS1() {
            return s1;
        }

        public void setS1(Scrollbar s1) {
            this.s1 = s1;
        }

        public Scrollbar getS2() {
            return s2;
        }

        public void setS2(Scrollbar s2) {
            this.s2 = s2;
        }

        public Scrollbar getS3() {
            return s3;
        }

        public void setS3(Scrollbar s3) {
            this.s3 = s3;
        }
    }  // end rightPanel

    public class LeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField df;
        private TextField tf;

        Label l1;
        Label l2;
        Label l3;
        Label l5;
        Label lmat;
        Label lm2;

        LeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            l1 = new Label("Press. Ratio", Label.CENTER);
            setF1(new TextField(String.valueOf((float)turbo.p3fp2d), 5));
            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)turbo.eta[13]), 5));
            l3 = new Label("Bypass Rat.", Label.CENTER);
            setF3(new TextField(String.valueOf((float)turbo.byprat), 5));
            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDf(new TextField(String.valueOf((float)turbo.dfan), 5));
            getDf().setBackground(Color.black);
            getDf().setForeground(Color.yellow);
            setTf(new TextField(String.valueOf((float)turbo.tfan), 5));
            getTf().setBackground(Color.black);
            getTf().setForeground(Color.yellow);

            add(l3);
            add(getF3());
            add(l1);
            add(getF1());
            add(l2);
            add(getF2());
            add(lm2);
            add(new Label(" ", Label.CENTER));
            add(lmat);
            add(getTf());
            add(l5);
            add(getDf());
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
            double v1;
            double v2;
            double v3;
            double v4;
            double v5;
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
            V4 = Double.valueOf(getDf().getText());
            v4 = V4;
            V5 = Double.valueOf(getTf().getText());
            v5 = V5;

            if(turbo.lunits <= 1) {
                // FanPanel pressure ratio
                turbo.prat[13] = turbo.p3fp2d = v1;
                turbo.vmn1 = turbo.fprmin;
                turbo.vmx1 = turbo.fprmax;
                if(v1 < turbo.vmn1) {
                    turbo.prat[13] = turbo.p3fp2d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.prat[13] = turbo.p3fp2d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                // FanPanel efficiency
                turbo.eta[13] = v2;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                if(v2 < turbo.vmn2) {
                    turbo.eta[13] = v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    turbo.eta[13] = v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                // bypass ratio
                turbo.byprat = v3;
                turbo.vmn3 = turbo.bypmin;
                turbo.vmx3 = turbo.bypmax;
                if(v3 < turbo.vmn3) {
                    turbo.byprat = v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    turbo.byprat = v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
            }
            if(turbo.lunits == 2) {
                // FanPanel pressure ratio
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
                turbo.prat[13] = turbo.p3fp2d = v1 * turbo.fpref / 100. + turbo.fpref;
                // FanPanel efficiency
                turbo.vmx2 = 100.0 - 100.0 * turbo.et13ref;
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
                turbo.eta[13] = turbo.et13ref + v2 / 100.;
                // bypass ratio
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
                turbo.byprat = v3 * turbo.bpref / 100. + turbo.bpref;
            }
            if(turbo.entype == 2) {
                turbo.a2 = turbo.afan = turbo.acore * (1.0 + turbo.byprat);
                turbo.a2d = turbo.a2 * turbo.aconv;
            }
            turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
            // materials
            if(turbo.mfan == 0) {
                if(v4 <= 1.0 * turbo.dconv) {
                    v4 = 1.0 * turbo.dconv;
                    getDf().setText(String.format("%.0f", v4 * turbo.dconv));
                }
                turbo.dfan = v4 / turbo.dconv;
                if(v5 <= 500. * turbo.tconv) {
                    v5 = 500. * turbo.tconv;
                    getTf().setText(String.format("%.0f", v5 * turbo.tconv));
                }
                turbo.tfan = v5 / turbo.tconv;
            }

            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

            rightPanel.getS1().setValue(i1);
            rightPanel.getS2().setValue(i2);
            rightPanel.getS3().setValue(i3);

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

        public TextField getDf() {
            return df;
        }

        public void setDf(TextField df) {
            this.df = df;
        }

        public TextField getTf() {
            return tf;
        }

        public void setTf(TextField tf) {
            this.tf = tf;
        }
    }  //  end  inletLeftPanel
}  // end FanPanel
 
