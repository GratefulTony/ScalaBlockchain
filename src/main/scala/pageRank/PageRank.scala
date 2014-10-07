package pageRank

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 10/5/14
 * Time: 10:18 PM
 */

import com.signalcollect._


object PageRankDemo extends App {

  //we have a popularity contest between mike jane bob and alex.
  val mike = "mike"
  val jane = "jane"
  val bob = "bob"
  val alex = "alex"

  val graphBuilder = Map.newBuilder[(String, String), Double]

  //they have some opinions about eachother...
  //mike likes bob and alex.
  graphBuilder += (mike, bob) -> 0.5
  graphBuilder += (mike, alex) -> 0.5

  //alex sort of likes bob
  graphBuilder += (alex, bob) -> 0.1
  //and really likes jane
  graphBuilder += (alex, jane) -> 0.9

  //jane only likes alex...
  graphBuilder += (jane, alex) -> 1.0

  //who is the most popular?

  PageRank.calculate(graphBuilder.result(), (b: String) => 0.1).map(println(_))

}

object PageRank {
  def calculate[T](edges: Map[(T, T), Double], baserank: (T) => Double = (i: T) => 0.15) = {
    // id: (T) => String
    val graph = GraphBuilder.build


    val vertexIds = edges.keys.flatMap(k => Seq(k._1, k._2)).toSet
    val vertices = vertexIds.map(v => new PageRankVertex[T](v, baserank(v)))

    vertices.foreach(v => graph.addVertex(v))

    edges.foreach(e => graph.addEdge(e._1._1, new PageRankEdge(e._1._2, e._2)))

    graph.execute

    val o = vertexIds.map {
      v =>
        (v, graph.forVertexWithId(v, (z: PageRankVertex[T]) => z.state))
    }.toMap

    graph.shutdown

    o
  }

}