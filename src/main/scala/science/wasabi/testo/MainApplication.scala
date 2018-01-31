package science.wasabi.testo


import monix.execution.Scheduler.Implicits.global

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

import science.wasabi.testo.dao._
import science.wasabi.testo.dto.Customer
import science.wasabi.testo.gui.CustomerUi
import science.wasabi.testo.service.CustomerService


object MainApplication extends JFXApp {

  val dao = new SormCustomerDao()

  private val customers = List(
    Customer("Jack", "Bauer"),
    Customer("Chloe", "O'Brian"),
    Customer("Kim", "Bauer"),
    Customer("David", "Palmer"),
    Customer("Michelle", "Dessler")
  )
  private val res = dao.insertCustomers(customers)
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