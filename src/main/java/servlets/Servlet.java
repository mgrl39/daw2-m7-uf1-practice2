package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import dominio.Alumno;
// 22 de novembre de 2024
@WebServlet("/AlumnoServlet")
public class Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PrintWriter out;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String sql = request.getParameter("sql");
        if (sql == null || sql.trim().isEmpty()) {
            out.println(getWebFormatter("<p style=\"color: #FF0000\">Error: <strong style=\"color: #FF0000\">No se proporcionó ninguna consulta SQL</strong></p>"));
            return;
        }
        try {
            List<Map<String, String>> data = Alumno.executeQuery(sql);
            String htmlTable = "";
            if (!data.isEmpty()) {
                htmlTable += "<table style=\"color:#00007e\" border='1'><tr>";
                for (String columnName : data.get(0).keySet()) {
                	htmlTable += "<th>" + columnName + "</th>";
                }
                System.out.printf(htmlTable + '\n');
                htmlTable += "</tr>";
                for (Map<String, String> row : data) {
                	htmlTable += "<tr>";
                    for (String value : row.values())  {
                    	htmlTable += "<td>" + value + "</td>";
                    	System.out.printf("testing " + value + "\n");
                    }
                    htmlTable += "</tr>";
                }
                
                htmlTable += "</table>";
            } else {
            	htmlTable += "<p style=\"color:#00007e\">No se encontraron resultados.</p>";
            }
            System.out.printf(htmlTable + '\n');
            out.println(getWebFormatter(htmlTable));
        } catch (RuntimeException e) {
            out.println(getWebFormatter("<p style=\"color:#00007e\">Error al ejecutar la consulta: <strong style=\"color: #FF0000\">" + e.getMessage() + "</strong></p>"));
        }
    }

	
    private String getWebFormatter(String msg) {
    	return ("<html><body style=\"background-color:#ffff9d\"><center><h1 style=\"color:#00007e\">Usa JDBC para recuperar registros de una tabla</h1></center><hr style=\"color:#00007e\">" + msg + "</body></html>");
    }
    
    private String postWebFormatter(String msg) {
    	return ("<html><body style=\"background-color:#ffff9d\"><center><h1 style=\"color:#00007e\">Usa JDBC para grabar un registro en una tabla</h1></center><hr style=\"color:#00007e\"><p style=\"color:#00007e\">" + msg + "</p></body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        out = response.getWriter();
        try {
            String idStr = request.getParameter("id");
            String curso = request.getParameter("curso");
            String nombre = request.getParameter("nombre");

            if (idStr != null && curso != null && nombre != null) {
                int id = Integer.parseInt(idStr);
                Alumno nuevoAlumno = new Alumno(id, curso, nombre);
                if (nuevoAlumno.save()) out.println(postWebFormatter("Alumno añadido con éxito"));
                else out.println(postWebFormatter("Error al añadir el alumno"));
            } else {
                out.println(postWebFormatter("Todos los campos son obligatorios"));
            }
        } catch (NumberFormatException e) {
            out.println(postWebFormatter("Error: El ID debe ser un número válido"));
        }
    }
}
