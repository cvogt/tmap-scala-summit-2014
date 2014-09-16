import org.cvogt.di.reflect._
import org.cvogt.di._
class Database(dsn: String){
  def query(sql: String) = "(some query result)"
}
class Logger{
  def log(msg: String) = println("Logging: "+msg)
}

object ProdConfig{
  val logger = new Logger
  val database = new Database("jdbc:/....")
  val context = TMap(logger) ++ TMap(database)
}
object TestConfig{
  val logger = new Logger
  val database = new Database("jdbc:/....")
  val context = TMap(logger) ++ TMap(database)
}
object lib{
  implicit class ComposableFunctions[T,R](f: T => R){
    def map[Q](g: R => Q) = (t: T) => g(f(t))
    def flatMap[Q,S](g: R => (S => Q)) = {
      (st: T with S) => g(f(st))(st)
    }
  }
  def Implicit[V:TTKey] = (c: TMap[V]) => c[V]
}

import lib._
object DAO{
  def getBlogPosts = for{
    logger <- Implicit[Logger].map(_.log("fetching blogposts"))
    res    <- Implicit[Database].map(_.query("SELECT * FROM POSTS"))
  } yield res

  def getTags = Implicit[Database].map(_.query("SELECT * FROM TAGS"))

  def renderBlog = {
    // see, no explicit dependencies here, just "magically" propagated
    for{
      tags <- getTags
      posts <- getBlogPosts
    } yield posts + " " + tags
  }
}

object MyApp extends App{
  println(
    DAO.renderBlog(ProdConfig.context)
  )
}

object MyAppTest{
  println(
    DAO.renderBlog(TestConfig.context)
  )
}
