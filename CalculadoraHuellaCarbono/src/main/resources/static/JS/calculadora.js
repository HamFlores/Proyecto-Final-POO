// ============================================================
// ESTADO GLOBAL
// ============================================================
let todosLosProductos = [];
let productosFiltrados = [];
let paginaActual = 1;
const ITEMS_POR_PAGINA = 8;

// ============================================================
// INICIALIZACIÓN
// ============================================================
document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("token");
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    cargarProductos();
    cargarHistorial();

    // Buscador con debounce
    document.getElementById("buscadorProductos").addEventListener("input", () => {
        filtrarYRenderizar();
    });

    // Form de gasolina
    document.getElementById("formTransporte").addEventListener("submit", (e) => {
        e.preventDefault();
        registrarGasolina();
    });
});

// ============================================================
// CARGA Y RENDERIZADO DE PRODUCTOS
// ============================================================
function cargarProductos() {
    fetch("/productos")
        .then(res => {
            if (!res.ok) throw new Error("Error al cargar productos");
            return res.json();
        })
        .then(productos => {
            todosLosProductos = productos;
            productosFiltrados = productos;
            renderizarTabla();
        })
        .catch(err => {
            console.error(err);
            document.getElementById("cuerpoTablaProductos").innerHTML =
                `<tr><td colspan="5" class="text-center text-danger py-4"><i class="bi bi-exclamation-triangle-fill me-2"></i>Error al cargar los productos del servidor.</td></tr>`;
        });
}

function filtrarYRenderizar() {
    const termino = document.getElementById("buscadorProductos").value.toLowerCase().trim();
    productosFiltrados = todosLosProductos.filter(p =>
        p.nombre.toLowerCase().includes(termino) ||
        (p.categoria && p.categoria.nombre.toLowerCase().includes(termino)) ||
        (p.empresa && p.empresa.toLowerCase().includes(termino))
    );
    paginaActual = 1;
    renderizarTabla();
}

function renderizarTabla() {
    const tbody = document.getElementById("cuerpoTablaProductos");
    const totalPaginas = Math.max(1, Math.ceil(productosFiltrados.length / ITEMS_POR_PAGINA));
    paginaActual = Math.min(paginaActual, totalPaginas);

    const inicio = (paginaActual - 1) * ITEMS_POR_PAGINA;
    const fin = inicio + ITEMS_POR_PAGINA;
    const productosPagina = productosFiltrados.slice(inicio, fin);

    tbody.innerHTML = "";

    if (productosPagina.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-4"><i class="bi bi-search me-2"></i>No se encontraron productos.</td></tr>`;
        actualizarPaginacion(totalPaginas);
        return;
    }

    productosPagina.forEach(producto => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>
                <span class="fw-semibold">${escHtml(producto.nombre)}</span>
                <br><small class="text-muted">${producto.empresa ? escHtml(producto.empresa) : ''}</small>
            </td>
            <td>
                <span class="badge bg-secondary">${producto.categoria ? escHtml(producto.categoria.nombre) : 'Sin categoría'}</span>
            </td>
            <td class="text-center fw-bold text-success">${producto.huellaCarbono} <small class="text-muted fw-normal">kg CO₂e/${escHtml(producto.unidad || 'kg')}</small></td>
            <td class="text-center" style="width: 130px;">
                <input type="number" class="form-control form-control-sm input-cantidad" 
                    id="cantidad-${producto.id}"
                    placeholder="Ej. 0.5" min="0.001" step="0.001"
                    aria-label="Cantidad para ${escHtml(producto.nombre)}">
            </td>
            <td class="text-center">
                <button class="btn btn-sm btn-success" onclick="registrarAlimento(${producto.id}, ${producto.categoria ? producto.categoria.id : 1})" title="Agregar al registro">
                    <i class="bi bi-plus-lg"></i> Agregar
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    actualizarPaginacion(totalPaginas);
}

function actualizarPaginacion(totalPaginas) {
    const inicio = (paginaActual - 1) * ITEMS_POR_PAGINA + 1;
    const fin = Math.min(paginaActual * ITEMS_POR_PAGINA, productosFiltrados.length);
    const total = productosFiltrados.length;

    document.getElementById("infoPaginacion").textContent =
        total === 0 ? "Sin resultados" : `Mostrando ${inicio}-${fin} de ${total} productos`;

    // Botones anterior / siguiente
    const btnAnterior = document.getElementById("btnAnterior");
    const btnSiguiente = document.getElementById("btnSiguiente");
    btnAnterior.parentElement.classList.toggle("disabled", paginaActual <= 1);
    btnSiguiente.parentElement.classList.toggle("disabled", paginaActual >= totalPaginas);

    // Números de página
    const contenedorNums = document.getElementById("numerosPagina");
    contenedorNums.innerHTML = "";
    const rango = 2;
    for (let i = Math.max(1, paginaActual - rango); i <= Math.min(totalPaginas, paginaActual + rango); i++) {
        const li = document.createElement("li");
        li.className = `page-item ${i === paginaActual ? "active" : ""}`;
        li.innerHTML = `<a class="page-link" href="#" onclick="irPagina(${i}); return false;">${i}</a>`;
        contenedorNums.appendChild(li);
    }
}

function irPagina(n) {
    paginaActual = n;
    renderizarTabla();
    document.getElementById("seccionProductos").scrollIntoView({ behavior: "smooth" });
}

function paginaAnterior() {
    if (paginaActual > 1) irPagina(paginaActual - 1);
}

function paginaSiguiente() {
    const totalPaginas = Math.ceil(productosFiltrados.length / ITEMS_POR_PAGINA);
    if (paginaActual < totalPaginas) irPagina(paginaActual + 1);
}

// ============================================================
// REGISTRO DE CONSUMO - ALIMENTOS
// ============================================================
function registrarAlimento(productoId, categoriaId) {
    const token = localStorage.getItem("token");
    const inputCantidad = document.getElementById(`cantidad-${productoId}`);
    const cantidad = parseFloat(inputCantidad.value);

    if (!cantidad || cantidad <= 0) {
        mostrarAlerta("Por favor ingresa una cantidad válida (mayor a 0).", "warning");
        inputCantidad.focus();
        return;
    }

    const payload = {
        productoId: productoId,
        categoriaId: categoriaId,
        cantidad: cantidad
    };

    fetch("/api/HistorialConsumo/registrar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (res.status === 401) {
            cerrarSesion();
            throw new Error("Sesión expirada");
        }
        if (!res.ok) throw new Error("Error al registrar");
        return res.json();
    })
    .then(() => {
        inputCantidad.value = "";
        mostrarAlerta("✅ Consumo registrado correctamente.", "success");
        cargarHistorial();
    })
    .catch(err => {
        console.error(err);
        mostrarAlerta("❌ Error al registrar el consumo. Verifica tu sesión.", "danger");
    });
}

// ============================================================
// REGISTRO DE GASOLINA
// ============================================================
function registrarGasolina() {
    const token = localStorage.getItem("token");
    const litros = parseFloat(document.getElementById("litrosGasolina").value);

    if (!litros || litros <= 0) {
        mostrarAlerta("Ingresa una cantidad de litros válida.", "warning");
        return;
    }

    const payload = {
        productoId: 0,
        categoriaId: 0,   // 0 = gasolina/transporte (< 1 en backend)
        cantidad: litros
    };

    fetch("/api/HistorialConsumo/registrar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": token
        },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (res.status === 401) {
            cerrarSesion();
            throw new Error("Sesión expirada");
        }
        if (!res.ok) throw new Error("Error al registrar gasolina");
        return res.json();
    })
    .then(() => {
        document.getElementById("litrosGasolina").value = "";
        mostrarAlerta("✅ Consumo de gasolina registrado correctamente.", "success");
        cargarHistorial();
    })
    .catch(err => {
        console.error(err);
        mostrarAlerta("❌ Error al registrar el transporte. Verifica tu sesión.", "danger");
    });
}

// ============================================================
// HISTORIAL DE CONSUMO
// ============================================================
function cargarHistorial() {
    const token = localStorage.getItem("token");

    fetch("/api/HistorialConsumo/historial", {
        headers: { "Authorization": token }
    })
    .then(res => {
        if (res.status === 401) {
            cerrarSesion();
            throw new Error("Sesión expirada");
        }
        if (!res.ok) throw new Error("Error al cargar historial");
        return res.json();
    })
    .then(historial => {
        renderizarHistorial(historial);
    })
    .catch(err => {
        console.error(err);
        document.getElementById("cuerpoHistorial").innerHTML =
            `<tr><td colspan="5" class="text-center text-danger py-3">Error al cargar tu historial.</td></tr>`;
    });
}

function renderizarHistorial(historial) {
    const tbody = document.getElementById("cuerpoHistorial");
    const totalEl = document.getElementById("totalHuella");
    tbody.innerHTML = "";

    if (!historial || historial.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-4"><i class="bi bi-inbox me-2"></i>Aún no tienes registros. ¡Empieza agregando alimentos o gasolina!</td></tr>`;
        totalEl.textContent = "0.000 kg CO₂e";
        return;
    }

    let totalCO2 = 0;
    historial.forEach(reg => {
        totalCO2 += reg.huellaCarbonoTotal;
        const nombre = reg.producto ? reg.producto.nombre : "🚗 Gasolina / Transporte";
        const tipo = reg.producto ? "Alimento" : "Transporte";
        const badgeClass = reg.producto ? "bg-success" : "bg-warning text-dark";
        const unidad = reg.producto ? (reg.producto.unidad || "kg") : "L";
        const tr = document.createElement("tr");
        tr.id = `reg-${reg.id}`;
        tr.innerHTML = `
            <td>${escHtml(reg.fecha)}</td>
            <td><span class="fw-semibold">${escHtml(nombre)}</span></td>
            <td><span class="badge ${badgeClass}">${tipo}</span></td>
            <td class="text-center">${reg.cantidad} ${escHtml(unidad)}</td>
            <td class="text-center fw-bold text-danger">${reg.huellaCarbonoTotal.toFixed(3)} kg</td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-danger" onclick="eliminarRegistro(${reg.id})" title="Eliminar registro">
                    <i class="bi bi-trash3"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    totalEl.textContent = `${totalCO2.toFixed(3)} kg CO₂e`;

    // Color e impacto textual según nivel de CO₂
    const badge = document.getElementById("badgeTotal");
    if (totalCO2 < 5) {
        badge.className = "badge bg-success fs-6";
        badge.textContent = "🌿 Impacto Bajo";
    } else if (totalCO2 < 15) {
        badge.className = "badge bg-warning text-dark fs-6";
        badge.textContent = "⚠️ Impacto Medio";
    } else {
        badge.className = "badge bg-danger fs-6";
        badge.textContent = "🔴 Impacto Alto";
    }
}

function eliminarRegistro(id) {
    if (!confirm("¿Estás seguro de que deseas eliminar este registro?")) return;

    const token = localStorage.getItem("token");

    fetch(`/api/HistorialConsumo/eliminar/${id}`, {
        method: "DELETE",
        headers: { "Authorization": token }
    })
    .then(res => {
        if (res.status === 401) {
            cerrarSesion();
            throw new Error("Sesión expirada");
        }
        if (!res.ok) throw new Error("Error al eliminar");
        // Animación de salida
        const fila = document.getElementById(`reg-${id}`);
        if (fila) {
            fila.style.transition = "opacity 0.4s";
            fila.style.opacity = "0";
            setTimeout(() => cargarHistorial(), 400);
        } else {
            cargarHistorial();
        }
        mostrarAlerta("🗑️ Registro eliminado.", "info");
    })
    .catch(err => {
        console.error(err);
        mostrarAlerta("❌ Error al eliminar el registro.", "danger");
    });
}

// ============================================================
// DESCARGA DE REPORTE EN ARCHIVO .TXT
// ============================================================
function descargarReporte() {
    const token = localStorage.getItem("token");
    const btn = document.getElementById("btnDescargar");

    // Feedback visual mientras descarga
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Generando...';

    fetch("/api/HistorialConsumo/exportar", {
        headers: { "Authorization": token }
    })
    .then(res => {
        if (res.status === 401) {
            cerrarSesion();
            throw new Error("Sesión expirada");
        }
        if (!res.ok) throw new Error("Error al generar el reporte");
        // Extraemos el nombre del archivo del header
        const disposition = res.headers.get("Content-Disposition");
        let nombreArchivo = "huella_carbono.txt";
        if (disposition && disposition.includes("filename=")) {
            nombreArchivo = disposition.split("filename=")[1].replace(/"/g, "").trim();
        }
        return res.blob().then(blob => ({ blob, nombreArchivo }));
    })
    .then(({ blob, nombreArchivo }) => {
        // Crear un enlace temporal para forzar la descarga
        const url = URL.createObjectURL(blob);
        const enlace = document.createElement("a");
        enlace.href = url;
        enlace.download = nombreArchivo;
        document.body.appendChild(enlace);
        enlace.click();
        document.body.removeChild(enlace);
        URL.revokeObjectURL(url);
        mostrarAlerta("📄 Reporte descargado exitosamente.", "success");
    })
    .catch(err => {
        console.error(err);
        mostrarAlerta("❌ Error al generar el reporte. Intenta de nuevo.", "danger");
    })
    .finally(() => {
        btn.disabled = false;
        btn.innerHTML = '<i class="bi bi-file-earmark-text me-1"></i>Descargar Reporte .txt';
    });
}

// ============================================================
// UTILIDADES
// ============================================================
function escHtml(str) {
    if (str === null || str === undefined) return "";
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

function mostrarAlerta(mensaje, tipo) {
    const zona = document.getElementById("zonaAlertas");
    if (!zona) return;
    const id = "alerta-" + Date.now();
    zona.innerHTML = `
        <div id="${id}" class="alert alert-${tipo} alert-dismissible fade show py-2 px-3 small" role="alert">
            ${mensaje}
            <button type="button" class="btn-close btn-sm" data-bs-dismiss="alert"></button>
        </div>
    `;
    setTimeout(() => {
        const el = document.getElementById(id);
        if (el) el.remove();
    }, 4000);
}
