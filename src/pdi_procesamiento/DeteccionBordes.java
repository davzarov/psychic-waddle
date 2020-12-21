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
public class DeteccionBordes extends ImagenOps {

    /**
     * @return canales
     */
    private Object[] iniciarCanales(BufferedImage imagen, int w, int h) {
        Object[] canales = new Object[3];
        Color color;
        int[][] r_ch = new int[w][h];
        int[][] g_ch = new int[w][h];
        int[][] b_ch = new int[w][h];
        for (int x = 0; x < imagen.getWidth(); x++) {
            for (int y = 0; y < imagen.getHeight(); y++) {
                color = new Color(imagen.getRGB(x, y));
                r_ch[x][y] = color.getRed();
                g_ch[x][y] = color.getGreen();
                b_ch[x][y] = color.getBlue();
            }
        }
        canales[0] = r_ch;
        canales[1] = g_ch;
        canales[2] = b_ch;
        return canales;
    }

    /**
     * @return matrices
     */
    private Object[] iniciarMatrices(int w, int h) {
        Object[] matrices = new Object[3];
        int[][] r_mat = new int[w][h];
        int[][] g_mat = new int[w][h];
        int[][] b_mat = new int[w][h];
        matrices[0] = r_mat;
        matrices[1] = g_mat;
        matrices[2] = b_mat;
        return matrices;
    }

    /**
     * @return resultados
     */
    private Object[] operacionHorizontal(BufferedImage imagenOriginal) {
        BufferedImage imagenBordes = super.copiarImagen(imagenOriginal);
        int w = imagenBordes.getWidth();
        int h = imagenBordes.getHeight();
        Object[] canales = this.iniciarCanales(imagenBordes, w, h);
        Object[] horizontales = this.iniciarMatrices(w, h);
        Object[] resultados = new Object[4];
        int[][] canal_rojo = (int[][]) canales[0];
        int[][] canal_verde = (int[][]) canales[1];
        int[][] canal_azul = (int[][]) canales[2];
        int[][] horizontal_rojo = (int[][]) horizontales[0];
        int[][] horizontal_verde = (int[][]) horizontales[1];
        int[][] horizontal_azul = (int[][]) horizontales[2];
        for (int x = 0; x < w - 1; x++) {
            for (int y = 0; y < h; y++) {
                horizontal_rojo[x][y] = Math.abs(canal_rojo[x + 1][y] - canal_rojo[x][y]);
                horizontal_verde[x][y] = Math.abs(canal_verde[x + 1][y] - canal_verde[x][y]);
                horizontal_azul[x][y] = Math.abs(canal_azul[x + 1][y] - canal_azul[x][y]);
                imagenBordes.setRGB(x, y, new Color(horizontal_rojo[x][y], horizontal_verde[x][y], horizontal_azul[x][y]).getRGB());
            }
        }
        resultados[0] = horizontal_rojo;
        resultados[1] = horizontal_verde;
        resultados[2] = horizontal_azul;
        resultados[3] = imagenBordes;
        return resultados;
    }

    /**
     * resultados
     */
    private Object[] operacionVertical(BufferedImage imagenOriginal) {
        BufferedImage imagenBordes = super.copiarImagen(imagenOriginal);
        int w = imagenBordes.getWidth();
        int h = imagenBordes.getHeight();
        Object[] canales = this.iniciarCanales(imagenBordes, w, h);
        Object[] verticales = this.iniciarMatrices(w, h);
        Object[] resultados = new Object[4];
        int[][] canal_rojo = (int[][]) canales[0];
        int[][] canal_verde = (int[][]) canales[1];
        int[][] canal_azul = (int[][]) canales[2];
        int[][] vertical_rojo = (int[][]) verticales[0];
        int[][] vertical_verde = (int[][]) verticales[1];
        int[][] vertical_azul = (int[][]) verticales[2];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h - 1; y++) {
                vertical_rojo[x][y] = Math.abs(canal_rojo[x][y + 1] - canal_rojo[x][y]);
                vertical_verde[x][y] = Math.abs(canal_verde[x][y + 1] - canal_verde[x][y]);
                vertical_azul[x][y] = Math.abs(canal_azul[x][y + 1] - canal_azul[x][y]);
                imagenBordes.setRGB(x, y, new Color(vertical_rojo[x][y], vertical_verde[x][y], vertical_azul[x][y]).getRGB());
            }
        }
        resultados[0] = vertical_rojo;
        resultados[1] = vertical_verde;
        resultados[2] = vertical_azul;
        resultados[3] = imagenBordes;
        return resultados;
    }

    /**
     * @return imagenBordes
     */
    private BufferedImage operacionMixta(BufferedImage imagenOriginal) {
        BufferedImage imagenBordes = super.copiarImagen(imagenOriginal);
        int r, g, b;
        int w = imagenBordes.getWidth();
        int h = imagenBordes.getHeight();
        Object[] resultados_horizontales = this.operacionHorizontal(imagenOriginal);
        int[][] horizontal_rojo = (int[][]) resultados_horizontales[0];
        int[][] horizontal_verde = (int[][]) resultados_horizontales[1];
        int[][] horizontal_azul = (int[][]) resultados_horizontales[2];
        Object[] resultados_verticales = this.operacionVertical(imagenOriginal);
        int[][] vertical_rojo = (int[][]) resultados_verticales[0];
        int[][] vertical_verde = (int[][]) resultados_verticales[1];
        int[][] vertical_azul = (int[][]) resultados_verticales[2];
        for (int x = 0; x < w - 1; x++) {
            for (int y = 0; y < h; y++) {
                r = Math.max(horizontal_rojo[x][y], vertical_rojo[x][y]);
                g = Math.max(horizontal_verde[x][y], vertical_verde[x][y]);
                b = Math.max(horizontal_azul[x][y], vertical_azul[x][y]);
                imagenBordes.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return imagenBordes;
    }
    
    /**
     * @param parentComponent
     * @return metodo
     */
    public String seleccionarMetodo(Component parentComponent) {
        Object[] opciones = {"Horizontal", "Vertical", "Mixto"};
        String metodo, opcionSeleccionada = (String) JOptionPane.showInputDialog(
                parentComponent,
                "Seleccione el método de detección de bordes.",
                "Métodos",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        // manejamos el retorno de opcionSeleccionada
        try {
            switch (opcionSeleccionada) {
                case "Horizontal":
                    metodo = "h";
                    break;
                case "Vertical":
                    metodo = "v";
                    break;
                case "Mixto":
                    metodo = "m";
                    break;
                default:
                    metodo = "m";
                    break;
            }
        } catch (NullPointerException e) {
            metodo = null;
        }
        return metodo;
    }
    
    /**
     * @param imagenOriginal
     * @param metodo
     * @return imagenBordes
     */
    public BufferedImage detectarBordes(BufferedImage imagenOriginal, String metodo) {
        BufferedImage imagenBordes = null;
        if (null != metodo) {
            switch (metodo) {
                case "h":
                    Object[] horizontales = this.operacionHorizontal(imagenOriginal);
                    imagenBordes = (BufferedImage) horizontales[3]; // imagen es el ultimo elemento del objeto
                    break;
                case "v":
                    Object[] verticales = this.operacionVertical(imagenOriginal);
                    imagenBordes = (BufferedImage) verticales[3]; // imagen es el ultimo elemento del objeto
                    break;
                case "m":
                    imagenBordes = this.operacionMixta(imagenOriginal);
                    break;
                default:
                    break;
            }
        }
        return imagenBordes;
    }
    
    /**
     * @param parentComponent
     * @return peso
     */
    public Double seleccionarPeso(Component parentComponent) {
        Double peso;
        SpinnerNumberModel modelo = new SpinnerNumberModel(0.0, 0.0, 1.5, 0.5);
        JOptionPane optionPane = new JOptionPane();
        JSpinner spinner = super.PrepararSpinner(optionPane, modelo);
        optionPane.setMessage(new Object[]{
            "Ajuste el peso",
            spinner
        });
        optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        JDialog dialogo = optionPane.createDialog(parentComponent, "Gradiente");
        dialogo.setVisible(true);
        try {
            peso = Double.parseDouble(optionPane.getInputValue().toString());
        } catch (NumberFormatException e) {
            peso = null;
        }
        return peso;
    }
    
    /**
     * @return gradiente
     */
    private int calcularGradiente(int a, int b, double w) {
        a *= a;
        b *= b;
        double c = w * Math.sqrt(a + b);
        return Math.round((float) c);
    }
    
    /**
     * @param imagenOriginal
     * @param peso
     * @return imagenBordes
     */
    public BufferedImage detectarBordesGradiente(BufferedImage imagenOriginal, double peso) {
        BufferedImage imagenBordes = super.copiarImagen(imagenOriginal);
        int r, g, b;
        int w = imagenBordes.getWidth();
        int h = imagenBordes.getHeight();
        Object[] resultados_horizontales = this.operacionHorizontal(imagenOriginal);
        int[][] horizontal_rojo = (int[][]) resultados_horizontales[0];
        int[][] horizontal_verde = (int[][]) resultados_horizontales[1];
        int[][] horizontal_azul = (int[][]) resultados_horizontales[2];
        Object[] resultados_verticales = this.operacionVertical(imagenOriginal);
        int[][] vertical_rojo = (int[][]) resultados_verticales[0];
        int[][] vertical_verde = (int[][]) resultados_verticales[1];
        int[][] vertical_azul = (int[][]) resultados_verticales[2];
        for (int x = 0; x < w - 1; x++) {
            for (int y = 0; y < h; y++) {
                r = this.calcularGradiente(horizontal_rojo[x][y], vertical_rojo[x][y], peso);
                g = this.calcularGradiente(horizontal_verde[x][y], vertical_verde[x][y], peso);
                b = this.calcularGradiente(horizontal_azul[x][y], vertical_azul[x][y], peso);
                imagenBordes.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return imagenBordes;
    }
}
