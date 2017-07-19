/** Funciones para cambio de clave. Requiere ajaxcall.js */
$(document).ready(function() {
    $('#go').click(cambiarClave);
    $('#formCambioClave').submit(function(evt) {
        console.log("Tratando de prevenir");
        evt.preventDefault();
        cambiarClave();
    });
});

// Realizar la acción de cambiar clave con los datos cargados
function cambiarClave() {
    jsonCallData(
        ".datosClave",
        ".datosClaveData",
        function(data) {
            clearPassword();
            alertify.success("Cambio de clave exitoso");
        },
        function(msg) {
            clearPassword();
            alertify.error(msg);
        }
    );
}

/** Eliminar el texto de la clave */
function clearPassword() {
    $('#nuevaClave').val('');
}
