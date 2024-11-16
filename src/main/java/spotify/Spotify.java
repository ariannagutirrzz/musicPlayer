package spotify;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

public class Spotify extends JFrame implements ActionListener {

    JLabel label;

//    JMenu
    private JMenuBar menuBar;
    private JMenu menuFile, menuView, menuSettings, menuHelp;
    private JMenuItem itemNew, itemOpen, itemLogout, itemExit, itemToggleSidebar, itemChangeTheme, itemPreferences, itemAccountSettings, itemConnectDevice, itemAbout, itemSupport;

//    Variables to change the theme
    private boolean isDarkTheme = true;
    private JButton themeToggleButton;

//    Lateral bar
    private JPanel sidebar;
    private JList<String> songList;
    private DefaultListModel<String> songListModel;
    private JButton playButton, pauseButton, nextButton, previousButton;

//    Main Content
    private JPanel mainContent;

    public Spotify(String loggedInUsername) {
        this.setTitle("Spotify");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        this.setSize(1000, 600);

        label = new JLabel("Welcome, " + loggedInUsername + "!");
        label.setBounds(10, 10, 300, 30);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(label);

        initializeMainContent();
        initializeThemeButton();
        initializeSidebar();
        initializeMenu();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Lógica para los elementos del menú
        if (e.getSource() == itemExit) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Are you sure you want to log out?", "Log out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }

        if (e.getSource() == itemNew) {
            String playlistName = JOptionPane.showInputDialog(this, "Type the name of the new reproduction list: ");
            if (playlistName != null && !playlistName.trim().isEmpty()) {
                addPlaylist(playlistName);
            }
        }

        if (e.getSource() == itemOpen) {
            // Simular abrir una lista de reproducción
            String[] playlists = {"Lista 1", "Lista 2", "Lista 3"};
            String selectedPlaylist = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecciona una lista de reproducción:",
                    "Abrir lista de reproducción",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    playlists,
                    playlists[0]);
            if (selectedPlaylist != null) {
                JOptionPane.showMessageDialog(this, "Abriendo " + selectedPlaylist);
            }
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

        if (e.getSource() == themeToggleButton) {
            // Alternar tema
            try {
                if (isDarkTheme) {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); // Tema claro
                    isDarkTheme = false;
                } else {
                    UIManager.setLookAndFeel(new FlatDarkLaf()); // Tema oscuro
                    isDarkTheme = true;
                }
                SwingUtilities.updateComponentTreeUI(this); // Aplicar cambio de tema
                updateThemeIcon();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cambiar tema.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        if (e.getSource() == itemToggleSidebar) {
            // Mostrar u ocultar la barra lateral
            sidebar.setVisible(!sidebar.isVisible());
            this.revalidate(); // Asegurarse de que la interfaz se actualice
            this.repaint();    // Redibujar la ventana
        }

        if (e.getSource() == itemPreferences) {
            JOptionPane.showMessageDialog(this, "Aquí podrías configurar las preferencias.");
        }

        if (e.getSource() == itemAccountSettings) {
            JOptionPane.showMessageDialog(this, "Configurar la cuenta de usuario.");
        }

        if (e.getSource() == itemAbout) {
            JOptionPane.showMessageDialog(this, "Acerca de Spotify. Versión 1.0");
        }
    }

//    THIS FUNCTION IS FOR CHANGE THE THEME WITH AN ICON

    private void updateThemeIcon() {
        String iconPath = isDarkTheme ? "/images/moon.png" : "/images/sun.png";

        try {
            themeToggleButton.setIcon(new ImageIcon(getClass().getResource(iconPath))); // Cargar ícono desde recursos
        } catch (Exception e) {
            themeToggleButton.setText(isDarkTheme ? "🌙" : "☀️"); // Alternativa textual
        }
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }


    private void initializeSidebar() {
        // Crear la barra lateral
        sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, this.getHeight()));
        sidebar.setBackground(Color.DARK_GRAY);

        // Crear el modelo de la lista de reproducción
        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setBackground(Color.LIGHT_GRAY);
        songList.setForeground(Color.BLACK);

        // Agregar listas de reproducción iniciales
        songListModel.addElement("Lista 1");
        songListModel.addElement("Lista 2");
        songListModel.addElement("Lista 3");

        // Agregar la lista a la barra lateral con un título
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Listas de Reproducción"));
        sidebar.add(scrollPane, BorderLayout.CENTER);

        // Crear botones de reproducción
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());
        playButton = createButton("▶️", e -> playSong());
        pauseButton = createButton("⏸", e -> pauseSong());
        nextButton = createButton("⏭️", e -> nextSong());
        previousButton = createButton("⏮️", e -> previousSong());

        // Agregar los botones al panel de controles
        controls.add(previousButton);
        controls.add(playButton);
        controls.add(pauseButton);
        controls.add(nextButton);

        // Agregar los controles a la barra lateral
        sidebar.add(controls, BorderLayout.SOUTH);

        // Mostrar la barra lateral por defecto
        sidebar.setVisible(true);

        // Agregar la barra lateral al JFrame
        this.add(sidebar, BorderLayout.WEST);
    }

    private void initializeThemeButton() {
        // Crear un panel para el botón
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.DARK_GRAY);

        // Configurar el botón de cambiar tema
        themeToggleButton = new JButton();
        themeToggleButton.setPreferredSize(new Dimension(40, 40));
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setBorder(BorderFactory.createEmptyBorder());
        themeToggleButton.setBackground(Color.DARK_GRAY);
        updateThemeIcon();
        themeToggleButton.addActionListener(this);

        // Agregar el botón al panel
        topPanel.add(themeToggleButton);

        // Agregar el panel al marco
        this.add(topPanel, BorderLayout.NORTH);
    }

    private void initializeMainContent() {
        // Crear el panel principal
        mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout()); // Puedes cambiar el layout si necesitas algo diferente
        mainContent.setBackground(Color.GRAY);

        // Añadir contenido de ejemplo
        JLabel contentLabel = new JLabel("Espacio principal para el reproductor o listado de canciones");
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentLabel.setForeground(Color.WHITE);

        mainContent.add(contentLabel, BorderLayout.CENTER);

        // Agregar el panel principal al layout central
        this.add(mainContent, BorderLayout.CENTER);
    }

    private void initializeMenu() {
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
        this.setJMenuBar(menuBar); // Mostrar el menú en la ventana
    }

    //
//    private void showPlayer() {
//        mainContent.removeAll(); // Eliminar contenido anterior
//
//        JLabel playerLabel = new JLabel("Aquí va el reproductor");
//        playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        playerLabel.setForeground(Color.WHITE);
//
//        mainContent.add(playerLabel, BorderLayout.CENTER);
//        mainContent.revalidate();
//        mainContent.repaint();
//    }
//

    private void addPlaylist(String playlistName) {
        if (!playlistName.trim().isEmpty()) {
            songListModel.addElement(playlistName); // Añadir la lista al modelo
            JOptionPane.showMessageDialog(this, "Lista de reproducción '" + playlistName + "' añadida.");
        } else {
            JOptionPane.showMessageDialog(this, "El nombre de la lista no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void playSong() {
        String selectedSong = songList.getSelectedValue();
        if (selectedSong != null) {
            JOptionPane.showMessageDialog(this, "Reproduciendo: " + selectedSong);
            // Aquí podrías integrar JLayer o JavaFX Media para reproducir la canción
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una canción primero.");
        }
    }

    private void pauseSong() {
        JOptionPane.showMessageDialog(this, "Pausa");
        // Implementa la lógica de pausa con JLayer o JavaFX Media
    }

    private void nextSong() {
        int index = songList.getSelectedIndex();
        if (index < songListModel.size() - 1) {
            songList.setSelectedIndex(index + 1);
            playSong();
        } else {
            JOptionPane.showMessageDialog(this, "No hay más canciones.");
        }
    }

    private void previousSong() {
        int index = songList.getSelectedIndex();
        if (index > 0) {
            songList.setSelectedIndex(index - 1);
            playSong();
        } else {
            JOptionPane.showMessageDialog(this, "No hay canciones anteriores.");
        }
    }


    public static void main(String[] args) {
        // Llamada para inicializar la ventana de Spotify
        new Spotify("example");
    }
}
