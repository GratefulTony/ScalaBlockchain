package ScalaBlockchain.blockchain.transaction

import ScalaBlockchain.blockchain.address.Address

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
trait TransactionOutput {
  def toAddress: Address

  def value: Double
}

trait TransactionInput {
  def fromAddress: Address

  def value: Double
}

trait Transaction {
  def inputs: Seq[TransactionInput]

  def outputs: Seq[TransactionOutput]
}


