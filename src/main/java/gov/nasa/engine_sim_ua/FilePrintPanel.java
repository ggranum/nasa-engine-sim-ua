package gov.nasa.engine_sim_ua;

import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class FilePrintPanel extends Panel {

    private Turbo turbo;
TextField namprnt,namlab ;
Button pbopen,pball,pbfs,pbeng,pbth,pbprat,pbpres,pbvol,
       pbtrat,pbttot,pbentr,pbgam,pbeta,pbarea ;

FilePrintPanel(Turbo turbo) {
    this.turbo = turbo;

  setLayout(new GridLayout(7, 3, 5, 5)) ;

  namprnt = new TextField() ;
  namprnt.setBackground(Color.white) ;
  namprnt.setForeground(Color.black) ;

  namlab = new TextField() ;
  namlab.setBackground(Color.white) ;
  namlab.setForeground(Color.black) ;

  pbopen = new Button("Open File") ;
  pbopen.setBackground(Color.red) ;
  pbopen.setForeground(Color.white) ;

  pball = new Button("All") ;
  pball.setBackground(Color.white) ;
  pball.setForeground(Color.blue) ;

  pbfs = new Button("Flight") ;
  pbfs.setBackground(Color.blue) ;
  pbfs.setForeground(Color.white) ;

  pbeng = new Button("Engine") ;
  pbeng.setBackground(Color.blue) ;
  pbeng.setForeground(Color.white) ;

  pbth = new Button("Thrust") ;
  pbth.setBackground(Color.blue) ;
  pbth.setForeground(Color.white) ;

  pbprat = new Button("Pt Ratio") ;
  pbprat.setBackground(Color.white) ;
  pbprat.setForeground(Color.blue) ;

  pbpres = new Button("Total Pres") ;
  pbpres.setBackground(Color.white) ;
  pbpres.setForeground(Color.blue) ;

  pbvol = new Button("Spec Vol") ;
  pbvol.setBackground(Color.white) ;
  pbvol.setForeground(Color.blue) ;

  pbtrat = new Button("Tt Ratio") ;
  pbtrat.setBackground(Color.white) ;
  pbtrat.setForeground(Color.blue) ;

  pbttot = new Button("Total Temp") ;
  pbttot.setBackground(Color.white) ;
  pbttot.setForeground(Color.blue) ;

  pbentr = new Button("Entropy") ;
  pbentr.setBackground(Color.white) ;
  pbentr.setForeground(Color.blue) ;

  pbgam = new Button("Gamma") ;
  pbgam.setBackground(Color.white) ;
  pbgam.setForeground(Color.blue) ;

  pbeta = new Button("Efficiency") ;
  pbeta.setBackground(Color.white) ;
  pbeta.setForeground(Color.blue) ;

  pbarea = new Button("Area") ;
  pbarea.setBackground(Color.white) ;
  pbarea.setForeground(Color.blue) ;

  add(new Label("File Name: ", Label.RIGHT)) ;
  add(namprnt) ;
  add(new Label(" ", Label.LEFT)) ;

  add(new Label("Label: ", Label.RIGHT)) ;
  add(namlab) ;
  add(new Label("Blue = Print", Label.CENTER)) ;

  add(pbfs) ;
  add(pbeng) ;
  add(pbth) ;

  add(pbprat) ;
  add(pbpres) ;
  add(pbvol) ;

  add(pbtrat) ;
  add(pbttot) ;
  add(pbentr) ;

  add(pbgam) ;
  add(pbeta) ;
  add(pbarea) ;

  add(pball) ;
  add(new Label("Push ->", Label.RIGHT)) ;
  add(pbopen) ;
}

public boolean action(Event evt, Object arg) {
  if(evt.target instanceof Button) {
    this.handleRefs(evt,arg) ;
    return true ;
  }
  else {
      return false;
  }
}

public void handleRefs(Event evt, Object arg) {
  String filnam, fillab ;
  String label = (String)arg ;

  if(label.equals("All")) {
     if (turbo.pall == 1) {
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pall = 1;turbo.pfs = 1;turbo.peng = 1;turbo.pth = 1;turbo.ptrat = 1;turbo.ppres = 1;
         turbo.pvol = 1;turbo.ptrat = 1;turbo.pttot = 1;turbo.pentr = 1 ;turbo.pgam = 1;
         turbo.peta = 1 ;turbo.parea = 1;
        pball.setBackground(Color.blue) ;
        pball.setForeground(Color.white) ;
        pbfs.setBackground(Color.blue) ;
        pbfs.setForeground(Color.white) ;
        pbeng.setBackground(Color.blue) ;
        pbeng.setForeground(Color.white) ;
        pbth.setBackground(Color.blue) ;
        pbth.setForeground(Color.white) ;
        pbprat.setBackground(Color.blue) ;
        pbprat.setForeground(Color.white) ;
        pbpres.setBackground(Color.blue) ;
        pbpres.setForeground(Color.white) ;
        pbvol.setBackground(Color.blue) ;
        pbvol.setForeground(Color.white) ;
        pbtrat.setBackground(Color.blue) ;
        pbtrat.setForeground(Color.white) ;
        pbttot.setBackground(Color.blue) ;
        pbttot.setForeground(Color.white) ;
        pbentr.setBackground(Color.blue) ;
        pbentr.setForeground(Color.white) ;
        pbgam.setBackground(Color.blue) ;
        pbgam.setForeground(Color.white) ;
        pbeta.setBackground(Color.blue) ;
        pbeta.setForeground(Color.white) ;
        pbarea.setBackground(Color.blue) ;
        pbarea.setForeground(Color.white) ;
     }
  }
  if(label.equals("Flight")) {
     if (turbo.pfs == 1) {
         turbo.pfs = 0 ;
        pbfs.setBackground(Color.white) ;
        pbfs.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pfs = 1 ;
        pbfs.setBackground(Color.blue) ;
        pbfs.setForeground(Color.white) ;
     }
  }
  if(label.equals("Engine")) {
     if (turbo.peng == 1) {
         turbo.peng = 0 ;
        pbeng.setBackground(Color.white) ;
        pbeng.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.peng = 1 ;
        pbeng.setBackground(Color.blue) ;
        pbeng.setForeground(Color.white) ;
     }
  }
  if(label.equals("Thrust")) {
     if (turbo.pth == 1) {
         turbo.pth = 0 ;
        pbth.setBackground(Color.white) ;
        pbth.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pth = 1 ;
        pbth.setBackground(Color.blue) ;
        pbth.setForeground(Color.white) ;
     }
  }
  if(label.equals("Pt Ratio")) {
     if (turbo.pprat == 1) {
         turbo.pprat = 0 ;
        pbprat.setBackground(Color.white) ;
        pbprat.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pprat = 1 ;
        pbprat.setBackground(Color.blue) ;
        pbprat.setForeground(Color.white) ;
     }
  }
  if(label.equals("Total Pres")) {
     if (turbo.ppres == 1) {
         turbo.ppres = 0 ;
        pbpres.setBackground(Color.white) ;
        pbpres.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.ppres = 1 ;
        pbpres.setBackground(Color.blue) ;
        pbpres.setForeground(Color.white) ;
     }
  }
  if(label.equals("Spec Vol")) {
     if (turbo.pvol == 1) {
         turbo.pvol = 0 ;
        pbvol.setBackground(Color.white) ;
        pbvol.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pvol = 1 ;
        pbvol.setBackground(Color.blue) ;
        pbvol.setForeground(Color.white) ;
     }
  }
  if(label.equals("Tt Ratio")) {
     if (turbo.ptrat == 1) {
         turbo.ptrat = 0 ;
        pbtrat.setBackground(Color.white) ;
        pbtrat.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.ptrat = 1 ;
        pbtrat.setBackground(Color.blue) ;
        pbtrat.setForeground(Color.white) ;
     }
  }
  if(label.equals("Total Temp")) {
     if (turbo.pttot == 1) {
         turbo.pttot = 0 ;
        pbttot.setBackground(Color.white) ;
        pbttot.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pttot = 1 ;
        pbttot.setBackground(Color.blue) ;
        pbttot.setForeground(Color.white) ;
     }
  }
  if(label.equals("Entropy")) {
     if (turbo.pentr == 1) {
         turbo.pentr = 0 ;
        pbentr.setBackground(Color.white) ;
        pbentr.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pentr = 1 ;
        pbentr.setBackground(Color.blue) ;
        pbentr.setForeground(Color.white) ;
     }
  }
  if(label.equals("Gamma")) {
     if (turbo.pgam == 1) {
         turbo.pgam = 0 ;
        pbgam.setBackground(Color.white) ;
        pbgam.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.pgam = 1 ;
        pbgam.setBackground(Color.blue) ;
        pbgam.setForeground(Color.white) ;
     }
  }
  if(label.equals("Efficiency")) {
     if (turbo.peta == 1) {
         turbo.peta = 0 ;
        pbeta.setBackground(Color.white) ;
        pbeta.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.peta = 1 ;
        pbeta.setBackground(Color.blue) ;
        pbeta.setForeground(Color.white) ;
     }
  }
  if(label.equals("Area")) {
     if (turbo.parea == 1) {
         turbo.parea = 0 ;
        pbarea.setBackground(Color.white) ;
        pbarea.setForeground(Color.blue) ;
         turbo.pall = 0 ;
        pball.setBackground(Color.white) ;
        pball.setForeground(Color.blue) ;
     }
     else {
         turbo.parea = 1 ;
        pbarea.setBackground(Color.blue) ;
        pbarea.setForeground(Color.white) ;
     }
  }

  if(label.equals("Open File")) {
     filnam = namprnt.getText() ;
     fillab = namlab.getText() ;
     try{
         Turbo.pfile = new FileOutputStream(filnam) ;
      pbopen.setBackground(Color.red) ;
      pbopen.setForeground(Color.white) ;

         Turbo.prnt = new PrintStream(Turbo.pfile) ;

         Turbo.prnt.println("  ");
         Turbo.prnt.println(" EngineSim Application Version 1.7a - Oct 05 ");
         Turbo.prnt.println("  ");
         Turbo.prnt.println(fillab);
         Turbo.prnt.println("  ");
         turbo.iprint = 1;
         turbo.layin.show(turbo.inputPanel, "first");
    } catch (IOException n) {
    }
  }
} // end handler
}  // end FilePrintPanel
 
