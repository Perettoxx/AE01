package ae01_Fitxers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Interfaz extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textDirectori;
    private JLabel lblCarpeta;
    private JButton btnBuscar;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField textCoincidencies;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Interfaz frame = new Interfaz();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Interfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        textDirectori = new JTextField();
        textDirectori.setBounds(104, 48, 730, 31);
        contentPane.add(textDirectori);
        textDirectori.setColumns(10);

        JLabel lblDirectori = new JLabel("Directori:");
        lblDirectori.setBounds(34, 55, 61, 16);
        contentPane.add(lblDirectori);

        lblCarpeta = new JLabel();
        ImageIcon folderIcon = new ImageIcon(Interfaz.class.getResource("/ae01_Fitxers/images/folder_icon.png"));
        Image scaledImage = folderIcon.getImage().getScaledInstance(60, 50, java.awt.Image.SCALE_SMOOTH);
        lblCarpeta.setIcon(new ImageIcon(scaledImage));
        lblCarpeta.setBounds(746, 162, 60, 50);
        contentPane.add(lblCarpeta);

        lblCarpeta.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                seleccionarDirectorio();
            }
        });

        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(721, 232, 117, 29);
        contentPane.add(btnBuscar);

        btnBuscar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		buscarArchivosTXT();
        	}
        });
        
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Nom");
        tableModel.addColumn("Extensió");
        tableModel.addColumn("Tamany (KB)");
        tableModel.addColumn("Última modificació");

        table = new JTable(tableModel);
        
        //Capa la edición de las celdas de la tabla
        table.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(34, 126, 627, 382);
        contentPane.add(scrollPane);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.getTableHeader().addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		int columnIndex = table.columnAtPoint(e.getPoint());
        		sorter.toggleSortOrder(columnIndex);
        	}
        });
        
        textCoincidencies = new JTextField();
        textCoincidencies.setColumns(10);
        textCoincidencies.setBounds(704, 331, 146, 31);
        contentPane.add(textCoincidencies);
        
        JButton btnCoincidencies = new JButton("Coincidències");
        btnCoincidencies.setBounds(704, 374, 146, 31);
        contentPane.add(btnCoincidencies);
        
        btnCoincidencies.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(contentPane, "La taula està buida.", "Taula Buida", JOptionPane.WARNING_MESSAGE);
                } else if (textCoincidencies.getText().isEmpty()){
                	JOptionPane.showMessageDialog(contentPane, "No hi ha res al field de coincidències.", "Field Coincidències Buit", JOptionPane.WARNING_MESSAGE);
                } else {
                	String palabraBuscada = textCoincidencies.getText();

                	contarCoincidencias(palabraBuscada);
                }
            }
        });

        
        JButton btnFusio = new JButton("Fusiò de fitxers");
        btnFusio.setBounds(704, 477, 146, 31);
        contentPane.add(btnFusio);

        btnFusio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //POR DESARROLLAR
            	//TENGO PROBLEMAS DE PERMISOS 
            }
        });

        
    }

    private void seleccionarDirectorio() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String directorioSeleccionado = chooser.getSelectedFile().getPath();
            textDirectori.setText(directorioSeleccionado);
        }
    }

    private void buscarArchivosTXT() {
        String directorioSeleccionado = textDirectori.getText();
        File directorio = new File(directorioSeleccionado);

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            tableModel.setRowCount(0);

            if (archivos != null) {
                for (File archivo : archivos) {
                    String nombre = archivo.getName();
                    String extension = nombre.substring(nombre.lastIndexOf(".") + 1);
                    long tamanoBytes = archivo.length();
                    double tamanoKB = (double) tamanoBytes / 1024.0;  // Convertir bytes a KB
                    Date fechaModificacion = new Date(archivo.lastModified());

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    tableModel.addRow(new Object[]{nombre, extension, tamanoKB, sdf.format(fechaModificacion)});
                }
            }
        }
    }

    
    private void contarCoincidencias(String palabra) {
        String directorioSeleccionado = textDirectori.getText();
        File directorio = new File(directorioSeleccionado);

        if (directorio.exists() && directorio.isDirectory()) {
            File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

            if (archivos != null) {
                for (File archivo : archivos) {
                    try {
                        int coincidencias = obtenerContenidoArchivo(archivo, palabra);
                        String mensaje = "En el archivo '" + archivo.getName() + "' se encontraron " + coincidencias + " coincidencias de la palabra '" + palabra + "'.";
                        JOptionPane.showMessageDialog(contentPane, mensaje, "Resultados", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private int obtenerContenidoArchivo(File archivo, String palabra) {
        int coincidencias = 0;
        try {
            Scanner scanner = new Scanner(archivo);

            while (scanner.hasNext()) {
                String linea = scanner.nextLine();
                if (linea.contains(palabra)) {
                    coincidencias++;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return coincidencias;
    }

}
