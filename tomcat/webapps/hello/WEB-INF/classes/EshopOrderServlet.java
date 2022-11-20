// To save as "<TOMCAT_HOME>/webapps/hello/WEB-INF/classes/HelloServlet.java"
import java.io.*;
import java.sql.*;
import jakarta.servlet.*; // Tomcat 10
import jakarta.servlet.http.*; // Tomcat 10
import jakarta.servlet.annotation.*; // Tomcat 10
// import javax.servlet.*; // Tomcat 9
// import javax.servlet.http.*; // Tomcat 9
// import javax.servlet.annotation.* // Tomcat 9

@WebServlet("/eshoporder") // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class EshopOrderServlet extends HttpServlet {
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
            // Step 3 & 4: Execute a SQL SELECT query and Process the query result
            // Retrieve the books' id. Can order more than one books.
            String[] ids = request.getParameterValues("id");
            if (ids != null) {
                String sqlStr;
                int count;
    
                // Process each of the books
                for (int i = 0; i < ids.length; ++i) {
                // Update the qty of the table books
                sqlStr = "UPDATE books SET qty = qty - 1 WHERE id = " + ids[i];
                out.println("<p>" + sqlStr + "</p>");  // for debugging
                count = stmt.executeUpdate(sqlStr);
                out.println("<p>" + count + " record updated.</p>");
    
                // Create a transaction record
                sqlStr = "INSERT INTO order_records (id, qty_ordered, cust_name, cust_email, cust_phone) VALUES ("
                        + ids[i] + ", 1, '" + request.getParameter("cust_name") + "', '" + request.getParameter("cust_email") + "', '" + request.getParameter("cust_phone") + "')";
                out.println("<p>" + sqlStr + "</p>");  // for debugging
                count = stmt.executeUpdate(sqlStr);
                out.println("<p>" + count + " record inserted.</p>");
                out.println("<h3>Your order for book id=" + ids[i]
                        + " has been confirmed.</h3>");
                }
                out.println("<h3>Thank you.<h3>");
            } else { // No book selected
                out.println("<h3>Please go back and select a book...</h3>");
            }
        } catch(Exception ex) {
            out.println("<p>Error: " + ex.getMessage() + "</p>");
            out.println("<p>Check Tomcat console for details.</p>");
            ex.printStackTrace();
        } // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)

        out.println("</body></html>");
        out.close(); // Always close the output writer
    }
}