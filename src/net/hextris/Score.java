package net.hextris;

import java.io.Serializable;

/**
 * simply contains a highscore entry
 * name and score
 * 
 * @author frank
 *
 */
public class Score implements Serializable
{
	private static final long serialVersionUID = 1071198925649496574L;
	private String name;
	private int score;
	
	public Score() {
		name="";
		score=0;
	}
	
	public Score(String name,int score) {
		this.name=name.trim();
		this.score=score;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setName(String string) {
		name = string.trim();
	}

	public void setScore(int i) {
		score = i;
	}

	public String toString()
	{
		return "      ".substring(new Integer(this.score).toString().length()) + this.score + " " + this.name;
	}

}

