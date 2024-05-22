import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import jason.environment.grid.GridWorldView;

class FloorView extends GridWorldView {

	public FloorView(final FloorModel model) {
        super(model, "Intelligent vaccum system", 720);
        defaultFont = new Font("Comic Sans MS", Font.PLAIN, 24);
        setVisible(true);
        repaint();
	}
	
	public void addClickListener(final Floor building) {
		getCanvas().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int column = e.getX() / cellSizeW;
				int row = e.getY() / cellSizeH;
				if (column >= 0 && row >= 0 && column < getModel().getWidth() && row < getModel().getHeight()) {
					building.add_garbage(column, row);
					update(column, row);
					System.out.println("Garbage generated at (" + column + "," + row + ")");
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

		});
	}
	
    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case 16: 
            	g.setColor(new Color(102,51,0));
            	g.fillOval((int)(cellSizeW * x + 0.1*cellSizeH), (int)(cellSizeH * y + 0.1*cellSizeH), (int)(cellSizeW*0.8), (int)(cellSizeH*0.8));
                g.setColor(Color.white);
                drawString(g, x, y, defaultFont, "Dirt"); 
                break;                                                                                  
            case 8: 
            	g.setColor(Color.black);
            	g.fillRect(cellSizeW * x, cellSizeH * y, cellSizeW, cellSizeH);
            	g.setColor(Color.white);
                drawString(g, x, y, defaultFont, "Base");
                break;
        }
    }

	@Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        super.drawAgent(g, x, y, Color.blue, -1);
        g.setColor(Color.white);
        String name;
        switch(id) {
        case 1: 
        	name = "A"; 
        	break;
        case 0: 
        	name = "B"; 
        	break;
        default: 
        	name = "C"; 
        	break;
        }

        super.drawString(g, x, y, defaultFont, name);
    }
} 