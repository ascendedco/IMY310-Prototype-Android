package co.ascended.waiterio.database;

import java.util.ArrayList;

import co.ascended.waiterio.entity.Category;
import co.ascended.waiterio.entity.Item;
import co.ascended.waiterio.entity.Order;
import co.ascended.waiterio.entity.Table;

public class Seeding {
    public Seeding(){
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Table> tables = new ArrayList<>();

        categories.add(new Category(0,"Drinks"));
        categories.add(new Category(1,"Starter"));
        categories.add(new Category(2,"Main"));
        categories.add(new Category(3,"Dessert"));

        tables.add(new Table(1,false));
        tables.add(new Table(2,false));
        tables.add(new Table(3,true));
        tables.add(new Table(4,false));
        tables.add(new Table(5,true));
        tables.add(new Table(6,false));
        tables.add(new Table(7,true));
        tables.add(new Table(8,false));


        //drinks
        items.add(new Item(0,"Buddy Coke 300ml",
                "",
                categories.get(0), true,0,20.00));
        items.add(new Item(1,"Cream Soda",
                "",
                categories.get(0), true,0,20.00));
        items.add(new Item(2,"Castle Lite",
                "",
                categories.get(0), true,0,23.00));
        items.add(new Item(3,"Castle Lite Draught",
                "",
                categories.get(0), true,0,32.00));
        items.add(new Item(4,"Rock Shandy",
                "",
                categories.get(0), true,0,30.00));

        //starters
        items.add(new Item(5,"Haloumi Bites",
                "Crumbed Haloumi bites served with tzatziki on the side.",
                categories.get(1), true,0,50.00));
        items.add(new Item(6,"Hummus Dip",
                "A tasty hummus dip served with pita bread.",
                categories.get(1), true,0,45.00));
        items.add(new Item(7,"Lamb Cigars",
                "Three lamb, spinach and feta inside a spring roll.",
                categories.get(1), true,0,50.00));
        items.add(new Item(8,"Sweet Potato Chips Plate",
                "Large plate of sweet potato chips.",
                categories.get(1), true,0,30.00));

        //mains
        items.add(new Item(9,"300g Fillet Steak",
                "300g Fillet Steak served with chips.",
                categories.get(2), true,0,79.95));
        items.add(new Item(10,"Lasagna",
                "400g of traditional beef lasagna.",
                categories.get(2), true,0,77.00));
        items.add(new Item(11,"Sirloin Steak",
                "Delicious 400g of Sirloin.",
                categories.get(2), true,0,93.00));
        items.add(new Item(12,"Bacon Cheeseburger",
                "Bacon cheeseburger served with chips.",
                categories.get(2), true,0,64.00));
        items.add(new Item(13,"300g Hake Fillet",
                "Hake fillet with a lemon zest.",
                categories.get(2), true,0,65.00));
        items.add(new Item(14,"Steak Sandwich",
                "200g of steak in a sandwich.",
                categories.get(2), true,0,65.00));

        //dessert
        items.add(new Item(15,"Lemon Meringue",
                "Traditional lemon meringue.",
                categories.get(3), true,0,45.00));
        items.add(new Item(16,"Chocolate Brownie",
                "Large chocolate brownie with some whipped cream.",
                categories.get(3), true,0,45.00));
        items.add(new Item(17,"Chocolate Mousse",
                "Traditional chocolate mousse.",
                categories.get(3), true,0,45.00));

        ArrayList<Item> orderOne = new ArrayList<>();
        orderOne.add(items.get(0));
        orderOne.add(items.get(5));
        orderOne.add(items.get(9));
        orderOne.add(items.get(7));
        orders.add(new Order(0,tables.get(2),orderOne,0,"Ready"));

        ArrayList<Item> orderTwo = new ArrayList<>();
        orderOne.add(items.get(2));
        orderOne.add(items.get(10));
        orderOne.add(items.get(10));
        orderOne.add(items.get(6));
        orders.add(new Order(1,tables.get(4),orderTwo,0,"Delayed"));

        ArrayList<Item> orderThree = new ArrayList<>();
        orderOne.add(items.get(3));
        orderOne.add(items.get(11));
        orderOne.add(items.get(13));
        orderOne.add(items.get(14));
        orders.add(new Order(2,tables.get(6),orderThree,0,"Ready"));

    }
}
