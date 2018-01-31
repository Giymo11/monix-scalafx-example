package science.wasabi.testo.dao


import science.wasabi.testo.dto.Customer


trait CustomerDao {
  def insertCustomers(customers: List[Customer]): List[Customer]
  def findAllCustomers(): List[Customer]
}


class DoobieCustomerDao extends CustomerDao {

  import cats.effect._
  import cats.implicits._

  import doobie.h2._
  import doobie.h2.implicits._
  import doobie.implicits._
  import doobie.free.connection.ConnectionIO
  import doobie.util.update.Update


  private val drop = sql"""
    DROP TABLE IF EXISTS customer
  """.update

  private val create = sql"""
    CREATE TABLE customer (
      id        BIGINT AUTO_INCREMENT,
      firstname VARCHAR NOT NULL,
      lastname  VARCHAR NOT NULL
    )
  """.update

  private val setup = for {
    xa <- H2Transactor.newH2Transactor[IO]("jdbc:h2:mem:testo;DB_CLOSE_DELAY=-1", "sa", "")
    _  <- xa.setMaxConnections(10)
    _  <- (drop.run *> create.run).transact(xa)
  } yield xa

  private val xa = setup.unsafeRunSync()



  def insert2_H2(firstname: String, lastname: String): ConnectionIO[Customer] =
    for {
      id <- sql"insert into customer (firstname, lastname) values ($firstname, $lastname)"
        .update
        .withUniqueGeneratedKeys[Int]("id")
      c  <- sql"select id, firstname, lastname from customer where id = $id"
        .query[Customer]
        .unique
    } yield c

  override def insertCustomers(customers: List[Customer]): List[Customer] = {
    def customerToDescription(customer: Customer) = (customer.firstname, customer.lastname)
    val customerDescriptions = customers map customerToDescription

    // this is an FS2 stream
    val streamWithEffects = Update[(String, String)]("insert into customer (firstname, lastname) values (?, ?)")
      .updateManyWithGeneratedKeys[Int]("id")(customerDescriptions)

    // here it is actually run
    val results = streamWithEffects.compile.toList.transact(xa).unsafeRunSync()

    for ((newId, oldCustomer) <- results zip customers) yield oldCustomer.copy(id = newId)
  }

  override def findAllCustomers(): List[Customer] = sql"select * from customer"
    .query[Customer]
    .list
    .transact(xa)
    .unsafeRunSync()

}
