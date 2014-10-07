package ScalaBlockchain.blockchain.vote.markedTransactions

import ScalaBlockchain.blockchain.vote.BlockchainPoll
import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.api.{BlockchainInfoApi, BlockchainApi}
import scala.concurrent.Future


/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/30/14
 * Time: 8:00 PM
 */

abstract class SimpleMarkedTransactionPoll(propagatorRegistrationAddressIn: Address, outcomeRegistrationAddressIn: Address) extends BlockchainPoll with MarkedTransactionMultiOutcomePoll with MarkedTransactionPropagatorPoll {
  val description: String = "this poll is a free for all. Anyone can register his or herself as a node, and any address can be registered as a valid outcome"

  //maybe we want to be able to attach some notes to addresses...
  def comments(a: Address): String = "this must be some sort of address"

  //this is the set of addresses which seed the network with trust to propagate...
  def trustSourceAddresses: Set[Address] = allPropagators

  //a place to get blockchain data
  def blockchainApi: BlockchainApi = BlockchainInfoApi

  //every address which sends a satoshi to this address is a valid propagator. By sending satoshis, these addresses become "marked"
  def propagatorRegistrationAddress: Address = propagatorRegistrationAddressIn

  //every address which sends a satoshi to this address is a valid outcome. By sending satoshis, these addresses become "marked"
  def outcomeRegistrationAddress: Address = outcomeRegistrationAddressIn



}
