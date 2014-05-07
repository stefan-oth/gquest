package de.oth.app.geekquest;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import de.oth.app.geekquest.model.Character;

@SuppressWarnings("serial")
public class DataMigrationServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/plain");
        
        migrateCharacter();
        
        resp.getWriter().println("migration done");
    }
    
    private void migrateCharacter() {
        Objectify ofy = ObjectifyService.ofy();
        
        List<Character> characters = ofy.load().type(Character.class).list();
        
        ofy.save().entities(characters);
    }

}
