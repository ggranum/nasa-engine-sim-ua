package gov.nasa.engine_sim_ua;

import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class LimitsPanel extends Panel {

    private Turbo turbo;
    TextField f1, f2, f3, f4, f5, f6, f7, f8;
    TextField f9, f10, f11, f12;
    Label l1, l2, l3, l4, l5, l6, l7, l8;
    Label l9, l10, l11, l12;
    Button submit;

    LimitsPanel(Turbo turbo) {
        this.turbo = turbo;
        setLayout(new GridLayout(6, 4, 10, 10));

        l1 = new Label("Speed-max", Label.CENTER);
        f1 = new TextField(String.valueOf((float)Turbo.u0max), 5);
        l2 = new Label("Alt-max", Label.CENTER);
        f2 = new TextField(String.valueOf((float)Turbo.altmax), 5);
        l3 = new Label("A2-min", Label.CENTER);
        f3 = new TextField(String.valueOf((float)Turbo.a2min), 3);
        l4 = new Label("A2-max", Label.CENTER);
        f4 = new TextField(String.valueOf((float)Turbo.a2max), 5);
        l5 = new Label("CPR-max", Label.CENTER);
        f5 = new TextField(String.valueOf((float)Turbo.cprmax), 5);
        l6 = new Label("T4-max", Label.CENTER);
        f6 = new TextField(String.valueOf((float)Turbo.t4max), 5);
        l7 = new Label("T7-max", Label.CENTER);
        f7 = new TextField(String.valueOf((float)Turbo.t7max), 5);
        l9 = new Label("FPR-max", Label.CENTER);
        f9 = new TextField(String.valueOf((float)Turbo.fprmax), 5);
        l10 = new Label("BPR-max", Label.CENTER);
        f10 = new TextField(String.valueOf((float)Turbo.bypmax), 5);
        l11 = new Label("Pt4/Pt3-max", Label.CENTER);
        f11 = new TextField(String.valueOf((float)Turbo.pt4max), 5);

        submit = new Button("Submit");
        submit.setBackground(Color.blue);
        submit.setForeground(Color.white);

        add(l1);
        add(f1);
        add(l2);
        add(f2);

        add(l3);
        add(f3);
        add(l4);
        add(f4);

        add(l5);
        add(f5);
        add(l11);
        add(f11);

        add(l6);
        add(f6);
        add(l7);
        add(f7);

        add(l9);
        add(f9);
        add(l10);
        add(f10);

        add(new Label("  ", Label.RIGHT));
        add(new Label(" ", Label.RIGHT));
        add(new Label(" Push --> ", Label.RIGHT));
        add(submit);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Button) {
            this.handleText(evt);
            return true;
        } else {
            return false;
        }
    }

    public void handleText(Event evt) {
        Double V1, V2, V3, V4;
        double v1, v2, v3, v4;
        int i1, i2, i3;
        float fl1;

        V1 = Double.valueOf(f1.getText());
        v1 = V1.doubleValue();
        V2 = Double.valueOf(f2.getText());
        v2 = V2.doubleValue();
        V3 = Double.valueOf(f3.getText());
        v3 = V3.doubleValue();
        V4 = Double.valueOf(f4.getText());
        v4 = V4.doubleValue();

        Turbo.u0max = v1;
        Turbo.altmax = v2;
        Turbo.a2min = v3;
        Turbo.a2max = v4;
        if(turbo.entype <= 2) {
            Turbo.u0mt = Turbo.u0max;
            Turbo.altmt = Turbo.altmax;
        }
        if(turbo.entype == 3) {
            Turbo.u0mr = Turbo.u0max;
            Turbo.altmr = Turbo.altmax;
        }

        // look for exceeding limits

        if(Turbo.u0d > Turbo.u0max) {
            if(Turbo.u0max < 0) {
                Turbo.u0max = Turbo.u0d + .1;
            }
            Turbo.u0d = Turbo.u0max;
        }
        if(Turbo.altd > Turbo.altmax) {
            if(Turbo.altmax < 0) {
                Turbo.altmax = Turbo.altd + .1;
            }
            Turbo.altd = Turbo.altmax;
        }
        if(Turbo.a2max <= Turbo.a2min) {
            Turbo.a2max = Turbo.a2min + .1;
        }
        if(Turbo.a2d > Turbo.a2max) {
            Turbo.a2d = Turbo.a2max;
            Turbo.a2 = Turbo.a2d / Turbo.aconv;
            if(turbo.entype != 2) {
                Turbo.acore = Turbo.a2;
            }
            if(turbo.entype == 2) {
                Turbo.afan = Turbo.a2;
                Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat);
            }
        }
        if(Turbo.a2d < Turbo.a2min) {
            Turbo.a2d = Turbo.a2min;
            Turbo.a2 = Turbo.a2d / Turbo.aconv;
            if(turbo.entype != 2) {
                Turbo.acore = Turbo.a2;
            }
            if(turbo.entype == 2) {
                Turbo.afan = Turbo.a2;
                Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat);
            }
        }

        V1 = Double.valueOf(f5.getText());
        v1 = V1.doubleValue();
        V2 = Double.valueOf(f6.getText());
        v2 = V2.doubleValue();
        V3 = Double.valueOf(f7.getText());
        v3 = V3.doubleValue();

        Turbo.cprmax = v1;
        Turbo.t4max = v2;
        Turbo.t7max = v3;

        // look for exceeding limits

        if(Turbo.cprmax <= Turbo.cprmin) {
            Turbo.cprmax = Turbo.cprmin + .1;
        }
        if(Turbo.p3p2d > Turbo.cprmax) {
            Turbo.p3p2d = Turbo.cprmax;
        }
        if(Turbo.t4max <= Turbo.t4min) {
            Turbo.t4max = Turbo.t4min + .1;
        }
        if(Turbo.tt4d > Turbo.t4max) {
            Turbo.tt4d = Turbo.t4max;
            Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
        }
        if(Turbo.t7max <= Turbo.t7min) {
            Turbo.t7max = Turbo.t7min + .1;
        }
        if(Turbo.tt7d > Turbo.t7max) {
            Turbo.tt7d = Turbo.t7max;
            Turbo.tt7 = Turbo.tt7d / Turbo.tconv;
        }

        V1 = Double.valueOf(f9.getText());
        v1 = V1.doubleValue();
        V2 = Double.valueOf(f10.getText());
        v2 = V2.doubleValue();
        V3 = Double.valueOf(f11.getText());
        v3 = V3.doubleValue();

        Turbo.fprmax = v1;
        Turbo.bypmax = v2;
        Turbo.pt4max = v3;

        if(Turbo.fprmax <= Turbo.fprmin) {
            Turbo.fprmax = Turbo.fprmin + .1;
        }
        if(Turbo.p3fp2d > Turbo.fprmax) {
            Turbo.p3fp2d = Turbo.fprmax;
        }
        if(Turbo.bypmax <= Turbo.bypmin) {
            Turbo.bypmax = Turbo.bypmin + .1;
        }
        if(Turbo.byprat > Turbo.bypmax) {
            Turbo.byprat = Turbo.bypmax;
            Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat);
        }
        if(Turbo.pt4max <= Turbo.etmin) {
            Turbo.pt4max = Turbo.etmin + .1;
        }
        if(Turbo.prat[4] > Turbo.pt4max) {
            Turbo.prat[4] = Turbo.pt4max;
        }

        turbo.varflag = 0;
        turbo.layin.show(turbo.inputPanel, "first");
        turbo.solve.comPute();
        turbo.flightConditionsPanel.setPanl();
    }  // end handle
}  // end inlimit
 
