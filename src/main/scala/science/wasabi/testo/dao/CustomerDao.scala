package science.wasabi.testo.dao


import science.wasabi.testo.dto.Customer


trait CustomerDao {
  def insertCustomers(customers: List[Customer]): List[Customer]
  def findAllCustomers(): List[Customer]
}



class SormCustomerDao extends CustomerDao {

  import sorm._


  object Db extends Instance(
    entities = Set(
      Entity[Customer]()
    ),
    url = "jdbc:h2:mem:testo1"
  )


  override def insertCustomers(customers: List[Customer]): List[Customer] = customers.map(c => Db.save(c))

  override def findAllCustomers(): List[Customer] = Db.query[Customer].fetch().toList
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


  override def insertCustomers(customers: List[Customer]): List[Customer] = {
    def customerToDescription(customer: Customer) = (customer.firstname, customer.lastname)
    val customerDescriptions = customers map customerToDescription

    // this is an FS2 stream
    val streamWithEffects = Update[(String, String)]("insert into customer (firstname, lastname) values (?, ?)")
      .updateManyWithGeneratedKeys[Int]("id")(customerDescriptions)

    // here it is actually run
    val results = streamWithEffects.compile.toList.transact(xa).unsafeRunSync()

    //for ((newId, oldCustomer) <- results zip customers) yield oldCustomer.copy(id = newId)
    customers
  }

  override def findAllCustomers(): List[Customer] = sql"select * from customer"
    .query[Customer]
    .list
    .transact(xa)
    .unsafeRunSync()
}
