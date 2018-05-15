package co.ascended.waiterio.entity

data class Order(var number: Int, var table: Table, var items: List<Item>,
                 var total: Double, var status: String)