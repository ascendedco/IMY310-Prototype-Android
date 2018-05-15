package co.ascended.waiterio.database

import co.ascended.waiterio.entity.*
import io.reactivex.subjects.BehaviorSubject

object Database {

    val tables: BehaviorSubject<List<Table>> = BehaviorSubject.create()
    val categories: BehaviorSubject<List<Category>> = BehaviorSubject.create()
    val items: BehaviorSubject<List<Item>> = BehaviorSubject.create()
    val orders: BehaviorSubject<List<Order>> = BehaviorSubject.create()

    private val tablesList: MutableList<Table> = mutableListOf()
    private val categoriesList: MutableList<Category> = mutableListOf()
    private val itemsList: MutableList<Item> = mutableListOf()
    private val ordersList: MutableList<Order> = mutableListOf()

    fun seed(tables: List<Table>, categories: List<Category>, items: List<Item>, orders: List<Order>) {
        this.tablesList.addAll(tables)
        this.categoriesList.addAll(categories)
        this.itemsList.addAll(items)
        this.ordersList.addAll(orders)

        this.tables.onNext(this.tablesList)
        this.categories.onNext(this.categoriesList)
        this.items.onNext(this.itemsList)
        this.orders.onNext(this.ordersList)
    }

    fun addOrder(order: Order) {
        if (!ordersList.contains(order)) {
            ordersList.add(order)
            orders.onNext(ordersList)
        }
    }

    fun updateOrder(order: Order) {
        if (ordersList.contains(order)) {
            val index = ordersList.indexOfFirst { it.number == order.number }
            ordersList.removeAt(index)
            ordersList.add(order)
            orders.onNext(ordersList)
        }
    }

    fun addTable(table: Table) {
        if (!tablesList.contains(table)) {
            tablesList.add(table)
            tables.onNext(tablesList)
        }
    }

    fun updateTable(table: Table) {
        if (tablesList.contains(table)) {
            val index = tablesList.indexOfFirst { it.number == table.number }
            tablesList.removeAt(index)
            tablesList.add(table)
            tables.onNext(tablesList)
        }
    }

    fun addItem(item: Item) {
        if (!itemsList.contains(item)) {
            itemsList.add(item)
            items.onNext(itemsList)
        }
    }

    fun updateItem(item: Item) {
        if (itemsList.contains(item)) {
            val index = itemsList.indexOfFirst { it.id == item.id }
            itemsList.removeAt(index)
            itemsList.add(item)
            items.onNext(itemsList)
        }
    }

    fun removeItem(item: Item) {
        itemsList.remove(item)
        items.onNext(itemsList)
    }
}