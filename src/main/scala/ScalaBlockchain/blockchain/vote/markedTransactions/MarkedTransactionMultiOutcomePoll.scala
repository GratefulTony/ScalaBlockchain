package ScalaBlockchain.blockchain.vote.markedTransactions

import ScalaBlockchain.blockchain.vote.BlockchainPoll
import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.transaction.Transaction

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/30/14
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
trait MarkedTransactionMultiOutcomePoll extends BlockchainPoll {

  //every address which sends a satoshi to this address is a valid outcome. By sending satoshis, these addresses become "marked"
  def outcomeRegistrationAddress: Address

  private def outcomeAddresses: Set[Address] = {
    blockchainApi.getTransactions(outcomeRegistrationAddress)
      .filter(t => t.outputs.map(_.destinationAddress).contains(outcomeRegistrationAddress)) //TODO: verify this "contains" works as expected!
      .flatMap(_.inputs.map(_.fromAddress)).toSet
  }

  override def validOutcomeAddress(a: Address): Boolean = outcomeAddresses.contains(a)

  override def outcomePreferenceTransactionsOutgoingFrom(a: Address) = {
    blockchainApi.getTransactions(a)
      .filter(validOutcomeSelectionTransaction)
      .flatMap(t => t.outputs)
      .filterNot(to => to.destinationAddress == a) //all addresses trust themselves already.
      .filterNot(to => to.destinationAddress == outcomeRegistrationAddress)
      .toSet
  }

  override def validOutcomeSelectionTransaction(t: Transaction) = {
    t.outputs.map(_.destinationAddress).contains(outcomeRegistrationAddress)
  }

  override def allOutcomes = outcomeAddresses

}
