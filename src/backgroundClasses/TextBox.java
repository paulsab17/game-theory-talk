package backgroundClasses;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TextBox extends Sprite{
	private String text;
	private String[] multiText;
	private int width;
	private int height;
	private Color backColor;
	private boolean drawBackground;
	private boolean multiline;
	private Color textColor;
	private int fontSize;

	public TextBox(String words,int x, int y,Color bC,Color tC,int fs){
		text = words;
		backColor = bC;
		textColor = tC;
		xPos = x;
		yPos = y;
		drawBackground = true;
		multiline = false;
		fontSize = fs;
	}
	public TextBox(String words,int x, int y,Color bC,Color tC){
		this(words,x,y,bC,tC,40);
	}
	public TextBox(String words,int x, int y,Color tC,int size){
		text = words;
		drawBackground = false;
		multiline = false;
		textColor = tC;
		xPos = x;
		yPos = y;
		fontSize = size;
	}
	public TextBox(String words,int x, int y,Color tC){
		this(words,x,y,tC,40);
	}
	public TextBox(String[] words,int x,int y,Color bC,Color tC,int size){
		multiText = words;
		backColor = bC;
		textColor = tC;
		xPos = x;
		yPos = y;
		drawBackground = true;
		multiline = true;
		fontSize = size;
	}
	public TextBox(String[] words,int x, int y,Color tC,int size){
		multiText = words;
		drawBackground = false;
		textColor = tC;
		xPos = x;
		yPos = y;
		multiline = true;
		fontSize = size;
	}
	public TextBox(String[] words,int x, int y,Color tC){
		this(words,x,y,tC,40);
	}

	public void setText(String newText){
		multiline = false;
		text = newText;
	}
	public void setText(String[] newText){
		multiline = true;
		multiText = newText;
	}

	protected void draw(Graphics gvar){
		if(!multiline){
			Graphics2D g2 = (Graphics2D) gvar;

			Font textFont = new Font("DialogueInput",Font.BOLD,fontSize);
			g2.setFont(textFont);

			FontMetrics font = g2.getFontMetrics();
			int stringWidth = font.stringWidth(text);
			width = stringWidth + 40;
			height = font.getHeight()+10;

			Rectangle background = new Rectangle((int)(xPos-width/2),(int)(yPos-height/2),width,height);
			setShape(background);
			if(drawBackground){
				g2.setColor(backColor);
				g2.fill(background);
				g2.setColor(textColor);
				g2.draw(background);
			}
			g2.setColor(textColor);
			g2.drawString(text,(int)(xPos-width/2)+20,(int)(yPos)+20);
		}else{
			Graphics2D g2 = (Graphics2D) gvar;

			Font textFont = new Font("DialogueInput",Font.BOLD,fontSize);
			g2.setFont(textFont);

			FontMetrics font = g2.getFontMetrics();
			int stringWidth = 0;
			for(int i=0;i<multiText.length;i++){
				stringWidth = Math.max(stringWidth,font.stringWidth(multiText[i]));
			}
			width = stringWidth + 40;
			int stringHeight = font.getHeight();
			height = stringHeight*multiText.length+10;

			Rectangle background = new Rectangle((int)(xPos-width/2),(int)(yPos-height/2),width,height);
			setShape(background);
			if(drawBackground){
				g2.setColor(backColor);
				g2.fill(background);
				g2.setColor(textColor);
				g2.draw(background);
			}
			g2.setColor(textColor);
			for(int i=0;i<multiText.length;i++){
				g2.drawString(multiText[i],(int)(xPos-width/2)+20,(int)(yPos-height/2)+(i+1)*stringHeight);
			}

		}
	}
}
