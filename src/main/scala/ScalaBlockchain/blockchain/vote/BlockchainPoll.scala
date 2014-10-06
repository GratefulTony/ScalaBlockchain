package ScalaBlockchain.blockchain.vote

import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.transaction.{TransactionOutput, Transaction}
import ScalaBlockchain.blockchain.api.BlockchainApi
import scala.concurrent.Future

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/30/14
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */

trait BlockchainPoll {

  //just some string which says some stuff about this poll
  val description: String

  //this function will tell us if a given transaction is allowed to be a propagator for the poll
  def validPropagationTransaction(t: Transaction): Boolean

  //this function will tell us if a given address is allowed to be a propagation node for the poll
  def validPropagationAddress(a: Address): Boolean

  //all valid propagation nodes.
  def allPropagators: Set[Address]

  //this function determines whether a transaction can specify a preference between a propagation node and an outcome
  def validOutcomeSelectionTransaction(t: Transaction)   :Boolean

  //this function tells us if a particular address is a valid "choice" in the poll.
  def validOutcomeAddress(a: Address): Boolean

  //the set of all outcomes
  def allOutcomes: Set[Address]

  //this helps us determine how much a truster trusts a trustee-- essentially trust propagation.
  def propagatedTrust(truster: Address, trustee: Address):Double

  //maybe we want to be able to attach some notes to addresses...
  def comments(a: Address): String

  //this is the set of addresses which seed the network with trust to propagate...
  def trustSourceAddresses: Set[Address]

  //all of the valid propagation transactions outgoing from a particular address
  def propagationTransactionsOutgoingFrom(a: Address): Set[TransactionOutput]

  //all of the transactions indicating preference for a valid outcome
  def outcomePreferenceTransactionsOutgoingFrom(a: Address): Set[TransactionOutput]

  //the distribution of preference for each outcome expressed by the network.
  def outcome: Map[Address, Double]

  //the trust graph. [(trust source, trust destination), trust magnitude] raw preferences from key node, not propagated.
  def trustGraph: Map[Address, (Address, Double)]

  //a place to get blockchain data
  def blockchainApi: BlockchainApi
}

//trait ExhaustiveMemberDiscovery extends BlockchainPoll {
//  override def allPropagators: Set[Address] = {
//
//    def explore(a: Address, explored: Set[Address]): Set[Address] = {
//      if (validPropagationAddress(a)) {
//        val trustees = propagationTransactionsOutgoingFrom(a)
//          .flatMap(t => t.outputs.map(_.destinationAddress))
//          .filter(validPropagationAddress)
//
//        val newlyExplored = trustees.toSet -- explored - a
//        val childrenOfNewlyExplored = newlyExplored.flatMap(explore(_, explored ++ newlyExplored + a))
//
//        explored ++ trustees ++ childrenOfNewlyExplored + a
//      }
//      else Set.empty
//    }
//
//    trustSourceAddresses.flatMap(explore(_, Set.empty))
//  }
//}
//
