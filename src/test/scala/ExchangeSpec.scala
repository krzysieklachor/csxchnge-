import org.specs2.Specification

class ExchangeSpec extends Specification {

  def is = s2""" 
  
  This is a specification for an exchange system which matches orders on stocks. 
  
  The exchange should match order with the same price. $e1 
  For a new sell order, match with order with the highest price. $e2 
  For a new buy order, match with order with the lowest price. $e3
  Executed orders are removed from the set of open orders, $e11
  and added to the set of executed orders. $e16
  Unmatched orders are added to the set of open orders. $e12
  
  
  Two orders should match $e4 
  if they have oposing directions, $e7 
  matching RICs, $e5 
  matching quantities, $e9 
  and sell price less than $e6 
  or equal to buy price. $e10 
  
  Open interest is the total quantity of all open orders for the given RIC and the direction at each price point: 
  multiple price points $e13 
  single price point    $e14
  
  Provide the average execution price for a given RIC
  single exec order $e15
  multiple exec orders $e17
  
  Provide executed quantity for a given RIC and user.
  Executed quantity is the sum of quantities of executed orders for the given RIC and user. $e18
  The quantity of sell orders should be negated. $e19
  
  
  """
  final val Ric1 = "VOD.L"
  final val Usr1 = "User1"
  final val Usr2 = "User2"
  final val QtyOne = 1

  def e4 = {
    //    val exch = new Exchange
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr2)
    Exchange.matchOrder(o1, o2) must beTrue
  }

  def e7 = {
    //    val exch = new Exchange
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Buy, Ric1, QtyOne, 1.0, Usr2)
    Exchange.matchOrder(o1, o2) must beFalse
  }

  def e5 = {
    //    val exch = new Exchange
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Sell, "ricOther", QtyOne, 1.0, Usr2)
    Exchange.matchOrder(o1, o2) must beFalse
  }

  def e9 = {
    //    val exch = new Exchange
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, 2, 1.0, Usr2)
    Exchange.matchOrder(o1, o2) must beFalse
  }

  def e6 = {
    //    val exch = new Exchange
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 2.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr1)
    Exchange.matchOrder(o1, o2) must beTrue
  }

  def e10 = {
    //    val exch = new Exchange
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 2.0, Usr1)
    Exchange.matchOrder(o1, o2) must beFalse
  }

  def e1 = {
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val exch = new Stock(List(o1))
    val o = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr1)
    exch.findMatch(o) must beEqualTo(Some(o1))
  }

  def e2 = {
    val o1 = Order(1, Direction.Buy, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Buy, Ric1, QtyOne, 3.0, Usr2)
    val o3 = Order(3, Direction.Buy, Ric1, QtyOne, 2.0, Usr2)
    val exch = new Stock(List(o1, o2, o3))
    val o = Order(4, Direction.Sell, Ric1, QtyOne, 1.0, Usr1)
    val v = exch.findMatch(o)
    v must beEqualTo(Some(o2))
  }

  def e3 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr2)
    val o3 = Order(3, Direction.Sell, Ric1, QtyOne, 2.0, Usr2)
    val exch = new Stock(List(o1, o2, o3))
    val o = Order(4, Direction.Buy, Ric1, QtyOne, 3.0, Usr1)
    exch.findMatch(o) must beEqualTo(Some(o2))
  }

  def e11 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr1)
    val o3 = Order(3, Direction.Sell, Ric1, QtyOne, 2.0, Usr1)
    val exch = new Stock(List(o1, o2, o3))
    val o = Order(4, Direction.Buy, Ric1, QtyOne, 3.0, Usr1)
    val newStock = Exchange.addOrder(exch, o)
    newStock.openOrders must beEqualTo(List(o1, o3))
  }

  def e16 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, "otherRic", QtyOne, 1.0, Usr1)
    val o3 = Order(3, Direction.Sell, "otherRic", QtyOne, 2.0, Usr1)
    val exch = new Stock(List(o1, o2, o3))
    val o = Order(4, Direction.Buy, Ric1, QtyOne, 3.0, Usr1)
    val newStock = Exchange.addOrder(exch, o)
    newStock.executedOrders must beEqualTo(List(o1, o))
  }

  def e12 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr2)
    val o3 = Order(3, Direction.Sell, Ric1, QtyOne, 2.0, Usr2)
    val exch = new Stock(List(o1, o2, o3))
    val o = Order(4, Direction.Buy, "ricOther", QtyOne, 3.0, Usr1)
    val newStock = Exchange.addOrder(exch, o)
    newStock.openOrders must beEqualTo(List(o, o1, o2, o3))
  }

  def e13 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr2)
    val exch = new Stock(List(o1, o2))
    exch.openInterest(Ric1, Direction.Sell) must beEqualTo(Map((3.0, QtyOne), (1.0, QtyOne)))
  }

  def e14 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 1.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, QtyOne, 1.0, Usr2)
    val exch = new Stock(List(o1, o2))
    exch.openInterest(Ric1, Direction.Sell) must beEqualTo(Map(BigDecimal.valueOf(1.0) -> 2))
  }

  def e15 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val exch = new Stock(List(o1))
    val o = Order(2, Direction.Buy, Ric1, QtyOne, 3.0, Usr1)
    val newStock = Exchange.addOrder(exch, o)
    newStock.avgExecutionPrice(Ric1) must beEqualTo(3.0)
  }

  def e17 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, 2, 1.0, Usr2)
    val exch = new Stock(List(o1, o2))
    val newStock = Exchange.addOrder(exch, Order(3, Direction.Buy, Ric1, QtyOne, 3.0, Usr1))
    val newStock2 = Exchange.addOrder(newStock, Order(4, Direction.Buy, Ric1, 2, 1.0, Usr1))
    newStock2.avgExecutionPrice(Ric1) must beEqualTo((BigDecimal.valueOf(5.0)) / 3)
  }

  def e18 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, 2, 1.0, Usr2)
    val exch = new Stock(List(o1, o2))
    val newStock = Exchange.addOrder(exch, Order(3, Direction.Buy, Ric1, QtyOne, 3.0, Usr2))
    val newStock2 = Exchange.addOrder(newStock, Order(4, Direction.Buy, Ric1, 2, 1.0, Usr1))
    newStock2.executedQuantity(Ric1, Usr1) must beEqualTo(1)
  }

  def e19 = {
    val o1 = Order(1, Direction.Sell, Ric1, QtyOne, 3.0, Usr1)
    val o2 = Order(2, Direction.Sell, Ric1, 2, 1.0, Usr2)
    val exch = new Stock(List(o1, o2))
    val newStock = Exchange.addOrder(exch, Order(3, Direction.Buy, Ric1, QtyOne, 3.0, Usr2))
    val newStock2 = Exchange.addOrder(newStock, Order(4, Direction.Buy, Ric1, 2, 1.0, Usr1))
    newStock2.executedQuantity(Ric1, Usr2) must beEqualTo(-1)
  }
}