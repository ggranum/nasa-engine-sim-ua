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
public class SizePanel extends Panel {

    SizeRightPanel sizeRightPanel;
    SizeLeftPanel sizeLeftPanel;

    SizePanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        sizeLeftPanel = new SizeLeftPanel(turbo);
        sizeRightPanel = new SizeRightPanel(turbo);

        add(sizeLeftPanel);
        add(sizeRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class SizeRightPanel extends Panel {

        Turbo turbo;
        private Scrollbar s1;
        private Choice chmat;
        Choice sizch;

        SizeRightPanel(Turbo target) {

            int i1;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            i1 = (int)(((Turbo.a2d - Turbo.a2min) / (Turbo.a2max - Turbo.a2min)) * 1000.);
            setS1(new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000));

            setChmat(new Choice());
            getChmat().addItem("Computed Weight");
            getChmat().addItem("Input Weight ");
            getChmat().select(0);

            sizch = new Choice();
            sizch.addItem("Input Frontal Area");
            sizch.addItem("Input Diameter ");
            sizch.select(0);

            add(sizch);
            add(getS1());
            add(new Label(" ", Label.CENTER));
            add(getChmat());
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleBar();
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

        public void handleBar() {     // engine sizePanel
            int i1;
            Double V2;
            Double V3;
            double v2;
            double v3;
            float fl1;
            float fl2;
            float fl3;

            turbo.siztype = sizch.getSelectedIndex();

            if(turbo.siztype == 0) {
                // area input
                i1 = getS1().getValue();
                Turbo.vmn1 = Turbo.a2min;
                Turbo.vmx1 = Turbo.a2max;

                Turbo.a2d = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
                Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);

                sizeLeftPanel.f1.setBackground(Color.white);
                sizeLeftPanel.f1.setForeground(Color.black);
                sizeLeftPanel.f3.setBackground(Color.black);
                sizeLeftPanel.f3.setForeground(Color.yellow);
            }

            if(turbo.siztype == 1) {
                // diameter input
                V3 = Double.valueOf(sizeLeftPanel.f3.getText());
                Turbo.diameng = v3 = V3.doubleValue();

                Turbo.a2d = 3.14159 * Turbo.diameng * Turbo.diameng / 4.0;

                sizeLeftPanel.f1.setBackground(Color.black);
                sizeLeftPanel.f1.setForeground(Color.yellow);
                sizeLeftPanel.f3.setBackground(Color.white);
                sizeLeftPanel.f3.setForeground(Color.black);
            }

            Turbo.a2 = Turbo.a2d / Turbo.aconv;
            if(turbo.entype == 2) {
                Turbo.afan = Turbo.a2;
                Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat);
            } else {
                Turbo.acore = Turbo.a2;
            }

            // compute or input weight
            turbo.wtflag = getChmat().getSelectedIndex();
            if(turbo.wtflag == 1) {
                sizeLeftPanel.f2.setForeground(Color.black);
                sizeLeftPanel.f2.setBackground(Color.white);
                V2 = Double.valueOf(sizeLeftPanel.f2.getText());
                v2 = V2.doubleValue();
                Turbo.weight = v2 / Turbo.fconv;
                if(Turbo.weight < 10.0) {
                    Turbo.weight = v2 = 10.0;
                    fl2 = (float)v2;
                    sizeLeftPanel.f2.setText(String.valueOf(fl2));
                }
            }
            if(turbo.wtflag == 0) {
                sizeLeftPanel.f2.setForeground(Color.yellow);
                sizeLeftPanel.f2.setBackground(Color.black);
            }

            fl1 = turbo.filter3(Turbo.a2d);
            fl3 = turbo.filter3(Turbo.diameng);

            sizeLeftPanel.f1.setText(String.valueOf(fl1));
            sizeLeftPanel.f3.setText(String.valueOf(fl3));

            turbo.solve.comPute();
        }  // end handle

        public Scrollbar getS1() {
            return s1;
        }

        public void setS1(Scrollbar s1) {
            this.s1 = s1;
        }

        public Choice getChmat() {
            return chmat;
        }

        public void setChmat(Choice chmat) {
            this.chmat = chmat;
        }
    }  // end rightPanel

    public class SizeLeftPanel extends Panel {

        Turbo turbo;
        TextField f1;
        TextField f2;
        TextField f3;
        Label l1;
        Label l2;
        Label l3;

        SizeLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            l1 = new Label("Area-sq ft", Label.CENTER);
            f1 = new TextField(String.valueOf((float)Turbo.a2d), 5);
            f1.setBackground(Color.white);
            f1.setForeground(Color.black);

            l2 = new Label("Weight-lbs", Label.CENTER);
            f2 = new TextField(String.valueOf((float)Turbo.weight), 5);
            f2.setBackground(Color.black);
            f2.setForeground(Color.yellow);

            l3 = new Label("Diameter-ft", Label.CENTER);
            f3 = new TextField(String.valueOf((float)Turbo.diameng), 5);
            f3.setBackground(Color.black);
            f3.setForeground(Color.yellow);

            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(l1);
            add(f1);
            add(l3);
            add(f3);
            add(l2);
            add(f2);
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
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
            double v1;
            double v2;
            double v3;
            int i1;
            float fl1;
            float fl2;
            float fl3;

            V1 = Double.valueOf(f1.getText());
            v1 = V1.doubleValue();
            V2 = Double.valueOf(f2.getText());
            v2 = V2.doubleValue();
            V3 = Double.valueOf(f3.getText());
            v3 = V3.doubleValue();
            // area input
            if(turbo.siztype == 0) {
                Turbo.a2d = v1;
                Turbo.vmn1 = Turbo.a2min;
                Turbo.vmx1 = Turbo.a2max;
                if(v1 < Turbo.vmn1) {
                    Turbo.a2d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    f1.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.a2d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    f1.setText(String.valueOf(fl1));
                }
                Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
                fl3 = turbo.filter3(Turbo.diameng);
                f3.setText(String.valueOf(fl3));
            }
            // diameter input
            if(turbo.siztype == 1) {
                Turbo.diameng = v3;
                Turbo.vmn1 = Turbo.diamin;
                Turbo.vmx1 = Turbo.diamax;
                if(v3 < Turbo.vmn1) {
                    Turbo.diameng = v3 = Turbo.vmn1;
                    fl3 = (float)v3;
                    f3.setText(String.valueOf(fl3));
                }
                if(v3 > Turbo.vmx1) {
                    Turbo.diameng = v3 = Turbo.vmx1;
                    fl3 = (float)v3;
                    f3.setText(String.valueOf(fl3));
                }
                Turbo.a2d = 3.14159 * Turbo.diameng * Turbo.diameng / 4.0;
                fl1 = turbo.filter3(Turbo.a2d);
                f1.setText(String.valueOf(fl1));
            }

            Turbo.a2 = Turbo.a2d / Turbo.aconv;
            if(turbo.entype == 2) {
                Turbo.afan = Turbo.a2;
                Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat);
            } else {
                Turbo.acore = Turbo.a2;
            }

            Turbo.weight = v2 / Turbo.fconv;
            if(Turbo.weight < 10.0) {
                Turbo.weight = v2 = 10.0;
                fl2 = (float)v2;
                f2.setText(String.valueOf(fl2));
            }

            i1 = (int)(((Turbo.a2d - Turbo.a2min) / (Turbo.a2max - Turbo.a2min)) * 1000.);

            sizeRightPanel.getS1().setValue(i1);

            turbo.solve.comPute();
        }  // end handle
    }  //  end  inletLeftPanel
}  // end SizePanel input
 
