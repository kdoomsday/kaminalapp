/** Confirmar un pago. Incompleto */
function confirmarPago(boton) {
    var id = boton.data('id');
    ajaxCall(
        "/item/confirmar",
        id,
        "Confirmar pago",
        "Confirme registro de pago",
        "Confirmar",
        "Cancelar",
        "Pago confirmado",
        "Error confirmando pago",
        function() {
            $('#pago'+id).remove();
        }
    );
}


$(document).ready(function () {
    $('.confirmar').click(function() { confirmarPago($(this)); });
});
