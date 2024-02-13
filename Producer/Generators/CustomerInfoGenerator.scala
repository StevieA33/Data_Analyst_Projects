package com.Producer.Generators
import com.ProductOrder
object CustomerInfoGenerator {

  //Each function return a string separated by commas
  //In the follow respective order:
  //orderId, Customer ID, Customer Name, Customer City

  private final val fullUsFile = os.read.lines(os.pwd / "clean_data" / "full_name_american.txt").toList
  private final val fCnFile = os.read.lines(os.pwd / "clean_data" / "first_name_chinese.txt").toList
  private final val lCnFile = os.read.lines(os.pwd / "clean_data" / "last_name_chinese.txt").toList
  private final val firstEuFile = os.read.lines(os.pwd / "clean_data" / "first_name_european.txt").toList
  private final val lastEuFile = os.read.lines(os.pwd / "clean_data" / "last_name_american_common.txt").drop(1).toList
  private final val usCityFile = os.read.lines(os.pwd / "clean_data" / "american_cities.txt").toList
  private final val cnCityFile = os.read.lines(os.pwd / "clean_data" / "chinese_cities.txt").toList
  private final val spCityFile = os.read.lines(os.pwd / "clean_data" / "spain_cities.txt").toList
  private final val ran = scala.util.Random



  def generateCustomer(po:ProductOrder):ProductOrder={
    po.country match{
      case "United States" => genUSCustomer(po)
      case "China" => genCNCustomer(po)
      case "Spain" => genEUCustomer(po)
    }
  }
  private def genUSCustomer(po:ProductOrder): ProductOrder = {
    val name = fullUsFile(Math.abs(ran.nextInt(fullUsFile.length)))
    po.customer_id = Math.abs(name.hashCode())
    po.customer_name = name
    po.city = usCityFile(Math.abs(ran.nextInt(usCityFile.length)))
    po
  }

  private def genCNCustomer(po:ProductOrder): ProductOrder = {
    val firstName = fCnFile(Math.abs(ran.nextInt(fCnFile.length)))
    val lastName = lCnFile(Math.abs(ran.nextInt(lCnFile.length)))
    po.customer_id = Math.abs(firstName.hashCode())
    po.customer_name = firstName + "" + lastName
    po.city = cnCityFile(Math.abs(ran.nextInt(cnCityFile.length)))
    po
  }

  private def genEUCustomer(po:ProductOrder): ProductOrder = {
    val firstName = firstEuFile(Math.abs(ran.nextInt(firstEuFile.length)))
    val lastName = lastEuFile(Math.abs(ran.nextInt(lastEuFile.length))).split(",")
    po.customer_id = Math.abs(firstName.hashCode())
    po.customer_name = firstName + " " + lastName(1).toLowerCase.capitalize
    po.city = spCityFile(Math.abs(ran.nextInt(spCityFile.length)))
    po
  }
}
