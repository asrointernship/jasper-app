/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.socialmap.ui;

import be.kuleuven.socialmap.database.Database;
import be.kuleuven.socialmap.exceptions.SocialMapException;
import be.kuleuven.socialmap.io.StaticIO;
import be.kuleuven.socialmap.model.*;
import be.kuleuven.socialmap.util.DataHelper;
import be.kuleuven.socialmap.util.GridHash;
import codeanticode.glgraphics.GLGraphics;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;
import com.modestmaps.StaticMap;
import com.modestmaps.core.Point2f;
import com.modestmaps.geo.Location;
import com.modestmaps.providers.Microsoft;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * The Processing applet where the visualization will happen.
 *
 * @author Jasper Moeys
 */
public class MapApplet extends PApplet {

    private GridHash<MapReduceResult> mrGridFs;
    private GridHash<MapReduceResult> mrGridFl;
    private GridHash<MapReduceResult> mrGridTw;
    private GridHash<MapReduceResult> mrGridIn;
    private GridHash<FoursquareVenue> gridFs;
    private GridHash<FlickrPhoto> gridFl;
    private GridHash<Tweet> gridTw;
    private GridHash<InstagramPhoto> gridIn;
    private List<MapReduceResult> mrListFs = new ArrayList<MapReduceResult>();
    private List<MapReduceResult> mrListFl = new ArrayList<MapReduceResult>();
    private List<MapReduceResult> mrListTw = new ArrayList<MapReduceResult>();
    private List<MapReduceResult> mrListIn = new ArrayList<MapReduceResult>();
    private List<FoursquareVenue> listFs;
    private List<FlickrPhoto> listFl ;
    private List<Tweet> listTw;
    private List<InstagramPhoto> listIn;
    private final PImage displayEmpty = new PImage();
    private PImage displayFs = displayEmpty;
    private PImage displayTw = displayEmpty;
    private PImage displayFl = displayEmpty;
    private PImage displayIn = displayEmpty;
    private PImage displayMap = displayEmpty;
    private PImage displayLegend = displayEmpty;
    private PImage displayLayers = displayEmpty;
    private boolean drawFoursquare, drawTwitter, drawInstagram, drawFlickr;
    private Location location;
    private int zoom;
    private StaticMap map;
    private boolean refresh, refreshMap, refreshFlickr, refreshFoursquare, refreshTwitter, refreshInstagram, showMap, showLegend, save;
    private boolean mapReduced = true;
    private boolean drawTextures = false;
    private Point2f pressedLocation;
    private GLTexture twitterDot, fsDot, flickrDot, instaDot;
    private int flickrColor, twitterColor, fsColor, instaColor;
    private PImage img;
    private GLTextureFilter filter;
    private int[] zoomToSize = {2, 3, 3, 5, 5, 5, 7, 7};
    private String filename;
    private MainWindow parent;
    private GLGraphicsOffScreen offscreen, offscreenMap, offscreenLegend, offscreenFlickr, offscreenFs, offscreenTwitter, offscreenInsta;

    public MapApplet(MainWindow parent){
        try {
            this.parent = parent;
            
            DataHelper helper = DataHelper.getInstance();
            mrListFs = helper.getMapReducedFoursquareVenues();
            mrListTw = helper.getMapReducedTweets();
            mrListFl = helper.getMapReducedFlickrPhotos();
            mrListIn = helper.getMapReducedInstagramPhotos();
            
//            listFs = helper.getFoursquareVenues();
//            listTw = helper.getTweets();
//            listFl = helper.getFlickrphotos();
//            listIn = helper.getInstagramPhotos();
        } catch (SocialMapException ex) {
            Logger.getLogger(MapApplet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void setup() {
//        try {
            size(700, 580, GLGraphics.GLGRAPHICS);
            zoom = 8;
            location = new Location(50.5f, 4.5f);

//            img = loadImage("belgium.gif");

            twitterDot = new GLTexture(this, StaticIO.getPath("blue.png"));
            fsDot = new GLTexture(this, StaticIO.getPath("green.png"));
            flickrDot = new GLTexture(this, StaticIO.getPath("red.png"));
            instaDot = fsDot;
            twitterColor = color(10,20,70);
            fsColor = color(10,70,20);
            flickrColor = color(70,20,10);
            instaColor = fsColor;
//            twitterColor = color(0,0,70);
//            fsColor = color(0,70,0);
//            flickrColor = color(70,0,0);
//            instaColor = fsColor;
            
            filter = new GLTextureFilter(this, StaticIO.getPath("grayscale.xml"));

//            Database<? extends Plottable> database = DatabaseFactory.getDatabase(MapReduceResult.class, FoursquareVenue.class);
//            coords = database.getAll();

            frameRate(4);
            
            drawFoursquare = true;
            refreshFoursquare = true;
            refresh = true;
            
            offscreen = new GLGraphicsOffScreen(this, width, height);
            offscreenMap = new GLGraphicsOffScreen(this, width, height);
            offscreenLegend = new GLGraphicsOffScreen(this, width, height);
            offscreenFlickr = new GLGraphicsOffScreen(this, width, height);
            offscreenFs = new GLGraphicsOffScreen(this, width, height);
            offscreenTwitter = new GLGraphicsOffScreen(this, width, height);
            offscreenInsta = new GLGraphicsOffScreen(this, width, height);
            
            mrGridFs = new GridHash<MapReduceResult>(width, height, 10);
            mrGridFl = new GridHash<MapReduceResult>(width, height, 10);
            mrGridTw = new GridHash<MapReduceResult>(width, height, 10);
            mrGridIn = new GridHash<MapReduceResult>(width, height, 10);

            this.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    pressedLocation = new Point2f(mouseX, mouseY);
//                    println(map.pointLocation(pressedLocation));
                    //location = map.pointLocation(new Point2f(mouseX, mouseY));
                    moveCursor();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    Point2f releasedLocation = new Point2f(mouseX, mouseY);
                    if(pressedLocation != null){
                        if (!(releasedLocation.x == pressedLocation.x && releasedLocation.y == pressedLocation.y)) {
                            float dx = releasedLocation.x - pressedLocation.x;
                            float dy = releasedLocation.y - pressedLocation.y;
                            Point2f center = new Point2f(width / 2, height / 2);
                            center.x -= dx;
                            center.y -= dy;
                            location = map.pointLocation(center);
                            refreshAll();
                        } else {
                            normalCursor();
                            if(drawFoursquare){
                                List<FoursquareVenue> clicked = getClickedFs(releasedLocation);
                                if(clicked.size()>0) new FoursquareDialog(clicked);
                            }
                            if(drawFlickr){
                                List<FlickrPhoto> clicked = getClickedFl(releasedLocation);
                                if(clicked.size()>0) new FlickrDialog(clicked);
                            }
                            if(drawInstagram){
                                List<InstagramPhoto> clicked = getClickedIn(releasedLocation);
                                if(clicked.size()>0) new InstagramDialog(clicked);
                            }
                            if(drawTwitter){
                                List<Tweet> clicked = getClickedTw(releasedLocation);
                                if(clicked.size()>0) new TwitterDialog(clicked);
                            }
                            
                        }
                        pressedLocation = null;
                    }
                }
            });

            this.addMouseWheelListener(new MouseAdapter() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() > 0) {
                        zoom--;
                    } else if (e.getWheelRotation() < 0) {
                        zoom++;
                    }
                    location = map.pointLocation(new Point2f(mouseX, mouseY));
                    refreshAll();
                }
            });


//        } catch (SocialMapException ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void draw() {
        background(0);

        if (refresh) {
            refresh = false;
            long start = System.nanoTime();

            map = new StaticMap(this, new Microsoft.AerialProvider(), new Point2f(width, height), location, zoom);

            int zoomIndex = (zoom - 8);
            zoomIndex = constrain(zoomIndex, 0, 7);
            int size = zoomToSize[zoomIndex];
            
            if (showMap && refreshMap) {
                drawMap();
            }

            if(drawFoursquare && refreshFoursquare){
                drawFoursquare(size);
            }
            
            if(drawTwitter && refreshTwitter){
                drawTwitter(size);
            }
            
            if(drawFlickr && refreshFlickr){
                drawFlickr(size);
            }
            
            if(drawInstagram && refreshInstagram){
                drawInstagram(size);
            }
            
            if(drawFlickr || drawFoursquare || drawInstagram || drawTwitter){
                drawLayers();
            } else {
                displayLayers = displayEmpty;
            }
            
            if(showLegend){
                drawLegend();
            }

            println((System.nanoTime() - start) / (double) 1000000000);
            
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    normalCursor();
                }
            });
        }
        
        if(showMap){
            image(displayMap, 0, 0, width, height);
        }
        
        image(displayLayers, 0, 0, width, height);
        
        if(showLegend){
            image(displayLegend, 0, 0, width, height);
        }

        if (save) {
            save(filename);
            save = false;
        }
    }
    
    private void drawLegend(){
        int count = 0;
        if(drawFlickr) count++;
        if(drawFoursquare) count++;
        if(drawInstagram) count++;
        if(drawTwitter) count++;
        int delta = (count>1) ? 80 / count : 40;
        int y = (count>0) ? 80 / (count+1) : 40;
        
        int legendWidth = 180;
        int legendHeight = 100;
        int originX = width-legendWidth;
        int originY = height-legendHeight;
        offscreenLegend.beginDraw();
        offscreenLegend.rectMode(CORNER);
        offscreenLegend.fill(220);
        offscreenLegend.stroke(0);
        offscreenLegend.rect(originX, originY, legendWidth+1, legendHeight+1);
        offscreenLegend.noStroke();
        offscreenLegend.fill(0);
        if(drawFlickr){
            flickrDot.render(offscreenLegend, originX + 20, originY + y, 10, 10);
            offscreenLegend.text("Flickr picture", originX + 40, originY + y + 10);
            y += delta;
        }
        if(drawTwitter){
            twitterDot.render(offscreenLegend, originX + 20, originY + y, 10, 10);
            offscreenLegend.text("Tweet", originX + 40, originY + y + 10);
            y += delta;
        }
        if(drawInstagram){
            instaDot.render(offscreenLegend, originX + 20, originY + y, 10, 10);
            offscreenLegend.text("Instagram picture", originX + 40, originY + y + 10);
            y += delta;
        }
        if(drawFoursquare){
            fsDot.render(offscreenLegend, originX + 20, originY + y, 10, 10);
            offscreenLegend.text("Foursquare Venue", originX + 40, originY + y + 10);
        }
        offscreenLegend.endDraw();
        
        displayLegend = offscreenLegend.getTexture();
    }

    private void drawLayers() {
        offscreen.beginDraw();
        offscreen.clear(0, 0, 0, 0);
        offscreen.setBlendMode(GLGraphics.ADD);
        if(drawFoursquare)
            offscreen.image(displayFs, 0, 0, width, height);
        if(drawTwitter)
            offscreen.image(displayTw, 0, 0, width, height);
        if(drawFlickr)
            offscreen.image(displayFl, 0, 0, width, height);
        if(drawInstagram)
            offscreen.image(displayIn, 0, 0, width, height);
        offscreen.setDefaultBlend();
        offscreen.endDraw();
        displayLayers = offscreen.getTexture();
    }

    private void drawMap() {
        refreshMap = false;
        offscreenMap.beginDraw();
        offscreenMap.clear(0, 0, 0, 0);
        offscreenMap.image(map.draw(), 0, 0);
        offscreenMap.endDraw();

        GLTexture tex = offscreenMap.getTexture();
        displayMap = new GLTexture(this, width, height);
        tex.filter(filter, (GLTexture) displayMap);
        System.gc();
    }

    private void drawInstagram(int size) {
        refreshInstagram = false;
        offscreenInsta.beginDraw();
        offscreenInsta.clear(0, 0, 0, 0);
        if(drawTextures){
            offscreenInsta.noStroke();
        } else {
            offscreenInsta.stroke(instaColor);
        }
        offscreenInsta.setBlendMode(GLGraphics.ADD);

        float delta = (float)size/2;
        if(mapReduced){
            mrGridIn.clear();
            for (MapReduceResult p : mrListIn) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    mrGridIn.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        instaDot.render(offscreenInsta, point.x - delta, point.y -delta, size, size);
                    } else {
                        offscreenInsta.point(point.x, point.y);
                    }
                }
            }
        } else {
            gridIn.clear();
            for (InstagramPhoto p : listIn) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    gridIn.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        instaDot.render(offscreenInsta, point.x - delta, point.y -delta, size, size);
                    } else {
                        offscreenInsta.point(point.x, point.y);
                    }
                }
            }
        }
        offscreenInsta.setDefaultBlend();
        offscreenInsta.endDraw();
        displayIn = offscreenInsta.getTexture();
    }

    private void drawFlickr(int size) {
        refreshFlickr = false;
        offscreenFlickr.beginDraw();
        offscreenFlickr.clear(0, 0, 0, 0);
        if(drawTextures){
            offscreenFlickr.noStroke();
            offscreenFlickr.rectMode(CENTER);
        } else {
            offscreenFlickr.stroke(flickrColor);
        }
        offscreenFlickr.setBlendMode(GLGraphics.ADD);

        float delta = (float)size/2;
        if(mapReduced){
            mrGridFl.clear();
            for (MapReduceResult p : mrListFl) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    mrGridFl.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        flickrDot.render(offscreenFlickr, point.x - delta, point.y - delta, size, size);
                    } else {
                        offscreenFlickr.point(point.x, point.y);
                    }
                }
            }
        } else {
            gridFl.clear();
            for (FlickrPhoto p : listFl) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    gridFl.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        flickrDot.render(offscreenFlickr, point.x - delta, point.y - delta, size, size);
                    } else {
                        offscreenFlickr.point(point.x, point.y);
                    }
                }
            }
        }
        offscreenFlickr.setDefaultBlend();
        offscreenFlickr.endDraw();
        displayFl = offscreenFlickr.getTexture();
    }

    private void drawTwitter(int size) {
        refreshTwitter = false;
        offscreenTwitter.beginDraw();
        offscreenTwitter.clear(0, 0, 0, 0);
        if(drawTextures){
            offscreenTwitter.noStroke();
            offscreenTwitter.rectMode(CENTER);
        } else {
            offscreenTwitter.stroke(twitterColor);
        }
        offscreenTwitter.setBlendMode(GLGraphics.ADD);

        float delta = (float)size/2;
        if(mapReduced){
            mrGridTw.clear();
            for (MapReduceResult p : mrListTw) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    mrGridTw.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        twitterDot.render(offscreenTwitter, point.x - delta, point.y - delta, size, size);
                    } else {
                        offscreenTwitter.point(point.x, point.y);
                    }
                }
            }
        } else {
            gridTw.clear();
            for (Tweet p : listTw) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    gridTw.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        twitterDot.render(offscreenTwitter, point.x - delta, point.y - delta, size, size);
                    } else {
                        offscreenTwitter.point(point.x, point.y);
                    }
                }
            }
        }
        offscreenTwitter.setDefaultBlend();
        offscreenTwitter.endDraw();
        displayTw = offscreenTwitter.getTexture();
    }

    private void drawFoursquare(int size) {
        refreshFoursquare = false;
        offscreenFs.beginDraw();
        offscreenFs.clear(0, 0, 0, 0);
        if(drawTextures){
            offscreenFs.noStroke();
            offscreenFs.rectMode(CENTER);
        } else {
            offscreenFs.stroke(fsColor);
        }
        offscreenFs.setBlendMode(GLGraphics.ADD);

        float delta = (float)size/2;
        if(mapReduced){
            mrGridFs.clear();
            for (MapReduceResult p : mrListFs) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    mrGridFs.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        fsDot.render(offscreenFs, point.x - delta, point.y - delta, size, size);
                    } else {
                        offscreenFs.point(point.x, point.y);
                    }
                }
            }
        } else {
            gridFs.clear();
            for (FoursquareVenue p : listFs) {
                Location loc = new Location(p.getLatitude(), p.getLongitude());
                Point2f point = map.locationPoint2f(loc);
                if (point.x >= 0 && point.x <= width && point.y >= 0 && point.y <= height) {
                    gridFs.addObject(p, (int)point.x, (int)point.y);
                    if(drawTextures) {
                        fsDot.render(offscreenFs, point.x - delta, point.y - delta, size, size);
                    } else {
                        offscreenFs.point(point.x, point.y);
                    }
                }
            }
        }
        offscreenFs.setDefaultBlend();
        offscreenFs.endDraw();
        displayFs = offscreenFs.getTexture();
    }

    public void refreshAll() {
        refreshFlickr = true;
        refreshFoursquare = true;
        refreshTwitter = true;
        refreshInstagram = true;
        refreshMap = true;
        refresh = true;
        waitCursor();
    }
    
    public void refreshAllButMap() {
        refreshFlickr = true;
        refreshFoursquare = true;
        refreshTwitter = true;
        refreshInstagram = true;
        refresh = true;
        waitCursor();
    }
    
    public void refreshMap() {
        refreshMap = true;
        refresh = true;
        waitCursor();
    }
    
    public void refreshTwitter() {
        refreshTwitter = true;
        refresh = true;
        waitCursor();
    }
    
    public void refreshFlickr() {
        refreshFlickr = true;
        refresh = true;
        waitCursor();
    }
    
    public void refreshFoursquare() {
        refreshFoursquare = true;
        refresh = true;
        waitCursor();
    }
    
    public void refreshInstagram() {
        refreshInstagram = true;
        refresh = true;
        waitCursor();
    }

    public void takeScreenshot(String filename) {
        this.filename = filename;
        save = true;
    }

    public void enableMap() {
        showMap = true;
        refreshMap();
    }
    
    public void disableMap(){
        showMap = false;
        displayMap = displayEmpty;
        refreshMap();
    }
    
    public void enableLegend() {
        showLegend = true;
        refresh = true;
    }
    
    public void disableLegend(){
        showLegend = false;
        displayLegend = displayEmpty;
        refresh = true;
    }

    public void zoomIn() {
        zoom++;
        refreshAll();
    }

    public void zoomOut() {
        zoom--;
        refreshAll();
    }
    
    public void enableFlickr(){
        drawFlickr = true;
        refreshFlickr();
    }
    
    public void disableFlickr(){
        drawFlickr = false;
        displayFl = displayEmpty;
        refreshFlickr();
    }
    
    public void enableFoursquare(){
        drawFoursquare = true;
        refreshFoursquare();
    }
    
    public void disableFoursquare(){
        drawFoursquare = false;
        displayFs = displayEmpty;
        refreshFoursquare();
    }
    
    public void enableTwitter(){
        drawTwitter = true;
        refreshTwitter();
    }
    
    public void disableTwitter(){
        drawTwitter = false;
        displayTw = displayEmpty;
        refreshTwitter();
    }
    
    public void enableInstagram(){
        drawInstagram = true;
        refreshInstagram();
    }
    
    public void disableInstagram(){
        drawInstagram = false;
        displayIn = displayEmpty;
        refreshInstagram();
    }
    
    public void enableTextures(){
        drawTextures = true;
        refreshAllButMap();
    }
    
    public void disableTextures(){
        drawTextures = false;
        refreshAllButMap();
    }
    
    public void enableMapReduce(){
        mapReduced = true;
        refreshAllButMap();
    }
    
    public void disableMapReduce(){
        mapReduced = false;
        refreshAllButMap();
    }
    
    private void waitCursor(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
    private void moveCursor(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }
    
    private void normalCursor(){
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private List getClicked(GridHash<? extends Plottable> grid, Point2f point) {
        List<? extends Plottable> list = grid.getUnitList(mouseX, mouseY);
        Location loc = map.pointLocation(point);
        System.out.println(loc.lat + " " + loc.lon);
        List res = new ArrayList();
        for (Plottable t : list) {
            if (Math.abs(t.getLongitude() - loc.lon) < 0.001 && Math.abs(t.getLatitude() - loc.lat) < 0.001) {
                res.add(t);
            }
        }
        return res;
    }
    
    private List getClickedFromMapReduced(List<MapReduceResult> list){
        List clicked = new ArrayList();
        if (list.size() > 0) {
            try {
                Database<? extends Plottable> database = DataHelper.getInstance().getDatabase(list.get(0).getType());
                for (MapReduceResult m : list) {
                    for (Object id : m.getIds()) {
                        clicked.add(database.getOne(id));
                    }
                }
            } catch (SocialMapException ex) {
                Logger.getLogger(MapApplet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return clicked;
    }
    
    private List<FoursquareVenue> getClickedFs(Point2f point){
        List<FoursquareVenue> clicked;
        if(mapReduced){
            List<MapReduceResult> res = getClicked(mrGridFs, point);
            clicked = getClickedFromMapReduced(res);
        } else {
            clicked = getClicked(gridFs, point);
        }
        return clicked;
    }
    
    private List<FlickrPhoto> getClickedFl(Point2f point){
        List<FlickrPhoto> clicked;
        if(mapReduced){
            List<MapReduceResult> res = getClicked(mrGridFl, point);
            clicked = getClickedFromMapReduced(res);
        } else {
            clicked = getClicked(gridFl, point);
        }
        return clicked;
    }
    
    private List<Tweet> getClickedTw(Point2f point){
        List<Tweet> clicked;
        if(mapReduced){
            List<MapReduceResult> res = getClicked(mrGridTw, point);
            clicked = getClickedFromMapReduced(res);
        } else {
            clicked = getClicked(gridTw, point);
        }
        return clicked;
    }
    
    private List<InstagramPhoto> getClickedIn(Point2f point){
        List<InstagramPhoto> clicked;
        if(mapReduced){
            List<MapReduceResult> res = getClicked(mrGridIn, point);
            clicked = getClickedFromMapReduced(res);
        } else {
            clicked = getClicked(gridIn, point);
        }
        return clicked;
    }
    
}
