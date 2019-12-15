/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.trabajos.controladores;

import com.toedter.calendar.JDateChooser;
import gui.interfaces.IControladorModificarAlumno;
import gui.interfaces.IGestorTrabajos;
import gui.trabajos.modelos.AlumnoEnTrabajo;
import gui.trabajos.modelos.GestorTrabajos;
import gui.trabajos.modelos.Trabajo;
import gui.trabajos.vistas.VentanaModificarAlumno;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;


public class ControladorModificarAlumno implements IControladorModificarAlumno {
    private VentanaModificarAlumno ventana;
    private Trabajo unTrabajo;
    private AlumnoEnTrabajo unAET;
    
//    public ControladorModificarAlumno(Dialog ventanaPadre) {
//        this.ventana = new VentanaModificarAlumno(this, ventanaPadre);
//        this.ventana.setTitle(TRABAJO_MODIFICAR);
//        this.ventana.setLocationRelativeTo(null);
//        this.ventana.setVisible(true);
//    }
    
    public ControladorModificarAlumno(Dialog ventanaPadre, Trabajo unTrabajo, AlumnoEnTrabajo unAET) {
        this.unTrabajo = unTrabajo;
        this.unAET = unAET;
        this.ventana = new VentanaModificarAlumno(this, ventanaPadre);
        this.ventana.setTitle(TRABAJO_MODIFICAR);
        //CONVIERTO LOCALDATE (fechaDesde) A DATE.
        Date date = Date.from(unAET.verFechaDesde().atStartOfDay(ZoneId.systemDefault()).toInstant());
        //EL JDateChooser MUESTRA LA fecgaDesde DEL PROFESOR SELECCIONADO AL ABRIRSE LA VENTANA (Como referencia).
        this.ventana.verFechaHasta().setDate(date);
        //Mostaramos la fechaDesde para mostrar como referencia.
        this.ventana.verFechaDesde().setText(unAET.verFechaDesde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        this.ventana.verFechaDesde().setEditable(false);
        
        this.ventana.verTxtRazon().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
            }
        });
        
        this.ventana.setLocationRelativeTo(null);
        this.ventana.setVisible(true);
    }
    
    
    @Override
    public void btnAceptarClic(ActionEvent evt) {
        this.guardar();
    }
    
    
    private void guardar(){
        LocalDate fechaHasta = obtenerFechaDeJDateChooser(this.ventana.verFechaHasta());
        String razon = this.ventana.verTxtRazon().getText();
    
        IGestorTrabajos gsT = GestorTrabajos.instanciar();
        int confirmacion = JOptionPane.showConfirmDialog(ventana, "¿Desea finalizar el Alumno?");
        if (confirmacion == 0) {//Si el usuario elige "Si" se procedera a midificar el trabajo seleccionado
        String resultado = gsT.finalizarAlumno(this.unTrabajo, this.unAET.verAlumno(), fechaHasta, razon);
        
            if (!resultado.equals(IGestorTrabajos.EXITO)) {
                gsT.cancelar();
                JOptionPane.showMessageDialog(null, resultado, TRABAJO_MODIFICAR, JOptionPane.ERROR_MESSAGE);
                colorCalendario();
            }
            else{
                JOptionPane.showMessageDialog(this.ventana, "El alumno se finalizo exitosamente", TRABAJO_MODIFICAR, JOptionPane.PLAIN_MESSAGE);
                this.ventana.dispose();
            }
        }
    }

    
    @Override
    public void btnCancelarClic(ActionEvent evt) {
        IGestorTrabajos gsT = GestorTrabajos.instanciar();
        gsT.cancelar();
        this.ventana.dispose();
    }

    
    @Override
    public void txtRazonPresionarTecla(KeyEvent evt) {
        char c = evt.getKeyChar();            
        if (!Character.isLetter(c)) { //sólo se aceptan letras, Enter, Del, Backspace y espacio
            switch(c) {
                case KeyEvent.VK_ENTER: 
                    this.guardar();
                    break;
                case KeyEvent.VK_ESCAPE:
                    this.btnCancelarClic(null);
                    break;
                case KeyEvent.VK_BACK_SPACE:  
                    break;
                case KeyEvent.VK_DELETE:
                    break;
                case KeyEvent.VK_SPACE:
                    break;
                default:
                    evt.consume(); //consume el evento para que no sea procesado por la fuente
            }
        }else{
            this.ventana.verTxtRazon().setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    
    @Override
    public void fechaHastaPresionarTecla(KeyEvent evt) {
        char c = evt.getKeyChar();            
//        if (!Character.isLetter(c)) { //sólo se aceptan letras, Enter, Del, Backspace y espacio
            switch(c) {
                case KeyEvent.VK_ENTER: 
                    this.guardar();
                    break;
                case KeyEvent.VK_BACK_SPACE:  
                    colorCalendario();
                    break;
                case KeyEvent.VK_ESCAPE:
                    this.btnCancelarClic(null);
                case KeyEvent.VK_DELETE:
                    colorCalendario();
                    break;
                case KeyEvent.VK_SPACE:
                    break;
                default:
                    evt.consume(); //consume el evento para que no sea procesado por la fuente
            }
//        }
    }
    
    
    private LocalDate obtenerFechaDeJDateChooser(JDateChooser dateChooser) { //Convierte a LocalDate la fecha obtenida del JDateChooser
        Date date;
        if (dateChooser.getCalendar() != null) {
            date = dateChooser.getCalendar().getTime();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        else{
            return null;
        }
    }
    
    
    private void colorCalendario(){
        if (this.ventana.verFechaHasta().getCalendar() == null || obtenerFechaDeJDateChooser(this.ventana.verFechaHasta()).isBefore(unAET.verFechaDesde())) {
            this.ventana.verFechaHasta().setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        }else{
            this.ventana.verFechaHasta().setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }
    
}
