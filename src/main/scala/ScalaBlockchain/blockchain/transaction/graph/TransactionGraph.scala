package ScalaBlockchain.blockchain.transaction.graph

import ScalaBlockchain.blockchain.address._
import ScalaBlockchain.blockchain.transaction.TransactionOutput
import ScalaBlockchain.blockchain.api.BlockchainApi
import ScalaBlockchain.blockchain.vote.BlockchainPoll

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/21/14
 * Time: 5:18 PM
 */


trait NormalizedTransactionTrustGraph extends BlockchainPoll{

  def getNeighborOutputsDistribution(a: Address): Map[(Address, Address), Double] = {
    //we normalize outputs by address
    val byDestination = propagationTransactionsOutgoingFrom(a).groupBy(edge => edge.destinationAddress)
    val totalOutputs = byDestination.mapValues {
      byDest => byDest.map(txOut => txOut.value).sum
    }
    val totalOutputsNormFactor = totalOutputs.map(_._2).sum
    val normalizedTotalOutputs = totalOutputs.mapValues(weight => weight.toDouble / totalOutputsNormFactor.toDouble)

    normalizedTotalOutputs.map(no=>(a,no._1)->no._2)
  }

  override def trustGraph:Map[(Address,Address),Double] =
    allPropagators.flatMap{p=>getNeighborOutputsDistribution(p)}.toMap
}
