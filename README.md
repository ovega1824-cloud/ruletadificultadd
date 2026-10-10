# Ruleta de Dificultad (Fabric 1.21.1)

Mod de Fabric para Minecraft 1.21.1 que agrega el ítem **Ruleta de dificultad**.

- Clic derecho con la ruleta abre un menú con fondo transparente: la rueda de 8 colores, los números del 1 al 21 y, al elegir un número, un recuadro con el cambio de dificultad y los botones **ACTIVAR** / **DESACTIVAR**.
- Al activar o desactivar no se muestra ningún mensaje: el cambio simplemente se aplica (se guarda en el mundo).
- Solo pueden usarla los admins del servidor (operadores con nivel de permiso 2 o más).
- Requiere **Fabric Loader 0.16+** y **Fabric API**. Hay que instalarlo en el servidor y en los clientes.
- El ítem está en la pestaña de herramientas del creativo. Para darlo: `/give @s ruletadificultad:ruleta_dificultad`

## Cambios de dificultad

| N.º | Cambio |
|----:|--------|
| 1 | las vacas ya no darán más leche |
| 2 | las gallinas ya no pondrán huevos |
| 3 | los creepers explotarán con el doble de radio |
| 4 | la vida ya no se regenerará sola |
| 5 | los monstruos serán 30% más rápidos |
| 6 | el hambre bajará mucho más rápido |
| 7 | ya no se podrá dormir en camas |
| 8 | siempre será de noche |
| 9 | siempre estará lloviendo |
| 10 | los jugadores tendrán fatiga de minado |
| 11 | los jugadores serán más lentos |
| 12 | las ovejas ya no se podrán esquilar |
| 13 | los aldeanos ya no querrán comerciar |
| 14 | los jugadores tendrán 3 corazones menos |
| 15 | los jugadores saltarán mucho menos |

Los números 16 al 21 no tienen cambio asignado todavía.

Para agregar o cambiar cambios: edita `Dificultades.java` (texto e ícono) y `DificultadLogic.java` (lo que hace).

## Descargar el .jar

Cada vez que se sube código a la rama `main`, GitHub Actions compila el mod y publica el `.jar` en **Releases** (etiqueta `latest`). También queda como artefacto en la pestaña **Actions**.
