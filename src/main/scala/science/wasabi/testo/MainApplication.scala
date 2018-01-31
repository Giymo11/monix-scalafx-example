package science.wasabi.testo


import monix.execution.Scheduler.Implicits.global

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

import science.wasabi.testo.dao.DoobieCustomerDao
import science.wasabi.testo.dto.Customer
import science.wasabi.testo.gui.CustomerUi
import science.wasabi.testo.service.CustomerService


object MainApplication extends JFXApp {

  val dao = new DoobieCustomerDao()

  private val customers = List(
    Customer(0, "Jack", "Bauer"),
    Customer(0, "Chloe", "O'Brian"),
    Customer(0, "Kim", "Bauer"),
    Customer(0, "David", "Palmer"),
    Customer(0, "Michelle", "Dessler")
  )
  val res = dao.insertCustomers(customers)
  println(res)

  lazy val service: CustomerService = new CustomerService(dao)

  lazy val ui: CustomerUi = new CustomerUi(service.currentCustomerOb)
  ui.customerButtonPressedOb.foreach(_ => service.rerollCurrentCustomer())

  stage = new PrimaryStage {
    title = "Hello World"
    width = 800
    height = 600
    scene = new Scene {
      root = ui.helloworld
    }
  }
}