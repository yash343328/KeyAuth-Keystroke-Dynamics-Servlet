import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class TypingAuth extends HttpServlet {

    private static final String FILE_NAME = "userpattern.txt";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body style='font-family:Arial;text-align:center'>");
        out.println("<h2>Keystroke Dynamics Authentication System</h2>");

        out.println("<form method='post'>");
        out.println("Username:<br><input type='text' name='user'><br><br>");
        out.println("Password:<br><input type='password' name='pass'><br><br>");
        out.println("Typing Pattern (in ms separated by comma):<br>");
        out.println("<input type='text' name='pattern' placeholder='e.g. 120,98,140'><br><br>");

        out.println("<input type='submit' name='action' value='Register'>");
        out.println("<input type='submit' name='action' value='Login'>");

        out.println("</form>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        String pattern = request.getParameter("pattern");
        String action = request.getParameter("action");

        if(action.equals("Register")){
            registerUser(user, pass, pattern);
            out.println("<h3>Registration Successful!</h3>");
        }
        else if(action.equals("Login")){
            String result = verifyUser(user, pass, pattern);
            out.println(result);
        }

        out.println("<br><a href='TypingAuth'>Back</a>");
    }

    private void registerUser(String user, String pass, String pattern) {
        try {
            FileWriter fw = new FileWriter(FILE_NAME, true);
            fw.write(user + ":" + pass + ":" + pattern + "\n");
            fw.close();
        } catch(Exception e) {}
    }

    private String verifyUser(String user, String pass, String currentPattern) {

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
            String line;

            while((line = br.readLine()) != null){

                String data[] = line.split(":");

                if(data[0].equals(user) && data[1].equals(pass)){

                    String savedPattern = data[2];

                    double match = calculateMatch(savedPattern, currentPattern);

                    if(match >= 75){
                        return "<h3>Password Correct ✔</h3>"
                        + "<h3>Typing Match: "+match+"%</h3>"
                        + "<h2 style='color:green'>User Verified ✅</h2>";
                    }
                    else{
                        return "<h3>Password Correct ✔</h3>"
                        + "<h3>Typing Match: "+match+"%</h3>"
                        + "<h2 style='color:red'>Suspicious User ❌ Login Blocked</h2>";
                    }
                }
            }

            br.close();
        } catch(Exception e){}

        return "<h3 style='color:red'>Invalid Username or Password</h3>";
    }

    private double calculateMatch(String saved, String current){

        String s1[] = saved.split(",");
        String s2[] = current.split(",");

        int len = Math.min(s1.length, s2.length);
        double totalDiff = 0;

        for(int i=0;i<len;i++){
            double a = Double.parseDouble(s1[i]);
            double b = Double.parseDouble(s2[i]);
            totalDiff += Math.abs(a-b)/a *100;
        }

        double avgDiff = totalDiff/len;
        return 100-avgDiff;
    }
}
