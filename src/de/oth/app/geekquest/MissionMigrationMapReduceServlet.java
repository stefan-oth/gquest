package de.oth.app.geekquest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.mapreduce.MapJob;
import com.google.appengine.tools.mapreduce.MapSettings;
import com.google.appengine.tools.mapreduce.MapSpecification;
import com.google.appengine.tools.mapreduce.inputs.DatastoreKeyInput;

import de.oth.app.geekquest.mapreduce.MissionMigrationMapJob;

@SuppressWarnings("serial")
public class MissionMigrationMapReduceServlet extends HttpServlet {

    private final UserService userService = UserServiceFactory.getUserService();

    private void writeResponse(HttpServletResponse resp) throws IOException {

        try (PrintWriter pw = new PrintWriter(resp.getOutputStream())) {
            pw.println("<html><body>" + "<br><form method='post'>"
                    + "Migrate all mission entities:" + "<div> <br />"
                    + "ShardCount: <input name='shardCount' value='3'> <br />"
                    + "<br /> <input type='submit' value='Migrate missions'>"
                    + "</div> </form> </body></html>");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (userService.getCurrentUser() == null) {
            return;
        }
        writeResponse(resp);
    }

    private String getPipelineStatusUrl(String pipelineId) {
        return "/_ah/pipeline/status.html?root=" + pipelineId;
    }

    private void redirectToPipelineStatus(HttpServletResponse resp,
            String pipelineId) throws IOException {
        String destinationUrl = getPipelineStatusUrl(pipelineId);
        resp.sendRedirect(destinationUrl);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (userService.getCurrentUser() == null) {

            return;
        }

        int shardCount = Integer.parseInt(req.getParameter("shardCount"));

        MapSpecification<Key, Void, Void> mapSpec = getMissionMigrationJobSpec(shardCount);
        MapSettings settings = getSettings();

        String id = MapJob.start(mapSpec, settings);

        redirectToPipelineStatus(resp, id);

    }

    private MapSpecification<Key, Void, Void> getMissionMigrationJobSpec(
            int mapShardCount) {
        DatastoreKeyInput input = new DatastoreKeyInput("Character",
                3);//mapShardCount);
        MissionMigrationMapJob mapper = new MissionMigrationMapJob();
        return new MapSpecification.Builder<Key, Void, Void>(input, mapper)
                .setJobName("Migrate all missions").build();
    }

    private MapSettings getSettings() {
        MapSettings settings = new MapSettings.Builder()
                .setWorkerQueueName("mapreduce-workers")
                //.setModule("mapreduce")
                .build();
        return settings;
    }
}
