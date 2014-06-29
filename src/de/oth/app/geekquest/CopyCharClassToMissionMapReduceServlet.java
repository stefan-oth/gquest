package de.oth.app.geekquest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.mapreduce.MapReduceJob;
import com.google.appengine.tools.mapreduce.MapReduceSettings;
import com.google.appengine.tools.mapreduce.MapReduceSpecification;
import com.google.appengine.tools.mapreduce.inputs.DatastoreInput;
import com.google.appengine.tools.mapreduce.outputs.NoOutput;

import de.oth.app.geekquest.mapreduce.CopyCharClassMapper;
import de.oth.app.geekquest.mapreduce.CopyCharClassReducer;

public class CopyCharClassToMissionMapReduceServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 6645857461012355729L;
    
    private final UserService userService = UserServiceFactory.getUserService();

    private void writeResponse(HttpServletResponse resp) throws IOException {

        try (PrintWriter pw = new PrintWriter(resp.getOutputStream())) {
            pw.println("<html><body>" + "<br><form method='post'>"
                    + "Copy charclass from character to all of its missions:" + "<div> <br />"
                    + "ShardCount: <input name='shardCount' value='3'> <br />"
                    + "ReducerCount: <input name='reducerCount' value='3'> <br />"
                    + "<br /> <input type='submit' value='Copy charclass'>"
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
        int reducerCount = Integer.parseInt(req.getParameter("reducerCount"));

        MapReduceSpecification<Entity, Key, Key, Void, Void> mapReduceSpec = 
                getJobSpec(shardCount, reducerCount);
        MapReduceSettings settings = getSettings();

        String id = MapReduceJob.start(mapReduceSpec, settings);

        redirectToPipelineStatus(resp, id);

    }

    private MapReduceSpecification<Entity, Key, Key, Void, Void> getJobSpec(
            int mapShardCount, int reducerCount) {
        
        DatastoreInput input = new DatastoreInput("Mission",
                3);//mapShardCount);
        CopyCharClassMapper mapper = new CopyCharClassMapper();
        CopyCharClassReducer reducer = new CopyCharClassReducer();
        NoOutput<Void, Void> output = new NoOutput<>();
        return new MapReduceSpecification.Builder<>(input, mapper, reducer, output)
                .setNumReducers(reducerCount)
                .setJobName("Copy charclass to missions").build();
    }

    private MapReduceSettings getSettings() {
        String bucket = AppIdentityServiceFactory.getAppIdentityService().getDefaultGcsBucketName();
        MapReduceSettings settings = new MapReduceSettings.Builder()
                .setWorkerQueueName("mapreduce-workers")
                .setBucketName(bucket)
                //.setModule("mapreduce")
                .build();
        return settings;
    }

}
