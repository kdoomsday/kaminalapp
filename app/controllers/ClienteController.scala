package controllers

import audits.EventDao
import controllers.actions.Actions
import daos.{ ClienteDao, ItemDao }
import format.DateFormatter
import javax.inject.Inject
import json.JsonHelpers._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.mvc.Controller
import scala.concurrent.Future

import forms.FormFormatters.cuenta

/** Controlador para acciones que tienen que ver con clientes */
class ClienteController @Inject() (
    actions: Actions,
    clienteDao: ClienteDao,
    itemDao: ItemDao,
    eventDao: EventDao,
    dateFormatter: DateFormatter,
    val messagesApi: MessagesApi
) extends Controller with I18nSupport {
  import ClienteController._

  implicit val dFmt = dateFormatter

  // Vista del listado de los clientes
  def clientes = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.cliente.clientes(clienteDao.clientesSaldo())))
  }

  // Accion de agregar el cliente nuevo
  def addCliente = actions.roleAction("interno") { implicit req ⇒
    val result = clienteForm.bindFromRequest.fold(
      formWithErrors ⇒ BadRequest(views.html.cliente.addCliente(formWithErrors)),
      cliente ⇒ {
        clienteDao.addCliente(cliente)
        eventDao.write(s"Cliente ${cliente.nombre} ${cliente.apellido} agregado")
        implicit val notifications = Notification.success(Messages("ClienteController.addCliente.success", s"${cliente.nombre} ${cliente.apellido}"))
        Ok(views.html.cliente.addCliente(clienteForm))
      }
    )

    Future.successful(result)
  }

  // Vista de agregar un cliente
  def addClienteView = actions.roleAction("interno") { implicit req ⇒
    Future.successful(Ok(views.html.cliente.addCliente(clienteForm)))
  }

  // Detalles de un cliente segun su id, como deben ser vistos por un usuario interno
  def cliente(id: Long) = actions.roleAction("interno") { implicit req ⇒
    val oDatos = itemDao.datosCliente(id)
    Future.successful(
      oDatos.fold {
        implicit val nots =
          Notification.warn(Messages("ClienteController.cliente.noExiste"))
        Ok(views.html.index())
      } { datos: (Cliente, List[Mascota], List[Telefono], List[Item]) ⇒
        val (cliente, mascotas, telefonos, items) = datos
        val saldo = items.foldLeft(BigDecimal(0))((acc, i) ⇒ acc + i.monto)
        Ok(views.html.cliente.cliente(cliente, mascotas, telefonos, saldo, items))
      }
    )
  }

  def addTelfView(idCliente: Long) = actions.roleAction("interno") { implicit req ⇒
    val res = clienteDao.byId(idCliente).fold {
      implicit val nots =
        Notification.warn(Messages("ClienteController.cliente.noExiste"))
      Redirect(routes.ClienteController.clientes())
    } { cliente ⇒
      Ok(views.html.cliente.addTelefono(telefonoForm, cliente))
    }

    Future.successful(res)
  }

  def addTelefono(idCliente: Long) = actions.roleAction("interno") { implicit req ⇒
    val res = telefonoForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        val cliente = clienteDao.unsafeById(idCliente)
        BadRequest(views.html.cliente.addTelefono(formWithErrors, cliente))
      },
      telefono ⇒ {
        clienteDao.addTelf(telefono)
        eventDao.write(s"Telefono agregado al usuario ${telefono.idCliente}")
        implicit val nots =
          Notification.warn(Messages("ClienteController.addTelf.exito"))
        Redirect(routes.ClienteController.cliente(telefono.idCliente))
      }
    )

    Future.successful(res)
  }

  def editNombre = actions.rAction("interno") { implicit req ⇒
    modNombreForm.bindFromRequest.fold(
      formWithErrors ⇒ {
        val msg = formWithErrors.errors.mkString(" ")
        Ok(jsonErr(messagesApi("ClienteController.edit.nombre.formErr")))
      },

      datos ⇒ {
        val (idCliente, nuevoNombre, nuevoApellido) = datos
        clienteDao.byId(idCliente) match {
          case Some(cliente) ⇒ {
            clienteDao.actualizarNombre(idCliente, nuevoNombre, nuevoApellido)
            Ok(jsonOk("nombre" → nuevoNombre, "apellido" → nuevoApellido))
          }
          case None ⇒ {
            Ok(jsonErr(messagesApi("ClienteController.edit.nombre.noCliente")))
          }
        }
      }
    )
  }
}

object ClienteController {
  // Formulario para la informacion de un cliente
  val clienteForm: Form[Cliente] = Form(
    mapping(
      "id" → ignored(0L),
      "nombre" → nonEmptyText,
      "apellido" → nonEmptyText,
      "direccion" → optional(text),
      "email" → optional(email),
      "cuenta" → optional(cuenta)
    )(Cliente.apply)(Cliente.unapply)
  )

  // Formulario para registrar telefonos a un cliente
  val telefonoForm: Form[Telefono] = Form(
    mapping(
      "numero" → nonEmptyText,
      "idCliente" → longNumber
    )(Telefono.apply)(Telefono.unapply)
  )

  val modNombreForm: Form[(Long, String, String)] = Form(
    tuple(
      "idCliente" → longNumber,
      "nombre" → nonEmptyText,
      "apellido" → nonEmptyText
    )
  )
}
