package ScalaBlockchain.blockchain.api

import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.transaction.Transaction

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:14 PM
 */
trait BlockchainApi {
  def getTransactions(a:Address):Seq[Transaction]
}
