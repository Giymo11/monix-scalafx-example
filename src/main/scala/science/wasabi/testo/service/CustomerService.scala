package science.wasabi.testo.service


import scala.util.Random

import monix.reactive.Observable
import monix.reactive.subjects.ConcurrentSubject
import monix.execution.Scheduler.Implicits.global

import science.wasabi.testo.dao.CustomerDao
import science.wasabi.testo.dto.Customer


class CustomerService(dao: CustomerDao) {

  private def customers = dao.findAllCustomers()

  private def chooseRandom[T](seq: Seq[T]) = seq(Random.nextInt(seq.size))

  private val currentCustomerSub = ConcurrentSubject.publish[Customer]
  val currentCustomerOb: Observable[Customer] = currentCustomerSub

  def rerollCurrentCustomer(): Unit = currentCustomerSub.onNext(chooseRandom(customers))
}
