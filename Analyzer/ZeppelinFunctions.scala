package com.Analyzer

object ZeppelinFunctions extends App{

  //  A place to paste scala functions (like for loading our data), as well as sql strings
  // Load data into dataframe in zeppelin
  """"

    "val SPARK_HOME = System.getenv("SPARK_HOME")

    val text = sc.textFile("hdfs://localhost:9000/user/bdyson/Data/valid_products.csv")
    val data = text.toDF().select("*")
    val df2 = data.select(
    split(col("value"), "\\|").getItem(0).as("order_id").cast("long"),
    split(col("value"), "\\|").getItem(1).as("customer_id").cast("long"),
    split(col("value"), "\\|").getItem(2).as("customer_name").cast("string"),
    split(col("value"), "\\|").getItem(3).as("product_id").cast("long"),
    split(col("value"), "\\|").getItem(4).as("product_name").cast("string"),
    split(col("value"), "\\|").getItem(5).as("product_category").cast("string"),
    split(col("value"), "\\|").getItem(6).as("payment_type").cast("string"),
    split(col("value"), "\\|").getItem(7).as("qty").cast("long"),
    split(col("value"), "\\|").getItem(8).as("price").cast("double"),
    split(col("value"), "\\|").getItem(9).as("datetime").cast("Date"),
    split(col("value"), "\\|").getItem(10).as("country").cast("string"),
    split(col("value"), "\\|").getItem(11).as("city").cast("string"),
    split(col("value"), "\\|").getItem(12).as("ecommerce_website_name").cast("string"),
    split(col("value"), "\\|").getItem(13).as("payment_txn_id").cast("long"),
    split(col("value"), "\\|").getItem(14).as("payment_txn_success").cast("string"),
    split(col("value"), "\\|").getItem(15).as("failure_reason").cast("string")
    ).drop("value")


    val df3 = df2.withColumn("datetime", date_format(col("datetime"),"dd/MM/yyyy"))

    df3.show(1000)

    df3.createOrReplaceTempView("products")

  """


  //Bao SQL Code Portion
  """
  - Spikes in purchases by date globally / Total Orders Placed By Each Country and Date
  SELECT SUBSTRING(datetime,1,10) as date,country,COUNT(product_id) AS total_orders FROM products GROUP BY date, country HAVING country NOT IN("null") AND date NOT IN ("null") ORDER BY date,total_orders DESC;
  - Orders Placed Globally By Each Category and Date
  SELECT SUBSTRING(datetime,1,10) as date,product_category,COUNT(product_id) AS total_orders FROM products GROUP BY date, product_category HAVING product_category IN("Electronics","Entertainment","Computers","Food","Home")AND date NOT IN ("null") ORDER BY date,total_orders DESC;
  - Product Category being purchased the most on New Year 
  SELECT SUBSTRING(datetime,1,10) as date,product_category,COUNT(product_id) AS total_orders FROM products GROUP BY date, product_category HAVING product_category IN("Electronics","Entertainment","Computers","Food","Home")AND date NOT IN ("null") AND date = "2022-01-01" ORDER BY total_orders DESC;
  - Globally Which day of the week products are being purchased the most
  SELECT WEEKDAY(SUBSTRING(datetime,1,10)) as date,country,COUNT(product_id) AS total_orders FROM products GROUP BY date, country HAVING country NOT IN("null") AND date NOT IN ("null") ORDER BY total_orders DESC;
  - Top 10 spender
  SELECT customer_id,customer_name, city, country, round(sum(price),2) AS total_amount FROM products GROUP BY customer_name,customer_id,city,country ORDER BY total_amount DESC LIMIT 10;
  """


  //Ben SQL Code Portion
  //Brady SQL Code Portion
  """
    -- Orders per hour. (Bar chart with hour as key, count(1) SUM as value.)
    SELECT hour, COUNT(*) FROM products GROUP BY hour

    -- Product categories
    - Distinct product categories.
      SELECT DISTINCT product_category FROM products WHERE product_category IN ('Electronics','Computers','Food','Entertainment','Home','null');
    - Percentages of product categories within each country (not different, so this suggests the same product cateogory generation algorithm was used irrespective of country)
      WITH countrytotals AS (SELECT Country, COUNT(*) AS countryCount FROM products WHERE product_category IN ('Electronics','Computers','Food','Entertainment','Home','null') GROUP BY Country),
      countryCatTotals AS (SELECT Country, product_category, COUNT(*) AS catCount FROM products WHERE product_category IN ('Electronics','Computers','Food','Entertainment','Home','null') GROUP BY Country, product_category)
      SELECT *, catCount/countryCount FROM countryCatTotals LEFT JOIN countrytotals ON countrycattotals.Country=countrytotals.Country

    -- Successes vs failures: payment success rate is about 90% success across product categories and countries (and probably across the board).
    - Success/fail percentages
      WITH counts AS (SELECT payment_txn_success, COUNT(*) AS Count FROM products GROUP BY 1)
      SELECT *, ROUND((Count / (SELECT SUM(Count) FROM counts) * 100),2) AS Percent FROM counts
    - (Also ran queries for number of successes and failures for each country and for each product category.)

    -- Failure reasons
    - There are a lot of failure reasons to weed out. Are these actually correlated with failures? Or are they false failures?
    - Failure reason percentages

    - Histogram of number of transactions per customer.
      SELECT number_of_transactions AS Customers FROM (SELECT customer_id, number_of_transactions FROM (SELECT customer_id, COUNT(*) as number_of_transactions FROM products GROUP BY customer_id))

    - Histogram of prices. (Use price as key, Frequency SUM as value.)
      SELECT price, COUNT(*) as Frequency FROM products GROUP BY price ORDER BY 1

    - Histogram: price distribution for each category. (Use product_category as key, price as group, Frecuency(SUM) as value)
      SELECT product_category, price, COUNT(*) as Frequency FROM products WHERE product_category IN ('Electronics','Computers','Food','Entertainment','Home','null') GROUP BY product_category, price ORDER BY price

    -

  """

  //David SQL Code Portion
  //Teddy SQL Code Portion
  //Tiffany SQL Code Portion

  //Steven SQL Code Portion
  // Ben, Tiffany, Brady SQL Code Portion
  """
  -- product category proportions for each country (e.g., what proportion of orders are electronics in China?)
  WITH
  countrytotals AS
    (SELECT Country, COUNT(*) AS countryCount
      FROM products
      WHERE product_category IN ('Electronics','Computers','Food','Entertainment','Home','null')
  GROUP BY Country),
  countryCatTotals AS
    (SELECT Country, product_category, COUNT(*) AS catCount
      FROM products
      WHERE product_category IN ('Electronics','Computers','Food','Entertainment','Home','null')
  GROUP BY Country, product_category)

  SELECT *, catCount/countryCount
  FROM countryCatTotals
    LEFT JOIN countrytotals ON countrycattotals.Country=countrytotals.Country
  """

  """

  SELECT distinct product_category, COUNT(product_category) as Number_Of_Products
    FROM products
    Where product_category In ("Electronics", "Computers", "Food", "Entertainment", "Home")
    GROUP By product_category;

  SELECT  product_category, product_name, COUNT(product_name) AS amount
    FROM products
    GROUP BY product_category,product_name
    HAVING product_category IN ("Electronics", "Computers", "Food", "Entertainment", "Home")
    ORDER BY amount DESC;


  """
}
