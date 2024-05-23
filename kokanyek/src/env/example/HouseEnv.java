package example;

// Environment code for project kokanyek

import jason.asSyntax.*;
import jason.environment.*;
import jason.asSyntax.parser.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Struct;
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

import java.util.Timer;
import java.util.TimerTask;

public class HouseEnv extends Environment {

    public static final int GSize = 15; // grid size
    public static final int GARB = 16;

    public static final Literal eat = Literal.parseLiteral("eat");
    public static final Literal work = Literal.parseLiteral("work");
    public static final Literal read = Literal.parseLiteral("read");
    public static final Literal cleanVacuum = Literal.parseLiteral("cleanVacuum");
    public static final Literal call = Literal.parseLiteral("call(911)");
    public static final Literal stopCall = Literal.parseLiteral("stopCall");
    public static final Literal sleep = Literal.parseLiteral("sleep");
    public static final Literal morning = Literal.parseLiteral("morning");
    public static final Literal duringDay = Literal.parseLiteral("duringDay");
    public static final Literal evening = Literal.parseLiteral("evening");
    public static final Literal lateEvening = Literal.parseLiteral("lateEvening");
    public static final Literal night = Literal.parseLiteral("night");
    public static final Literal alarm = Literal.parseLiteral("alarm");
    public static final Literal humanInTheHouse = Literal.parseLiteral("humanInTheHouse");
    public static final Literal bagFull = Literal.parseLiteral("bagFull");
    public static final Literal ready = Literal.parseLiteral("ready");
    public static final Literal docked = Literal.parseLiteral("docked");
    public static final Literal cleanDirtyFloor = Literal.parseLiteral("cleanDirtyFloor");
    public static final Literal emptyVacuumBag = Literal.parseLiteral("emptyVacuumBag");
    public static final Literal goHome = Literal.parseLiteral("goHome");





    private Logger logger = Logger.getLogger("kokanyek."+HouseEnv.class.getName());

    private HouseModel model;
    private HouseView  view;

    private int time = 0;

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

        startTimer();
        model.vacuumDocked = true;
        model.humanSleeping = true;
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeFliesBy();
            }
        }, 0, 200); // 200 milliseconds delay before the first execution, and 200 milliseconds between subsequent executions
    }

    private void timeFliesBy() {
        time++;
        updatePercepts();
        informAgsEnvironmentChanged();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        //logger.info(ag+" doing: "+ action);
        try {
            if (action.getFunctor().equals("human_move_towards")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.humanDoNothing();
                model.humanWalking = true;
                model.humanMoveTowards(x,y);
            }else if (action.getFunctor().equals("vacuum_move_towards")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.vacuumDoNothing();
                model.vacuumGoing = true;
                model.vacuumMoveTowards(x,y);
            }else if (action.equals(eat)) {
                model.humanDoNothing();
                model.humanEating = true;
            } else if (action.equals(work)) {
                model.humanDoNothing();
                model.humanWorking = true;
            } else if (action.equals(read)) {
                model.humanDoNothing();
                model.humanReading  = true;
            } else if (action.equals(cleanVacuum)) {
                model.vacuumBag = 0;

            } else if (action.equals(call)) {
                model.humanDoNothing();
                model.humanCalling = true;
            } else if (action.equals(stopCall)) {
                model.humanDoNothing();
                model.humanCalling = false;
            } else if (action.equals(sleep)) {
                model.humanDoNothing();
                model.humanSleeping = true;
            } else if (action.equals(ready)) {
                model.vacuumDoNothing();
                model.vacuumReady = true;
            } else if (action.equals(docked)) {
                model.vacuumDoNothing();
                model.vacuumDocked = true;
            } else if (action.equals(cleanDirtyFloor)) {
                model.vacuumBag++;
                model.remove(GARB, model.garbageX, model.garbageY);
                view.update(model.garbageX, model.garbageY);
                if(model.vacuumBag < 3)
                    model.vacuumShouldGoHome = true;
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

        Location humanLoc = model.getAgPos(0);
        Location vacuumLoc = model.getAgPos(1);

        Literal pos1 = Literal.parseLiteral("pos(human," + humanLoc.x + "," + humanLoc.y + ")");
        Literal pos2 = Literal.parseLiteral("pos(vacuum," + vacuumLoc.x + "," + vacuumLoc.y + ")");
        addPercept(pos1);
        addPercept(pos2);
        //System.out.println("time: " + time);
        //This is important for human's day routine
        if(time == 10)
            addPercept(morning);
        if(time == 30)
            addPercept(duringDay);
        if(time == 60)
            addPercept(evening);
        if(time == 90)
            addPercept(lateEvening);
        if(time == 120)
            addPercept(night);
        if(time == 150)
            time = 0;
        removePercept(emptyVacuumBag);

        if(model.alarmIsOn)
            addPercept(alarm);
        else
            removePercept(alarm);

        if(humanLoc.x > 12 && humanLoc.y > 12)
            removePercept(humanInTheHouse);
        else
            addPercept(humanInTheHouse);

        if(model.floorIsDirty){
            Literal needForClean = Literal.parseLiteral("needToClean(" + model.garbageX + "," + model.garbageY + ")");
            addPercept(needForClean);
            model.floorIsDirty = false;
        }
        if(model.vacuumShouldGoHome){
            addPercept(goHome);
            model.vacuumShouldGoHome = false;
        }

        if(model.vacuumBag == 3)
            addPercept(bagFull);

    }
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }

    class HouseModel extends GridWorldModel {

        public boolean humanEating = false;
        public boolean humanWorking = false;
        public boolean humanReading = false;
        public boolean humanSleeping = false;
        public boolean humanVacuum = false;
        public boolean humanCalling = false;
        public boolean humanWalking = false;
        public boolean alarmIsOn = false;
        public int vacuumBag = 0;
        public boolean vacuumReady = false;
        public boolean vacuumDocked = false;
        public int garbageX = 0;
        public int garbageY = 0;
        public boolean floorIsDirty = false;
        public boolean vacuumGoing = false;
        public boolean vacuumShouldGoHome = false;


        Random random = new Random(System.currentTimeMillis());

        private HouseModel() {
            super(GSize, GSize, 3);

            // initial location of agents
            try {
                setAgPos(0, 1, 1);
                setAgPos(1,13,8);

            } catch (Exception e) {
                e.printStackTrace();
            }

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

        void humanMoveTowards(int x, int y) throws Exception {
            Location humanLoc = getAgPos(0);
            if (humanLoc.x < x)
                humanLoc.x++;
            else if (humanLoc.x > x)
                humanLoc.x--;
            if (humanLoc.y < y)
                humanLoc.y++;
            else if (humanLoc.y > y)
                humanLoc.y--;
            setAgPos(0, humanLoc);
        }

        void vacuumMoveTowards(int x, int y) throws Exception {
            Location vacuumLoc = getAgPos(1);
            if (vacuumLoc.x < x)
                vacuumLoc.x++;
            else if (vacuumLoc.x > x)
                vacuumLoc.x--;
            if (vacuumLoc.y < y)
                vacuumLoc.y++;
            else if (vacuumLoc.y > y)
                vacuumLoc.y--;
            setAgPos(1, vacuumLoc);
        }

        void humanDoNothing(){
            humanEating = false;
            humanSleeping = false;
            humanReading = false;
            humanVacuum = false;
            humanWorking = false;
            humanWalking = false;
        }
        void vacuumDoNothing(){
            vacuumDocked = false;
            vacuumReady = false;
            vacuumGoing = false;
        }

        void cleanGarbageAt(int x, int y){
            floorIsDirty = true;
            garbageX = x;
            garbageY = y;
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
                        ((HouseModel)model).cleanGarbageAt(col, lin);
                    }
                }
                public void mouseExited(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mousePressed(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
            });
            getCanvas().addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // Check if the pressed key is 'a'
                    if (e.getKeyChar() == 'a') {
                        // Call the alarmPushed function
                        ((HouseModel) model).alarmIsOn = !((HouseModel) model).alarmIsOn;
                    }
                }
                @Override
                public void keyTyped(KeyEvent e) {
                    // Not used in this example
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    // Not used in this example
                }
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
            String label = "";
            if (id == 0) {
                label = "Human";
                if (((HouseModel)model).humanEating) {
                    label += " - Eating";
                    c = Color.RED;
                }
                if (((HouseModel)model).humanWorking) {
                    label += " - Working";
                    c = Color.RED;
                }
                if (((HouseModel)model).humanReading) {
                    label += " - Reading";
                    c = Color.RED;
                }
                if (((HouseModel)model).humanCalling) {
                    label += " - Calling 911";
                    c = Color.RED;
                }
                if (((HouseModel)model).humanSleeping) {
                    label += " - Sleeping";
                    c = Color.RED;
                }
                if (((HouseModel)model).humanWalking) {
                    label += " - Walking";
                    c = Color.GREEN;
                }
            }
            if(id == 1) {
                label = "Vacuum";
                if (((HouseModel)model).vacuumReady) {
                    label += " - Ready";
                    c = Color.GREEN;
                }
                if (((HouseModel)model).vacuumDocked) {
                    label += " - Charging";
                    c = Color.YELLOW;
                }
                if (((HouseModel)model).vacuumGoing) {
                    label += " - Going";
                    c = Color.BLUE;
                }
                if (((HouseModel)model).vacuumBag == 3) {
                    label = "Vacuum - FULL";
                    c = Color.RED;
                }

            }
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) {
                g.setColor(Color.black);
            } else {
                g.setColor(Color.black);
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

