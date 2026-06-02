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

    
    // POST /registrar Registra un nuevo consumo (alimento o gasolina)
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestHeader("Authorization") String token,
            @RequestBody ConsumoRequest request) {

        //obtiene id de usuario por el token, pero si el token no es valido lanza una excepcion
        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);

        //si la cantidad que ingresa el usuario es incorrecta, lanza una excepcion
        if (request.getCantidad() <= 0) {
            throw new CantidadInvalidaException(request.getCantidad());
        }

        //Formatea el historial de consumo para guarda en la base de datos
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

        //guarda el consumo en la base de datos
        historialConsumoUsuariosDao.registrarHistorialConsumoUsuarios(consumo);
        //devuelve un mensaje de éxito
        return ResponseEntity.ok("{\"status\": \"success\"}");
    }

    // GET /historial  — Devuelve el historial del usuario autenticado
    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(@RequestHeader("Authorization") String token) {
        //obtiene id de usuario por el token, pero si el token no es valido lanza una excepcion
        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);
        //Usa el dao para obtener el historial de consumo del usuario y lo devuelve como respuesta
        List<HistorialConsumoUsuarios> historial =
                historialConsumoUsuariosDao.getHistorialConsumoUsuarios(Integer.parseInt(usuarioId));
        //devuelve el historial como respuesta
        return ResponseEntity.ok(historial);
    }

    // DELETE /eliminar/{id}  — Elimina un registro del historial
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(
            @RequestHeader("Authorization") String token,
            @PathVariable int id) {

        //obtiene id de usuario por el token, pero si el token no es valido lanza una excepcion
        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);

        // Usa el dao para eliminar el registro del historial, pero solo si pertenece al usuario autenticado
        boolean eliminado = historialConsumoUsuariosDao
                .eliminarHistorialConsumoUsuarios(id, Integer.parseInt(usuarioId));

        // Si no se eliminó ningún registro, significa que el ID no existe o no pertenece al usuario
        if (!eliminado) {
            throw new RegistroNoEncontradoException(id);
        }
        // Devuelve un mensaje de éxito
        return ResponseEntity.ok("{\"status\": \"deleted\"}");
    }

    // GET /exportar  — Genera y descarga un archivo .txt con el historia   
    @GetMapping("/exportar")
    //Devuelve un archivo de texto
    public ResponseEntity<byte[]> exportarHistorial(@RequestHeader("Authorization") String token) {
        //obtiene id de usuario por el token, pero si el token no es valido lanza una excepcion
        String usuarioId = obtenerUsuarioIdOLanzarExcepcion(token);
        //Convierte el ID de usuario a entero para usarlo en las consultas
        int idInt = Integer.parseInt(usuarioId);

        //Obtiene el usuario y su historial de consumo para generar el contenido del archivo de texto
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

    private String generarArchivoTexto(Usuario usuario, List<HistorialConsumoUsuarios> historial) {
        StringBuilder sb = new StringBuilder();
        String linea = "=".repeat(60);
        String lineaFina = "-".repeat(60);

        //encabezado
        sb.append(linea).append("\n");
        sb.append("   REPORTE DE HUELLA DE CARBONO PERSONAL\n");
        sb.append("   Calculadora de Huella de Carbono - ProyectoFinal\n");
        sb.append(linea).append("\n\n");

        sb.append("Usuario : ").append(usuario.getNombre()).append("\n");
        sb.append("Correo  : ").append(usuario.getCorreo_electronico()).append("\n");
        sb.append("Fecha   : ").append(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        //tabla de consumo
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

                //recorta el nombre
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

            //Clasificación del impacto
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

        // consejos
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
