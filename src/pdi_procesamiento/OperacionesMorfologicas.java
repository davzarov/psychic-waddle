/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdi_procesamiento;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author davzarov
 */
public class OperacionesMorfologicas extends ImagenOps {
    
    private Color[] obtenerVecinos(BufferedImage imagen, int x, int y) {
        Color[] vecinos = new Color[9];
        vecinos[0] = new Color(imagen.getRGB(x - 1, y - 1));
        vecinos[1] = new Color(imagen.getRGB(x, y - 1));
        vecinos[2] = new Color(imagen.getRGB(x + 1, y - 1));
        vecinos[3] = new Color(imagen.getRGB(x - 1, y));
        vecinos[4] = new Color(imagen.getRGB(x, y));
        vecinos[5] = new Color(imagen.getRGB(x + 1, y));
        vecinos[6] = new Color(imagen.getRGB(x - 1, y + 1));
        vecinos[7] = new Color(imagen.getRGB(x, y + 1));
        vecinos[8] = new Color(imagen.getRGB(x + 1, y + 1));
        return vecinos;
    }
    
    /**
     * @param parentComponent
     * @return pasadas
     */
    public Integer seleccionarPasadas(Component parentComponent) {
        Integer pasadas;
        JOptionPane optionPane = new JOptionPane();
        SpinnerNumberModel modelo = new SpinnerNumberModel(1, 1, 5, 1);
        JSpinner spinner = super.PrepararSpinner(optionPane, modelo);
        optionPane.setMessage(new Object[]{
            "Número de pasadas",
            spinner
        });
        optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialogo = optionPane.createDialog(parentComponent, "Operación Morfológica");
        dialogo.setVisible(true);
        try {
            pasadas = Integer.parseInt(optionPane.getInputValue().toString());
        } catch (NumberFormatException e) {
            pasadas = Integer.parseInt(modelo.getValue().toString());
        }
        return pasadas;
    }

    /**
     * @param imagenOriginal
     * @return imagenErosionada
     */
    public BufferedImage erosion(BufferedImage imagenOriginal) {
        BufferedImage imagenErosionada = this.copiarImagen(imagenOriginal);
        for (int x = 1; x < imagenOriginal.getWidth() - 1; x++) {
            for (int y = 1; y < imagenOriginal.getHeight() - 1; y++) {
                Color[] color = this.obtenerVecinos(imagenOriginal, x, y);
                int[] suma = new int[9];
                for (int k = 0; k < 9; k++) {
                    suma[k] = super.sumarCanales(color[k]);
                }
                Color nuevoColor = color[super.mininmoColor(suma)];
                imagenErosionada.setRGB(x, y, nuevoColor.getRGB());
            }
        }
        return imagenErosionada;
    }

    /**
     * @param imagenOriginal
     * @return imagenDilatada
     */
    public BufferedImage dilatacion(BufferedImage imagenOriginal) {
        BufferedImage imagenDilatada = this.copiarImagen(imagenOriginal);
        for (int x = 1; x < imagenOriginal.getWidth() - 1; x++) {
            for (int y = 1; y < imagenOriginal.getHeight() - 1; y++) {
                Color[] color = this.obtenerVecinos(imagenOriginal, x, y);
                int[] suma = new int[9];
                for (int k = 0; k < 9; k++) {
                    suma[k] = super.sumarCanales(color[k]);
                }
                Color nuevoColor = color[super.maximoColor(suma)];
                imagenDilatada.setRGB(x, y, nuevoColor.getRGB());
            }
        }
        return imagenDilatada;
    }
}
