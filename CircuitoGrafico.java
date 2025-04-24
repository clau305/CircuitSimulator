import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CircuitoGrafico extends JFrame {
private boolean interruptor = false;
    private double voltaje = 9.0;
    private double resistencia = 3.0;

    private JLabel corrienteLabel, estadoLabel, voltajeLabel;
    private JPanel circuitoPanel;
    private JSlider resistenciaSlider;

    // Representación de conexiones
    private Map<String, Boolean> conexiones = new HashMap<>();

    public CircuitoGrafico() {
        setTitle("Simulación de Circuito Eléctrico Interactivo");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializar conexiones
        conexiones.put("fuente-interruptor", true);
        conexiones.put("interruptor-resistencia", true);
        conexiones.put("resistencia-medidor", true);
        conexiones.put("medidor-fuente", true);

        // Panel superior con controles
        JPanel controlPanel = new JPanel(new GridLayout(3, 1));

        JButton botonInterruptor = new JButton("Encender / Apagar");
        botonInterruptor.addActionListener(e -> {
            interruptor = !interruptor;
            actualizarEstado();
        });

        resistenciaSlider = new JSlider(1, 10, (int) resistencia);
        resistenciaSlider.setMajorTickSpacing(1);
        resistenciaSlider.setPaintTicks(true);
        resistenciaSlider.setPaintLabels(true);
        resistenciaSlider.addChangeListener(e -> {
            resistencia = resistenciaSlider.getValue();
            actualizarEstado();
        });

        controlPanel.add(botonInterruptor);
        controlPanel.add(new JLabel("Resistencia (Ω):"));
        controlPanel.add(resistenciaSlider);

        add(controlPanel, BorderLayout.NORTH);

        // Panel lateral con interruptores de conexiones
        JPanel conexionesPanel = new JPanel(new GridLayout(4, 1));
        conexionesPanel.setBorder(BorderFactory.createTitledBorder("Conexiones"));

        for (String conexion : conexiones.keySet()) {
            JCheckBox checkBox = new JCheckBox(conexion, conexiones.get(conexion));
            checkBox.addActionListener(e -> {
                conexiones.put(conexion, checkBox.isSelected());
                actualizarEstado();
            });
            conexionesPanel.add(checkBox);
        }

        add(conexionesPanel, BorderLayout.WEST);

        // Panel lateral con info
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        estadoLabel = new JLabel("Estado: Apagado");
        corrienteLabel = new JLabel("Corriente: 0 A");
        voltajeLabel = new JLabel("Voltaje en medidor: 0 V");
        infoPanel.add(new JLabel("Voltaje de la fuente: " + voltaje + " V"));
        infoPanel.add(corrienteLabel);
        infoPanel.add(estadoLabel);
        infoPanel.add(voltajeLabel);
        add(infoPanel, BorderLayout.SOUTH);

        // Panel gráfico del circuito
        circuitoPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                // Fuente
                g.drawRect(30, 100, 40, 40);
                g.drawString("+", 35, 95);
                g.drawString("-", 35, 160);

                // Línea a interruptor
                g.drawLine(70, 120, 120, 120);
                // Interruptor
                g.setColor(interruptor ? Color.GREEN : Color.RED);
                g.drawLine(120, 120, 150, interruptor ? 120 : 100);
                g.setColor(Color.BLACK);
                g.drawRect(120, 110, 30, 20);

                // Línea a resistencia
                g.drawLine(150, 120, 200, 120);
                g.drawRect(200, 110, 40, 20);
                g.drawString(resistencia + " Ω", 200, 105);

                // Línea al medidor
                g.drawLine(240, 120, 300, 120);
                g.setColor(Color.BLUE);
                g.drawRect(300, 100, 60, 40);
                g.drawString("Medidor", 310, 125);

                // Línea de regreso
                g.setColor(Color.BLACK);
                g.drawLine(360, 120, 360, 160);
                g.drawLine(360, 160, 30, 160);
                g.drawLine(30, 160, 30, 140);
            }
        };

        circuitoPanel.setPreferredSize(new Dimension(500, 200));
        add(circuitoPanel, BorderLayout.CENTER);

        actualizarEstado();
    }

    private void actualizarEstado() {
        if (circuitoCerrado()) {
            double corriente = interruptor ? voltaje / resistencia : 0.0;
            double voltajeMedidor = interruptor ? voltaje : 0.0;
            corrienteLabel.setText(String.format("Corriente: %.2f A", corriente));
            voltajeLabel.setText(String.format("Voltaje en medidor: %.2f V", voltajeMedidor));
            estadoLabel.setText("Estado: " + (interruptor ? "Encendido" : "Apagado"));
        } else {
            corrienteLabel.setText("Corriente: 0 A");
            voltajeLabel.setText("Voltaje en medidor: 0 V");
            estadoLabel.setText("Estado: Circuito Abierto");
        }
        circuitoPanel.repaint();
    }

    private boolean circuitoCerrado() {
        // Verificar si todas las conexiones están activas
        return conexiones.values().stream().allMatch(conectado -> conectado);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CircuitoGrafico().setVisible(true));
    }
