import Inventory.Inventory;
import Items.Item;

import java.security.InvalidParameterException;
import java.util.Scanner;
public class Main {

    public static void main(String[] args){
        Inventory inventory = new Inventory();
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to inventory management system!");
        System.out.println("You can start typing in commands, type 'help' to get a list of available commands.");

        while(true){
            String command = sc.next();

            if(command.equalsIgnoreCase("help")){
                System.out.println("Commands: \n add_item : add a new item \n item_list : print the item list \n upgrade : upgrade an item \n exit : exit the terminal \n add_random_rarity_item : give an item name and get a random rarity of it!");
            }

            else if(command.equalsIgnoreCase("add_item")){
                System.out.println("please provide a name and rarity of an item! (separate item name by '_' instead of space" );
                String item_name = sc.next();
                String rarity = sc.next();

                try{
                    int upgrade_count = 0;
                    if(rarity.equalsIgnoreCase("epic")){
                        System.out.println("please provide an upgrade_count for your epic item");
                        upgrade_count = sc.nextInt();
                    }
                    if (inventory.addItem(item_name, get_rarity(rarity), 1, upgrade_count)){
                        System.out.println("successfully added item!");
                    };

                }
                catch (Exception e){
                    continue;
                }

            }
            else if(command.equalsIgnoreCase("item_list")){
                inventory.print_items();
            }

            else if(command.equalsIgnoreCase("upgrade")){
                System.out.println("please provide an item name and rarity to upgrade:");
                String item_name = sc.next();
                String rarity = sc.next();
                try{
                    int upgrade_count = 0;
                    if(rarity.equalsIgnoreCase("epic")){
                        System.out.println("please provide an upgrade_count for your epic item");
                        upgrade_count = sc.nextInt();
                    }
                    if(inventory.upgradeItem(item_name, get_rarity(rarity), upgrade_count)){
                        System.out.println("Succesfully upgraded item!");
                    };
                }catch (Exception e){
                    continue;
                }
            }
            else if(command.equalsIgnoreCase("add_random_rarity_item")){
                System.out.println("please provide an item name to add to your inventory!");
                String item_name = sc.next();
                Item.RARITIES rarity;
                int upgrade_count = 0;

                double rand = Math.random();

                //50% chance to get a common
                if(rand < 0.5){
                    rarity = Item.RARITIES.Common;
                }
                //25% chance to get a great
                else if(rand < 0.75){
                    rarity = Item.RARITIES.Great;
                }
                //15% chance to get a rare
                else if(rand < 0.9){
                    rarity = Item.RARITIES.Rare;
                }
                //8% chance for epic
                else if(rand < 0.98){
                    rarity = Item.RARITIES.Epic;

                    double random_upgrade = Math.random();

                    //10% chance for tier 1
                    if(random_upgrade < 0.9)
                        upgrade_count = 1;
                    //5% chance for tier 2 (after failing tier 1)
                    else if (upgrade_count < 0.95)
                        upgrade_count = 2;
                }
                //2% chance for legendary
                else{
                    rarity = Item.RARITIES.Legendary;
                }
                try{
                    if(inventory.addItem(item_name, rarity, 1, upgrade_count)){
                        System.out.println("Succesfully upgraded item!");
                    };
                }catch (Exception e){
                    continue;
                }
            }
            else if(command.equalsIgnoreCase("exit")){
                break;
            }
        }
    }

    public static Item.RARITIES get_rarity(String rarity){
        try {
            switch (rarity.toLowerCase()) {
                case "common" -> {
                    return Item.RARITIES.Common;
                }
                case "great" -> {
                    return Item.RARITIES.Great;
                }
                case "rare" -> {
                    return Item.RARITIES.Rare;
                }
                case "epic" -> {
                    return Item.RARITIES.Epic;
                }
                case "legendary" -> {
                    return Item.RARITIES.Legendary;
                }
                default -> throw new IllegalArgumentException("provided rarity does not exist");
            }
        }
        catch (IllegalArgumentException e){
            System.out.println(e.getLocalizedMessage());
            return Item.RARITIES.Legendary;
        }
    }
}
