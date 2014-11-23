package at.cpickl.agrotlin;

import android.graphics.Color;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class classes {

    public static class Game {
        private final Collection<Player> players;
        private final Map map;

        public Game(Collection<Player> players, Map map) {
            this.players = players;
            this.map = map;
        }

        public Collection<Player> getPlayers() {
            return players;
        }

        public Map getMap() {
            return map;
        }
    }

    public static class Map {
        private final Collection<Region> regions;

        public Map(Collection<Region> regions) {
            this.regions = regions;
        }
    }

    public static class Player {
        private final String name;
        private final int color;

        public Player(String name, int color) {
            this.name = name;
            this.color = color;
        }

        public String name() {
            return name;
        }

        public int color() {
            return color;
        }

        @Override
        public String toString() {
            return "Player[name=" + name + "]";
        }
    }

    public static class Region {
        private final Collection<Region> outs = new LinkedHashSet<Region>();
        private Player owner;
        private int armies;

        public Region addOuts(Region... out) {
            for (Region o : out) {
                outs.add(o);
                o.outs.add(this); // do it bi-directional!
            }
            return this;
        }
        public Region ownedBy(Player newOwner, int newArmies) {
            owner = newOwner;
            armies = newArmies;
            return this;
        }
        public Collection<Region> getOuts() {
            return outs;
        }
        public Player getOwner() {
            return owner;
        }
        public int getArmies() {
            return armies;
        }
        public void setArmies(int armies) {
            this.armies = armies;
        }

        public String toString() {
            return "Region[owner="+owner+"]";
        }

    }

}
