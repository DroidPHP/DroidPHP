package org.opendroidphp.app.common.tasks;

import android.os.AsyncTask;

import org.opendroidphp.app.ComponentExecutorPool;

/**
 * Created by shushant on 3/19/14.
 */
public class ExecuteServerComponents extends AsyncTask<Void, Void, Void> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //migrated to HomeActivity.class

        /*List<ComponentProviderInterface> executor = null;
        //server components to be registered
        executor.add(new LighttpdExecutor());
        executor.add(new PHPExecutor());
        executor.add(new MySqlExecutor());

        ComponentExecutorPool.registerExecutor(executor);*/
    }

    @Override
    protected Void doInBackground(Void... voids) {

        //connect to all the components
        try {
            ComponentExecutorPool.connectAll();
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
