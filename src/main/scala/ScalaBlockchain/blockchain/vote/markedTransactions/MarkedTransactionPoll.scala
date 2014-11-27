package ScalaBlockchain.blockchain.vote.markedTransactions

import ScalaBlockchain.blockchain.vote.BlockchainPoll
import ScalaBlockchain.blockchain.address.Address
import ScalaBlockchain.blockchain.api.{BlockchainInfoApi, BlockchainApi}
import ScalaBlockchain.blockchain.transaction.graph.NormalizedTransactionTrustGraph
import ScalaBlockchain.blockchain.transaction.graph.pageRankTrustPropagation.PageRankTrustPropagation

import java.io.File


import org.bitcoinj.core._
import org.bitcoinj.store.MemoryBlockStore
import org.bitcoinj.wallet.WalletTransaction.Pool


/**
 * Created with IntelliJ IDEA.
 * User: tony
 * Date: 9/30/14
 * Time: 8:00 PM
 **/

class SimpleMarkedTransactionPoll(propagatorRegistrationAddressIn: Address, outcomeRegistrationAddressIn: Address) extends BlockchainPoll with MarkedTransactionMultiOutcomePoll with MarkedTransactionPropagatorPoll with NormalizedTransactionTrustGraph with PageRankTrustPropagation {

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

object WalletDemo extends App {

  import org.bitcoinj.core._
  import org.bitcoinj.net.discovery.DnsDiscovery;
  import org.bitcoinj.params.TestNet3Params;
  import org.bitcoinj.store.SPVBlockStore;
  import org.bitcoinj.wallet.DeterministicSeed;
  //just playing with bitcoinj wallet...
  val params = TestNet3Params.get();
  // Bitcoinj supports hierarchical deterministic wallets (or "HD Wallets"): https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki
  // HD wallets allow you to restore your wallet simply from a root seed. This seed can be represented using a short mnemonic sentence as described in BIP 39: https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki
  // Here we restore our wallet from a seed with no passphrase. Also have a look at the BackupToMnemonicSeed.java example that shows how to backup a wallet by creating a mnemonic sentence.
  val seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal";
  val passphrase = "";
  val creationtime = 1409478661L;

  val seed = new DeterministicSeed(seedCode, null, passphrase, creationtime);

  // The wallet class provides a easy fromSeed() function that loads a new wallet from a given seed.
  val wallet = Wallet.fromSeed(params, seed);
  // Because we are importing an existing wallet which might already have transactions we must re-download the blockchain to make the wallet picks up these transactions
  // You can find some information about this in the guides: https://bitcoinj.github.io/working-with-the-wallet#setup
  // To do this we clear the transactions of the wallet and delete a possible existing blockchain file before we download the blockchain again further down.
  println(wallet.toString());
  wallet.clearTransactions(0);

  val chainFile = new File("restore-from-seed.spvchain");
  if (chainFile.exists()) {
    chainFile.delete();
  }
  // Setting up the BlochChain, the BlocksStore and connecting to the network.
  val chainStore = new SPVBlockStore(params, chainFile);
  val chain = new BlockChain(params, chainStore);
  val peers = new PeerGroup(params, chain);
  peers.addPeerDiscovery(new DnsDiscovery(params));
  // Now we need to hook the wallet up to the blockchain and the peers. This registers event listeners that notify our wallet about new transactions.
  chain.addWallet(wallet);
  peers.addWallet(wallet);
  val bListener = new DownloadListener() {
    override def doneDownload() {
      System.out.println("blockchain downloaded");
    }
  };
  // Now we re-download the blockchain. This replays the chain into the wallet. Once this is completed our wallet should know of all its transactions and print the correct balance.
  peers.startAsync();
  peers.awaitRunning();
  peers.startBlockChainDownload(bListener);
  bListener.await();
  // Print a debug message with the details about the wallet. The correct balance should now be displayed.
  System.out.println(wallet.toString());

  implicit val networkParams = wallet.getNetworkParameters
  val a0 = ScalaBlockchain.blockchain.address.Address("mvAp1rCJgMdWMVHHCNg6Fzq919fpC6qDY5")
  val a1 = ScalaBlockchain.blockchain.address.Address.fromBTCJAddress(ScalaBlockchain.blockchain.address.Address.asBTCJAddress(a0))
  println("amatch?: " + a1.toString)

  val p = new SimpleMarkedTransactionPoll(ScalaBlockchain.blockchain.address.Address("mjrt6qyruYXNy3rW5E2ij5Q56CzJ3uYkYk"), ScalaBlockchain.blockchain.address.Address("mywek51jjHJdoSooqfd9ShTfm3iTFYhUb7"))
  val c = new SimpleMarkedTransactionPollClient(p, wallet, peers, Some(ScalaBlockchain.blockchain.address.Address("mgWjSpQJMxNsDTaGD6pUHPWpxb3uKotFg1")))

  c.setTrustDistribution(Map(ScalaBlockchain.blockchain.address.Address("mqo3KvdShS9fLUHsTYKdVU5n23JEc3RytQ") -> 0.5, ScalaBlockchain.blockchain.address.Address("mvdXX1b37wY2wNEdS8rCSsH9sRhM8sPWj2") -> 0.5))
  println(c.myTrustDistribution.mkString("\n"))
  // shutting down again
  peers.stopAsync();
  peers.awaitTerminated();

}

class SimpleMarkedTransactionPollClient(poll: SimpleMarkedTransactionPoll, wallet: Wallet, peerGroup: PeerGroup, nodeAddress:Option[ScalaBlockchain.blockchain.address.Address]) {
  //ugly shit going on in here #ihavenoideawhatimdoing
  //if anyone wants to help drive the snakes out of how I am parsing the blockchain, and creating txs and moving coin around... hilf mir!

  import scala.collection.JavaConverters._

  implicit val networkParams = wallet.getNetworkParameters
  val blockStore = new MemoryBlockStore(networkParams)
  val chain = new BlockChain(networkParams, wallet, blockStore)



  val myOriginNode = {
    //does this wallet have an address registered as a propagator?
    //If so, that will be our node in the graph... if not, lets try to get registered.
    println("finding origin node...")
    val earliestTxToReg = try {
     nodeAddress.get
     val valid = wallet.getTransactionPool(Pool.SPENT).asScala.values
       .find(t=>t.getOutputs.asScala
       .map(_.getScriptPubKey.getToAddress(networkParams).toString)
       .contains(poll.propagatorRegistrationAddress.stringVal))
     .isDefined

     require(valid, "no registered address!!")

      Some(ScalaBlockchain.blockchain.address.Address.asBTCJAddress(nodeAddress.get))
    }
    catch {
      case _ => None
    }


    val o = earliestTxToReg.getOrElse(
    {
      println("no existing transactions to reg. ad...")
      val t = Wallet.SendRequest.to(poll.propagatorRegistrationAddress, Coin.valueOf(10000l))

      val res = wallet.sendCoins(peerGroup, t)
      println("broadcasted origin init.")
      while (!res.broadcastComplete.isDone) {
        Thread.sleep(1000)
        println(res.tx)
      }
      res.tx.getInputs.get(0).getFromAddress
    })

    println("my origin node: " + o)
    o
  }

  def originSpendableOutputs = (try {
    val ts = wallet.getTransactions(false).asScala
    ts.map("transact: " + println(_))
    ts
  }
  catch {
    case _ => Set.empty
  }).flatMap(t => t.getOutputs.asScala.filter(o => o.isAvailableForSpending && o.getSpentBy.getFromAddress.toString == myOriginNode.toString))

  def myTrustDistribution = poll.outcome(Map(Address(myOriginNode.toString) -> 1.0))

  def setTrustDistribution(dist: Map[Address, Double]) = {
    val t = new Transaction(wallet.getParams)

    t.clearInputs()
    originSpendableOutputs.foreach(o => t.addInput(o))

    val distNorm = {
      val normFactor = dist.map(_._2).sum
      val s1 = dist.mapValues(_ / normFactor)
      val min = s1.minBy(_._2)._2
      s1.mapValues(_ / min)
    }

    //add outputs establishing the distribution...
    distNorm.foreach {
      o =>
        val target = new org.bitcoinj.core.Address(wallet.getParams(), o._1.stringVal)
        t.addOutput(Coin.valueOf(o._2.toLong * 10000), target)
    }

    //include the registration address which denotes these as propagation addresses
    val propagationRegistrationAddress = new org.bitcoinj.core.Address(wallet.getParams(), poll.propagatorRegistrationAddress.stringVal)
    t.addOutput(Coin.valueOf(10000l), propagationRegistrationAddress)

    //execute the tx
    val request = Wallet.SendRequest.forTx(t)

    request.changeAddress = myOriginNode

    wallet.completeTx(request)
    wallet.commitTx(request.tx)

    peerGroup.broadcastTransaction(request.tx).get()

  }


}