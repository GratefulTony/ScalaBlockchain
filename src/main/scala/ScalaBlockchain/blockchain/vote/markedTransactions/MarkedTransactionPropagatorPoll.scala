package ScalaBlockchain.blockchain.vote.markedTransactions

import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.vote.BlockchainPoll
import ScalaBlockchain.blockchain.transaction.Transaction

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/30/14
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
trait MarkedTransactionPropagatorPoll extends BlockchainPoll {
  //every address which sends a satoshi to this address is a valid propagator. By sending satoshis, these addresses become "marked"
  def propagatorRegistrationAddress: Address

  private def propagationAddresses: Set[Address] = {
    blockchainApi.getTransactions(propagatorRegistrationAddress)
      .filter(t => t.outputs.map(_.destinationAddress).contains(propagatorRegistrationAddress))
      .flatMap(_.inputs.map(_.fromAddress)).toSet
  }

  override def validPropagationAddress(a: Address): Boolean = propagationAddresses.contains(a)

  override def propagationTransactionsOutgoingFrom(a: Address) = {
    blockchainApi.getTransactions(a)
      .filter(validPropagationTransaction)
      .flatMap(t => t.outputs)
      .filterNot(to => to.destinationAddress == a) //all addresses trust themselves already.
      .filterNot(to => to.destinationAddress == propagatorRegistrationAddress)
      .toSet
  }

  override def validPropagationTransaction(t: Transaction) = {
    t.outputs.map(_.destinationAddress).contains(propagatorRegistrationAddress)
  }

  override def allPropagators = propagationAddresses
}
