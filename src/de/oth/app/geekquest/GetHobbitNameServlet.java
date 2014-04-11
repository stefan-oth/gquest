package de.oth.app.geekquest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class GetHobbitNameServlet extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    //public void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException, ServletException {
        
        String hobbitName = "";
        String errorMsg = "";
        String backToUrl = "/GeekQuest.jsp";
        
        String gender = checkNull(req.getParameter("gender"));
        String fFirstname = "";
        String fLastname = "";
        String mFirstname = "";
        String mLastname = "";
        
        if (gender.equals("male")) {
            mFirstname = checkNull(req.getParameter("firstname"));
            mLastname = checkNull(req.getParameter("lastname"));
        } else if (gender.equals("female")) {
            fFirstname = checkNull(req.getParameter("firstname"));
            fLastname = checkNull(req.getParameter("lastname"));
        } else {
            errorMsg = "No valid gender selected";
            resp.sendRedirect("/Error.jsp?error=" + errorMsg + "&url=" + backToUrl);
        }
        
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("f_firstname=").append(URLEncoder.encode(fFirstname, "UTF-8"));
            sb.append("&f_lastname=").append(URLEncoder.encode(fLastname, "UTF-8"));
            sb.append("&m_firstname=").append(URLEncoder.encode(mFirstname, "UTF-8"));
            sb.append("&m_lastname=").append(URLEncoder.encode(mLastname, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            errorMsg = e1.getMessage();
            resp.sendRedirect("/Error.jsp?error=" + errorMsg + "&url=" + backToUrl);
        }
        
        try {
            URL url = new URL("http://www.chriswetherell.com/hobbit/index.php");
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            try (OutputStreamWriter wr = new OutputStreamWriter(
                    connection.getOutputStream())) {
                wr.write(sb.toString());
                wr.flush();
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));) {
                
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    
                    resp.setContentType("text/plain");
                    // OK
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("<div class=\"answer\">")) {
                            hobbitName = line.replaceFirst("<div class=\"answer\">", 
                                    "");
                            hobbitName = hobbitName.replaceFirst("</div>", "");
                        }
                    }
                } else {
                    errorMsg = "HttpRequest to " + url.toString() 
                            + " returned response code " + connection.getResponseCode();
                    resp.sendRedirect("/Error.jsp?error=" + errorMsg + "&url=" + backToUrl);
                }
            }

        } catch (MalformedURLException e) {
            errorMsg = e.getMessage();
            resp.sendRedirect("/Error.jsp?error=" + errorMsg + "&url=" + backToUrl);
        } catch (IOException e) {
            errorMsg = e.getMessage();
            resp.sendRedirect("/Error.jsp?error=" + errorMsg + "&url=" + backToUrl);
        }
        
        resp.sendRedirect("/geekquest?hobbitName=" + hobbitName);
        
        //TODO search for alternative return method
        
//        URL url = new URL("http://localhost:8888/GeekQuest.jsp");
//        sendPostRequest(url, "hobbitName=" + hobbitName);
        
//        req.setAttribute("hobbitName", hobbitName);
//        
//        ServletContext sc = getServletContext();
//        RequestDispatcher rd = sc.getRequestDispatcher("/geekquest");
//        rd.forward(req, resp);
    }
   
    private String checkNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
}
