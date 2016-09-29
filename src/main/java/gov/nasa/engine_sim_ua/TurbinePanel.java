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
public class TurbinePanel extends Panel {

    TurbineRightPanel turbineRightPanel;
    public TurbineLeftPanel turbineLeftPanel;

    TurbinePanel(Turbo turbo) {
        setLayout(new GridLayout(1, 2, 10, 10));

        turbineLeftPanel = new TurbineLeftPanel(turbo);
        turbineRightPanel = new TurbineRightPanel(turbo);

        add(turbineLeftPanel);
        add(turbineRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class TurbineRightPanel extends Panel {

        Turbo turbo;
        Scrollbar s1;
        Label lmat;
        Choice tmat, stgch;

        TurbineRightPanel(Turbo target) {

            int i1;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            i1 = (int)(((Turbo.eta[5] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.);

            s1 = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);

            stgch = new Choice();
            stgch.addItem("Compute # Stages");
            stgch.addItem("Input # Stages");
            stgch.select(0);

            tmat = new Choice();
            tmat.setBackground(Color.white);
            tmat.setForeground(Color.blue);
            tmat.addItem("<-- My Material");
            tmat.addItem("Aluminum");
            tmat.addItem("Titanium ");
            tmat.addItem("Stainless Steel");
            tmat.addItem("Nickel Alloy");
            tmat.addItem("Nickel Crystal");
            tmat.addItem("Ceramic");
            tmat.select(4);

            lmat = new Label("lbm/ft^3", Label.LEFT);
            lmat.setForeground(Color.blue);

            add(stgch);
            add(s1);
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(tmat);
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

            Turbo.ntflag = stgch.getSelectedIndex();
            if(Turbo.ntflag == 0) {
                turbineLeftPanel.getF3().setBackground(Color.black);
                turbineLeftPanel.getF3().setForeground(Color.yellow);
            }
            if(Turbo.ntflag == 1) {
                turbineLeftPanel.getF3().setBackground(Color.white);
                turbineLeftPanel.getF3().setForeground(Color.black);
            }
            // turnine
            Turbo.mturbin = tmat.getSelectedIndex();
            if(Turbo.mturbin > 0) {
                turbineLeftPanel.getDt().setBackground(Color.black);
                turbineLeftPanel.getDt().setForeground(Color.yellow);
                turbineLeftPanel.getTt().setBackground(Color.black);
                turbineLeftPanel.getTt().setForeground(Color.yellow);
            }
            if(Turbo.mturbin == 0) {
                turbineLeftPanel.getDt().setBackground(Color.white);
                turbineLeftPanel.getDt().setForeground(Color.blue);
                turbineLeftPanel.getTt().setBackground(Color.white);
                turbineLeftPanel.getTt().setForeground(Color.blue);
            }
            switch (Turbo.mturbin) {
                case 0: {
                    V1 = Double.valueOf(turbineLeftPanel.getDt().getText());
                    v1 = V1.doubleValue();
                    V2 = Double.valueOf(turbineLeftPanel.getTt().getText());
                    v2 = V2.doubleValue();
                    Turbo.dturbin = v1 / Turbo.dconv;
                    Turbo.tturbin = v2 / Turbo.tconv;
                    break;
                }
                case 1:
                    Turbo.dturbin = 170.7;
                    Turbo.tturbin = 900.;
                    break;
                case 2:
                    Turbo.dturbin = 293.02;
                    Turbo.tturbin = 1500.;
                    break;
                case 3:
                    Turbo.dturbin = 476.56;
                    Turbo.tturbin = 2000.;
                    break;
                case 4:
                    Turbo.dturbin = 515.2;
                    Turbo.tturbin = 2500.;
                    break;
                case 5:
                    Turbo.dturbin = 515.2;
                    Turbo.tturbin = 3000.;
                    break;
                case 6:
                    Turbo.dturbin = 164.2;
                    Turbo.tturbin = 3000.;
                    break;
            }
            turbo.solve.comPute();
        }

        public void handleBar() {     // turbine
            int i1;
            double v1;
            float fl1;

            i1 = s1.getValue();

            if(turbo.lunits <= 1) {
                Turbo.vmn1 = Turbo.etmin;
                Turbo.vmx1 = Turbo.etmax;
            }
            if(turbo.lunits == 2) {
                Turbo.vmx1 = 100.0 - 100.0 * Turbo.et5ref;
                Turbo.vmn1 = Turbo.vmx1 - 20.0;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;

            fl1 = (float)v1;

            if(turbo.lunits <= 1) {
                Turbo.eta[5] = v1;
            }
            if(turbo.lunits == 2) {
                Turbo.eta[5] = Turbo.et5ref + v1 / 100.;
            }

            turbineLeftPanel.getF1().setText(String.valueOf(fl1));

            turbo.solve.comPute();
        }  // end handle
    }  // end rightPanel

    public class TurbineLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f3;
        private TextField dt;
        private TextField tt;
        Label l1, l5, lmat, lm2;

        TurbineLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            setF3(new TextField(String.valueOf((int)Turbo.nturb), 5));
            getF3().setBackground(Color.black);
            getF3().setForeground(Color.yellow);

            l1 = new Label("Efficiency", Label.CENTER);
            setF1(new TextField(String.valueOf((float)Turbo.eta[5]), 5));
            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDt(new TextField(String.valueOf((float)Turbo.dturbin), 5));
            getDt().setBackground(Color.black);
            getDt().setForeground(Color.yellow);
            setTt(new TextField(String.valueOf((float)Turbo.tturbin), 5));
            getTt().setBackground(Color.black);
            getTt().setForeground(Color.yellow);

            add(new Label("Stages ", Label.CENTER));
            add(getF3());
            add(l1);
            add(getF1());
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(lm2);
            add(new Label(" ", Label.CENTER));
            add(lmat);
            add(getTt());
            add(l5);
            add(getDt());
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleText();
            }
        }

        public void handleText() {
            Double V1, V4, V8;
            double v1, v4, v8;
            Integer I3;
            int i1, i3;
            float fl1;

            V1 = Double.valueOf(getF1().getText());
            v1 = V1.doubleValue();
            V4 = Double.valueOf(getDt().getText());
            v4 = V4.doubleValue();
            V8 = Double.valueOf(getTt().getText());
            v8 = V8.doubleValue();

            I3 = Integer.valueOf(getF3().getText());
            i3 = I3.intValue();
            // number of stages
            if(Turbo.ntflag == 1 && i3 >= 1) {
                Turbo.nturb = i3;
            }
            // materials
            if(Turbo.mturbin == 0) {
                if(v4 <= 1.0 * Turbo.dconv) {
                    v4 = 1.0 * Turbo.dconv;
                    getDt().setText(String.valueOf(turbo.filter0(v4 * Turbo.dconv)));
                }
                Turbo.dturbin = v4 / Turbo.dconv;
                if(v8 <= 500. * Turbo.tconv) {
                    v8 = 500. * Turbo.tconv;
                    getTt().setText(String.valueOf(turbo.filter0(v8 * Turbo.tconv)));
                }
                Turbo.tturbin = v8 / Turbo.tconv;
            }
            // turbine efficiency
            if(turbo.lunits <= 1) {
                Turbo.eta[5] = v1;
                Turbo.vmn1 = Turbo.etmin;
                Turbo.vmx1 = Turbo.etmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.eta[5] = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.eta[5] = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
            }
            if(turbo.lunits == 2) {
                // Turbine efficiency
                Turbo.vmx1 = 100.0 - 100.0 * Turbo.et5ref;
                Turbo.vmn1 = Turbo.vmx1 - 20.0;
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
                Turbo.eta[5] = Turbo.et5ref + v1 / 100.;
            }

            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);

            turbineRightPanel.s1.setValue(i1);

            turbo.solve.comPute();
        }  // end handle

        public TextField getF1() {
            return f1;
        }

        public void setF1(TextField f1) {
            this.f1 = f1;
        }

        public TextField getF3() {
            return f3;
        }

        public void setF3(TextField f3) {
            this.f3 = f3;
        }

        public TextField getDt() {
            return dt;
        }

        public void setDt(TextField dt) {
            this.dt = dt;
        }

        public TextField getTt() {
            return tt;
        }

        public void setTt(TextField tt) {
            this.tt = tt;
        }
    }  //  end  inletLeftPanel
}  // end TurbinePanel
 
