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
import javax.swing.JSlider;

/**
 *
 * @author davzarov
 */
public class FiltrosBasicos extends ImagenOps {

    /**
     * @param imagenOriginal
     * @return imagenGris
     */
    public BufferedImage escalarGris(BufferedImage imagenOriginal) {
        BufferedImage imagenGris = super.copiarImagen(imagenOriginal);
        Color color;
        int colorPromedio;
        for (int x = 0; x < imagenOriginal.getWidth(); x++) {
            for (int y = 0; y < imagenOriginal.getHeight(); y++) {
                color = new Color(imagenOriginal.getRGB(x, y));
                colorPromedio = super.promediarColor(color);
                imagenGris.setRGB(x, y, super.desplazarValores(new Color(colorPromedio, colorPromedio, colorPromedio)));
            }
        }
        return imagenGris;
    }
    
    /**
     * @param parentComponent
     * @return umbral
     */
    public Integer seleccionarUmbral(Component parentComponent) {
        Integer umbral;
        JOptionPane optionPane = new JOptionPane();
        JSlider slider = super.PrepararSlider(optionPane);
        optionPane.setMessage(new Object[]{
            "Seleccione el valor",
            slider
        });
        optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialogo = optionPane.createDialog(parentComponent, "Binarizar");
        dialogo.setVisible(true);
        try {
            umbral = Integer.parseInt(optionPane.getInputValue().toString());
        } catch (NumberFormatException e) {
            umbral = null;
        }
        return umbral;
    }

    /**
     * @param color
     * @param umbral
     * @return sRGB
     */
    private int binarizarPixel(Color color, int umbral) {
        int colorPromedio;
        boolean cruzoUmbral;
        int sRGB;
        colorPromedio = super.promediarColor(color);
        cruzoUmbral = colorPromedio >= umbral;
        if (cruzoUmbral) {
            sRGB = super.desplazarValores(new Color(255, 255, 255));
        } else {
            sRGB = super.desplazarValores(new Color(0, 0, 0));
        }
        return sRGB;
    }

    /**
     * @param imagenOriginal
     * @param umbral
     * @return imagenBinaria
     */
    public BufferedImage binarizar(BufferedImage imagenOriginal, Integer umbral) {
        BufferedImage imagenBinaria = this.copiarImagen(imagenOriginal);
        Color color;
        for (int x = 0; x < imagenOriginal.getWidth(); x++) {
            for (int y = 0; y < imagenOriginal.getHeight(); y++) {
                color = new Color(imagenOriginal.getRGB(x, y));
                imagenBinaria.setRGB(x, y, this.binarizarPixel(color, umbral));
            }
        }
        return imagenBinaria;
    }
    
    /**
     * @param parentComponent
     * @return canal
     */
    public String seleccionarCanal(Component parentComponent) {
        Object[] opciones = {"RGB", "Rojo", "Verde", "Azul"};
        String canal, opcionSeleccionada = (String) JOptionPane.showInputDialog(
                parentComponent,
                "Seleccione el canal a invertir.",
                "Canales",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        // manejamos el retorno de opcionSeleccionada
        try {
            switch (opcionSeleccionada) {
                case "RGB":
                    canal = "RGB";
                    break;
                case "Rojo":
                    canal = "R";
                    break;
                case "Verde":
                    canal = "G";
                    break;
                case "Azul":
                    canal = "B";
                    break;
                default:
                    canal = "RGB";
                    break;
            }
        } catch (NullPointerException e) {
            canal = null;
        }
        return canal;
    }

    /**
     * @return sRGB
     */
    private int invertirCanal(Color color, String canal) {
        int sRGB = 0;
        switch (canal) {
            case "RGB":
                sRGB = super.desplazarValores(new Color(
                        255 - color.getRGB()
                ));
                break;
            case "R":
                sRGB = super.desplazarValores(new Color(
                        255 - color.getRed(),
                        color.getGreen(),
                        color.getBlue()
                ));
                break;
            case "G":
                sRGB = super.desplazarValores(new Color(
                        color.getRed(),
                        255 - color.getGreen(),
                        color.getBlue()
                ));
                break;
            case "B":
                sRGB = super.desplazarValores(new Color(
                        color.getRed(),
                        color.getGreen(),
                        255 - color.getBlue()
                ));
                break;
        }
        return sRGB;
    }

    /**
     * @param imagenOriginal
     * @param canal
     * @return imagenInvertida
     */
    public BufferedImage invertir(BufferedImage imagenOriginal, String canal) {
        BufferedImage imagenInvertida = this.copiarImagen(imagenOriginal);
        Color color;
        for (int x = 0; x < imagenOriginal.getWidth(); x++) {
            for (int y = 0; y < imagenOriginal.getHeight(); y++) {
                color = new Color(imagenOriginal.getRGB(x, y));
                imagenInvertida.setRGB(x, y, this.invertirCanal(color, canal));
            }
        }
        return imagenInvertida;
    }
}
