package ScalaBlockchain.blockchain.api

import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.transaction.{TransactionInput, TransactionOutput, Transaction}

import org.json4s.native.JsonMethods._
import scalaj.http.{HttpOptions, Http}

/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/13/14
 * Time: 9:11 PM
 * To change this template use File | Settings | File Templates.
 */

object BlockchainAPIDemo extends App {

  import BlockchainInfoApi._

  println(getTransactions(Address("15JvRK9BJHUtqxDyrihxphX1e8e4fdjb4o")).map(a=>(a.result(Address("15JvRK9BJHUtqxDyrihxphX1e8e4fdjb4o")))).sum)
}

object BlockchainInfoApi extends BlockchainApi {
  //  http://blockchain.info/rawaddr/15JvRK9BJHUtqxDyrihxphX1e8e4fdjb4o
  def getTransactions(a: Address): Seq[Transaction] = {
    val res = Http.get( """http://blockchain.info/rawaddr/""" + a.stringVal).option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(5000)).asString
    parse(res).\("txs").children.toArray.map {
      c =>
        val ins = c.\("inputs").children.toArray.map {
          cc =>
            val addr = cc.\\("addr")
            val qty = cc.\\("value")
            TransactionInput(Address(addr.values.toString), qty.values.toString.toDouble)
        }
        val outs = c.\("out").children.toArray.map {
          cc =>
            val addr = cc.\\("addr")
            val qty = cc.\\("value")
            TransactionOutput(Address(addr.values.toString), qty.values.toString.toDouble)
        }
        val bh =  c.\("block_height").values.toString.toLong
        Transaction(ins, outs, bh)
    }
  }
}
