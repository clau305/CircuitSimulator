
# Simulación de Circuito Eléctrico Interactivo

Este proyecto es una simulación gráfica de un circuito eléctrico simple, desarrollada en Java usando `Swing`. Permite visualizar la corriente eléctrica moviéndose por un circuito cerrado, con componentes como una fuente de voltaje, un interruptor, una resistencia ajustable y un medidor de voltaje.

## Características

- Visualización gráfica del circuito.
- Interruptor para abrir/cerrar el circuito.
- Slider para ajustar la resistencia (1Ω a 10Ω).
- Indicadores de estado del circuito, voltaje y corriente.
- Representación animada de la corriente eléctrica en función del signo del voltaje:
    - **Voltaje positivo** → corriente circula en sentido horario.
    - **Voltaje negativo** → corriente circula en sentido antihorario, y la flecha animada apunta en la dirección opuesta.**Se utilizó valor negativo a fines de representar sentido antihorario de recorrido**
- Posibilidad de simular cortes en el circuito mediante checkboxes que representan conexiones clave.

## Cómo ejecutar

1. Asegúrate de tener Java instalado (Java 8 o superior).
2. Compila el archivo:

   ```bash
   javac CircuitoGrafico.java
   
3. Ejecuta el programa:

   ```bash
   java CircuitoGrafico
   ```
   
## Componentes del circuito

- **Fuente de voltaje:** Valor fijo en la línea 11 (`private final double voltaje = 9.0;`). Cambia el valor a negativo para invertir la dirección de la corriente.

- **Interruptor:** Activa o desactiva el paso de corriente.

- **Resistencia:** Su valor puede ajustarse dinámicamente con un slider (entre 1Ω y 10Ω).

- **Medidor:** Muestra el voltaje actual cuando el circuito está cerrado.

- **Conexiones:** Cada tramo puede desconectarse individualmente para simular un circuito abierto.

## Notas sobre advertencias (warnings)

En determinadas circunstancias (por ejemplo, cuando el voltaje es cero, positivo o negativo), podrían generarse *warnings* durante la ejecución o compilación.

Estos *warnings* están relacionados con la variable `voltaje`, que fue declarada como `final` por cuestiones prácticas en la línea 11. Si bien esto no es considerado una buena práctica en aplicaciones productivas, se hizo conscientemente para simplificar el flujo del programa, sabiendo que este código no será expuesto a usuarios finales.

## Mejoras futuras

- Permitir que el voltaje sea ajustable dinámicamente mediante un campo numérico o *slider*.
- Representación más precisa de la polaridad de la fuente en pantalla.
- Simulación de más componentes eléctricos (condensadores, LEDs, etc.).
