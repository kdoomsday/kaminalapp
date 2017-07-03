/** Funcion que realiza un llamado para eliminar un item */
function eliminarItem(id) {
    var callData = loadCallData('.eliminarItemData');
    ajaxCall(callData.route,
             id,
             callData.title,
             callData.msg,
             callData.ok,
             callData.cancel,
             callData.okmsg,
             callData.errmsg,
             function() { removeItem(id); });
}

/** Eliminar un item de la interfaz. Se busca por id, esperando que sea de la
 * forma itemN, donde N es el id
 */
function removeItem(id) {
    $('#item'+id).remove();
}

$(document).ready(function() {
    // Establecer la funcion de eliminarItem para los elementos que la requieran
    $('.eliminarItem').click(function() {
        eliminarItem($(this).data('id'));
    })
})
