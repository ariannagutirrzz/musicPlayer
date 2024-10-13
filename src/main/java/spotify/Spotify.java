package spotify;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Spotify extends JFrame implements ActionListener {

    JLabel label;

    Spotify(){
        this.setTitle("Spotify");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setSize(1000, 600);

        label = new JLabel("HOLA");
        label.setBounds(10, 10, 100, 30);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        this.add(label);


        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
