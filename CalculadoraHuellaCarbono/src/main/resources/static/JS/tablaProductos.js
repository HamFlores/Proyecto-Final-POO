document.addEventListener("DOMContentLoaded", () => {
    cargarTablaProductos();
});

async function cargarTablaProductos() {
    const request = await fetch("/productos", {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        }
    });

    const productos = await request.json();

    console.log(productos);

    let listadoProductos = '';
    for (let producto of productos) {
        let rowProducto = '<tr> <th scope="row">'+ producto.id +'</th> <td>' 
        + producto.nombre + '</td> <td>' + producto.categoria_id + '</td> <td>' 
        + producto.unidad + '</td> <td>' + producto.huellaCarbono + '</td> <td>' 
        + producto.empresa + '</td> </tr>';

        listadoProductos += rowProducto;
    }

    document.querySelector('#tablaAlimentos tbody').outerHTML = listadoProductos;

}