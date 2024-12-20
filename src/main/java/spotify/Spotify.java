package spotify;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class Spotify extends JFrame implements ActionListener {

    JLabel label;
    private int index;

    // Para el control de volumen
    private AudioPlayer audioPlayer = new AudioPlayer();

    //    JMenu
    private JMenuBar menuBar;
    private JMenu menuFile, menuView, menuSettings, menuHelp;
    private JMenuItem itemOpen, itemLogout, itemExit, itemToggleSidebar, itemChangeTheme, itemPreferences, itemAccountSettings, itemConnectDevice, itemAbout, itemSupport;

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
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private String[] songPathsList = getSongPathsFromDB();

    // Índices separados para playlist y canciones
    private int songIndex = -1;       // Índice para la canción seleccionada

    private void playSelectedSong(JLabel currentSongLabel) {
        if (songIndex >= 0 && songIndex < songPathsList.length) {
            String selectedSongPath = songPathsList[songIndex];  // Usar el índice correcto de la canción
            currentSongLabel.setText("Reproduciendo: " + new File(selectedSongPath).getName());
            System.out.println("Reproduciendo: " + selectedSongPath);  // Depuración
            audioPlayer.play(selectedSongPath);  // Método para reproducir la canción
        } else {
            System.out.println("Por favor, selecciona una canción.");
            currentSongLabel.setText("Por favor, selecciona una canción.");
        }
    }

    private void nextSong(JLabel currentSongLabel) {
        // Verificar si no estamos en la última canción
        if (songIndex < songPathsList.length - 1) {
            songIndex++;  // Avanzar a la siguiente canción
            playSelectedSong(currentSongLabel);
        } else {
            JOptionPane.showMessageDialog(this, "No hay más canciones.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void previousSong(JLabel currentSongLabel) {
        // Verificar si no estamos en la primera canción
        if (songIndex > 0) {
            songIndex--;  // Retroceder a la canción anterior
            playSelectedSong(currentSongLabel);
        } else {
            JOptionPane.showMessageDialog(this, "No hay canciones anteriores.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/Spotify"; // Cambia esto según tu configuración
        String user = "postgres";
        String password = "password";
        return DriverManager.getConnection(url, user, password);
    }

    public static String[] getSongPathsFromDB() {
        ArrayList<String> songPathsList = new ArrayList<>();
        String url = "jdbc:postgresql://localhost:5432/Spotify"; // Cambia esto según tu configuración
        String user = "postgres";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            String sql = "SELECT path FROM songs";  // Consulta SQL para obtener las rutas de las canciones
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                songPathsList.add(rs.getString("path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Convertimos la lista de rutas a un arreglo de String
        return songPathsList.toArray(new String[0]);
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

    DefaultListModel<String> playListListModel = new DefaultListModel<>();
    private void initializeSidebar() {
        // Crear la barra lateral
        sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, this.getHeight()));
        sidebar.setBackground(Color.DARK_GRAY);

        // Crear el JList para las listas de reproducción
        JList<String> playListList = new JList<>(playListListModel);  // Usa el modelo de lista
        playListList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playListList.setBackground(Color.LIGHT_GRAY);
        playListList.setForeground(Color.BLACK);

        // Cargar listas de reproducción desde la base de datos
        loadPlaylistsFromDatabase();

        // Agregar la lista a la barra lateral con un título
        JScrollPane scrollPane = new JScrollPane(playListList);  // Aquí usa playListList, no songList
        scrollPane.setBorder(BorderFactory.createTitledBorder("Listas de Reproducción"));
        sidebar.add(scrollPane, BorderLayout.CENTER);

        // Crear controles de reproducción
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(2, 1, 5, 5)); // Dos botones en una columna
        controls.setBackground(Color.DARK_GRAY);

        // Botones de añadir y eliminar playlists
        JButton addPlaylistButton = createButton("Añadir Playlist", e -> {
            String playlistName = JOptionPane.showInputDialog(this, "Ingresa el nombre de la nueva lista de reproducción:");
            if (playlistName != null && !playlistName.trim().isEmpty()) {
                addPlaylist(playlistName);
            }
        });

        JButton deletePlaylistButton = createButton("Eliminar Playlist", e -> deleteSelectedPlaylist());

        // Agregar los botones al panel de controles
        controls.add(addPlaylistButton);
        controls.add(deletePlaylistButton);

        // Agregar controles al panel inferior de la barra lateral
        sidebar.add(controls, BorderLayout.SOUTH);

        // Mostrar la barra lateral por defecto
        sidebar.setVisible(true);

        // Agregar la barra lateral al JFrame
        this.add(sidebar, BorderLayout.WEST);
    }

    private void loadPlaylistsFromDatabase() {
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM playlists")) {

            while (rs.next()) {
                playListListModel.addElement(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las listas: " + e.getMessage(),
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
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
        mainContent.setLayout(new BorderLayout());
        mainContent.setBackground(Color.GRAY);

        // Etiqueta para mostrar la canción actual
        JLabel currentSongLabel = new JLabel("Selecciona una canción", SwingConstants.CENTER);
        currentSongLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentSongLabel.setForeground(Color.WHITE);

        // Panel de controles de reproducción
        JPanel playerControls = new JPanel();
        playerControls.setLayout(new FlowLayout(FlowLayout.CENTER));
        playerControls.setBackground(Color.DARK_GRAY);

        // Botones de reproducción
        playButton = createButton("▶️", e -> playSelectedSong(currentSongLabel));
        pauseButton = createButton("⏸", e -> audioPlayer.stop());
        nextButton = createButton("⏭️", e -> nextSong(currentSongLabel));
        previousButton = createButton("⏮️", e -> previousSong(currentSongLabel));

        // Agregar botones al panel de controles
        playerControls.add(previousButton);
        playerControls.add(playButton);
        playerControls.add(pauseButton);
        playerControls.add(nextButton);

        // Inicializa y llena el modelo de lista con las canciones
        songListModel = new DefaultListModel<>();
        for (String songPath : songPathsList) {
            songListModel.addElement(new File(songPath).getName());
        }

// Asegúrate de que las canciones están siendo agregadas correctamente
        System.out.println("Canciones en la lista:");
        for (int i = 0; i < songListModel.size(); i++) {
            System.out.println(songListModel.getElementAt(i));
        }

        songList = new JList<>(songListModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Selección única
        songList.setBackground(Color.LIGHT_GRAY);
        songList.setForeground(Color.BLACK);


        // Asegúrate de que solo se actualice cuando se selecciona una canción
        songList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                songIndex = songList.getSelectedIndex(); // Actualiza el índice de la canción
                System.out.println("Índice de la canción seleccionado: " + songIndex);

                // Solo reproducir la canción si el índice es válido
                if (songIndex >= 0) {
                    playSelectedSong(currentSongLabel);  // Reproducir la canción
                } else {
                    System.out.println("Por favor, selecciona una canción.");
                    currentSongLabel.setText("Por favor, selecciona una canción.");
                }
            }
        });

        // Crear JScrollPane para el JList
        JScrollPane songListScrollPane = new JScrollPane(songList);
        songListScrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Canciones"));


        // Agregar componentes al panel principal
        mainContent.add(songListScrollPane, BorderLayout.CENTER);
        mainContent.add(currentSongLabel, BorderLayout.NORTH);
        mainContent.add(playerControls, BorderLayout.SOUTH);

        // Verifica que el panel no sea null antes de agregarlo al JFrame
        if (mainContent != null) {
            this.add(mainContent, BorderLayout.CENTER);
        } else {
            System.out.println("Error: mainContent es null");
        }
    }



    // Función para generar el texto con los nombres de las canciones
    private String getSongListText() {
        StringBuilder songListText = new StringBuilder("<html>");

        // Recorrer el array songPaths y agregar solo los nombres de los archivos
        for (String songPath : songPathsList) {
            String songName = songPath.substring(songPath.lastIndexOf("\\") + 1); // Obtener el nombre del archivo
            songListText.append(songName).append("<br>");
        }

        songListText.append("</html>");
        return songListText.toString();
    }


    private void initializeMenu() {
        // Crear el JMenuBar
        menuBar = new JMenuBar();

        // Crear el menú "Archivo"
        menuFile = new JMenu("Archivo");

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
            try (Connection conn = connectToDatabase()) {
                String query = "INSERT INTO playlists (name) VALUES (?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, playlistName);
                pstmt.executeUpdate();

                // Añadir al modelo de la lista
                songListModel.addElement(playlistName);

                JOptionPane.showMessageDialog(this, "Lista de reproducción '" + playlistName + "' añadida.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El nombre de la lista no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedPlaylist() {
        String selectedPlaylist = songList.getSelectedValue();
        if (selectedPlaylist != null) {
            try (Connection conn = connectToDatabase()) {
                String query = "DELETE FROM playlists WHERE name = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, selectedPlaylist);
                pstmt.executeUpdate();

                // Eliminar del modelo
                songListModel.removeElement(selectedPlaylist);

                JOptionPane.showMessageDialog(this, "Lista de reproducción '" + selectedPlaylist + "' eliminada.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una lista para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
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