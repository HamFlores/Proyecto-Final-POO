document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();
});

function cargarProductos() {
    // Apunta al endpoint de tus productos
    const urlApi = "/productos"; 

    fetch(urlApi)
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al conectar con el servidor de productos");
            }
            return response.json();
        })
        .then(productos => {
            const tbody = document.getElementById("cuerpoTabla");
            tbody.innerHTML = ""; // Limpiamos el texto de carga

            if (productos.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6">No hay productos en la base de datos.</td></tr>`;
                return;
            }

            // Iteramos sobre cada producto e insertamos la fila
            productos.forEach(producto => {
                const fila = document.createElement("tr");

                fila.innerHTML = `
                    <th scope="row">${producto.id}</th>
                    <td><strong>${producto.nombre}</strong></td>
                    <td>${producto.categoria ? producto.categoria.nombre : "Sin Categoría"}</td>
                    <td>${producto.unidad}</td>
                    <td>${producto.huellaCarbono} kg CO₂e</td>
                    <td>${producto.empresa ? producto.empresa : '<i>No especificada</i>'}</td>
                `;

                tbody.appendChild(fila);
            });
        })
        .catch(error => {
            console.error("Error en Fetch:", error);
            const tbody = document.getElementById("cuerpoTabla");
            tbody.innerHTML = `<tr><td colspan="6" style="color: red; font-weight: bold;">Error al conectar con el backend de la calculadora.</td></tr>`;
        });
}