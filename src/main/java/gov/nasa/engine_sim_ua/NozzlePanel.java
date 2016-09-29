package gov.nasa.engine_sim_ua;

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
        Scrollbar s1, s2, s3;
        Label lmat;
        Choice arch, nmat;

        NozzleRightPanel(Turbo target) {

            int i1, i2, i3;

            turbo = target;
            setLayout(new GridLayout(7, 1, 10, 5));

            i1 = (int)(((Turbo.tt7d - Turbo.t7min) / (Turbo.t7max - Turbo.t7min)) * 1000.);
            i2 = (int)(((Turbo.eta[7] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.);
            i3 = (int)(((Turbo.a8rat - Turbo.a8min) / (Turbo.a8max - Turbo.a8min)) * 1000.);

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

        public boolean handleEvent(Event evt) {
            if(evt.id == Event.ACTION_EVENT) {
                this.handleMat(evt);
                return true;
            }
            if(evt.id == Event.SCROLL_ABSOLUTE) {
                this.handleBar(evt);
                return true;
            }
            if(evt.id == Event.SCROLL_LINE_DOWN) {
                this.handleBar(evt);
                return true;
            }
            if(evt.id == Event.SCROLL_LINE_UP) {
                this.handleBar(evt);
                return true;
            }
            if(evt.id == Event.SCROLL_PAGE_DOWN) {
                this.handleBar(evt);
                return true;
            }
            if(evt.id == Event.SCROLL_PAGE_UP) {
                this.handleBar(evt);
                return true;
            } else {
                return false;
            }
        }

        public void handleMat(Event evt) {
            Double V1, V2;
            double v1, v2;

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
            Turbo.mnozl = nmat.getSelectedIndex();
            if(Turbo.mnozl > 0) {
                nozzleLeftPanel.getTn().setBackground(Color.black);
                nozzleLeftPanel.getTn().setForeground(Color.yellow);
                nozzleLeftPanel.getDn().setBackground(Color.black);
                nozzleLeftPanel.getDn().setForeground(Color.yellow);
            }
            if(Turbo.mnozl == 0) {
                nozzleLeftPanel.getTn().setBackground(Color.white);
                nozzleLeftPanel.getTn().setForeground(Color.black);
                nozzleLeftPanel.getDn().setBackground(Color.white);
                nozzleLeftPanel.getDn().setForeground(Color.black);
            }
            switch (Turbo.mnozl) {
                case 0: {
                    V1 = Double.valueOf(nozzleLeftPanel.getDn().getText());
                    v1 = V1.doubleValue();
                    V2 = Double.valueOf(nozzleLeftPanel.getTn().getText());
                    v2 = V2.doubleValue();
                    Turbo.dnozl = v1 / Turbo.dconv;
                    Turbo.tnozl = v2 / Turbo.tconv;
                    break;
                }
                case 1:
                    Turbo.dnozl = 293.02;
                    Turbo.tnozl = 1500.;
                    break;
                case 2:
                    Turbo.dnozl = 476.56;
                    Turbo.tnozl = 2000.;
                    break;
                case 3:
                    Turbo.dnozl = 515.2;
                    Turbo.tnozl = 2500.;
                    break;
                case 4:
                    Turbo.dnozl = 164.2;
                    Turbo.tnozl = 3000.;
                    break;
                case 5:
                    Turbo.dnozl = 400.2;
                    Turbo.tnozl = 4100.;
                    break;
            }
            turbo.solve.comPute();
        }

        public void handleBar(Event evt) {     // nozzle design
            int i1, i2, i3;
            double v1, v2, v3;
            float fl1, fl2, fl3;

            i1 = s1.getValue();
            i2 = s2.getValue();
            i3 = s3.getValue();

            if(turbo.lunits <= 1) {
                Turbo.vmn1 = Turbo.t7min;
                Turbo.vmx1 = Turbo.t7max;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.a8min;
                Turbo.vmx3 = Turbo.a8max;
            }
            if(turbo.lunits == 2) {
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0;
                Turbo.vmn3 = -10.0;
                Turbo.vmx3 = 10.0;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;

            fl1 = (float)v1;
            fl2 = (float)v2;
            fl3 = turbo.filter3(v3);

            // nozzle design
            if(turbo.lunits <= 1) {
                Turbo.tt7d = v1;
                Turbo.eta[7] = v2;
                Turbo.a8rat = v3;
            }
            if(turbo.lunits == 2) {
                Turbo.tt7d = v1 * Turbo.t7ref / 100. + Turbo.t7ref;
                Turbo.eta[7] = Turbo.et7ref + v2 / 100.;
                Turbo.a8rat = v3 * Turbo.a8ref / 100. + Turbo.a8ref;
            }
            Turbo.tt7 = Turbo.tt7d / Turbo.tconv;

            nozzleLeftPanel.getF1().setText(String.valueOf(fl1));
            nozzleLeftPanel.getF2().setText(String.valueOf(fl2));
            nozzleLeftPanel.getF3().setText(String.valueOf(fl3));

            turbo.solve.comPute();
        }  // end handle
    }  // end rightPanel

    public class NozzleLeftPanel extends Panel {

        Turbo turbo;
        private TextField f1;
        private TextField f2;
        private TextField f3;
        private TextField dn;
        private TextField tn;
        Label l1, l2, l3, l5, lmat, lm2;

        NozzleLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(7, 2, 5, 5));

            l1 = new Label("Tmax -R", Label.CENTER);
            setF1(new TextField(String.valueOf((float)Turbo.tt7d), 5));

            l2 = new Label("Efficiency", Label.CENTER);
            setF2(new TextField(String.valueOf((float)Turbo.eta[7]), 5));

            setF3(new TextField(String.valueOf((float)Turbo.a8rat), 5));
            getF3().setBackground(Color.black);
            getF3().setForeground(Color.yellow);

            lmat = new Label("T lim-R", Label.CENTER);
            lmat.setForeground(Color.blue);
            lm2 = new Label("Materials:", Label.CENTER);
            lm2.setForeground(Color.blue);
            l5 = new Label("Density", Label.CENTER);
            l5.setForeground(Color.blue);

            setDn(new TextField(String.valueOf((float)Turbo.dnozl), 5));
            getDn().setBackground(Color.black);
            getDn().setForeground(Color.yellow);
            setTn(new TextField(String.valueOf((float)Turbo.tnozl), 5));
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

        public boolean handleEvent(Event evt) {
            if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt);
                return true;
            } else {
                return false;
            }
        }

        public void handleText(Event evt) {
            Double V1, V2, V3, V7, V8;
            double v1, v2, v3, v7, v8;
            int i1, i2, i3;
            float fl1;

            V1 = Double.valueOf(getF1().getText());
            v1 = V1.doubleValue();
            V2 = Double.valueOf(getF2().getText());
            v2 = V2.doubleValue();
            V3 = Double.valueOf(getF3().getText());
            v3 = V3.doubleValue();
            V7 = Double.valueOf(getDn().getText());
            v7 = V7.doubleValue();
            V8 = Double.valueOf(getTn().getText());
            v8 = V8.doubleValue();

            // Materials
            if(Turbo.mnozl == 0) {
                if(v7 <= 1.0 * Turbo.dconv) {
                    v7 = 1.0 * Turbo.dconv;
                    getDn().setText(String.valueOf(turbo.filter0(v7 * Turbo.dconv)));
                }
                Turbo.dnozl = v7 / Turbo.dconv;
                if(v8 <= 500. * Turbo.tconv) {
                    v8 = 500. * Turbo.tconv;
                    getTn().setText(String.valueOf(turbo.filter0(v8 * Turbo.tconv)));
                }
                Turbo.tnozl = v8 / Turbo.tconv;
            }

            if(turbo.lunits <= 1) {
                // Max afterburner temp
                Turbo.tt7d = v1;
                Turbo.vmn1 = Turbo.t7min;
                Turbo.vmx1 = Turbo.t7max;
                if(v1 < Turbo.vmn1) {
                    Turbo.tt7d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.tt7d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    getF1().setText(String.valueOf(fl1));
                }
                Turbo.tt7 = Turbo.tt7d / Turbo.tconv;
                // nozzle  efficiency
                Turbo.eta[7] = v2;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[7] = v2 = Turbo.vmn2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[7] = v2 = Turbo.vmx2;
                    fl1 = (float)v2;
                    getF2().setText(String.valueOf(fl1));
                }
                //  nozzle area ratio
                Turbo.a8rat = v3;
                Turbo.vmn3 = Turbo.a8min;
                Turbo.vmx3 = Turbo.a8max;
                if(v3 < Turbo.vmn3) {
                    Turbo.a8rat = v3 = Turbo.vmn3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
                if(v3 > Turbo.vmx3) {
                    Turbo.a8rat = v3 = Turbo.vmx3;
                    fl1 = (float)v3;
                    getF3().setText(String.valueOf(fl1));
                }
            }
            if(turbo.lunits == 2) {
                // Max afterburner temp
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
                Turbo.tt7d = v1 * Turbo.t7ref / 100. + Turbo.t7ref;
                Turbo.tt7 = Turbo.tt7d / Turbo.tconv;
                // nozzl e  efficiency
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
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
                Turbo.eta[7] = Turbo.et7ref + v2 / 100.;
                //  nozzle area ratio
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
                Turbo.a8rat = v3 * Turbo.a8ref / 100. + Turbo.a8ref;
            }

            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

            nozzleRightPanel.s1.setValue(i1);
            nozzleRightPanel.s2.setValue(i2);
            nozzleRightPanel.s3.setValue(i3);

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
 
