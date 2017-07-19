/* Lib for calling something with an ID and getting a response
 * Necesita que se definan los siguientes elementos:
 * - Un elemento con name='csrfToken' para cargar el toke CSRF y poder hacer el
 *   llamado post.
 * - Call data. No es estrictamente necesario, pero ayuda a cargar la i18n del
 *   llamado. Ver referencia de loadCallData() para este punto
 */

/** Obtener el token csrf para los requests.
  * Necesita un elemento con nombre 'csrfToken' con el valor en la pagina
  */
function getCSRFToken() {
    return $('[name="csrfToken"]').val();
}

/* Bring up confirmation dialog and generate an ajax call if user chooses yes.
 * Call is a 'post' call. The only parameter passed is an id.
 * route - Url for ajax call
 * id    - Id to pass on to post call
 * title - Confirm dialog title
 * text  - Confirm dialog text
 * okText - Confirm dialog ok button text
 * cancelText - Confirm dialog cancel button text
 * okMsg      - Message to display if everything was ok
 * errorMsg   - Error message in case something failed
 * fok        - Function to call on ok response
 */
function ajaxCall(route, id, title, text, okText, cancelText, okMsg, errorMsg, fok) {
    // Confirm and call ifConfirm() when necessary
    alertify.confirm(
        title,
        text,
        function() { ifConfirm(route, id, okMsg, errorMsg, fok); },
        function() { }
    ).set(
        'labels', {ok: okText, cancel: cancelText}
    );
}

/* Calls route, sending "id". Expects a response of "ok"; anything else is an
 * error
 */
function ifConfirm(callRoute, id, okMsg, errorMsg, fok) {
    $.ajax({
        url:      callRoute,
        method:   'post',
        data:     { "id": id },
        headers:  { "Csrf-Token": getCSRFToken() },
        dataType: "text",
        success:  function(data, textStatus, jqXHR) {
            if (data == "ok") {
                fok();
                alertify.success(okMsg);
            }
            else {
                alertify.error(errorMsg);
            }
        },
        error:    function(jqXHR, textStatus, errorThrown) {
            alertify.error('General error');  // TODO Maybe do something about this?
        }
    });
}

/** Make a POST call sending a json object as data.
 * The json object is built using mkJson from the specified selector
 * @param selector   What will be packaged into the json object
 * @param route      What will be called if the op is confirmed
 * @param title      Confirm dialog title
 * @param text       Confirm dialog text
 * @param okText     Confirm dialog ok button text
 * @param cancelText Confirm dialog cancel button text
 * @param okFun      Function to call on success
 * @param errorFun   Function to call if an error ocurred
 */
function jsonCall(selector, route, title, text, okText, cancelText, okFun, errorFun) {
    alertify.confirm(
        title,
        text,
        function() { // On ok
            $.ajax({
                url: route,
                method: "post",
                data: mkJson(selector),
                dataType: "json",
                headers:  { "Csrf-Token": getCSRFToken() },
                success: function(sentData, textStatus, jqXHR) {
                    if (sentData.ok) {
                        okFun(sentData);
                    }
                    else {
                        errorFun(sentData.data);
                    }
                },
                error: function(sentData) {
                    errorFun(sentData.responseJSON.data);
                }
            })
        },
        function() { }  // Nothing on cancel
    ).set(
        'labels', {ok: okText, cancel: cancelText}
    );
}

/** jsonCall que llena los datos a partir de un loadCallData
 * @param selector Selector de lo que se va a enviar en el llamado
 * @param dataSelector Selector del elemento que contiene los datos para loadCallData
 * @param okFun Funcion a llamar si el llamado termina bien
 * @param errorFun Funcion a llamar en caso de error
 */
function jsonCallData(selector, dataSelector, okFun, errorFun) {
    var data = loadCallData(dataSelector);
    jsonCall(
        selector,
        data.route,
        data.title,
        data.okmsg,
        data.ok,
        data.cancel,
        okFun,
        errorFun
    );
}


/** Cargar los datos del llamado en forma de un objeto Json a partir de
 * la data de un selector
 */
function loadCallData(selector) {
    var obj = $(selector);
    return {
        route : obj.data('route'),
        title : obj.data('title'),
        msg   : obj.data('msg'),
        ok    : obj.data('ok'),
        cancel: obj.data('cancel'),
        okmsg : obj.data('okmsg'),
        errmsg: obj.data('errmsg')
    }
}

/** Build a json object from all values that match 'selector' */
function mkJson(selector) {
    var jsonData = {};

    $(selector).each(function() {
        jsonData[$(this).attr("name")] = $(this).val();
    });

    return jsonData;
}
