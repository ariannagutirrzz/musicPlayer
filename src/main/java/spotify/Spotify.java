package spotify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Spotify extends JFrame implements ActionListener {

    JLabel label;
    private JMenuBar menuBar;
    private JMenu menuFile, menuView, menuSettings, menuHelp;
    private JMenuItem itemNew, itemOpen, itemLogout, itemExit, itemToggleSidebar, itemChangeTheme, itemPreferences, itemAccountSettings, itemConnectDevice, itemAbout, itemSupport;

    public Spotify(String loggedInUsername) {
        this.setTitle("Spotify");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setSize(1000, 600);

        // Crear el JMenuBar
        menuBar = new JMenuBar();

        // Crear el menú "Archivo"
        menuFile = new JMenu("Archivo");
        itemNew = new JMenuItem("Nuevo");
        itemNew.addActionListener(this);
        menuFile.add(itemNew);

        itemOpen = new JMenuItem("Abrir");
        itemOpen.addActionListener(this);
        menuFile.add(itemOpen);

        itemLogout = new JMenuItem("Cerrar sesión");
        itemLogout.addActionListener(this);
        menuFile.add(itemLogout);

        itemExit = new JMenuItem("Salir");
        itemExit.addActionListener(this);
        menuFile.add(itemExit);

        // Crear el menú "Ver"
        menuView = new JMenu("Ver");
        itemToggleSidebar = new JMenuItem("Mostrar/Ocultar Barra Lateral");
        itemToggleSidebar.addActionListener(this);
        menuView.add(itemToggleSidebar);

        itemChangeTheme = new JMenuItem("Cambiar Tema");
        itemChangeTheme.addActionListener(this);
        menuView.add(itemChangeTheme);

        // Crear el menú "Configuración"
        menuSettings = new JMenu("Configuración");
        itemPreferences = new JMenuItem("Preferencias");
        itemPreferences.addActionListener(this);
        menuSettings.add(itemPreferences);

        itemAccountSettings = new JMenuItem("Ajustes de la Cuenta");
        itemAccountSettings.addActionListener(this);
        menuSettings.add(itemAccountSettings);

        itemConnectDevice = new JMenuItem("Conectar Dispositivo");
        itemConnectDevice.addActionListener(this);
        menuSettings.add(itemConnectDevice);

        // Crear el menú "Ayuda"
        menuHelp = new JMenu("Ayuda");
        itemAbout = new JMenuItem("Acerca de Spotify");
        itemAbout.addActionListener(this);
        menuHelp.add(itemAbout);

        itemSupport = new JMenuItem("Soporte");
        itemSupport.addActionListener(this);
        menuHelp.add(itemSupport);

        // Agregar los menús al JMenuBar
        menuBar.add(menuFile);
        menuBar.add(menuView);
        menuBar.add(menuSettings);
        menuBar.add(menuHelp);

        // Establecer el JMenuBar en el JFrame
        setJMenuBar(menuBar);  // Mostrar el menú en la ventana

        // Crear una etiqueta como ejemplo
        label = new JLabel("Hi, " + loggedInUsername + "!");
        label.setBounds(10, 10, 300, 30);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(label);

        // Hacer visible la ventana
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Lógica para los elementos del menú
        if (e.getSource() == itemExit) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres salir?", "Salir", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);  // Cierra la aplicación
            }
        }

        if (e.getSource() == itemNew) {
            JOptionPane.showMessageDialog(this, "Crear nueva lista de reproducción.");
        }

        if (e.getSource() == itemOpen) {
            JOptionPane.showMessageDialog(this, "Abrir archivo o lista de reproducción.");
        }

        if (e.getSource() == itemLogout) {
            // Limpiar la variable estática (terminar sesión)
            Frame.loggedInUsername = null;
            JOptionPane.showMessageDialog(this, "Has cerrado sesión.");

            // Mostrar el login nuevamente
            try {
                new Frame();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            this.dispose();
        }

        if (e.getSource() == itemToggleSidebar) {
            JOptionPane.showMessageDialog(this, "Mostrar/Ocultar barra lateral.");
        }

        if (e.getSource() == itemChangeTheme) {
            JOptionPane.showMessageDialog(this, "Cambiar entre tema claro y oscuro.");
        }

        if (e.getSource() == itemPreferences) {
            JOptionPane.showMessageDialog(this, "Aquí podrías configurar las preferencias.");
        }

        if (e.getSource() == itemAccountSettings) {
            JOptionPane.showMessageDialog(this, "Configurar la cuenta de usuario.");
        }

        if (e.getSource() == itemConnectDevice) {
            JOptionPane.showMessageDialog(this, "Conectar dispositivo (altavoz, teléfono, etc.).");
        }

        if (e.getSource() == itemAbout) {
            JOptionPane.showMessageDialog(this, "Acerca de Spotify. Versión 1.0");
        }

        if (e.getSource() == itemSupport) {
            JOptionPane.showMessageDialog(this, "Acceder al soporte técnico.");
        }
    }

    public static void main(String[] args) {
        // Llamada para inicializar la ventana de Spotify
        new Spotify("example");
    }
}
