package ScalaBlockchain.blockchain.transaction.graph

import ScalaBlockchain.blockchain.address._
import ScalaBlockchain.blockchain.transaction.TransactionOutput
import ScalaBlockchain.blockchain.api.BlockchainApi

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/21/14
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */


trait TransactionGraph {

  //is this transaction edge included in the propagation graph?
  def isValidPropagationEdge: (TransactionOutput) => Boolean

  //is this a
  def isValidTerminalEdge: (TransactionOutput) => Boolean

  def findOutgoingEdgesFrom(a: Address): Seq[TransactionOutput] = {
    blockchain.getTransactions(a).map(t => t.outputs).flatten
  }

  def getPathWeightBetween(a1: Address, a2: Address): Double

  def getNeighborOutputsDistribution(a: Address): Map[Address, Double] = {
    //we normalize outputs by address
    val byDestination = findOutgoingEdgesFrom(a).filter(isValidPropagationEdge).groupBy(edge => edge.destinationAddress)
    val totalOutputs = byDestination.mapValues {
      byDest => byDest.map(txOut => txOut.value).sum
    }
    val totalOutputsNormFactor = totalOutputs.map(_._2).sum
    val normalizedTotalOutputs = totalOutputs.mapValues(weight => weight.toDouble / totalOutputsNormFactor.toDouble)

    normalizedTotalOutputs
  }

  def getPropagatedOutputsDistribution(a: Address): Map[Address, Double]

  def blockchain: BlockchainApi
}


trait PagerankRelationshipQuantization extends TransactionGraph {
//  https://uzh.github.io/signal-collect/
//  override def getPropagatedOutputsDistribution(a: Address): Map[Address, Double] = {
//
//
//
//    def getProppedOutputsDistributionRecurse(a: Address, visited:Set[Address]):Map[Address, Double] = {
//      if (!visited.contains(a)) {
//        getNeighborOutputsDistribution(a)
//      }
//    }
//
//    getProppedOutputsDistributionRecurse(a, Seq.empty)
//  }


}