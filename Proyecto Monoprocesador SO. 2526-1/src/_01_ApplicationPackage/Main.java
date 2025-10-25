/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package _01_ApplicationPackage;
import _07_GUI.MainJframe;
import javax.swing.SwingUtilities;

/**
 *
 * @author DiegoM
 */
public class Main {
    
     public static void main(String[] args) {
        // Iniciar la GUI de manera segura en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            MainJframe frame = new MainJframe();
            frame.setVisible(true);
        });
    }
    
}
