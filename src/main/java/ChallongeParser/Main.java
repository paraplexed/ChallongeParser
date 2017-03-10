package ChallongeParser;

import java.io.IOException;
import javax.swing.*;

public class Main {
    public static void main(String... args) throws IOException {
        JFrame frame = new JFrame();
        frame.setContentPane(new CustomForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1250, 850);
    }

}
