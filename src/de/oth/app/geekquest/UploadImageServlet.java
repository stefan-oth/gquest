package de.oth.app.geekquest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import de.oth.app.geekquest.dao.CharacterDAO;
import de.oth.app.geekquest.dao.DAOManager;
import de.oth.app.geekquest.model.Character;

@SuppressWarnings("serial")
public class UploadImageServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        
        User user = (User) req.getAttribute("user");
        if (user == null) {
            UserService userService = UserServiceFactory.getUserService();
            user = userService.getCurrentUser();
        }
        
        Long charId = Long.valueOf(checkNull(req.getParameter("characterId")));
        CharacterDAO charDAO = DAOManager.getCharacterDAO();
        Character character = charDAO.find(charId, user.getUserId());
        
        if (character != null) {

            Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
            List<BlobKey> blobKeys = blobs.get("characterImage");
            
            if (blobKeys.get(0) != null) {
                if (character.getImageBlobKey() != null) {
                    BlobKey key = new BlobKey(character.getImageBlobKey());
                    blobstoreService.delete(key);
                }
               character.setImageBlobKey(blobKeys.get(0).getKeyString());
               charDAO.update(character);
            }

//            if (blobKeys.get(0) == null) {
//                res.sendRedirect("/");
//            } else {
//                res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
//            }
            res.sendRedirect("/GeekQuest.jsp");
        }
    }
    
    private String checkNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

}
