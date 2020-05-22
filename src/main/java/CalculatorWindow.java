import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CalculatorWindow extends JFrame {
    private static final int WINDOW_WIDTH = 507;
    private static final int WINDOW_HEIGHT = 555;
    private static final int WINDOW_POSX = 450;
    private static final int WINDOW_POSY = 150;
    private String expression = "";

    private CalculatorWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);

        JPanel buttonsPanel = new JPanel(new GridLayout(0, 3));
        JPanel bottomPanel = new JPanel(new GridLayout(0, 2));

        JTextField exprField = new JTextField(expression);

        //Creation of numerical and operation buttons
        ArrayList<Button> buttons = createButtonBlock();
        for (Button button : buttons) {
            buttonsPanel.add(button);
        }

        //Creation of result button and clean button
        Button resultButton = new Button("=");
        Button cleanButton = new Button("Clean");
        resultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expression += resultButton.getLabel();
                //TODO
            }
        });
        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                expression = "";
            }
        });
        bottomPanel.add(resultButton);
        bottomPanel.add(cleanButton);

        add(exprField, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private ArrayList<Button> createButtonBlock() {
        ArrayList<Button> buttons = new ArrayList<>();

        for (int i = 1; i < 11; i++) {
            buttons.add(new Button(String.valueOf(i % 10)));
        }
        buttons.add(new Button("+"));
        buttons.add(new Button("-"));
        buttons.add(new Button("*"));
        buttons.add(new Button("/"));
        buttons.add(new Button("%"));

        for (Button button : buttons) {
            button.setSize(20, 20);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    expression += button.getLabel();
                    System.out.println(button.getLabel());
                }
            });
        }
        return buttons;
    }


    public static void main(String[] args) {
        new CalculatorWindow();
    }
}
