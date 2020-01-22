package backgroundClasses;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComponent;

public class SpriteImage extends JComponent
{
	private ArrayList<Sprite> sprites;
	
	public SpriteImage(ArrayList<Sprite> spriteObjs){
		sprites = spriteObjs;
	}
	
	public void paintComponent(Graphics g)
	{
		for(int i = 0;i<sprites.size();i++){
			sprites.get(i).draw(g);
		}
	}
	private static final long serialVersionUID = -1836184437435110915L;
}
