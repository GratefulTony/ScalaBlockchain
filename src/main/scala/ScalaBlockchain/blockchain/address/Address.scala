package ScalaBlockchain.blockchain.address

import org.bitcoinj.core
import org.bitcoinj.core.NetworkParameters

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:08 PM
 * To change this template use File | Settings | File Templates.
 */


object Address{
  implicit def asBTCJAddress(a:Address)(implicit networkParameters: NetworkParameters) = {
     new org.bitcoinj.core.Address(core.Address.getParametersFromAddress(a.stringVal), a.stringVal)

  }
  implicit def fromBTCJAddress(inn: org.bitcoinj.core.Address) = {
    Address(inn.toString)
  }

}
case class Address(stringVal: String) {
  def ==(o: Address) = stringVal == o.stringVal
}
