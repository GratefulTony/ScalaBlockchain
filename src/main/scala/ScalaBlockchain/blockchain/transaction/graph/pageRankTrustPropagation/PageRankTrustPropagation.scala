package ScalaBlockchain.blockchain.transaction.graph.pageRankTrustPropagation


import ScalaBlockchain.blockchain.address.Address
import pageRank.PageRank
import ScalaBlockchain.blockchain.vote.BlockchainPoll

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 10/6/14
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
trait PageRankTrustPropagation extends BlockchainPoll {
  override def outcome: Map[Address, Double] = {
    val initialMass = trustSourceAddresses.size.toDouble
    val unnorm = PageRank.calculate(trustGraph, (a: Address) => if (this.trustSourceAddresses.contains(a)) initialMass else 0.0)
    val normFactor = unnorm.map(_._2).sum
    unnorm.mapValues(_ / normFactor)
  }
}
