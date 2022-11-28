/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Timestamp;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   
   //Added for string utils
   public static String padRight(String s, int n) {
      return String.format("%-" + n + "s", s);
   }
   public static String padLeft(String s, int n) {
      return String.format("%" + n + "s", s);
   }
   
   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public static double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate


   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      int maxPadLength = 10;
      String paddingChar = " ";
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			   System.out.print(padRight(rsmd.getColumnName(i), 20));
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print(padRight(rs.getString(i), 20));
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult


   public String executeQueryAndReturnResultFirstRow(String query) throws SQLException {
      String str = "0";
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;


      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			str = rs.getString(i);
      }//end while
      stmt.close ();
      return str;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql, authorisedUser); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql, authorisedUser); break;
                   case 4: viewRecentOrders(esql, authorisedUser); break;
                   case 5: updateProduct(esql, authorisedUser); break;
                   case 6: viewRecentUpdates(esql, authorisedUser); break;
                   case 7: viewPopularProducts(esql, authorisedUser); break;
                   case 8: viewPopularCustomers(esql, authorisedUser); break;
                   case 9: placeProductSupplyRequests(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
       
	 System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         String type="Customer";

	 String query = String.format(
		"INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
	 String query2 = "SELECT name FROM USERS";
	 esql.executeQueryAndPrintResult(query2);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here
   public static void printColumnNames(Retail esql, String queryStatement) {
      try {
         Statement stmt = esql._connection.createStatement();
         ResultSet rs = stmt.executeQuery(queryStatement);
         ResultSetMetaData rsmd = rs.getMetaData();
         int numCol = rsmd.getColumnCount();
         int rowCount = 0;
		   boolean outputHead = true;
		   while(rs.next()) {
			   if(outputHead) {
				   for(int i = 1; i <= numCol; i++) {
					   System.out.print(rsmd.getColumnName(i) + "\t");
				   }
				   System.out.println();
				   outputHead = false;
			   }
         }
         stmt.close();
      }
      catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void viewProducts(Retail esql) {
      try {
         System.out.print("Please input the store id: ");
         String storeid = in.readLine();
         String query = String.format("SELECT * FROM Product WHERE storeid = '" + storeid + "'");
         esql.executeQueryAndPrintResult(query);
      }
      catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }



   public static void viewStores(Retail esql, String authorizedUser) {
	try {
		double lat = 0.0;
		double longi = 0.0;
		Statement stmt = esql._connection.createStatement();
		//get lat/long for user and lat/long for store
		String honse = "SELECT latitude, longitude FROM Users WHERE name = '"+ authorizedUser+ "'";
		String lat_long_user_query = (honse);
		ResultSet rs = stmt.executeQuery(lat_long_user_query);
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCol = rsmd.getColumnCount();
		int rowCount = 0;
		boolean outputHead = true;
		while(rs.next()) {
			if(outputHead) {
				for(int i = 1; i <= numCol; i++) {
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				outputHead = false;
			}
			lat = rs.getDouble(1);
			longi = rs.getDouble(2);
			System.out.print(lat);
			System.out.print("\t");
			System.out.println(longi);
			System.out.println();
			++rowCount;
		}
		stmt.close();

		stmt = esql._connection.createStatement();
		String lat_long_store_query = ("SELECT latitude, longitude FROM Store");
		rs = stmt.executeQuery(lat_long_store_query);
		rsmd = rs.getMetaData();
		numCol = rsmd.getColumnCount();
		rowCount = 0;
		outputHead = true;
		while(rs.next()) {
			if(outputHead) {
				for(int i = 1; i <= numCol; i++) {
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				outputHead = false;
			}
			double lat2 = rs.getDouble(1);
			double longi2 = rs.getDouble(2);
			double result = calculateDistance(lat, longi, lat2, longi2);
			if(result < 30) {
				String query3 = String.format("SELECT name, latitude, longitude FROM Store WHERE latitude = '"+ lat2 + "' AND longitude = '"+longi2+ "'" );
				esql.executeQueryAndPrintResult(query3);
			}

		}
		stmt.close();		
	}catch(Exception e) {
		System.err.println(e.getMessage());
	}	
   }

  //Order Products. User can order any product from the store within 30 miles radius of his/her location. 
  //User will be asked to input storeID, productName, and numberofUnits. After placing the order, the order 
  //information needs to be inserted in the Orders table. Product tables will need to be updated accordingly. 
   public static void placeOrder(Retail esql, String authorizedUser) {
      try {
         //Query 1 to retrieve the users latitude and longitude
		   double lat = 0.0;
		   double longi = 0.0;
		   Statement stmt = esql._connection.createStatement();
		   String honse = "SELECT latitude, longitude FROM Users WHERE name = '"+ authorizedUser+ "'";
		   String lat_long_user_query = (honse);
		   ResultSet rs = stmt.executeQuery(lat_long_user_query);
		   ResultSetMetaData rsmd = rs.getMetaData();
		   int numCol = rsmd.getColumnCount();
		   int rowCount = 0;
		   stmt.close();

         //Query 2 to retrieve the stores that the user can order from
         stmt = esql._connection.createStatement();
		   String lat_long_store_query = ("SELECT latitude, longitude FROM Store");
		   rs = stmt.executeQuery(lat_long_store_query);
		   rsmd = rs.getMetaData();
		   numCol = rsmd.getColumnCount();
		   rowCount = 0;
         System.out.println("Calculating possible stores you can order from...");
         System.out.println();
		   while(rs.next()) {
			   double lat2 = rs.getDouble(1);
			   double longi2 = rs.getDouble(2);
			   double result = calculateDistance(lat, longi, lat2, longi2);
			   if(result < 30) {
               System.out.println();
               String query4 = String.format("SELECT P.StoreID, P.productName, P.numberOfUnits, P.pricePerUnit FROM Product P, Store S WHERE P.storeID = S.storeID AND S.latitude = '"+ lat2 + "' AND longitude = '"+longi2+ "'");
               esql.executeQueryAndPrintResult(query4);
			   }
		   }

         //Get the user input for their order
         System.out.print("Please enter the store id: ");
         String storeid = in.readLine();
         System.out.print("Please enter product name: ");
         String productName = in.readLine();
         System.out.print("Please enter number of units: ");
         String numberOfUnits = in.readLine();
         String cust = "SELECT userID FROM Users WHERE name = '" + authorizedUser + "'";
         String customerID = esql.executeQueryAndReturnResultFirstRow(cust);
         java.util.Date date = new java.util.Date();

         //insert into orders table
         String query5 = String.format(
            "INSERT INTO Orders (customerID, storeID, productName, unitsOrdered, orderTime) VALUES('%s','%s', '%s', '%s','%s')", customerID, storeid, productName, numberOfUnits, date);
         esql.executeUpdate(query5);

         // String query8 = String.format("SELECT * FROM ORDERS");
         // esql.executeQueryAndPrintResult(query8);
         

         //update products table
         String get_prev_units = "SELECT P.numberOfUnits FROM Product P WHERE P.storeID = '" + storeid + "'";
         get_prev_units = esql.executeQueryAndReturnResultFirstRow(get_prev_units);
         System.out.println(get_prev_units);
         int prev_units = Integer.parseInt(get_prev_units);
         int new_units = Integer.parseInt(numberOfUnits);
         new_units = prev_units - new_units;
         System.out.println(new_units);
         String query6 = String.format(
             "UPDATE Product P "
             + "SET numberOfUnits = '" + new_units + "'" 
             + "WHERE P.storeID = '" + storeid + "'"
             + "AND P.productName = '" + productName + "'");
         esql.executeUpdate(query6);


         // String query7 = String.format("SELECT * FROM Product P, Store S WHERE P.storeID = '" + storeid + "'");
         // esql.executeQueryAndPrintResult(query7);
		   stmt.close();		
	   }
      catch(Exception e) {
		   System.err.println(e.getMessage());
	   }
   }


//Browse Orders List: Customers will be able to see the last 5 of
//his/her recent orders from the Orders table. They will be able to
//see storeID, storeName, productName, number of units ordered
//and date ordered. A customer is not allowed to see the order list
//of other customers.
   public static void viewRecentOrders(Retail esql, String authorizedUser) {
      try {
         Statement stmt = esql._connection.createStatement();
		   String check_user = ("SELECT userID FROM Users WHERE name = '" + authorizedUser + "'");
         ResultSet rs = stmt.executeQuery(check_user);
         ResultSetMetaData rsmd = rs.getMetaData();
		   int numCol = rsmd.getColumnCount();
		   int rowCount = 0;
         while(rs.next()) {
            String userID = rs.getString(1);
            //query to match the userID to the order table
            String match_user_to_order_table = ("SELECT S.name, O.productName, O.unitsOrdered, O.orderTime FROM Orders AS O, Store AS S WHERE customerID = '" + userID + "'AND S.storeID = O.storeID ORDER BY customerID DESC LIMIT 5");
            esql.executeQueryAndPrintResult(match_user_to_order_table);
         }
      }
      catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }

   //For Managers, they can update the information of any product given the storeID. Manager can only update the product information (number of units, price per unit) of the store he/she manages. Product and ProductUpdates tables will need to be updated accordingly if any updates take place.
   //Manager can also view the information of last 5 recent updates of his/her store(s).
   public static void updateProduct(Retail esql, String authorizedUser) {
      try {
         //grab the manager ID after they login
         Statement stmt = esql._connection.createStatement();
		   String check_user = ("SELECT userID, type FROM Users WHERE name = '" + authorizedUser + "'");
		   ResultSet rs = stmt.executeQuery(check_user);
		   ResultSetMetaData rsmd = rs.getMetaData();
		   int numCol = rsmd.getColumnCount();
		   int rowCount = 0;
         while(rs.next()) {
			   int userID = rs.getInt(1);
			   String type = rs.getString(2);
            type = type.trim();
			   if(type.equals("manager")) {
               System.out.println("You are a manager");
               //now we need to get the store id by matching manager ID to both tables
               String get_store_id = ("SELECT storeID FROM Store S, Users U WHERE U.userID = '" + userID + "'");
               String storeID = esql.executeQueryAndReturnResultFirstRow(get_store_id);
               //now we need to allow the manager to see the proudct table of only his/her store
               String get_product = ("SELECT * FROM Product WHERE storeID = '" + storeID + "'");
               esql.executeQueryAndPrintResult(get_product);
               //now managers can update no_units, ppp
               System.out.print("Please enter the product name: ");
               String productName = in.readLine();
               System.out.print("Please enter number of units: ");
               String num_units = in.readLine();
               int num_units_ = Integer.parseInt(num_units);
               System.out.print("Please enter price per unit: ");
               String price_per_unit = in.readLine();
               int price_per_unit_ = Integer.parseInt(price_per_unit);
               //now we update the table product & productupdates
               String update_product_table = String.format(
                                                            "UPDATE Product SET numberOfUnits = '" + num_units_ + "', pricePerUnit = '" + price_per_unit_ + "' WHERE storeID = '" + storeID + "' AND productName = '" + productName + "'");
               esql.executeUpdate(update_product_table);
               java.util.Date date = new java.util.Date();
               String insert_productUpdates_tables = String.format(
                     "INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES('%s','%s', '%s', '%s')", userID, storeID, productName, date);
               esql.executeUpdate(insert_productUpdates_tables);
               //check query
               String query = "SELECT * FROM Product";
               esql.executeQueryAndPrintResult(query);
               String query2 = "SELECT * FROM ProductUpdates";
               esql.executeQueryAndPrintResult(query2);
			   }
            if(type.equals("admin")) {
               //print out all the products
               String retrieve_all_products = "SELECT * FROM Product";
               esql.executeQueryAndPrintResult(retrieve_all_products);
               //prompt admin for the info to update the product
               System.out.print("Please enter the store id: ");
               String storeID = in.readLine();
               System.out.print("Please enter the product name: ");
               String productName = in.readLine();
               System.out.print("Please enter number of units: ");
               String num_units = in.readLine();
               int num_units_ = Integer.parseInt(num_units);
               System.out.print("Please enter price per unit: ");
               String price_per_unit = in.readLine();
               int price_per_unit_ = Integer.parseInt(price_per_unit);
               //update the product
               String update_product_table = String.format(
                     "UPDATE Product SET numberOfUnits = '" + num_units_ + "', pricePerUnit = '" + price_per_unit_ + "' WHERE storeID = '" + storeID + "' AND productName = '" + productName + "'");
               esql.executeUpdate(update_product_table);
               //update user
               //retrieve all users
               String retrieve_all_users = "SELECT * FROM USER";
               esql.executeQueryAndPrintResult(retrieve_all_products);
               //prompt for info
               System.out.print("Please enter the user id: ");
               String userID_ = in.readLine();
               System.out.print("Please enter name: ");
               String userName = in.readLine();
               System.out.print("Please enter password: ");
               String password = in.readLine();
               System.out.print("Please enter latitude: ");
               String latitude = in.readLine();
               System.out.print("Please enter longitude: ");
               String longitude = in.readLine();
               System.out.println("Please enter type: ");
               String type_ = in.readLine();
               //update table
               String update_user_table = String.format(
                     "UPDATE User SET name = '" + userName + "', password = '" + password + "', latitude = '" + latitude + "', longitude = '" + longitude + "', type = '" + type_ + "' WHERE userID = '" + userID_ + "'");
               esql.executeUpdate(update_user_table);

            }
            else {
               System.out.println("You are not a manager");
            }
		   }
		   stmt.close();
      }
      catch(Exception e) {
         System.err.println (e.getMessage ());
      }
   }
   //Manager can also view the information of last 5 recent updates of
   //his/her store(s).
   public static void viewRecentUpdates(Retail esql, String authorizedUser) {
      try {
         Statement stmt = esql._connection.createStatement();
		   String check_user = ("SELECT userID, type FROM Users WHERE name = '" + authorizedUser + "'");
         ResultSet rs = stmt.executeQuery(check_user);
         ResultSetMetaData rsmd = rs.getMetaData();
		   int numCol = rsmd.getColumnCount();
		   int rowCount = 0;
         while(rs.next()) {
            int userID = rs.getInt(1);
			   String type = rs.getString(2);
            type = type.trim();
            if(type.equals("manager")) {
               String get_recent_updates = "SELECT * FROM productUpdates AS PU, Store AS S WHERE S.storeID = PU.storeID AND PU.managerID = '" + userID + "' ORDER BY PU.updateNumber DESC LIMIT 5";
               esql.executeQueryAndPrintResult(get_recent_updates);  
            }
         }
      }
      catch(Exception e) {
         System.err.println (e.getMessage ());
      }

   }
   //Manager will be able to see top 5
   //most popular products (product name) in his/her store(s) (Based
   //on the order count of Product)
   public static void viewPopularProducts(Retail esql, String authorizedUser) {
      try {
         Statement stmt = esql._connection.createStatement();
		   String check_user = ("SELECT userID, type FROM Users WHERE name = '" + authorizedUser + "'");
         ResultSet rs = stmt.executeQuery(check_user);
         ResultSetMetaData rsmd = rs.getMetaData();
		   int numCol = rsmd.getColumnCount();
		   int rowCount = 0;
         while(rs.next()) {
            int userID = rs.getInt(1);
			   String type = rs.getString(2);
            type = type.trim();
            if(type.equals("manager")) {
               //grab the store based off id
               String get_popular_products = "SELECT O.productName, COUNT(*) AS TOTAL_TIMES_ORDERED FROM Orders O, Store S WHERE O.storeID = S.storeID AND S.managerID = '" + userID + "' GROUP BY O.productName ORDER BY TOTAL_TIMES_ORDERED DESC LIMIT 5";
               esql.executeQueryAndPrintResult(get_popular_products);
            }     
         }
      }
      catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }
   //Manager can also view the top
   //5 customer’s information who placed the most orders in his/her
   //store(s).
   public static void viewPopularCustomers(Retail esql, String authorizedUser) {
      try {
         Statement stmt = esql._connection.createStatement();
		   String check_user = ("SELECT userID, type FROM Users WHERE name = '" + authorizedUser + "'");
         ResultSet rs = stmt.executeQuery(check_user);
         ResultSetMetaData rsmd = rs.getMetaData();
		   int numCol = rsmd.getColumnCount();
		   int rowCount = 0;
         while(rs.next()) {
            int userID = rs.getInt(1);
			   String type = rs.getString(2);
            type = type.trim();
            if(type.equals("manager")) {
               //String get_popular_users = "SELECT U.userID FROM User U";
               //String get_popular_users = "SELECT O.customerID, COUNT(*) AS TOTAL_TIMES_ORDERED FROM Orders AS O GROUP BY O.customerID";
               String get_popular_users = "SELECT U.userID, U.name, U.password, U.latitude, U.longitude, U.type, COUNT(*) AS TOTAL_TIMES_ORDERED FROM Orders AS O, Users U WHERE U.userID = O.customerID GROUP BY U.userID ORDER BY TOTAL_TIMES_ORDERED DESC LIMIT 5";
               esql.executeQueryAndPrintResult(get_popular_users);
            }
         }
      }
      catch(Exception e) {
         System.err.println(e.getMessage()); 
      }
   }
   public static void placeProductSupplyRequests(Retail esql) {}

}//end Retail

