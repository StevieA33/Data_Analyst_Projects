package com.Producer.Generators

import os.RelPath

import java.nio.file.NoSuchFileException

object DataValidator {
  /**
   * Checks if the file is validated or not. Returns true if the file is pipe-delimited and false if not.
   * @param filePath The relative path of the file in the
   * @return Boolean: True or false depending on if the file is tab-delimited or not.
   */
  private def isValid(filePath: os.pwd.ThisType): Boolean = {
    //If the file exists, check if it is pipe['|'] delimited
    if (os.isFile(filePath)) {
      return os
        .read
        .lines(filePath)
        .toList
        .forall(_.contains("|"))
    }
    false
  }

  /**
   * This method is used to turn comma-delimited files into pipe-delimited files and returns the filepath of the file.
   * If the original filepath is not in the a member of the clean_data folder, it will make a copy of the original file
   * and place it in the clean_data directory before replacing all the commas for pipes for version control purposes.
   * If the file is already valid, it will just return the filepath.
   * @param relativeFilePath
   * @return os.pwd.ThisType: The file path of the file as a ThisType.
   */
  def validatedData(relativeFilePath: String): os.pwd.ThisType = {
    var filePath = os.pwd / RelPath(relativeFilePath)

    if(!os.isFile(filePath)) {
      throw new NoSuchFileException(s"$filePath does not exist. Check input string.")
    }

    if (relativeFilePath.split("/").head != "clean_data") {
      //println("Validate Data Log, Copy file from pwd/data to pwd/clean_data and validating it.")
      val cleanFilePath = os.pwd / "clean_data" / relativeFilePath.split("/").tail
      //println("cleaned file path", cleanFilePath)
      os.copy(filePath, cleanFilePath)
      filePath = cleanFilePath
    }

    if(!isValid(filePath)) {
      val validatedText = os
        .read
        .lines(filePath)
        .map(_.replace(",", "|"))
        .mkString("\n")
      os.write.over(filePath, validatedText)
    }
    filePath
  }


  def proofOfConcept(): Unit = {
    val a = os
      .read
      .lines(os.pwd / "clean_data" / "dates_and_weekdays_starting_1970.csv")
      .take(5)
      .map(l => l.split(","))
      .map{ case Array(a,b) => (a,b)}
      .toMap[String,String]
      .foreach(println)
  }

  def main(args: Array[String]): Unit = {
    //validatedData("data/hello.txt")
  }
}
