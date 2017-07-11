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
        function() {
            alertify.success("Ok!");
        },
        function(msg) {
            alertify.error(msg);
        }
    )
}
