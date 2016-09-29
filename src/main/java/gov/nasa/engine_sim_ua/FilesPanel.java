package gov.nasa.engine_sim_ua;

import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesPanel extends Panel {  // save file

    private Turbo turbo;
    TextField nsavin;
    TextField nsavout;
    Button savread;
    Button savwrit;
    Button cancel;

    FilesPanel(Turbo turbo) {
        this.turbo = turbo;

        setLayout(new GridLayout(6, 2, 10, 10));

        nsavin = new TextField();
        nsavin.setBackground(Color.white);
        nsavin.setForeground(Color.black);

        nsavout = new TextField();
        nsavout.setBackground(Color.white);
        nsavout.setForeground(Color.black);

        savread = new Button("Retrieve Data");
        savread.setBackground(Color.blue);
        savread.setForeground(Color.white);

        savwrit = new Button("Save Data");
        savwrit.setBackground(Color.red);
        savwrit.setForeground(Color.white);

        cancel = new Button("Cancel");
        cancel.setBackground(Color.yellow);
        cancel.setForeground(Color.black);

        add(new Label("Enter File Name -  ", Label.RIGHT));
        add(new Label("Then Push Button", Label.LEFT));

        add(new Label("Save Data to File:", Label.RIGHT));
        add(nsavout);

        add(new Label(" ", Label.CENTER));
        add(savwrit);

        add(new Label("Get Data from File:", Label.RIGHT));
        add(nsavin);

        add(new Label(" ", Label.CENTER));
        add(savread);

        add(new Label(" ", Label.CENTER));
        add(cancel);
    }

    public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Button) {
            this.handleRefs(arg);
            return true;
        } else {
            return false;
        }
    }

    public void handleRefs(Object arg) {
        String filnam;
        String label = (String)arg;

        if(label.equals("Retrieve Data")) {  // Read inputPanel saved case
            filnam = nsavin.getText();

            try {
                turbo.sfili = new FileInputStream(filnam);
                turbo.savin = new DataInputStream(turbo.sfili);

                turbo.inflag = 0;
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcMode.select(0);
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.lunits = 0;
                turbo.flightConditionsPanel.setUnits();
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcUnits.select(turbo.lunits);

                turbo.entype = turbo.savin.readInt();
                turbo.abflag = turbo.savin.readInt();
                turbo.fueltype = turbo.savin.readInt();
                turbo.fhvd = turbo.fhv = turbo.savin.readDouble();
                turbo.tt[4] = turbo.tt4 = turbo.tt4d = turbo.savin.readDouble();
                turbo.tt[7] = turbo.tt7 = turbo.tt7d = turbo.savin.readDouble();
                turbo.prat[3] = turbo.p3p2d = turbo.savin.readDouble();
                turbo.prat[13] = turbo.p3fp2d = turbo.savin.readDouble();
                turbo.byprat = turbo.savin.readDouble();
                turbo.acore = turbo.savin.readDouble();
                turbo.afan = turbo.acore * (1.0 + turbo.byprat);
                turbo.a2d = turbo.a2 = turbo.savin.readDouble();
                turbo.a4 = turbo.savin.readDouble();
                turbo.a4p = turbo.savin.readDouble();
                turbo.ac = .9 * turbo.a2;
                turbo.gama = turbo.savin.readDouble();
                turbo.gamopt = turbo.savin.readInt();
                turbo.pt2flag = turbo.savin.readInt();
                turbo.eta[2] = turbo.savin.readDouble();
                turbo.prat[2] = turbo.savin.readDouble();
                turbo.prat[4] = turbo.savin.readDouble();
                turbo.eta[3] = turbo.savin.readDouble();
                turbo.eta[4] = turbo.savin.readDouble();
                turbo.eta[5] = turbo.savin.readDouble();
                turbo.eta[7] = turbo.savin.readDouble();
                turbo.eta[13] = turbo.savin.readDouble();
                turbo.a8d = turbo.savin.readDouble();
                turbo.a8max = turbo.savin.readDouble();
                turbo.a8rat = turbo.savin.readDouble();

                turbo.u0max = turbo.savin.readDouble();
                turbo.u0d = turbo.savin.readDouble();
                turbo.altmax = turbo.savin.readDouble();
                turbo.altd = turbo.savin.readDouble();
                turbo.arsched = turbo.savin.readInt();

                turbo.wtflag = turbo.savin.readInt();
                turbo.weight = turbo.savin.readDouble();
                turbo.minlt = turbo.savin.readInt();
                turbo.dinlt = turbo.savin.readDouble();
                turbo.tinlt = turbo.savin.readDouble();
                turbo.mfan = turbo.savin.readInt();
                turbo.dfan = turbo.savin.readDouble();
                turbo.tfan = turbo.savin.readDouble();
                turbo.mcomp = turbo.savin.readInt();
                turbo.dcomp = turbo.savin.readDouble();
                turbo.tcomp = turbo.savin.readDouble();
                turbo.mburner = turbo.savin.readInt();
                turbo.dburner = turbo.savin.readDouble();
                turbo.tburner = turbo.savin.readDouble();
                turbo.mturbin = turbo.savin.readInt();
                turbo.dturbin = turbo.savin.readDouble();
                turbo.tturbin = turbo.savin.readDouble();
                turbo.mnozl = turbo.savin.readInt();
                turbo.dnozl = turbo.savin.readDouble();
                turbo.tnozl = turbo.savin.readDouble();
                turbo.mnozr = turbo.savin.readInt();
                turbo.dnozr = turbo.savin.readDouble();
                turbo.tnozr = turbo.savin.readDouble();
                turbo.ncflag = turbo.savin.readInt();
                turbo.ntflag = turbo.savin.readInt();

                if(turbo.entype == 3) {
                    turbo.athsched = turbo.savin.readInt();
                    turbo.aexsched = turbo.savin.readInt();
                    turbo.arthd = turbo.savin.readDouble();
                    turbo.arexitd = turbo.savin.readDouble();
                }

                turbo.flightConditionsPanel.setPanl();
                turbo.solve.compute();
            } catch (IOException n) {
            }
        }

        if(label.equals("Save Data")) {  // Restart Write
            filnam = nsavout.getText();

            try {
                turbo.sfilo = new FileOutputStream(filnam);
                turbo.savout = new DataOutputStream(turbo.sfilo);

                turbo.savout.writeInt(turbo.entype);
                turbo.savout.writeInt(turbo.abflag);
                turbo.savout.writeInt(turbo.fueltype);
                turbo.savout.writeDouble(turbo.fhv / turbo.flconv);
                turbo.savout.writeDouble(turbo.tt4d / turbo.tconv);
                turbo.savout.writeDouble(turbo.tt7d / turbo.tconv);
                turbo.savout.writeDouble(turbo.p3p2d);
                turbo.savout.writeDouble(turbo.p3fp2d);
                turbo.savout.writeDouble(turbo.byprat);
                turbo.savout.writeDouble(turbo.acore);
                turbo.savout.writeDouble(turbo.a2d / turbo.aconv);
                turbo.savout.writeDouble(turbo.a4);
                turbo.savout.writeDouble(turbo.a4p);
                turbo.savout.writeDouble(turbo.gama);
                turbo.savout.writeInt(turbo.gamopt);
                turbo.savout.writeInt(turbo.pt2flag);
                turbo.savout.writeDouble(turbo.eta[2]);
                turbo.savout.writeDouble(turbo.prat[2]);
                turbo.savout.writeDouble(turbo.prat[4]);
                turbo.savout.writeDouble(turbo.eta[3]);
                turbo.savout.writeDouble(turbo.eta[4]);
                turbo.savout.writeDouble(turbo.eta[5]);
                turbo.savout.writeDouble(turbo.eta[7]);
                turbo.savout.writeDouble(turbo.eta[13]);
                turbo.savout.writeDouble(turbo.a8d / turbo.aconv);
                turbo.savout.writeDouble(turbo.a8max / turbo.aconv);
                turbo.savout.writeDouble(turbo.a8rat);

                turbo.savout.writeDouble(turbo.u0max / turbo.lconv2);
                turbo.savout.writeDouble(turbo.u0d / turbo.lconv2);
                turbo.savout.writeDouble(turbo.altmax / turbo.lconv1);
                turbo.savout.writeDouble(turbo.altd / turbo.lconv1);
                turbo.savout.writeInt(turbo.arsched);

                turbo.savout.writeInt(turbo.wtflag);
                turbo.savout.writeDouble(turbo.weight);
                turbo.savout.writeInt(turbo.minlt);
                turbo.savout.writeDouble(turbo.dinlt);
                turbo.savout.writeDouble(turbo.tinlt);
                turbo.savout.writeInt(turbo.mfan);
                turbo.savout.writeDouble(turbo.dfan);
                turbo.savout.writeDouble(turbo.tfan);
                turbo.savout.writeInt(turbo.mcomp);
                turbo.savout.writeDouble(turbo.dcomp);
                turbo.savout.writeDouble(turbo.tcomp);
                turbo.savout.writeInt(turbo.mburner);
                turbo.savout.writeDouble(turbo.dburner);
                turbo.savout.writeDouble(turbo.tburner);
                turbo.savout.writeInt(turbo.mturbin);
                turbo.savout.writeDouble(turbo.dturbin);
                turbo.savout.writeDouble(turbo.tturbin);
                turbo.savout.writeInt(turbo.mnozl);
                turbo.savout.writeDouble(turbo.dnozl);
                turbo.savout.writeDouble(turbo.tnozl);
                turbo.savout.writeInt(turbo.mnozr);
                turbo.savout.writeDouble(turbo.dnozr);
                turbo.savout.writeDouble(turbo.tnozr);
                turbo.savout.writeInt(turbo.ncflag);
                turbo.savout.writeInt(turbo.ntflag);

                if(turbo.entype == 3) {
                    turbo.savout.writeInt(turbo.athsched);
                    turbo.savout.writeInt(turbo.aexsched);
                    turbo.savout.writeDouble(turbo.arthd);
                    turbo.savout.writeDouble(turbo.arexitd);
                }

                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
            } catch (IOException n) {
            }
        }
        if(label.equals("Cancel")) {  // Forget it
            turbo.varflag = 0;
            turbo.layin.show(turbo.inputPanel, "first");
        }
    }  // end handler
} // end FilesPanel
 
