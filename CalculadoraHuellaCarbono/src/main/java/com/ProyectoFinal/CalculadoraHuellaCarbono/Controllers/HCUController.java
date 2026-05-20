package com.ProyectoFinal.CalculadoraHuellaCarbono.Controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoFinal.CalculadoraHuellaCarbono.Dao.HistorialConsumoUsuariosDao;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions.CantidadInvalidaException;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions.RegistroNoEncontradoException;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Exceptions.UsuarioNoAutorizadoException;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.ConsumoRequest;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.HistorialConsumoUsuarios;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Producto;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Models.Usuario;
import com.ProyectoFinal.CalculadoraHuellaCarbono.Utils.JWTUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/HistorialConsumo")
public class HCUController {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private HistorialConsumoUsuariosDao historialConsumoUsuariosDao;

    @Autowired
    private JWTUtil jwtUtil;

    // ----------------------------------------------------------------
    // POST /registrar  — Registra un nuevo consumo (alimento o gasolina)
    // ----------------------------------------------------------------
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestHeader("Authorization") String token,
            @RequestBody ConsumoRequest request) {

        // 1. Validar token con excepción personalizada
        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);

        // 2. Validar cantidad con excepción personalizada
        if (request.getCantidad() <= 0) {
            throw new CantidadInvalidaException(request.getCantidad());
        }

        // 3. Construir el registro de consumo
        HistorialConsumoUsuarios consumo = new HistorialConsumoUsuarios();
        Usuario usuario = entityManager.find(Usuario.class, Integer.parseInt(usuarioId));
        consumo.setUsuario(usuario);
        consumo.setFecha(LocalDate.now().toString()); // Guarda como "YYYY-MM-DD"
        consumo.setCantidad(request.getCantidad());

        if (request.getCategoriaId() >= 1) {
            // Es un alimento/producto
            Producto producto = entityManager.find(Producto.class, request.getProductoId());
            if (producto == null) {
                throw new RegistroNoEncontradoException("El producto con ID " + request.getProductoId() + " no existe.");
            }
            consumo.setProducto(producto);
            consumo.setHuellaCarbonoTotal(request.getCantidad() * producto.getHuellaCarbono());
        } else {
            // Es gasolina/transporte — factor fijo del sistema
            final double FACTOR_GASOLINA_KG_CO2_POR_LITRO = 2.31;
            consumo.setHuellaCarbonoTotal(request.getCantidad() * FACTOR_GASOLINA_KG_CO2_POR_LITRO);
        }

        // 4. Persistir en la base de datos
        historialConsumoUsuariosDao.registrarHistorialConsumoUsuarios(consumo);
        return ResponseEntity.ok("{\"status\": \"success\"}");
    }

    // ----------------------------------------------------------------
    // GET /historial  — Devuelve el historial del usuario autenticado
    // ----------------------------------------------------------------
    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(@RequestHeader("Authorization") String token) {
        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);
        List<HistorialConsumoUsuarios> historial =
                historialConsumoUsuariosDao.getHistorialConsumoUsuarios(Integer.parseInt(usuarioId));
        return ResponseEntity.ok(historial);
    }

    // ----------------------------------------------------------------
    // DELETE /eliminar/{id}  — Elimina un registro del historial
    // ----------------------------------------------------------------
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {

        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);

        boolean eliminado = historialConsumoUsuariosDao
                .eliminarHistorialConsumoUsuarios(id, Integer.parseInt(usuarioId));

        if (!eliminado) {
            throw new RegistroNoEncontradoException(id);
        }
        return ResponseEntity.ok("{\"status\": \"deleted\"}");
    }

    // ----------------------------------------------------------------
    // GET /exportar  — Genera y descarga un archivo .txt con el historial
    // ----------------------------------------------------------------
    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarHistorial(@RequestHeader("Authorization") String token) {

        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);
        int idInt = Integer.parseInt(usuarioId);

        Usuario usuario = entityManager.find(Usuario.class, idInt);
        List<HistorialConsumoUsuarios> historial =
                historialConsumoUsuariosDao.getHistorialConsumoUsuarios(idInt);

        // Generar el contenido del archivo de texto
        String contenido = generarArchivoTexto(usuario, historial);

        // Nombre de archivo con fecha actual
        String nombreArchivo = "huella_carbono_" + LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", nombreArchivo);

        return ResponseEntity.ok()
                .headers(headers)
                .body(contenido.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    // ----------------------------------------------------------------
    // Métodos privados de apoyo
    // ----------------------------------------------------------------

    /**
     * Extrae el ID del usuario del token JWT.
     * Lanza UsuarioNoAutorizadoException si el token es inválido.
     */
    private String obtenerUsuarioIdOLanzarExcepcion(String token) {
        try {
            String id = jwtUtil.getKey(token);
            if (id == null) throw new UsuarioNoAutorizadoException();
            return id;
        } catch (UsuarioNoAutorizadoException e) {
            throw e;
        } catch (Exception e) {
            throw new UsuarioNoAutorizadoException();
        }
    }

    /**
     * Genera el contenido del archivo .txt con el reporte de consumo.
     */
    private String generarArchivoTexto(Usuario usuario, List<HistorialConsumoUsuarios> historial) {
        StringBuilder sb = new StringBuilder();
        String linea = "=".repeat(60);
        String lineaFina = "-".repeat(60);

        // ---- Encabezado ----
        sb.append(linea).append("\n");
        sb.append("   REPORTE DE HUELLA DE CARBONO PERSONAL\n");
        sb.append("   Calculadora de Huella de Carbono - ProyectoFinal\n");
        sb.append(linea).append("\n\n");

        sb.append("Usuario : ").append(usuario.getNombre()).append("\n");
        sb.append("Correo  : ").append(usuario.getCorreo_electronico()).append("\n");
        sb.append("Fecha   : ").append(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        // ---- Tabla de registros ----
        sb.append(lineaFina).append("\n");
        sb.append(String.format("%-12s %-22s %-12s %-10s %-10s%n",
                "Fecha", "Producto/Actividad", "Tipo", "Cantidad", "CO2e (kg)"));
        sb.append(lineaFina).append("\n");

        if (historial == null || historial.isEmpty()) {
            sb.append("  (Sin registros de consumo)\n");
        } else {
            double totalCO2 = 0;
            for (HistorialConsumoUsuarios reg : historial) {
                String nombre = reg.getProducto() != null
                        ? reg.getProducto().getNombre()
                        : "Gasolina/Transporte";
                String tipo = reg.getProducto() != null ? "Alimento" : "Transporte";
                String unidad = reg.getProducto() != null
                        ? (reg.getProducto().getUnidad() != null ? reg.getProducto().getUnidad() : "kg")
                        : "Lts";

                // Truncar nombre si es muy largo
                if (nombre.length() > 20) nombre = nombre.substring(0, 18) + "..";

                sb.append(String.format("%-12s %-22s %-12s %-10s %-10s%n",
                        reg.getFecha() != null ? reg.getFecha() : "N/A",
                        nombre,
                        tipo,
                        String.format("%.3f %s", reg.getCantidad(), unidad),
                        String.format("%.4f", reg.getHuellaCarbonoTotal())));

                totalCO2 += reg.getHuellaCarbonoTotal();
            }

            sb.append(lineaFina).append("\n");
            sb.append(String.format("%-48s TOTAL: %.4f kg CO2e%n", "", totalCO2));
            sb.append(linea).append("\n\n");

            // ---- Clasificación del impacto ----
            sb.append("CLASIFICACION DE IMPACTO:\n");
            if (totalCO2 < 5) {
                sb.append("  [BAJO]    Menos de 5 kg CO2e. ¡Excelente! Sigue así.\n");
            } else if (totalCO2 < 15) {
                sb.append("  [MEDIO]   Entre 5 y 15 kg CO2e. Puedes mejorar un poco mas.\n");
            } else {
                sb.append("  [ALTO]    Mas de 15 kg CO2e. Considera reducir tu consumo.\n");
            }
            sb.append("\n");
        }

        // ---- Consejos ----
        sb.append(linea).append("\n");
        sb.append("CONSEJOS PARA REDUCIR TU HUELLA DE CARBONO:\n");
        sb.append(lineaFina).append("\n");
        sb.append("  1. Prefiere alimentos de origen vegetal (menor emision que carnes).\n");
        sb.append("  2. Reduce el uso del automovil; usa transporte publico o bicicleta.\n");
        sb.append("  3. Consume productos locales y de temporada.\n");
        sb.append("  4. Evita desperdiciar alimentos; planifica tus compras.\n");
        sb.append("  5. Considera el uso de energias renovables en tu hogar.\n\n");
        sb.append(linea).append("\n");
        sb.append("  Reporte generado automaticamente por CalculadoraHuellaCarbono\n");
        sb.append(linea).append("\n");

        return sb.toString();
    }
}
