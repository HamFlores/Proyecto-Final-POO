async function registrarUsuario() {
    let datos = {};

    datos.nombre = document.getElementById("reg-nombre").value;
    datos.correo_electronico = document.getElementById("reg-email").value;
    datos.contraseña = document.getElementById("reg-password").value;
    const contraseñaConfirm = document.getElementById("reg-password-confirm").value;

    if (datos.contraseña !== contraseñaConfirm) {
        alert("Las contraseñas no coinciden.");
        return;
    }

    const request = await fetch('/api/registroUsuario', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(datos)
    });
}

async function iniciarSesion() {
    let datos = {};

    datos.correo_electronico = document.getElementById("login-email").value;
    datos.contraseña = document.getElementById("login-password").value;

    const request = await fetch('/api/iniciarSesion', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(datos)
    });

    const response = await request.json();
}