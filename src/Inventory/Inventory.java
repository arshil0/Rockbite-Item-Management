package Inventory;


import java.util.Scanner;
import Items.Item;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//this class will hold the list of currently held items
public class Inventory {

    //since in this task, we don't need to look up if a player has a specific item, BEFORE looking at its rarity for upgrading
    //I came up with this design.
    //I will save the rarities, then inside of each rarity the item name, which will hold the {upgrade_count, how many of these items we have}
    //here are some examples.
    //{Rare: {wooden staff : {1 : 2}}} we have 2 rare Iron swords, with upgrade_count 1
    //{Common: {small arcana : {0 : 1}}} we have 1 common small arcana (which will have upgrade_count 0)
    private Map<Item.RARITIES, Map<String, Map<Integer, Integer>>> item_list;

    public Inventory(){
        item_list = new HashMap<>();
    }

    //add the 1 item to the inventory, with upgrade_count 0
    public boolean addItem(String name, Item.RARITIES rarity){
        return addItem(name, rarity, 1, 0);
    }

    //add 1 or more items to the inventory
    public boolean addItem(String name, Item.RARITIES rarity, int amount) throws IllegalArgumentException{
        //if the player is trying to add 0 or less items, throw an exception
        if(amount <= 0){
            //I am using a try catch to throw the error and NOT terminate the program
            try{
                throw new IllegalArgumentException("Please provide a positive number when adding items");
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getLocalizedMessage());
                return false;
            }

        }
        return addItem(name, rarity, amount, 0);
    }


    //add the item(s) to the inventory
    //I am fully aware that this is not the most amazing way of adding an item, not sure if it's because of my choice of using maps.
    //I really wanted to have a dictionary style data_structure, using maps inside of maps was my best idea.
    //if we want to change the inventory layout it will be somewhat painful to change this, but with limited time, this is what I came up with :D
    public boolean addItem(String name, Item.RARITIES rarity, int amount, int upgrade_count) throws IllegalArgumentException{
        //I am using this to avoid case-sensitive errors and not finding items
        name = name.toLowerCase();

        if(upgrade_count < 0 || upgrade_count > 2){
            try{
                throw new IllegalArgumentException("Please provide an upgrade_count of either 0, 1 or 2");
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getLocalizedMessage());
                return false;
            }
        }
        //if the player is trying to add 0 or less items, throw an exception
        if(amount <= 0){
            //I am using a try catch to throw the error and NOT terminate the program
            try{
                throw new IllegalArgumentException("Please provide a positive number when adding items");
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getLocalizedMessage());
                return false;
            }

        }

        //if the rarity is not epic then upgrade_count can't be more than 0!
        if(rarity != Item.RARITIES.Epic){
            upgrade_count = 0;
        }

        //we already have an item with the same rarity!
        if(item_list.containsKey(rarity)){
            Map<String, Map<Integer,Integer>> rarity_map = item_list.get(rarity);

            //check if we also have the same item in our inventory
            if(rarity_map.containsKey(name)){
                Map<Integer,Integer> item_map = rarity_map.get(name);


                //check if we have the item with the same upgrade count.
                if(item_map.containsKey(upgrade_count)){
                    //take the item_amount we currently have and add 1
                    item_map.put(upgrade_count, item_map.get(upgrade_count) + amount);
                }

                else {
                    //otherwise, create a new key-value pair, with value amount
                    item_map.put(upgrade_count, amount);
                }
            }

            //if it's a new item in this rarity field, add a new key-value pair
            else{
                //create the {upgrade_count, item_amount} map with upgrade_count = to the given upgrade_count and amount 1 (as we didn't have this item before)
                Map<Integer, Integer> count_and_amount = new HashMap<>();
                count_and_amount.put(upgrade_count, amount);

                rarity_map.put(name, count_and_amount);
            }
        }

        //we don't have an item with this rarity, create a new key-value pair
        else{
            //the same exact code as above :)
            Map<Integer, Integer> count_and_amount = new HashMap<>();
            count_and_amount.put(upgrade_count, amount);

            //now create the {name, {upgrade_count, item_amount}} map
            Map<String, Map<Integer, Integer>> item_name_map = new HashMap<>();
            item_name_map.put(name, count_and_amount);

            item_list.put(rarity, item_name_map);
        }
        return true;
    }

    //try to upgrade an item given its name and rarity
    public boolean upgradeItem(String item_name, Item.RARITIES rarity){
        return upgradeItem(item_name, rarity, 0);
    }
    //given an item name, its rarity and its upgrade_count, try to upgrade the item
    public boolean upgradeItem(String item_name, Item.RARITIES rarity, int upgrade_count) throws IllegalStateException, IllegalArgumentException{
        //I am using this to avoid case-sensitive errors and not finding items
        item_name = item_name.toLowerCase();

        try{

            //check if the provided upgrade_count is valid
            if (upgrade_count < 0 || upgrade_count > 2){
                throw new IllegalArgumentException("You need to provide an upgrade value of either 0, 1, 2");
            }

            //if the item is NOT an epic, change the upgrade_count to 0
            if(upgrade_count != 0 && rarity != Item.RARITIES.Epic){
                upgrade_count = 0;
            }

            //pick up the rarity map {rarity : {item_name : {upgrade_count : amount}}}
            Map<String, Map<Integer, Integer>> rarity_map = item_list.get(rarity);

            //check if we even have an item with that rarity
            if(rarity_map == null){
                //check if the item is epic to write "an epic ..." instead of "a epic ..." :)
                if(rarity == Item.RARITIES.Epic)
                    throw new IllegalStateException("you don't have an " + rarity + " " + item_name + " in your inventory");
                throw new IllegalStateException("you don't have a " + rarity + " " + item_name + " in your inventory");
            }

            //pick up the item {item_name : {upgrade_count : amonut}}
            Map<Integer, Integer> item = rarity_map.get(item_name);

            //check if we have that item with the provided rarity
            if(item == null){
                if(rarity == Item.RARITIES.Epic)
                    throw new IllegalStateException("you don't have an " + rarity + " " + item_name + " in your inventory");
                throw new IllegalStateException("you don't have a " + rarity + " " + item_name + " in your inventory");
            }

            //I am using an Integer class instead of the primitive int type, to check for null exception
            Integer item_amount = item.get(upgrade_count);

            if(item_amount == null){
                if(rarity == Item.RARITIES.Epic)
                    throw new IllegalStateException("you don't have an " + rarity + upgrade_count + " " + item_name + " in your inventory");
                throw new IllegalStateException("you don't have a " + rarity + upgrade_count + " " + item_name + " in your inventory");
            }

            //handle the case of epic and epic1 rarity upgrades (they have different requirements)
            if(rarity == Item.RARITIES.Epic && upgrade_count < 2){
                System.out.println("Provide another epic item to upgrade with!");
                System.out.println("Here is a list of your epic items");

                //remove the item immediately, to not show it in the upgrade list
                remove_item(item_name, Item.RARITIES.Epic, 1, upgrade_count);
                print_items(Item.RARITIES.Epic);

                Scanner sc = new Scanner(System.in);

                String epic_item_name = sc.next();

                Map<Integer, Integer> epic_item_map = rarity_map.get(epic_item_name);

                if(epic_item_map == null){
                    //failed to upgrade item, add it back
                    addItem(item_name, Item.RARITIES.Epic, 1, upgrade_count);
                    throw new IllegalArgumentException("You don't seem to have an epic " + item_name);
                }

                //prioritize upgrading with the other epic's tier 0, if not, try tier 1, else tier 2 (which will be inconvenient, but maybe the player wants it).

                //get the tier0 (epic) item amount, I am using the Integer class to get null if the tier doesn't exist

                int tier = 0;
                for(tier = 0; tier < 2; tier++){
                    Integer tier_amount = epic_item_map.get(tier);

                    if(tier_amount != null){
                        addItem(item_name, Item.RARITIES.Epic, 1, upgrade_count + 1);
                        break;
                    }
                }


                //handle removing 1 of the other epic item
                remove_item(epic_item_name, Item.RARITIES.Epic, 1, tier);
            }

            else {
                //check if the player has at least 3 of the same items (requirement to upgrade)
                if(item_amount < 3){
                    throw new IllegalStateException("You have " + item_amount + " " + rarity + (upgrade_count > 0 ? upgrade_count : "") + " " + item_name + "'s in your inventory, you need at least 3 to upgrade!");
                }

                //now finally the fun part!
                //if we have 3 of the same items, upgrade!
                addItem(item_name, Item.get_rarity_above(rarity), 1);

                //now, handle removing 3 of the items that we just upgraded
                remove_item(item_name, rarity, 3, upgrade_count);

            }
        }
        catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    //given an item name, rarity, upgrade_count, remove an "amount" number of these items
    public void remove_item(String item_name, Item.RARITIES rarity, int amount, int upgrade_count) throws IllegalArgumentException{
        //I am using this to avoid case-sensitive errors and not finding items
        item_name = item_name.toLowerCase();

        //if the player is trying to add 0 or less items, throw an exception
        if(amount <= 0){
            //I am using a try catch to throw the error and NOT terminate the program
            try{
                throw new IllegalArgumentException("Please provide a positive number when adding items");
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getLocalizedMessage());
                return;
            }

        }

        Map<String, Map<Integer, Integer>> rarity_map = item_list.get(rarity);
        try{
            if(rarity_map == null){
                throw new IllegalArgumentException("Your provided item doesn't exist in your inventory");
            }

            Map<Integer, Integer> item_map = rarity_map.get(item_name);

            if(item_map == null){
                throw new IllegalArgumentException("Your provided item doesn't exist in your inventory");
            }

            Integer item_amount = item_map.get(upgrade_count);

            if(item_amount == null){
                throw new IllegalArgumentException("Your provided item doesn't exist in your inventory");
            }

            //we have the item!

            //if there is more than the amount in the inventory
            if(item_amount > amount){
                item_map.put(upgrade_count, item_amount - amount);
            }
            //otherwise, remove the item completely
            else{
                item_map.remove(upgrade_count);

                //if we have no other tiers for the item, remove the whole item
                if(item_map.size() == 0){
                    rarity_map.remove(item_name);
                }
            }

        }
        catch (IllegalArgumentException e){
            System.out.println(e.getLocalizedMessage());
        }

    }

    public void print_items(){
        //this is a huge mess, but my time is limited to make it look nicer, let me explain it!

        //iterate through the rarities and pick up {RARITY, Name}
        for(Map.Entry<Item.RARITIES, Map<String, Map<Integer, Integer>>> rarity_name_entry: item_list.entrySet()){
            //store the rarity to print later
            Item.RARITIES rarity = rarity_name_entry.getKey();

            //a bit slower to call this function to find the rarity map again in the list, but it's more convenient
            print_items(rarity);
        }
    }

    //print items by a given rarity
    public void print_items(Item.RARITIES rarity){
        //print the rarity of the item
        System.out.println(rarity + " :");
        //now, iterate through each item name, and pick up the entry {name, upgrade_count}
        for(Map.Entry<String, Map<Integer, Integer>> name_upgrade_entry : item_list.get(rarity).entrySet()){
            //store the item name to print it later, after potentially printing the upgrade_count if the item is epic
            String item_name = name_upgrade_entry.getKey();

            //FINALLY, iterate through the upgrade_counts and
            for(Map.Entry<Integer, Integer> upgrade_amount_entry : name_upgrade_entry.getValue().entrySet()){
                //a nice arrow symbol I found online
                System.out.print("\u2023 ");

                //print the item upgrade_count (if it's epic)
                if (rarity == Item.RARITIES.Epic){
                    System.out.print("tier " + upgrade_amount_entry.getKey());
                }


                //print the item name
                System.out.print(" " + item_name + " ");

                //and print the number of these items the player has
                System.out.println(upgrade_amount_entry.getValue());
            }
        }
    }

    public static void main(String[] args){
        Inventory in = new Inventory();

        in.addItem("iron_sword", Item.RARITIES.Common);
        in.addItem("iron_sword", Item.RARITIES.Common);
        in.addItem("iron_sword", Item.RARITIES.Common);
        in.addItem("iron_sword", Item.RARITIES.Great);
        in.addItem("iron_sword", Item.RARITIES.Great);
        in.addItem("Something_rare", Item.RARITIES.Common);
        in.addItem("Something_rar", Item.RARITIES.Common, 1, 1);
        in.addItem("Something_epic", Item.RARITIES.Epic, 1, 1);
        in.addItem("Something_epic", Item.RARITIES.Epic, 1, 1);
        in.addItem("Something_epic", Item.RARITIES.Epic, 1, 1);
        in.addItem("Something_epic", Item.RARITIES.Epic, 1, 2);
        in.addItem("Something_epic", Item.RARITIES.Epic, 1, 2);
        in.addItem("epic_3", Item.RARITIES.Epic, 1);
        in.print_items();
        in.upgradeItem("iron_sword", Item.RARITIES.Common, 2);
        in.upgradeItem("iron_sword", Item.RARITIES.Great);
        in.upgradeItem("SoMEthing_rare", Item.RARITIES.Common, 0);
        in.upgradeItem("iron_sword", Item.RARITIES.Epic, 0);
        System.out.println("PRINTING ITEMS");
        in.print_items();

        in.upgradeItem("epic_3", Item.RARITIES.Epic, 0);
        //in.upgradeItem("something_epic", Item.RARITIES.Epic, 1);
        System.out.println("PRINTING ITEMS");
        in.print_items();

        in.upgradeItem("something_epic", Item.RARITIES.Epic, 1);
        in.upgradeItem("something_epic", Item.RARITIES.Epic, 2);
        System.out.println("PRINTING ITEMS");
        in.print_items();
    }
}
