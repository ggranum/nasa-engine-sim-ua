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

            int i1, i2, i3;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            i1 = (int)(((Turbo.p3fp2d - Turbo.fprmin) / (Turbo.fprmax - Turbo.fprmin)) * 1000.);
            i2 = (int)(((Turbo.eta[13] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.);
            i3 = (int)(((Turbo.byprat - Turbo.bypmin) / (Turbo.bypmax - Turbo.bypmin)) * 1000.);

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
            Double V1, V2;
            double v1, v2;

            // fanPanel
            Turbo.mfan = fmat.getSelectedIndex();
            if(Turbo.mfan > 0) {
                leftPanel.getDf().setBackground(Color.black);
                leftPanel.getDf().setForeground(Color.yellow);
                leftPanel.getTf().setBackground(Color.black);
                leftPanel.getTf().setForeground(Color.yellow);
            }
            if(Turbo.mfan == 0) {
                leftPanel.getDf().setBackground(Color.white);
                leftPanel.getDf().setForeground(Color.blue);
                leftPanel.getTf().setBackground(Color.white);
                leftPanel.getTf().setForeground(Color.blue);
            }
            switch (Turbo.mfan) {
                case 0: {
                    V1 = Double.valueOf(leftPanel.getDf().getText());
                    v1 = V1.doubleValue();
                    V2 = Double.valueOf(leftPanel.getTf().getText());
                    v2 = V2.doubleValue();
                    Turbo.dfan = v1 / Turbo.dconv;
                    Turbo.tfan = v2 / Turbo.tconv;
                    break;
                }
                case 1:
                    Turbo.dfan = 170.7;
                    Turbo.tfan = 900.;
                    break;
                case 2:
                    Turbo.dfan = 293.02;
                    Turbo.tfan = 1500.;
                    break;
                case 3:
                    Turbo.dfan = 476.56;
                    Turbo.tfan = 2000.;
                    break;
                case 4:
                    Turbo.dfan = 515.2;
                    Turbo.tfan = 2500.;
                    break;
                case 5:
                    Turbo.dfan = 515.2;
                    Turbo.tfan = 3000.;
                    break;
                case 6:
                    Turbo.dfan = 164.2;
                    Turbo.tfan = 3000.;
                    break;
            }
            turbo.solve.comPute();
        }

        public void handleBar() {     // fanPanel design
            int i1, i2, i3;
            double v1, v2, v3;
            float fl1, fl2, fl3;

            i1 = getS1().getValue();
            i2 = getS2().getValue();
            i3 = getS3().getValue();

            if(turbo.lunits <= 1) {
                Turbo.vmn1 = Turbo.fprmin;
                Turbo.vmx1 = Turbo.fprmax;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.bypmin;
                Turbo.vmx3 = Turbo.bypmax;
            }
            if(turbo.lunits == 2) {
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et13ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0;
                Turbo.vmn3 = -10.0;
                Turbo.vmx3 = 10.0;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;

            fl1 = (float)v1;
            fl2 = (float)v2;
            fl3 = (float)v3;

            // fanPanel design
            if(turbo.lunits <= 1) {
                Turbo.prat[13] = Turbo.p3fp2d = v1;
                Turbo.eta[13] = v2;
                Turbo.byprat = v3;
            }
            if(turbo.lunits == 2) {
                Turbo.prat[13] = Turbo.p3fp2d = v1 * Turbo.fpref / 100. + Turbo.fpref;
                Turbo.eta[13] = Turbo.et13ref + v2 / 100.;
                Turbo.byprat = v3 * Turbo.bpref / 100. + Turbo.bpref;
            }
            if(turbo.entype == 2) {
                Turbo.a2 = Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
                Turbo.a2d = Turbo.a2 * Turbo.aconv;
            }
            Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);

            leftPanel.getF1().setText(String.valueOf(fl1));
            leftPanel.getF2().setText(String.valueOf(fl2));
            leftPanel.getF3().setText(String.valueOf(fl3));

            turbo.solve.comPute();
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

        Label l1, l2, l3, l5, lmat, lm2;

        LeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            l1 = new Label("Press. Ratio", Label.CENTER);
            setF1(new TextField(String.valueOf((float)Turbo.p3fp2d), 5));
            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)Turbo.eta[13]), 5));
            l3 = new Label("Bypass Rat.", Label.CENTER);
            setF3(new TextField(String.valueOf((float)Turbo.byprat), 5));
            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDf(new TextField(String.valueOf((float)Turbo.dfan), 5));
            getDf().setBackground(Color.black);
            getDf().setForeground(Color.yellow);
            setTf(new TextField(String.valueOf((float)Turbo.tfan), 5));
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
            Double V1, V2, V3, V4, V5;
            double v1, v2, v3, v4, v5;
            int i1, i2, i3;
            float fl1;

            V1 = Double.valueOf(getF1().getText());
            v1 = V1.doubleValue();
            V2 = Double.valueOf(getF2().getText());
            v2 = V2.doubleValue();
            V3 = Double.valueOf(getF3().getText());
            v3 = V3.doubleValue();
            V4 = Double.valueOf(getDf().getText());
            v4 = V4.doubleValue();
            V5 = Double.valueOf(getTf().getText());
            v5 = V5.doubleValue();

            if(turbo.lunits <= 1) {
                // FanPanel pressure ratio
                Turbo.prat[13] = Turbo.p3fp2d = v1;
                Turbo.vmn1 = Turbo.fprmin;
                Turbo.vmx1 = Turbo.fprmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.prat[13] = Turbo.p3fp2d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.prat[13] = Turbo.p3fp2d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                // FanPanel efficiency
                Turbo.eta[13] = v2;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[13] = v2 = Turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[13] = v2 = Turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                // bypass ratio
                Turbo.byprat = v3;
                Turbo.vmn3 = Turbo.bypmin;
                Turbo.vmx3 = Turbo.bypmax;
                if(v3 < Turbo.vmn3) {
                    Turbo.byprat = v3 = Turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > Turbo.vmx3) {
                    Turbo.byprat = v3 = Turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
            }
            if(turbo.lunits == 2) {
                // FanPanel pressure ratio
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
                Turbo.prat[13] = Turbo.p3fp2d = v1 * Turbo.fpref / 100. + Turbo.fpref;
                // FanPanel efficiency
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et13ref;
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
                Turbo.eta[13] = Turbo.et13ref + v2 / 100.;
                // bypass ratio
                Turbo.vmn3 = -10.0;
                Turbo.vmx3 = 10.0;
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
                Turbo.byprat = v3 * Turbo.bpref / 100. + Turbo.bpref;
            }
            if(turbo.entype == 2) {
                Turbo.a2 = Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
                Turbo.a2d = Turbo.a2 * Turbo.aconv;
            }
            Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
            // materials
            if(Turbo.mfan == 0) {
                if(v4 <= 1.0 * Turbo.dconv) {
                    v4 = 1.0 * Turbo.dconv;
                    getDf().setText(String.valueOf(turbo.filter0(v4 * Turbo.dconv)));
                }
                Turbo.dfan = v4 / Turbo.dconv;
                if(v5 <= 500. * Turbo.tconv) {
                    v5 = 500. * Turbo.tconv;
                    getTf().setText(String.valueOf(turbo.filter0(v5 * Turbo.tconv)));
                }
                Turbo.tfan = v5 / Turbo.tconv;
            }

            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

            rightPanel.getS1().setValue(i1);
            rightPanel.getS2().setValue(i2);
            rightPanel.getS3().setValue(i3);

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
 
