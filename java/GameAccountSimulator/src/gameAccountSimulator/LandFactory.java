package gameAccountSimulator;

public class LandFactory {

    public Land newLand(LandType type) {
        return createLand(type, 1);
    }

    public Land newLand(LandType type, int quantity) {
        return createLand(type, quantity);
    }
    
    private Land createLand(LandType type, int quantity) {
        Land land;

        switch (type) {
            case FARM:
                land = new Land("Farm", 50, quantity, 1);
                break;
            case LUMBER_MILL:
                land = new Land("Lumber Mill", 250, quantity, 5);
                break;
            case TAVERN:
                land = new Land("Tavern", 650, quantity, 12);
                break;
            case STONE_QUARRY:
                land = new Land("Stone Quarry", 2800, quantity, 50);
                break;
            case BARRAKS:
                land = new Land("Barraks", 9000, quantity, 150);
                break;
            case BLACKSMITH:
                land = new Land("Blacksmith", 14000, quantity, 250);
                break;
            case TEMPLE:
                land = new Land("Temple", 50000, quantity, 800);
                break;
            case VILLAGE:
                land = new Land("Village", 100000, quantity, 1400);
                break;
            case SHIPYARD:
                land = new Land("Shipyard", 180000, quantity, 2200);
                break;
            case CASTLE_KEEP:
                land = new Land("Castle Keep", 320000, quantity, 3200);
                break;
            case ROYAL_CASTLE:
                land = new Land("Royal Castle", 540000, quantity, 4500);
                break;
            case ROYAL_TRADE_ROUTE:
                land = new Land("Royal Trade Route", 1250000, quantity, 6200);
                break;
            case GOLD_MINE:
                land = new Land("Gold Mine", 4000000, quantity, 15000);
                break;
            default:
                land = null;
        }

        return land;
    }

}
