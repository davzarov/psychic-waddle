/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pdi_procesamiento;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import org.imgscalr.Scalr;
import pdi_dzarate.TratamientoImagenes;

/**
 *
 * @author davzarov
 */
public class ImagenOps {

    private JFileChooser archivosChooser;

    /**
     * @param parentComponent
     * @param labelOrigen
     * @return imagenOriginal
     */
    public BufferedImage abrirImagen(Component parentComponent, JLabel labelOrigen) {
        boolean esValido;
        BufferedImage imagenOriginal = null;
        this.archivosChooser = new JFileChooser();
        this.archivosChooser.addChoosableFileFilter(new FiltroImagenes());
        this.archivosChooser.setAcceptAllFileFilterUsed(false);
        this.archivosChooser.setDialogTitle("Seleccione una imágen - Parcial 3 PDI UA 2020.2");
        esValido = this.archivosChooser.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION;
        if (esValido) {
            try {
                File archivoSeleccionado = this.archivosChooser.getSelectedFile();
                imagenOriginal = ImageIO.read(archivoSeleccionado);
                // si la imágen existe
                if (imagenOriginal != null) {
                    // insertamos la imagen al JLabel
                    this.insertarImagen(imagenOriginal, labelOrigen);
                }
            } catch (IOException e) {
                Logger.getLogger(TratamientoImagenes.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return imagenOriginal;
    }
    
    /**
     * @param parentComponent
     * @param imagenProcesada
     */
    public void guardarImagen(Component parentComponent, BufferedImage imagenProcesada) {
        boolean esValido;
        this.archivosChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        this.archivosChooser.addChoosableFileFilter(new FiltroImagenes());
        this.archivosChooser.setAcceptAllFileFilterUsed(false);
        this.archivosChooser.setDialogTitle("Guardar imágen - Parcial 3 PDI UA 2020.2");
        esValido = this.archivosChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION;
        if (esValido) {
            try {
                String imagePath = (String) this.archivosChooser.getSelectedFile().getAbsolutePath();
                ImageIO.write(imagenProcesada, "png", new File(imagePath));
            } catch (IOException e) {
                Logger.getLogger(TratamientoImagenes.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * @param imagen
     * @param label
     */
    public void insertarImagen(BufferedImage imagen, JLabel label) {
        label.setText("");
        label.setIcon(null);
        // tratando de mantener aspect ratio, si la imágen
        // es más ancha que que el label que la contendrá
        if (imagen.getWidth() > label.getWidth()) {
            // inserta la imágen escalada a las dimensiones del JLabel
            label.setIcon(new ImageIcon(this.escalarImagen(
                    imagen,
                    label.getWidth()
            )));
        } else {
            // de lo contrario se inserta la imágen como está
            label.setIcon(new ImageIcon(imagen));
        }
    }
    
    /**
     * @param label
     */
    public void removerImagen(JLabel label) {
        label.setText("");
        label.setIcon(null);
    }

    /**
     * @param imagenOriginal
     * @return imagenCopiada
     */
    public BufferedImage copiarImagen(BufferedImage imagenOriginal) {
        BufferedImage imagenCopiada = new BufferedImage(
                imagenOriginal.getWidth(),
                imagenOriginal.getHeight(),
                imagenOriginal.getType()
        );
        imagenCopiada.setData(imagenOriginal.getData());
        return imagenCopiada;
    }

    /**
     * @param imagenOriginal
     * @param anchuraObjetivo
     * @return imagenEscalada
     */
    private BufferedImage escalarImagen(BufferedImage imagenOriginal, int anchuraObjetivo) {
        BufferedImage imagenEscalada;
        imagenEscalada = Scalr.resize(imagenOriginal, anchuraObjetivo);
        return imagenEscalada;
    }

    /**
     * @param parentComponent
     * @param mensaje
     * @param titulo
     */
    public void ErrorCargarImagen(Component parentComponent, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(
                parentComponent,
                mensaje,
                titulo,
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * @param color
     * @return
     */
    protected int sumarCanales(Color color) {
        return color.getRed() + color.getGreen() + color.getBlue();
    }

    /**
     * @param color
     * @return colorPromedio
     */
    protected int promediarColor(Color color) {
        int colorPromedio = (int) (this.sumarCanales(color) / 3);
        return colorPromedio;
    }

    /**
     * @param color
     * @return sRGB
     */
    protected int desplazarValores(Color color) {
        int sRGB = (color.getRed() << 16) | (color.getGreen() << 8) | (color.getBlue());
        return sRGB;
    }

    /**
     * @param suma
     * @return
     */
    protected int mininmoColor(int[] suma) {
        int pos = 0;
        int min = 255;
        for (int i = 0; i < suma.length; i++) {
            if (suma[i] < min) {
                pos = i;
                min = suma[i];
            }
        }
        return pos;
    }

    /**
     * @param suma
     * @return
     */
    protected int maximoColor(int[] suma) {
        int pos = 0;
        int max = 0;
        for (int i = 0; i < suma.length; i++) {
            if (suma[i] > max) {
                pos = i;
                max = suma[i];
            }
        }
        return pos;
    }

    class FiltroImagenes extends FileFilter {

        // archivos permitidos
        List<String> extensionesPermitidas = new ArrayList<String>() {
            {
                add("jpeg");
                add("jpg");
                add("png");
                add("gif");
                add("tiff");
                add("tif");
            }
        };

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = obtenerExtension(f);
            if (extension != null) {
                return extensionesPermitidas.contains(extension);
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Archivos de imágenes";
        }

        // extrae el último segmento de un string
        public String obtenerExtension(File archivo) {
            String extension = null, nombreArchivo;
            nombreArchivo = archivo.getName();
            // extension = nombreArchivo.split(".")[-1]; // error de indice
            int i = nombreArchivo.lastIndexOf('.');
            if (i > 0 && i < nombreArchivo.length() - 1) {
                extension = nombreArchivo.substring(i + 1).toLowerCase();
            }
            return extension;
        }
    }
    
    /**
     * @param optionPane
     * @return slider
     */
    protected JSlider PrepararSlider(JOptionPane optionPane) {
        JSlider slider = new JSlider();
        ChangeListener changeListener;
        slider.setMinimum(0);
        slider.setMaximum(255);
        slider.setMinorTickSpacing(0);
        slider.setMajorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        changeListener = (ChangeEvent e) -> {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                optionPane.setInputValue(source.getValue());
            }
        };
        slider.addChangeListener(changeListener);
        return slider;
    }
    
    /**
     *
     * @param optionPane
     * @param modelo
     * @return spinner
     */
    protected JSpinner PrepararSpinner(JOptionPane optionPane, SpinnerNumberModel modelo) {
        JSpinner spinner = new JSpinner(modelo);
        ChangeListener changeListener;
        changeListener = (ChangeEvent e) -> {
            JSpinner source = (JSpinner) e.getSource();
            optionPane.setInputValue(source.getValue());
        };
        spinner.addChangeListener(changeListener);
        return spinner;
    }
}
