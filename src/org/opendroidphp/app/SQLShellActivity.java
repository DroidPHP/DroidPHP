package org.opendroidphp.app;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import org.opendroidphp.app.common.utils.ShellOutputFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class SQLShellActivity extends SherlockActivity {

    private Button runCommand;
    private TextView shellResult;

    private static Process proc;
    private static InputStream stdout;
    private static OutputStream stdin;

    //private static Shell.Interactive sqlSession;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlshell);
        handler = new Handler();
        runCommand = (Button) findViewById(R.id.run_cmd);
        shellResult = (TextView) findViewById(R.id.shell_result);

        if (proc == null) {
            initialize();
        }

        runCommand.setOnClickListener(executeQuery);
        (new SyncSQLResultTask()).execute();
    }

    protected void initialize() {

        if (proc != null) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String username = preferences.getString("mysql_username", "root");
        String password = preferences.getString("mysql_password", "");

        String[] baseShell = new String[]{
                Constants.MYSQL_MONITOR_SBIN_LOCATION, "-h",
                "127.0.0.1", "-T", "-f", "-r", "-t", "-E", "--disable-pager",
                "-n", "--user=" + username, "--password=" + password,
                "--default-character-set=utf8", "-L"};

        try {

            proc = new ProcessBuilder(baseShell).
                    redirectErrorStream(true).
                    start();

            stdin = proc.getOutputStream();
            stdout = proc.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();

        }

        /*sqlSession = new Shell.Builder().
                useSH().
                //setShell(baseShell).
                setWantSTDERR(true).
                setWatchdogTimeout(5).
                setMinimalLogging(true).
                open(new Shell.OnCommandResultListener() {


                    @Override
                    public void onCommandResult(int commandCode, final int exitCode, final List<String> output) {

                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (output != null) {
                                        for (String error : output) {
                                            shellResult.append(error);
                                        }
                                    }
                                }
                            });

                        } else {

                        }

                    }
                });*/
    }

    private class SyncSQLResultTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BufferedReader buff = new BufferedReader(
                    new InputStreamReader(stdout));

            try {
                while (true) {

                    String READ = ShellOutputFormatter.toHTML(buff.readLine());
                    if (READ == null) {
                        break;
                    }
                    publishProgress(READ);

                }
                buff.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            shellResult.append(ShellOutputFormatter.toHTML(values[0]));
        }
    }


    private final View.OnClickListener executeQuery = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            try {

                String command = ((EditText) findViewById(R.id.command))
                        .getText()
                        .toString();

                try {
                    stdin.write((command + "\r\n").getBytes());
                    stdin.flush();
                } catch (Exception e) {

                }

              /*  sqlSession.addCommand(command, new Shell.OnCommandResultListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, final List<String> output) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (output != null) {
                                    for (String error : output) {
                                        shellResult.append(error);
                                    }
                                }
                            }
                        });
                    }
                });*/
            } catch (NullPointerException e) {

            }


        }
    };
}