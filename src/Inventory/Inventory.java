package Inventory;


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
    public void addItem(String name, Item.RARITIES rarity){
        addItem(name, rarity, 1, 0);
    }

    //add 1 or more items to the inventory
    public void addItem(String name, Item.RARITIES rarity, int amount) throws IllegalArgumentException{
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
        addItem(name, rarity, amount, 0);
    }


    //add the item(s) to the inventory
    //I am fully aware that this is not the most amazing way of adding an item, not sure if it's because of my choice of using maps.
    //I really wanted to have a dictionary style data_structure, using maps inside of maps was my best idea.
    //if we want to change the inventory layout it will be somewhat painful to change this, but with limited time, this is what I came up with :D
    public void addItem(String name, Item.RARITIES rarity, int amount, int upgrade_count) throws IllegalArgumentException{
        //I am using this to avoid case-sensitive errors and not finding items
        name = name.toLowerCase();

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

        //if the rarity is not epic then upgrade_count can't be more than 0!
        if(rarity != Item.RARITIES.Epic){
            upgrade_count = 0;
        }
        Item item = new Item(name, rarity);

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
    }

    //try to upgrade an item given its name and rarity
    public void upgradeItem(String item_name, Item.RARITIES rarity){
        upgradeItem(item_name, rarity, 0);
    }
    //given an item name, its rarity and its upgrade_count, try to upgrade the item
    public void upgradeItem(String item_name, Item.RARITIES rarity, int upgrade_count) throws IllegalStateException, IllegalArgumentException{
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

            //check if the player has at least 3 of the same items (requirement to upgrade)
            else if(item_amount < 3){
                throw new IllegalStateException("You have " + item_amount + " " + rarity + (upgrade_count > 0 ? upgrade_count : "") + " " + item_name + "'s in your inventory, you need at least 3 to upgrade!");
            }

            //now finally the fun part!
            //if we have 3 of the same items, upgrade!
            //but we must handle the case of having an epic item.

            if(rarity != Item.RARITIES.Epic)
                addItem(item_name, Item.get_rarity_above(rarity), 1);

            //we have an epic item
            else{
                //we either have a 0 or 1 upgrade epic item
                if (upgrade_count < 2){
                    addItem(item_name, Item.RARITIES.Epic, 1, upgrade_count + 1);
                }

                //we have an epic with upgrade_count 2, upgrade to LEGENDARY
                else{
                    addItem(item_name, Item.get_rarity_above(rarity), 1);
                }
            }

            //now, handle removing 3 of the items that we just upgraded

            //if we have 4 or more of the same item, simply subtract 3
            if(item_amount - 3 > 0){
                item.put(upgrade_count, item_amount - 3);
                return;
            }

            //however, if we have excatly 3, remove that item completely from this rarity field, handling the epic items
            if(rarity == Item.RARITIES.Epic && upgrade_count < 2){
                item.remove(upgrade_count);
            }

            else{
                rarity_map.remove(item_name);
            }



        }
        catch (Exception e){
            System.out.println(e.getLocalizedMessage());
            return;
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
        //now, iterate through each item name, and pick up the entry {name, upgrade_count}
        for(Map.Entry<String, Map<Integer, Integer>> name_upgrade_entry : item_list.get(rarity).entrySet()){

            //store the item name to print it later, after potentially printing the upgrade_count if the item is epic
            String item_name = name_upgrade_entry.getKey();

            //FINALLY, iterate through the upgrade_counts and
            for(Map.Entry<Integer, Integer> upgrade_amount_entry : name_upgrade_entry.getValue().entrySet()){
                //a nice arrow symbol I found online
                System.out.print("\u2023 ");
                //print the rarity of the item
                System.out.print(rarity);

                //print the item upgrade_count if it's bigger than 0 (if it's epic)
                int upgrade_count = upgrade_amount_entry.getKey();
                if(upgrade_count > 0){
                    System.out.print(upgrade_amount_entry.getKey());
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

        in.addItem("iron sword", Item.RARITIES.Common);
        in.addItem("iron sword", Item.RARITIES.Common);
        in.addItem("iron sword", Item.RARITIES.Common);
        in.addItem("iron sword", Item.RARITIES.Great);
        in.addItem("iron sword", Item.RARITIES.Great);
        in.addItem("Something rare", Item.RARITIES.Common);
        in.addItem("Something rar", Item.RARITIES.Common, 1, 1);
        in.addItem("Something epic", Item.RARITIES.Epic, 1, 1);
        in.addItem("Something epic", Item.RARITIES.Epic, 1, 1);
        in.addItem("Something epic", Item.RARITIES.Epic, 1, 1);
        in.addItem("Something epic", Item.RARITIES.Epic, 1, 2);
        in.addItem("Something epic", Item.RARITIES.Epic, 1, 2);
        in.print_items();
        in.upgradeItem("iron sword", Item.RARITIES.Common, 2);
        in.upgradeItem("iron sword", Item.RARITIES.Great);
        in.upgradeItem("SoMEthing rare", Item.RARITIES.Common, 0);
        in.upgradeItem("iron sword", Item.RARITIES.Epic, 0);
        System.out.println("PRINTING ITEMS");
        in.print_items();

        in.upgradeItem("something epic", Item.RARITIES.Epic, 1);
        System.out.println("PRINTING ITEMS");
        in.print_items();

        in.upgradeItem("something epic", Item.RARITIES.Epic, 1);
        in.upgradeItem("something epic", Item.RARITIES.Epic, 2);
        System.out.println("PRINTING ITEMS");
        in.print_items();
    }
}
