async function procesarRegistro(event) {
    if (event) event.preventDefault();

    let datos = {};
    datos.nombre = document.getElementById("reg-nombre").value;
    datos.correo_electronico = document.getElementById("reg-email").value;
    datos.contraseña = document.getElementById("reg-password").value;
    const contraseñaConfirm = document.getElementById("reg-password-confirm").value;

    if (datos.contraseña !== contraseñaConfirm) {
        alert("Las contraseñas no coinciden.");
        return;
    }

    try {
        const response = await fetch('/api/registroUsuario', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        if (response.ok) {
            alert("Cuenta creada exitosamente. Por favor inicia sesión.");
            // Cambiar a la pestaña de login automáticamente
            document.getElementById('tab-login').click();
            document.getElementById("form-registro").reset();
        } else {
            alert("Error al crear la cuenta. Es posible que el correo ya esté en uso.");
        }
    } catch (error) {
        console.error(error);
        alert("Error de red al intentar registrar.");
    }
}

async function procesarLogin(event) {
    if (event) event.preventDefault();

    let datos = {};
    datos.correo_electronico = document.getElementById("login-email").value;
    datos.contraseña = document.getElementById("login-password").value;

    try {
        const request = await fetch('/api/iniciarSesion', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        const response = await request.text();

        if (response != "FAIL" && response.trim() !== "") {
            localStorage.token = response;
            localStorage.correo_electronico = datos.correo_electronico;
            window.location.href = "index.html";
        } else {
            alert("Credenciales inválidas. Por favor verifica tu correo y contraseña.");
        }
    } catch (error) {
        console.error(error);
        alert("Error de red al intentar iniciar sesión.");
    }
}