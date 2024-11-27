package spotify;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Spotify extends JFrame implements ActionListener {

    JLabel label;
    private int index;

    //    Timer
    private Timer songTimer;
    private int elapsedTime = 0;
    private JLabel timeLabel;
    private JLabel totalDurationLabel;

    // Para el control de volumen
    private AudioPlayer audioPlayer = new AudioPlayer();
    private JSlider progressSlider;

    //    JMenu
    private JMenuBar menuBar;
    private JMenu menuFile, menuView, menuSettings, menuHelp;
    private JMenuItem itemOpen, itemLogout, itemExit, itemToggleSidebar, itemChangeTheme, itemPreferences, itemAccountSettings, itemConnectDevice, itemAbout, itemSupport;

    private JButton themeToggleButton;

    //    Lateral bar
    private JPanel sidebar;
    private JList<String> songList, playListList;
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
            String selectedSongPath = songPathsList[songIndex];
            currentSongLabel.setText("Reproduciendo: " + new File(selectedSongPath).getName());

            // Calcular y mostrar la duración total
            String duration = getSongDuration(selectedSongPath);
            totalDurationLabel.setText("Duración total: " + duration);

            // Configurar el rango del JSlider según la duración
            int totalSeconds = getTotalSecondsFromDuration(duration);
            progressSlider.setMaximum(totalSeconds);
            progressSlider.setValue(0);
            progressSlider.setEnabled(true);

            // Configurar las etiquetas del JSlider
            Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
            labelTable.put(0, new JLabel("00:00")); // Inicio del JSlider
            labelTable.put(totalSeconds, new JLabel(duration)); // Final del JSlider
            progressSlider.setLabelTable(labelTable);
            progressSlider.setPaintLabels(true); // Habilitar las etiquetas
            progressSlider.repaint();

            // Reiniciar el tiempo y actualizar la etiqueta
            elapsedTime = 0;
            updateTimerLabel();

            // Detén el temporizador anterior si existe
            stopTimer();

            // Configura el nuevo Timer para actualizar cada segundo
            songTimer = new Timer(1000, e -> {
                elapsedTime++;
                progressSlider.setValue(elapsedTime); // Actualizar el JSlider
                updateTimerLabel();

                // Actualizar la etiqueta del inicio del JSlider
                if (labelTable != null) {
                    JLabel startLabel = labelTable.get(0);
                    if (startLabel != null) {
                        int minutes = elapsedTime / 60;
                        int seconds = elapsedTime % 60;
                        startLabel.setText(String.format("%02d:%02d", minutes, seconds));
                        progressSlider.setLabelTable(labelTable); // Refrescar la tabla de etiquetas
                    }
                }

                if (elapsedTime >= totalSeconds) {
                    stopTimer();
                    progressSlider.setValue(0);
                    JOptionPane.showMessageDialog(this, "La canción ha terminado.");
                }
            });
            songTimer.start();

            // Reproduce la canción
            audioPlayer.play(selectedSongPath);
        } else {
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

        // Cuando se actualiza el tema o se agrega una canción a la base de datos:
        if (e.getSource() == themeToggleButton) {
            // Recargar las canciones desde la base de datos
            songPathsList = getSongPathsFromDB();  // Método que obtiene la lista de canciones de la base de datos
            songListModel = new DefaultListModel<>();  // Crear un nuevo DefaultListModel

            // Añadir las canciones al modelo (actualizar con las nuevas canciones)
            for (String songPath : songPathsList) {
                songListModel.addElement(new File(songPath).getName());  // Agregar el nombre del archivo de cada canción
            }

            // Actualizar el modelo del JList con el nuevo modelo
            songList.setModel(songListModel);

            // Si es necesario, hacer que el JList se actualice y redibuje
            songList.revalidate();
            songList.repaint();
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
        playListList = new JList<>(playListListModel);  // Usa el modelo de lista
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

        // Cargar la imagen original
        ImageIcon originalIcon = new ImageIcon("C:\\Users\\anton\\OneDrive\\Escritorio\\dev\\javaprojetcs\\musicPlayer\\musicPlayer\\src\\main\\java\\images\\refresh.png");

        // Redimensionar y crear una imagen redonda a partir de la imagen original
        ImageIcon roundedIcon = createRoundedIcon(originalIcon, 35, 35); // Tamaño deseado 40x40

        // Configurar el botón de cambiar tema con la imagen redonda
        themeToggleButton = new JButton();
        themeToggleButton.setIcon(roundedIcon);
        themeToggleButton.setPreferredSize(new Dimension(40, 40));
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setBorder(BorderFactory.createEmptyBorder());
        themeToggleButton.setBackground(Color.DARK_GRAY);
        themeToggleButton.addActionListener(this);

        // Agregar el botón al panel
        topPanel.add(themeToggleButton);

        // Agregar el panel al marco
        this.add(topPanel, BorderLayout.NORTH);
    }

    private ImageIcon createRoundedIcon(ImageIcon originalIcon, int targetWidth, int targetHeight) {
        // Redimensionar la imagen original para que se ajuste al tamaño del botón (por ejemplo, 40x40)
        Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        originalIcon = new ImageIcon(scaledImage);

        // Obtener el tamaño de la imagen redimensionada
        int width = originalIcon.getIconWidth();
        int height = originalIcon.getIconHeight();

        // Crear una imagen de tipo BufferedImage con transparencia (ARGB)
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Crear un objeto Graphics2D para dibujar sobre la imagen
        Graphics2D g2d = bufferedImage.createGraphics();

        // Establecer el antialiasing para un borde suave
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Crear un círculo dentro del área de la imagen
        g2d.fillRoundRect(0, 0, width, height, width, height);  // Esto crea un círculo

        // Establecer la máscara para recortar la imagen en forma circular
        g2d.setComposite(AlphaComposite.SrcIn);

        // Dibujar la imagen redimensionada sobre la imagen circular
        g2d.drawImage(originalIcon.getImage(), 0, 0, null);

        // Liberar los recursos gráficos
        g2d.dispose();

        // Devolver la nueva imagen redonda como ImageIcon
        return new ImageIcon(bufferedImage);
    }

    private int getTotalSecondsFromDuration(String duration) {
        try {
            String[] parts = duration.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60) + seconds;
        } catch (Exception e) {
            return 0;
        }
    }

    private void initializeMainContent() {
        // Crear el panel principal
        mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBackground(Color.GRAY);

        progressSlider = new JSlider();
        progressSlider.setMinimum(0);
        progressSlider.setValue(0); // Valor inicial
        progressSlider.setPaintTicks(true);
        progressSlider.setPaintLabels(true); // Habilitar etiquetas
        progressSlider.setEnabled(false); // Se habilitará cuando se reproduzca una canción

        progressSlider.addChangeListener(e -> {
            if (!progressSlider.getValueIsAdjusting()) {
                int newTime = progressSlider.getValue();
                if (newTime != elapsedTime) {
                    elapsedTime = newTime;
                    updateTimerLabel();

                    // Cambiar la posición de reproducción en el audio
                    audioPlayer.seekTo(newTime); // Necesitarás implementar esta función en tu clase AudioPlayer
                }
            }
        });
        
        // Etiqueta para mostrar la canción actual
        JLabel currentSongLabel = new JLabel("Selecciona una canción", SwingConstants.CENTER);
        currentSongLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentSongLabel.setForeground(Color.WHITE);

        timeLabel = new JLabel("Tiempo transcurrido: 00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(Color.WHITE);

        totalDurationLabel = new JLabel("Duración total: 00:00", SwingConstants.CENTER);
        totalDurationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        totalDurationLabel.setForeground(Color.WHITE);

        // Panel de controles de reproducción
        JPanel playerControls = new JPanel();
        playerControls.setLayout(new FlowLayout(FlowLayout.CENTER));
        playerControls.setBackground(Color.DARK_GRAY);

        // Botones de reproducción
        playButton = createButton("▶️", e -> playSelectedSong(currentSongLabel));
        pauseButton = createButton("⏸", e -> pauseSong());
        nextButton = createButton("⏭️", e -> nextSong(currentSongLabel));
        previousButton = createButton("⏮️", e -> previousSong(currentSongLabel));

        // Agregar botones al panel de controles
        playerControls.add(previousButton);
        playerControls.add(playButton);
        playerControls.add(pauseButton);
        playerControls.add(nextButton);

        // Crear un panel que combine los controles y el tiempo
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        southPanel.add(timeLabel, BorderLayout.NORTH);
        southPanel.add(totalDurationLabel, BorderLayout.CENTER);
        southPanel.add(progressSlider, BorderLayout.CENTER);

        southPanel.add(playerControls, BorderLayout.SOUTH);

        // Inicializa y llena el modelo de lista con las canciones
        songListModel = new DefaultListModel<>();
        for (String songPath : songPathsList) {
            songListModel.addElement(new File(songPath).getName());
        }

        songList = new JList<>(songListModel);
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setBackground(Color.LIGHT_GRAY);
        songList.setForeground(Color.BLACK);

        // Selección de canciones
        songList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                songIndex = songList.getSelectedIndex();
                if (songIndex >= 0) {
                    playSelectedSong(currentSongLabel);
                } else {
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
        mainContent.add(southPanel, BorderLayout.SOUTH);

        this.add(mainContent, BorderLayout.CENTER);
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

    private void addPlaylist(String playlistName) {
        if (!playlistName.trim().isEmpty()) {
            try (Connection conn = connectToDatabase()) {
                String query = "INSERT INTO playlists (name) VALUES (?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, playlistName);
                pstmt.executeUpdate();

                // Añadir al modelo de la lista
                playListListModel.addElement(playlistName);

                JOptionPane.showMessageDialog(this, "Lista de reproducción '" + playlistName + "' añadida.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El nombre de la lista no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedPlaylist() {
        String selectedPlaylist = playListList.getSelectedValue();
        if (selectedPlaylist != null) {
            try (Connection conn = connectToDatabase()) {
                String query = "DELETE FROM playlists WHERE name = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, selectedPlaylist);
                pstmt.executeUpdate();

                // Eliminar del modelo
                playListListModel.removeElement(selectedPlaylist);

                JOptionPane.showMessageDialog(this, "Lista de reproducción '" + selectedPlaylist + "' eliminada.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una lista para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSongDuration(String songPath) {
        try {
            File songFile = new File(songPath);
            FileInputStream fis = new FileInputStream(songFile);
            Bitstream bitstream = new Bitstream(fis);
            Header header = bitstream.readFrame();

            // Calcular duración (en segundos)
            long fileSize = songFile.length();
            int bitRate = header.bitrate();
            int durationInSeconds = (int) ((fileSize * 8) / bitRate);

            // Convertir a minutos:segundos
            int minutes = durationInSeconds / 60;
            int seconds = durationInSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);

        } catch (Exception e) {
            e.printStackTrace();
            return "00:00"; // Duración desconocida
        }
    }


    private void pauseSong() {
        if (songTimer != null && songTimer.isRunning()) {
            songTimer.stop();
            audioPlayer.stop();
        }
    }

    private void stopTimer() {
        if (songTimer != null) {
            songTimer.stop();
        }
        elapsedTime = 0;
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timeLabel.setText(String.format("Tiempo transcurrido: %02d:%02d", minutes, seconds));
    }

    public static void main(String[] args) {
        // Llamada para inicializar la ventana de Spotify
        new Spotify("example");
    }

}