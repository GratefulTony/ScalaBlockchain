package ScalaBlockchain.blockchain.vote.markov

import ScalaBlockchain.blockchain.vote.BlockchainPoll
import ScalaBlockchain.blockchain.address.Address

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/30/14
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
//class MarkovTrustGraph extends BlockchainPoll {
////
////  def n: Int //number of iterations
////
////  private def normalizedValidOutputs(a: Address) = {
////    val outputs = propagationTransactionsOutgoingFrom(a)
////    val unnorm = outputs.groupBy(o => o.destinationAddress).mapValues(o => o.map(_.value).sum)
////    val k = unnorm.map(v => v._2).sum
////    unnorm.mapValues(v => v.toDouble / k.toDouble)
////  }
////
////  private def rawTrustGraph: Map[(Address, Address), Double] = {
////    allPropagators.map(p => normalizedValidOutputs(p).toSeq.map(e => ((p, e._1), e._2)))
////      .flatten
////      .toMap
////  }
////
////  override def propagatedTrust(truster: Address, trustee: Address): Double = {
////    for(i<-0 until n){
////
////    }
////  }
////
////  //}
//
//}
