package ScalaBlockchain.blockchain.transaction

import ScalaBlockchain.blockchain.address.Address

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
case class TransactionOutput(toAddress: Address,
                             value: Double)

case class TransactionInput(fromAddress: Address,
                            value: Double)

case class Transaction(inputs: Seq[TransactionInput],
                       outputs: Seq[TransactionOutput],
                       blockHeight: Long)    {
  def result(address:Address) = outputs.filter(a=>a.toAddress == address).map(_.value).sum - inputs.filter(a=>a.fromAddress == address).map(_.value).sum
}



