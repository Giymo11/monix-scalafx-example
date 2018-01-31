package science.wasabi.testo.gui


import monix.execution.Scheduler.Implicits.global
import monix.reactive.Observable
import monix.reactive.subjects.ConcurrentSubject

import scalafx.application.Platform
import scalafx.geometry.Pos
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.GridPane

import science.wasabi.testo.dto.Customer


class ButtonPressedEvent

class CustomerUi(val currentCustomer: Observable[Customer]) {

  private val label = new Label()
  currentCustomer.foreach(customer => Platform.runLater(() =>
    label.text = s"Hello ${customer.firstname} ${customer.lastname}")
  )

  private val customerButtonPressedSub = ConcurrentSubject.publish[ButtonPressedEvent]
  val customerButtonPressedOb: Observable[ButtonPressedEvent] = customerButtonPressedSub

  private val button = new Button {
    text = "Show Customer"
    onAction = (event) => customerButtonPressedSub.onNext(new ButtonPressedEvent())
  }

  val helloworld = new GridPane {
    alignment = Pos.Center
    hgap = 10
    vgap = 10
    add(button, 0, 0)
    add(label, 0, 1)
  }
}
