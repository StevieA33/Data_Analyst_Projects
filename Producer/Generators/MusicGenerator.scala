package com.Producer.Generators
import com.ProductOrder
import com.Tools.MathHelper
object MusicGenerator {
  private final val ran = scala.util.Random
  private final val musicFile = os.read.lines(os.pwd/"clean_data"/"track_info.txt").drop(1).toList

  def genMusic(po:ProductOrder): ProductOrder = {
    val music = musicFile(Math.abs(ran.nextInt(musicFile.length))).split("""\|""")
    val price = MathHelper.roundDouble(music(2).toDouble / 10000 ) + 2
    po.product_id = Math.abs(music(1).hashCode + 1)
    po.product_name = music(1)
    po.price = MathHelper.roundDouble(price)
    po.qty = 1
    po
  }

}
