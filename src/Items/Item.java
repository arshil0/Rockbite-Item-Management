package Items;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.InvalidParameterException;

public class Item {

    public enum RARITIES {
        Common,
        Great,
        Rare,
        Epic,
        Legendary
    }

    //I will be assuming each item name is unique, hence saving and creating items will depend on its name.
    private String name;
    private RARITIES rarity;
    private int upgrade_count;

    //initialization of an item
    public Item(String name, RARITIES rarity){
        this.name = name;
        this.rarity = rarity;
        upgrade_count = 0;
    }

    //maybe we found an epic item that is higher than level 0.
    public Item(String name, RARITIES rarity, int upgrade_count){
        this.name = name;
        this.rarity = rarity;
        this.upgrade_count = upgrade_count;
    }

    //given a rarity, return the rarity 1 level higher, used for upgrading
    public static RARITIES get_rarity_above(RARITIES rarity) throws InvalidParameterException {
        try {
            switch (rarity) {
                case Common -> {
                    return RARITIES.Great;
                }
                case Great -> {
                    return RARITIES.Rare;
                }
                case Rare -> {
                    return RARITIES.Epic;
                }
                case Epic -> {
                    return RARITIES.Legendary;
                }
                case Legendary -> {
                    //there is no higher rank than legendary, so throw and exception (without terminating the program)
                    throw new InvalidParameterException("There is no higher rank than Legendary");
                }
            }
        }
        catch (InvalidParameterException e){
            System.out.println(e.getLocalizedMessage());
            return RARITIES.Legendary;
        }

        //I have handled all cases, but I have to return something for java not to complain!
        return RARITIES.Common;
    }

    /**
    //create a new item and save it in the "database", which in this case is the .txt file "list_of_items.txt"
    //if the given item name already exists (item names should be unique)
    public static void create_new_or_update_item(String name, RARITIES rarity){
        try{
            //open the list_of_items file and append the new item onto the list
            FileWriter file = new FileWriter("list_of_items.txt", true);
            file.write(name + " " + rarity + "\n");
            file.close();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public static void main(String[] args){
        create_new_item("iron sword", RARITIES.Common);
    }
     */
}
