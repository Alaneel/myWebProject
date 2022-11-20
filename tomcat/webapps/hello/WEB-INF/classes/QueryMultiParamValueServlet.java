// To save as "<TOMCAT_HOME>/webapps/hello/WEB-INF/classes/HelloServlet.java"
import java.io.*;
import java.sql.*;
import jakarta.servlet.*; // Tomcat 10
import jakarta.servlet.http.*; // Tomcat 10
import jakarta.servlet.annotation.*; // Tomcat 10
// import javax.servlet.*; // Tomcat 9
// import javax.servlet.http.*; // Tomcat 9
// import javax.servlet.annotation.* // Tomcat 9

@WebServlet("/querymvp") // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class QueryMultiParamValueServlet extends HttpServlet {
    // The doGet() runs once per HTTP GET request to this servlet
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Set the response MIME type of the response message
        response.setContentType("text/html");
        // Allocate a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();

        // Write the response message, in an HTML page
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Query Response</title></head>");
        out.println("<body>");
        
        try (
            // Step 1: Allocate a database 'Connection' object
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", "myuser", "!Wym031009"); // For MySQL
                // The format is: "jdbc:mysql://locahost:port/databaseName", "username", "password"
            
            // Step 2: Allocate a 'Statement' object in the Connection
            Statement stmt = conn.createStatement();
        ) {
            // Step 3: Execute a SQL SELECT query
            String[] authors = request.getParameterValues("author");  // Returns an array of Strings
            // Add the following lines after 
            // String[] authors = request.getParameterValues("author");
            if (authors == null) {
                out.println("<h2>No author selected. Please go back to select author(s)</h2><body></html>");
                return; // Exit doGet()
            } 
            // Okay to perform the database query using the above codes
            String sqlStr = "SELECT * FROM books WHERE author IN (";
            for (int i = 0; i < authors.length; ++i) {
                if (i < authors.length - 1) {
                sqlStr += "'" + authors[i] + "', ";  // need a commas
                } else {
                sqlStr += "'" + authors[i] + "'";    // no commas
                }
            }
            sqlStr += ") AND price < " + request.getParameter("price") + " AND qty > 0 ORDER BY author ASC, title ASC";

            out.println("<h3>Thank you for your query</h3>");
            out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging
            ResultSet rset = stmt.executeQuery(sqlStr); // Send the query to the server

            // Step 4: Process the query result get
            int count = 0;
            while (rset.next()) {
                // Print a paragraph <p>...</p> for each record
                out.println("<p>" + rset.getString("author") + ", " + rset.getString("title") + ", $" + rset.getDouble("price") + "</p>");
                count++;
            }
            out.println("<p>==== " + count + " records found =====</p>");
        } catch(Exception ex) {
            out.println("<p>Error: " + ex.getMessage() + "</p>");
            out.println("<p>Check Tomcat console for details.</p>");
            ex.printStackTrace();
        } // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)

        out.println("</body></html>");
        out.close(); // Always close the output writer
    }
}