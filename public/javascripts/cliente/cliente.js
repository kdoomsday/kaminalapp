/** Funciones para manipular cliente
 * Depende de ajaxcall.js
 */

$(document).ready(function() {
    // Asignar el evento de modificar nombre
    $('#nombreModal .ok').click(function() {
        modificarNombre();
    })
});

function modificarNombre() {
    jsonCallData(
        '.modNombre',
        '.modNombreData',
        function(data) {
            $('#nombreModal').modal('hide')
            var nombreCompleto = data.nombre + ' ' + data.apellido;
            $('#nombreCompleto').text(nombreCompleto);
            $('#nombre').val(data.nombre);
            $('#apellido').val(data.apellido);
            alertify.success("Nuevo nombre: " + nombreCompleto);
        },
        function(msg) {
            alertify.error(msg);
        }
    )
}
