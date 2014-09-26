package ScalaBlockchain.blockchain.transaction

import ScalaBlockchain.blockchain.address.Address

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:02 PM
 * To change this template use File | Settings | File Templates.
 */
case class TransactionOutput(destinationAddress: Address,
                             value: Long)

case class TransactionInput(fromAddress: Address,
                            value: Long)

case class Transaction(inputs: Seq[TransactionInput],
                       outputs: Seq[TransactionOutput],
                       blockHeight: Long)    {
  def result(address:Address) = outputs.filter(a=>a.destinationAddress == address).map(_.value).sum - inputs.filter(a=>a.fromAddress == address).map(_.value).sum
}



