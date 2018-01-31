# Monix + SORM / doobie + ScalaFX

 This is just a simple example to explore some ways to build a traditional 3-layer application.
 
 Keep in mind:
 
 - SORM is not officially compiled for scala 2.12 :(
 - SORM with H2 kinda doesnt work when in the un-forked SBT JVM :(
 - sbt-assembly doesnt work with sbt 1.1.0, fix is coming in 1.1.1
 
 How this works:
 
 0. All dependency injection is done by the MainApplication
 0. The dao is created and with it the database schema
 0. Default values are inserted into the database
 0. The Service depends on the Dao
 0. The UI depends on the Service (which is kinda the data-model here)
 0. The actions of the UI are mapped to changes in the Service/Model
 
 The idea is that:
 
 - the DAO can be changed without touching anything else
 - the UI can be modified, actions can be (re-)wired in the MainApplication
 - the service can be rewritten, additional state can be passed to any UI able to display it
 