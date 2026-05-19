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

    window.location.href = "login.html";
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

    const response = await request.text();

    if(response != "FAIL") {
        localStorage.token = response; // Guardar el token JWT en el almacenamiento local
        localStorage.correo_electronico = datos.correo_electronico; // Guardar el correo electrónico en el almacenamiento local
        window.location.href = "index.html";
    } else {
        alert("Credenciales inválidas.");
    }
}