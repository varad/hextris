package net.hextris;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * maintains a highscore table and offers functions for uploading and reading highscores from the server 
 * 
 * @author fränk
 * 
 */
public class ScoreList 
{
	private static final long serialVersionUID = 1481294261049397840L;
	private static int maxSize = 100;
	private static URL baseURL;
	private static ArrayList<Score> scoreList = null;//new ScoreList();
	

	/**
	 * returns the scorelist
	 * if necessary ceates and initializes a new scorelist
	 * @return
	 */
	public static ArrayList<Score> getScoreList()
	{
		if (scoreList==null) {
			scoreList=new ArrayList<Score>(maxSize);
			Properties prop = new Properties();
			try {
	            InputStream inS = ScoreList.class.getClassLoader().getResourceAsStream("net/hextris/hextris.properties");
	            if (inS!=null) prop.load(inS);
	            setBaseURL(new URL(prop.getProperty("hextris.ServerScriptUrl","")));
	            read();
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			
		}
		return scoreList;
	}
	
	/**
	 * Adds the score s into the highscore list. If the score is too low it
	 * won't be added.
	 * 
	 * @param s
	 * @return
	 */
//	public boolean add(Score s) {
//		int c;
//		for (c = 0; c < size() && s.getScore() <= ((Score) get(c)).getScore(); c++) {
//		}
//		if (c < maxSize) {
//			add(c, s);
//		}
//		return true;
//	}

	/**
	 * upload a new score to the server
	 */
	public static void upload(Score s) 
	{
		OutputStreamWriter wr = null;
		try {
	        // post data
	        String data = URLEncoder.encode("name", "ISO-8859-1") + "=" + URLEncoder.encode(s.getName(), "ISO-8859-1");
	        data += "&" + URLEncoder.encode("score", "ISO-8859-1") + "=" + URLEncoder.encode(new Integer(s.getScore()).toString(), "ISO-8859-1");
	        // post senden
	        HttpURLConnection conn = (HttpURLConnection)getBaseURL().openConnection();
	        conn.setDoOutput(true);
	        wr = new OutputStreamWriter(conn.getOutputStream());
	        wr.write(data);
	        wr.flush();
	        wr.close();
			read(conn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * reads the scorelist from from the server
	 * 
	 * @param conn use this connection
	 */
	public static boolean read(HttpURLConnection conn) 
	{
		ArrayList<Score> scoreList = ScoreList.getScoreList();
		scoreList.clear();
		try {
			InputStream is = null;
			if (conn==null) is = baseURL.openStream();
			else is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"ISO-8859-1"));
			String line = "";
			int count=0;
			while ((line = br.readLine()) != null && ++count<=maxSize) {
				if (line.trim().length()==0) continue;
				StringTokenizer tok = new StringTokenizer(line, ";");
				int score = Integer.valueOf(tok.nextToken());
				String name = tok.nextToken();
				//System.out.println(name + " " + score);
				scoreList.add(new Score(name, score));
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * reads scorelist from the server
	 * @return
	 */
	public static boolean read() 
	{
		return read(null);
	}

	public static int getMaxSize() {
		return maxSize;
	}

	/**
	 * returns the least score in the scorelist
	 * @return
	 */
	public static int getMinScore() {
		int s = scoreList.size();
		if (s >= maxSize) {
			return ((Score) scoreList.get(scoreList.size() - 1)).getScore();
		} else {
			return 0;
		}
	}

	/**
	 * the url of the scorelist
	 * @return
	 */
	private static URL getBaseURL() {
		return baseURL;
	}

	/**
	 * @param url
	 */
	private static void setBaseURL(URL url) {
		baseURL = url;
	}

	/**
	 * returns the place in the scorelist for the given score
	 * @param score
	 * @return
	 */
	public static int getPlace(int score)
	{
		int res = scoreList.size()+1;
		for (int i = 0; i<scoreList.size(); i++) {
			if (score>scoreList.get(i).getScore()) 
			{
				res = i+1;
				break;
			}
		}
		return res;
	}
}
