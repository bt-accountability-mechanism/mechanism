package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogzLogHandler extends Handler {

	public LogzLogHandler() {
		super();
		this.setFormatter(new SimpleFormatter());
	}
	
	@Override
	public void publish(LogRecord record) {
		if (record != null) {
			(new Runnable() {

				@Override
				public void run() {
					String message = getFormatter().formatMessage(record);
					String[] params = message.split(";");
					SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
					SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						String startTime = newFormat.format(oldFormat.parse(params[0]));
						String endTime = newFormat.format(oldFormat.parse(params[1]));
						String body = "{\"token\":\"TtVnQZXFKDwVhPwVseSUbOomQFTLHeZR\",\"type\":\"logmechanism\",\"message\":\"" + message + "\",\"logalg_dt_start\":\"" + startTime + "\",\"logalg_dt_end\":\"" + endTime + "\",\"logalg_matched_name\":\"" + params[2] + "\",\"logalg_start\":\"" + params[3] + "\",\"logalg_end\":\"" + params[4] + "\"}";
						URL url = new URL( "https://listener.logz.io:8091" );
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod( "POST" );
						connection.setDoInput( true );
						connection.setDoOutput( true );
						connection.setUseCaches( false );
						connection.setRequestProperty( "Content-Type",
						                               "application/json" );
						connection.setRequestProperty( "Content-Length", String.valueOf(body.length()) );
						
						OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
						writer.write( body );
						writer.flush();


						BufferedReader reader = new BufferedReader(
						                          new InputStreamReader(connection.getInputStream()) );

						writer.close();
						reader.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
			}).run();
		}
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
		
	}

}
