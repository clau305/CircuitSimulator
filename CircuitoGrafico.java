
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CircuitoGrafico extends JFrame {
    private final Timer animacionTimer;
    private boolean interruptor = false;
    private final double voltaje = 9.0;
    private double resistencia = 3.0;
    private final java.util.List<Point> recorridoCorriente;
    private int indiceFlecha = 0;
    private boolean direccionReversa = false;

    private final JLabel corrienteLabel;
    private final JLabel estadoLabel;
    private final JLabel voltajeLabel;
    private final JPanel circuitoPanel;
    private final JSlider resistenciaSlider;

    private final Map<String, Boolean> conexiones = new HashMap<>();

    public CircuitoGrafico() {
        setTitle("Simulación de Circuito Eléctrico Interactivo");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        conexiones.put("fuente-interruptor", true);
        conexiones.put("interruptor-resistencia", true);
        conexiones.put("resistencia-medidor", true);
        conexiones.put("medidor-fuente", true);

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

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        estadoLabel = new JLabel("Estado: Apagado");
        corrienteLabel = new JLabel("Corriente: 0 A");
        voltajeLabel = new JLabel("Voltaje en medidor: 0 V");
        infoPanel.add(new JLabel("Voltaje de la fuente: " + voltaje + " V"));
        infoPanel.add(corrienteLabel);
        infoPanel.add(estadoLabel);
        infoPanel.add(voltajeLabel);
        add(infoPanel, BorderLayout.SOUTH);

        recorridoCorriente = new ArrayList<>(Arrays.asList(
                new Point(70, 120), new Point(120, 120), new Point(150, 120), new Point(200, 120),
                new Point(240, 120), new Point(300, 120), new Point(360, 120), new Point(360, 160),
                new Point(30, 160), new Point(30, 140), new Point(30, 120), new Point(70, 120)
        ));

        circuitoPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawRect(30, 100, 40, 40);
                g.drawString("+", 35, 95);
                g.drawString("-", 35, 160);

                g.drawLine(70, 120, 120, 120);
                g.setColor(interruptor ? Color.GREEN : Color.RED);
                g.drawLine(120, 120, 150, interruptor ? 120 : 100);
                g.setColor(Color.BLACK);
                g.drawRect(120, 110, 30, 20);

                g.drawLine(150, 120, 200, 120);
                g.drawRect(200, 110, 40, 20);
                g.drawString(resistencia + " Ω", 200, 105);

                g.drawLine(240, 120, 300, 120);
                g.setColor(Color.BLUE);
                g.drawRect(300, 100, 60, 40);
                g.drawString("Medidor", 310, 125);

                g.setColor(Color.BLACK);
                g.drawLine(360, 120, 360, 160);
                g.drawLine(360, 160, 30, 160);
                g.drawLine(30, 160, 30, 140);

                if (circuitoCerrado() && interruptor && voltaje != 0 && recorridoCorriente.size() > 1) {
                    Point p1 = recorridoCorriente.get(indiceFlecha);
                    Point p2 = recorridoCorriente.get((indiceFlecha + 1) % recorridoCorriente.size());

                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(Color.ORANGE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                    int dx = p2.x - p1.x;
                    int dy = p2.y - p1.y;
                    double angle = Math.atan2(dy, dx);
                    int len = 10;
                    int arrowX = (int)(p2.x - len * Math.cos(angle));
                    int arrowY = (int)(p2.y - len * Math.sin(angle));

                    Polygon flecha = new Polygon();
                    flecha.addPoint(p2.x, p2.y);
                    flecha.addPoint(arrowX - 5, arrowY + 5);
                    flecha.addPoint(arrowX + 5, arrowY - 5);
                    g2.fill(flecha);
                }
            }
        };

        circuitoPanel.setPreferredSize(new Dimension(500, 200));
        add(circuitoPanel, BorderLayout.CENTER);

        animacionTimer = new Timer(100, e -> {
            if (circuitoCerrado() && interruptor && voltaje != 0) {
                direccionReversa = voltaje < 0;
                if (direccionReversa) {
                    indiceFlecha--;
                    if (indiceFlecha < 0) indiceFlecha = recorridoCorriente.size() - 2;
                } else {
                    indiceFlecha++;
                    if (indiceFlecha >= recorridoCorriente.size() - 1) indiceFlecha = 0;
                }
                circuitoPanel.repaint();
            }
        });

        animacionTimer.start();
        actualizarEstado();
    }

    private void actualizarEstado() {
        if (circuitoCerrado()) {
            double corriente = interruptor ? Math.abs(voltaje) / resistencia : 0.0;
            double voltajeMedidor = interruptor ? Math.abs(voltaje) : 0.0;
            corrienteLabel.setText(String.format("Corriente: %.2f A", corriente));
            voltajeLabel.setText(String.format("Voltaje en medidor: %.2f V", voltajeMedidor));
            estadoLabel.setText("Estado: " + (interruptor ? "Encendido" : "Apagado"));
            animacionTimer.start();
        } else {
            corrienteLabel.setText("Corriente: 0 A");
            voltajeLabel.setText("Voltaje en medidor: 0 V");
            estadoLabel.setText("Estado: Circuito Abierto");
            animacionTimer.stop();
        }
        circuitoPanel.repaint();
    }

    private boolean circuitoCerrado() {
        return conexiones.values().stream().allMatch(v -> v);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CircuitoGrafico().setVisible(true));
    }
}