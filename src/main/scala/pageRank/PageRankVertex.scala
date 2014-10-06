package pageRank

import com.signalcollect.{DefaultEdge, DataGraphVertex}

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 10/5/14
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
class PageRankVertex(id: String, baseRank: Double = 0.15)
  extends DataGraphVertex(id, baseRank) {
  type Signal = Double
  def dampingFactor = 1 - baseRank
  def collect = baseRank + dampingFactor * signals.sum

}

class PageRankEdge(targetId: String, weightIn:Double = 1.0)
  extends DefaultEdge(targetId) {
  type Source = PageRankVertex
  def signal = source.state * weight / source.sumOfOutWeights
  override def weight = weightIn
}