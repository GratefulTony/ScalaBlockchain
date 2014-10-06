package pageRank

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 10/5/14
 * Time: 10:18 PM
 * To change this template use File | Settings | File Templates.
 */

import com.signalcollect._


object PageRankDemo extends App {

  //we have a popularity contest between mike jane bob and alex.
  val mike = "mike"
  val jane = "jane"
  val bob = "bob"
  val alex = "alex"

  val graphBuilder = Map.newBuilder[(String, String), Double]

  //they have some opiniona about eachother...
  //mike likes bob and alex.
  graphBuilder += (mike, bob) -> 0.5
  graphBuilder += (mike, alex) -> 0.5

  //alex sort of likes bob
  graphBuilder += (alex, bob) -> 0.1
  //and really likes jane
  graphBuilder += (alex,jane) -> 0.8

  //jane only likes alex...
  graphBuilder += (jane,alex) -> 1.0

  //who is the most popular?

  PageRank.calculate(graphBuilder.result(), (s:String)=>s)

}

object PageRank {
  def calculate[T](edges: Map[(T, T), Double], id: (T) => String) = {
    val graph = GraphBuilder.build

    val vertices = edges.keys.flatMap(k => Seq(k._1, k._2)).toSet
    vertices.foreach(v => graph.addVertex(new PageRankVertex(id(v))))

    edges.foreach(e => graph.addEdge(id(e._1._1), new PageRankEdge(id(e._1._2), e._2)))

    graph.execute
    graph.foreachVertex(println(_))
    graph.shutdown
  }

}