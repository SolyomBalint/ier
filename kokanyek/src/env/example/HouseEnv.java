package example;

// Environment code for project kokanyek

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.*;

import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

public class HouseEnv extends Environment {

    public static final int GSize = 15; // grid size
    public static final int GARB  = 16; // garbage code in grid model

    public static final Term    ns = Literal.parseLiteral("next(slot)");
    public static final Term    pg = Literal.parseLiteral("pick(garb)");
    public static final Term    dg = Literal.parseLiteral("drop(garb)");
    public static final Term    bg = Literal.parseLiteral("burn(garb)");
    public static final Literal g1 = Literal.parseLiteral("garbage(r1)");
    public static final Literal g2 = Literal.parseLiteral("garbage(r2)");


    private Logger logger = Logger.getLogger("kokanyek."+HouseEnv.class.getName());

    private HouseModel model;
    private HouseView  view;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        model = new HouseModel();
        view  = new HouseView(model);
        try {
            addPercept(ASSyntax.parseLiteral("percept("+args[0]+")"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        model.setView(view);
        updatePercepts();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" doing: "+ action);
        try {
            if (action.equals(ns)) {
                model.nextSlot();
            } else if (action.getFunctor().equals("move_towards")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.moveTowards(x,y);
            } else if (action.equals(pg)) {
                //model.pickGarb();
            } else if (action.equals(dg)) {
                //model.dropGarb();
            } else if (action.equals(bg)) {
                //model.burnGarb();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePercepts();

        try {
            Thread.sleep(200);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }
    //Updates scene
    void updatePercepts() {
        clearPercepts();

        Location r1Loc = model.getAgPos(0);
        Location r2Loc = model.getAgPos(1);

        Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y + ")");
        Literal pos2 = Literal.parseLiteral("pos(r2," + r2Loc.x + "," + r2Loc.y + ")");

        addPercept(pos1);
        addPercept(pos2);

        if (model.hasObject(GARB, r1Loc)) {
            addPercept(g1);
        }
        if (model.hasObject(GARB, r2Loc)) {
            addPercept(g2);
        }
    }
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }

    class HouseModel extends GridWorldModel {

        public static final int MErr = 2; // max error in pick garb
        int nerr; // number of tries of pick garb
        boolean r1HasGarb = false; // whether r1 is carrying garbage or not

        Random random = new Random(System.currentTimeMillis());

        private HouseModel() {
            super(GSize, GSize, 2);

            // initial location of agents
            try {
                setAgPos(0, 0, 0);

                Location r2Loc = new Location(GSize/2, GSize/2);
                setAgPos(1, r2Loc);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // initial location of garbage
            //add(GARB, 3, 0);
            //add(GARB, GSize-1, 0);
            //add(GARB, 1, 2);
            //add(GARB, 0, GSize-2);
            //add(GARB, GSize-1, GSize-1);
            for (int i = 0; i < GSize; i++) {
                add(OBSTACLE, 0, i);
            }
            for (int i = 0; i < GSize; i++) {
                add(OBSTACLE, i, 0);
            }
            for (int i = 0; i < GSize-3; i++) {
                add(OBSTACLE, i, GSize-1);
            }
            for (int i = 0; i < GSize-3; i++) {
                add(OBSTACLE, GSize-1, i);
            }
            for (int i = 0; i < GSize/2+1; i++) {
                add(OBSTACLE,GSize/2,i);
            }

            add(OBSTACLE,GSize-2,GSize/2);
            add(OBSTACLE,GSize-3,GSize/2);
            add(OBSTACLE,GSize-4,GSize/2);
            add(OBSTACLE,GSize-5,GSize/2);

            add(OBSTACLE,1,GSize/2);
            add(OBSTACLE,2,GSize/2);
            add(OBSTACLE,3,GSize/2);
            add(OBSTACLE,4,GSize/2);

            add(OBSTACLE,GSize-2,GSize-4);
            add(OBSTACLE,GSize-3,GSize-4);
            add(OBSTACLE,GSize-4,GSize-4);


        }

        void nextSlot() throws Exception {
            Location r1 = getAgPos(0);
            r1.x++;
            if (r1.x == getWidth()) {
                r1.x = 0;
                r1.y++;
            }
            // finished searching the whole grid
            if (r1.y == getHeight()) {
                return;
            }
            setAgPos(0, r1);
            setAgPos(1, getAgPos(1)); // just to draw it in the view
        }

        void moveTowards(int x, int y) throws Exception {
            Location r1 = getAgPos(0);
            if (r1.x < x)
                r1.x++;
            else if (r1.x > x)
                r1.x--;
            if (r1.y < y)
                r1.y++;
            else if (r1.y > y)
                r1.y--;
            setAgPos(0, r1);
            setAgPos(1, getAgPos(1)); // just to draw it in the view
        }

        void pickGarb() {
            // r1 location has garbage
            if (model.hasObject(GARB, getAgPos(0))) {
                // sometimes the "picking" action doesn't work
                // but never more than MErr times
                if (random.nextBoolean() || nerr == MErr) {
                    remove(GARB, getAgPos(0));
                    nerr = 0;
                    r1HasGarb = true;
                } else {
                    nerr++;
                }
            }
        }
        void dropGarb() {
            if (r1HasGarb) {
                r1HasGarb = false;
                add(GARB, getAgPos(0));
            }
        }
        void burnGarb() {
            // r2 location has garbage
            if (model.hasObject(GARB, getAgPos(1))) {
                remove(GARB, getAgPos(1));
            }
        }
    }

    class HouseView extends GridWorldView {

        public HouseView(HouseModel model) {
            super(model, "Hosue World", 600);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }

        @Override
        public void initComponents(int width) {
            super.initComponents(width);

            getCanvas().addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    int col = e.getX() / cellSizeW;
                    int lin = e.getY() / cellSizeH;
                    if (col >= 0 && lin >= 0 && col < getModel().getWidth() && lin < getModel().getHeight()) {
                        HouseModel hm = (HouseModel) model;
                        hm.add(GARB, col, lin);
                        update(col, lin);
                    }
                }
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
            });
        }

        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
                case HouseEnv.GARB:
                    drawGarb(g, x, y);
                    break;
            }
        }

        @Override
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "R"+(id+1);
            c = Color.blue;
            if (id == 0) {
                c = Color.yellow;
                if (((HouseModel)model).r1HasGarb) {
                    label += " - G";
                    c = Color.orange;
                }
            }
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.white);
            }
            super.drawString(g, x, y, defaultFont, label);
            repaint();
        }

        public void drawGarb(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y);
            g.setColor(Color.white);
            drawString(g, x, y, defaultFont, "G");
        }



    }
}

