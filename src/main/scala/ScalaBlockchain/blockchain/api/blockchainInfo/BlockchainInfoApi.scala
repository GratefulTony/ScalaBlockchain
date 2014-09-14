package ScalaBlockchain.blockchain.api

import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.transaction.Transaction

import org.json4s.native.JsonMethods._
import scalaj.http.{HttpOptions, Http}

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */

object BlockchainInfoApi extends BlockchainApi{
  def getOutgoingTransactions(a:Address):Seq[Transaction] = {
    val res = Http.get( """http://blockchain.info/rawaddr/""" + in).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).asString
    parse(res).\("txs").children.toArray.map {
      c =>
        val fs = c.\("out").children.toArray

        fs.map(cc =>

          Transaction(
            c.toString,
            cc.\("addr").toString,
            cc.\("value").values.toString.toDouble)
        )

    }.toSeq.flatten.toSeq
  }
}
